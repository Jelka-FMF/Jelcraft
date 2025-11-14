package si.progklub.jelcraft

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.sse.SSE
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.ping.Status
import si.progklub.jelcraft.components.Pattern
import si.progklub.jelcraft.components.State
import si.progklub.jelcraft.components.constructLights
import si.progklub.jelcraft.utils.loadResourceBytes
import si.progklub.jelcraft.utils.startEventListener
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

private val client =
    HttpClient(CIO) {
        expectSuccess = true
        install(ContentEncoding)
        install(SSE) {
            maxReconnectionAttempts = Config.SSE_RECONNECTION_ATTEMPTS
            reconnectionTime = Config.SSE_RECONNECTION_DELAY.milliseconds
        }
    }

private val lights = constructLights()

private var patterns = emptyList<Pattern>()
private var state = State(null, null, 0.0, null, false)

fun main() {
    // Initialize the server
    val minecraftServer = MinecraftServer.init()

    // Create the instance
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    // Initialize components
    initializeInstance(instanceContainer)
    initializeEvents(instanceContainer)

    // Spawn lights in the instance
    spawnLights(instanceContainer)

//    val scheduler = MinecraftServer.getSchedulerManager()
//    var color = 0
//    scheduler.submitTask({
//        color += 1
//        if (color > 255) color = 0
//        lights.values.forEach { light -> light.color = TextColor.color(color, 255 - color, 0) }
//        TaskSchedule.millis(10)
//    })

    // Start the server
    logger.info { "Starting server at ${Config.SERVER_ADDRESS}:${Config.SERVER_PORT}" }
    minecraftServer.start(Config.SERVER_ADDRESS, Config.SERVER_PORT)

    // Start event streams
    startEventStreams(instanceContainer)
}

private fun initializeInstance(instanceContainer: InstanceContainer) {
    // Set the time to midnight with a new moon
    instanceContainer.time = 114000
    instanceContainer.timeRate = 0

    // Set the view distance
    instanceContainer.viewDistance(Config.VIEW_DISTANCE)

    // Configure the chunk lighting
    instanceContainer.setChunkSupplier(::LightingChunk)
}

private fun initializeEvents(instanceContainer: InstanceContainer) {
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    // Load the server icon from resources
    val favicon = loadResourceBytes("/favicon.png")

    // Construct the server description
    val description =
        Component
            .empty()
            .append(Component.text("Jelcraft", NamedTextColor.GOLD))
            .append(Component.text(" • ", NamedTextColor.WHITE))
            .append(Component.text("Programerski klub FMF", NamedTextColor.LIGHT_PURPLE))

    // Show icon and description in the server list
    globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
        val onlinePlayers = MinecraftServer.getConnectionManager().onlinePlayerCount
        val maxPlayers = ((onlinePlayers + 10 + 9) / 10) * 10

        event.status =
            Status
                .builder()
                .favicon(favicon)
                .description(description)
                .playerInfo(onlinePlayers, maxPlayers)
                .build()
    }

    // Configure players when they join
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        logger.info { "Player ${event.player.username} (${event.player.uuid}) joined the game" }

        event.spawningInstance = instanceContainer
        event.player.respawnPoint = Pos(Config.SPAWN_POINT_X, Config.SPAWN_POINT_Y, Config.SPAWN_POINT_Z)
        event.player.gameMode = GameMode.SPECTATOR
    }

    // Send a welcome message to players
    globalEventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
        if (!event.isFirstSpawn) return@addListener

        // Send a static welcome message
        event.player.sendMessage(description.decorate(TextDecoration.UNDERLINED))
        event.player.sendMessage(Component.empty())
        event.player.sendMessage(
            Component
                .empty()
                .append(Component.text("Spletna stran: ", NamedTextColor.YELLOW))
                .append(
                    Component
                        .text(Config.WELCOME_MESSAGE_WEBSITE)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(Config.WELCOME_MESSAGE_WEBSITE)),
                ),
        )
        event.player.sendMessage(
            Component
                .empty()
                .append(Component.text("Izvorna koda: ", NamedTextColor.YELLOW))
                .append(
                    Component
                        .text(Config.WELCOME_MESSAGE_REPOSITORY)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(Config.WELCOME_MESSAGE_REPOSITORY)),
                ),
        )
        event.player.sendMessage(Component.empty())

        if (state.runnerIsActive && state.currentPatternIdentifier != null && state.currentPatternRemaining >= 0) {
            // Send the current pattern if it's running
            sendPatternMessage(event.player, state.currentPatternIdentifier!!)
        } else {
            // Send a static message if the tree is currently sleeping
            event.player.sendMessage("Jelka trenutno spi ...")
        }
    }

    // Log when players leave
    globalEventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
        logger.info { "Player ${event.player.username} (${event.player.uuid}) left the game" }
    }

    // Disable sending chat messages
    globalEventHandler.addListener(PlayerChatEvent::class.java) { event ->
        event.isCancelled = true
    }
}

private fun spawnLights(instanceContainer: InstanceContainer) {
    lights.values.forEach { light -> light.spawn(instanceContainer) }
}

private fun startEventStreams(instanceContainer: InstanceContainer) {
    try {
        val statusSseClient = setupStatusStream(instanceContainer)
        val driverSseClient = setupDriverStream()
        runBlocking { awaitAll(statusSseClient, driverSseClient) }
    } catch (exception: Exception) {
        exception.printStackTrace()
        exitProcess(1)
    }
}

private fun setupStatusStream(instanceContainer: InstanceContainer): Deferred<Unit> {
    @Serializable
    data class StatusPayload(
        val patterns: List<Pattern>,
        val state: State,
    )

    @Serializable
    data class StartedPayload(
        val identifier: String,
        val started: String,
        val remaining: Double,
    )

    val json = Json { ignoreUnknownKeys = true }

    return startEventListener(client, Config.SSE_STATUS_URL) { event ->
        when (event.event) {
            "status" -> {
                logger.info { "Received status event" }

                val payload = json.decodeFromString<StatusPayload>(event.data!!)
                patterns = payload.patterns
                state = payload.state
            }

            "started" -> {
                logger.info { "Received started event" }

                val payload = json.decodeFromString<StartedPayload>(event.data!!)
                sendPatternMessage(instanceContainer, payload.identifier)
            }

            "stopped" -> {
                logger.info { "Received stopped event" }
            }
        }
    }
}

private fun setupDriverStream(): Deferred<Unit> =
    startEventListener(client, Config.SSE_DRIVER_URL) { event ->
        if (event.event != "message") return@startEventListener

        val line = event.data?.trim()
        if (line == null || line[0] != '#') return@startEventListener

        for (i in 0 until (line.length - 1) / 6) {
            val red = line.substring(i * 6 + 1, i * 6 + 3).toInt(16)
            val green = line.substring(i * 6 + 3, i * 6 + 5).toInt(16)
            val blue = line.substring(i * 6 + 5, i * 6 + 7).toInt(16)

            lights[i]?.color = TextColor.color(red, green, blue)
        }
    }

private fun sendPatternMessage(
    audience: Audience,
    identifier: String,
) {
    val pattern = patterns.find { pattern -> pattern.identifier == identifier }

    if (pattern == null) {
        logger.error { "No pattern with identifier $identifier" }
        return
    }

    val nameText = pattern.name.ifBlank { pattern.identifier }

    var nameComponent = Component.text(nameText, NamedTextColor.YELLOW)
    if (pattern.source?.isNotBlank() == true) nameComponent = nameComponent.clickEvent(ClickEvent.openUrl(pattern.source))

    val authorText =
        listOfNotNull(pattern.author?.ifBlank { null }, pattern.school?.ifBlank { null })
            .joinToString(", ")
            .ifBlank { "/" }

    val authorComponent = Component.text(authorText, NamedTextColor.GOLD)

    val message =
        Component
            .text("▶ ")
            .append(nameComponent)
            .append(Component.text(" • "))
            .append(authorComponent)

    audience.sendMessage(message)
}
