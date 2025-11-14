package si.progklub.jelcraft.components

import io.github.oshai.kotlinlogging.KotlinLogging
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.InstanceContainer
import si.progklub.jelcraft.Config
import si.progklub.jelcraft.utils.loadPositions
import si.progklub.jelcraft.utils.normalizePositions

private val logger = KotlinLogging.logger {}

class Light(
    position: Pos,
    color: TextColor = TextColor.color(0, 0, 0),
    glyph: TextComponent = Component.text(Config.LIGHTS_GLYPH),
) {
    private val entity = Entity(EntityType.TEXT_DISPLAY)
    private val meta = entity.entityMeta as TextDisplayMeta

    private fun refreshText() {
        meta.text = glyph.color(color)
    }

    var position: Pos = position
        set(value) {
            if (field == value) return
            field = value

            // Teleport the entity to the new position
            entity.teleport(value)
        }

    var color: TextColor = color
        set(value) {
            if (field == value) return
            field = value

            // Rerender the displayed text
            refreshText()
        }

    var glyph: TextComponent = glyph
        set(value) {
            if (field == value) return
            field = value

            // Rerender the displayed text
            refreshText()
        }

    init {
        // Disable gravity
        entity.setNoGravity(true)

        // Set the entity metadata
        meta.scale = Vec(Config.LIGHTS_SCALE, Config.LIGHTS_SCALE, Config.LIGHTS_SCALE)
        meta.viewRange = Config.LIGHTS_VIEW_RANGE
        meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.CENTER
        meta.backgroundColor = 0
        meta.setBrightness(15, 15)

        // Set the initial text color
        refreshText()
    }

    fun spawn(instance: InstanceContainer) {
        entity.setInstance(instance, position)
    }
}

fun constructLights(): Map<Int, Light> {
    // Load and normalize positions
    logger.info { "Loading positions from ${Config.POSITIONS_FILE}" }
    val rawPositions = loadPositions(Config.POSITIONS_FILE)
    val normalizedParameters = normalizePositions(rawPositions, Config.POSITIONS_SCALE)
    logger.info { "Loaded ${normalizedParameters.size} positions" }

    // Construct lights from positions
    return normalizedParameters.mapValues { (_, pos) -> Light(pos) }
}
