package si.progklub.jelcraft.components

import kotlinx.serialization.Serializable

@Serializable
data class Pattern(
    val identifier: String,
    val name: String,
    val description: String?,
    val source: String?,
    val docker: String,
    val duration: Int,
    val author: String?,
    val school: String?,
    val enabled: Boolean,
    val visible: Boolean,
)

/**
 * Represents the current state.
 *
 * @property currentPatternIdentifier The current pattern identifier.
 * @property currentPatternStarted An ISO-8601 timestamp representing when the current pattern started.
 * @property currentPatternRemaining The number of seconds remaining for the current pattern.
 * @property runnerLastActive An ISO-8601 timestamp representing when the runner was last active.
 * @property runnerIsActive Whether the runner is currently active.
 */
@Serializable
data class State(
    val currentPatternIdentifier: String?,
    val currentPatternStarted: String?,
    val currentPatternRemaining: Double,
    val runnerLastActive: String?,
    val runnerIsActive: Boolean,
)
