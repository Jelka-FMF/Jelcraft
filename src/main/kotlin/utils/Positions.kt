package si.progklub.jelcraft.utils

import net.minestom.server.coordinate.Pos
import java.io.File
import kotlin.math.sqrt

/**
 * Loads the positions of the lights from a file.
 *
 * @param filename The name of the file to load from.
 *
 * @return The positions of the lights.
 */
fun loadPositions(filename: String): Map<Int, Pos> {
    return File(filename)
        .bufferedReader()
        .lineSequence()
        .mapNotNull { line ->
            val line = line.trim()
            if (line.isEmpty()) return@mapNotNull null

            val parts = line.split(',')

            val id = parts[0].toInt()
            val x = -parts[1].toDouble()
            val y = parts[3].toDouble()
            val z = parts[2].toDouble()

            id to Pos(x, y, z)
        }.toMap()
}

/**
 * Normalizes the positions of the lights.
 *
 * The function normalizes the positions of the lights by scaling them
 * into a ball with a center at the origin `(0, 0, 0)` and radius
 * specified the scale parameter.
 *
 * The positions are normalized by dividing each coordinate by the maximum
 * radius and then multiplying by the scale.
 *
 * @param positions The positions to normalize.
 * @param scale The radius of the ball.
 *
 * @return The normalized positions.
 */
fun normalizePositions(
    positions: Map<Int, Pos>,
    scale: Double,
): Map<Int, Pos> {
    // Calculate the max distance from the origin (0, 0, 0) to any light
    val maxRadius =
        positions.values.maxOf { position ->
            val x = position.x()
            val y = position.y()
            val z = position.z()
            sqrt(x * x + y * y + z * z)
        }

    if (maxRadius == 0.0) return positions

    // Calculate the scaling factor
    val factor = scale / maxRadius

    // Scale the positions by the factor
    return positions.mapValues { (_, position) ->
        Pos(
            position.x() * factor,
            position.y() * factor,
            position.z() * factor,
        )
    }
}
