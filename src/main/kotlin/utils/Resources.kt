package si.progklub.jelcraft.utils

fun loadResourceString(path: String): String? = object {}.javaClass.getResource(path)?.readText(Charsets.UTF_8)

fun loadResourceBytes(path: String): ByteArray? = object {}.javaClass.getResource(path)?.readBytes()
