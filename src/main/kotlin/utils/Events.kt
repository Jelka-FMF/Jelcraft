package si.progklub.jelcraft.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive

private val logger = KotlinLogging.logger {}

fun startEventListener(
    client: HttpClient,
    url: String,
    handler: suspend (ServerSentEvent) -> Unit,
) = CoroutineScope(Dispatchers.IO).async {
    while (isActive) {
        logger.info { "Connecting to $url" }

        client.sse(url) {
            incoming.collect { event ->
                handler(event)
            }
        }
    }
}
