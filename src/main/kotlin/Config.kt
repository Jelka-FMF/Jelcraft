package si.progklub.jelcraft

import si.progklub.jelcraft.utils.loadEnvironmentBoolean
import si.progklub.jelcraft.utils.loadEnvironmentDouble
import si.progklub.jelcraft.utils.loadEnvironmentFloat
import si.progklub.jelcraft.utils.loadEnvironmentInt
import si.progklub.jelcraft.utils.loadEnvironmentString

object Config {
    /**
     * Path to the CSV file with position coordinates.
     */
    val POSITIONS_FILE: String = loadEnvironmentString("POSITIONS_FILE")

    /**
     * Scale to apply to the position coordinates.
     */
    val POSITIONS_SCALE: Double = loadEnvironmentDouble("POSITIONS_SCALE", 80.0)

    /**
     * Glyph to use for the light display entities.
     */
    val LIGHTS_GLYPH: String = loadEnvironmentString("LIGHTS_GLYPH", "⬤")

    /**
     * Scaling factor of the light display entity. The entity model
     * is scaled centered on the origin in both directions.
     */
    val LIGHTS_SCALE: Double = loadEnvironmentDouble("LIGHTS_SCALE", 4.0)

    /**
     * Maximum view range of the light display entity. When the distance
     * is more than `<viewRange> * <entityDistanceScaling> * 64`, the
     * entity is not rendered.
     */
    val LIGHTS_VIEW_RANGE: Float = loadEnvironmentFloat("LIGHTS_VIEW_RANGE", 4.0f)

    /**
     * The spawn point of the players (x coordinate).
     */
    val SPAWN_POINT_X: Double = loadEnvironmentDouble("SPAWN_POINT_X", 0.0)

    /**
     * The spawn point of the players (y coordinate).
     */
    val SPAWN_POINT_Y: Double = loadEnvironmentDouble("SPAWN_POINT_Y", 35.0)

    /**
     * The spawn point of the players (z coordinate).
     */
    val SPAWN_POINT_Z: Double = loadEnvironmentDouble("SPAWN_POINT_Z", -60.0)

    /**
     * The server view distance.
     */
    val VIEW_DISTANCE: Int = loadEnvironmentInt("VIEW_DISTANCE", 16)

    /**
     * The address on which the server will be running.
     */
    val SERVER_ADDRESS: String = loadEnvironmentString("SERVER_ADDRESS", "0.0.0.0")

    /**
     * The port on which the server will be running.
     */
    val SERVER_PORT: Int = loadEnvironmentInt("SERVER_PORT", 25565)

    /**
     * The URL of the status server-sent events stream.
     */
    val SSE_STATUS_URL: String = loadEnvironmentString("SSE_STATUS_URL")

    /**
     * The URL of the driver server-sent events stream.
     */
    val SSE_DRIVER_URL: String = loadEnvironmentString("SSE_DRIVER_URL")

    /**
     * The number of maximum reconnection attempts to the SSE server.
     */
    val SSE_RECONNECTION_ATTEMPTS: Int = loadEnvironmentInt("SSE_RECONNECTION_ATTEMPTS", 10)

    /**
     * The delay between reconnection attempts to the SSE server.
     */
    val SSE_RECONNECTION_DELAY: Int = loadEnvironmentInt("SSE_RECONNECTION_DELAY", 1000)

    /**
     * The URL of the website displayed in the welcome message.
     */
    val WELCOME_MESSAGE_WEBSITE: String = loadEnvironmentString("WELCOME_MESSAGE_WEBSITE", "https://jelka.fmf.uni-lj.si/")

    /**
     * The URL of the repository displayed in the welcome message.
     */
    val WELCOME_MESSAGE_REPOSITORY: String = loadEnvironmentString("WELCOME_MESSAGE_REPOSITORY", "https://github.com/Jelka-FMF/Jelcraft")

    /**
     * Whether to enable the server resource pack.
     */
    val RESOURCE_PACK_ENABLED: Boolean = loadEnvironmentBoolean("RESOURCE_PACK_ENABLED", false)

    /**
     * The UUID of the server resource pack.
     */
    val RESOURCE_PACK_ID: String = loadEnvironmentString("RESOURCE_PACK_ID", "")

    /**
     * The URL of the server resource pack.
     */
    val RESOURCE_PACK_URL: String = loadEnvironmentString("RESOURCE_PACK_URL", "")

    /**
     * The SHA1 hash of the server resource pack.
     */
    val RESOURCE_PACK_HASH: String = loadEnvironmentString("RESOURCE_PACK_HASH", "")
}
