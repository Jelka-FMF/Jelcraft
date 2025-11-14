package si.progklub.jelcraft.utils

private const val PREFIX = "JELCRAFT_"

private inline fun <T> loadEnvironmentInternal(
    name: String,
    default: T? = null,
    crossinline parse: (String) -> T?,
): T {
    val name = PREFIX + name
    val raw = System.getenv(name) ?: return default ?: throw Exception("Environment variable $name not set")
    return parse(raw) ?: throw Exception("Environment variable $name ($raw) not the correct type")
}

fun loadEnvironmentString(
    name: String,
    default: String? = null,
): String = loadEnvironmentInternal(name, default) { it }

fun loadEnvironmentInt(
    name: String,
    default: Int? = null,
): Int = loadEnvironmentInternal(name, default) { it.toIntOrNull() }

fun loadEnvironmentFloat(
    name: String,
    default: Float? = null,
): Float = loadEnvironmentInternal(name, default) { it.toFloatOrNull() }

fun loadEnvironmentDouble(
    name: String,
    default: Double? = null,
): Double = loadEnvironmentInternal(name, default) { it.toDoubleOrNull() }

fun loadEnvironmentBoolean(
    name: String,
    default: Boolean? = null,
): Boolean =
    loadEnvironmentInternal(name, default) {
        when (it.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> null
        }
    }
