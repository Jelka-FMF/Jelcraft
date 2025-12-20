# Jelcraft

Minecraft server for displaying Jelka FMF simulation.

## About

Jelcraft is a custom Minecraft server, built using [Minestom](https://minestom.net/), designed
to visualize [Jelka FMF](https://jelka.fmf.uni-lj.si/) in Minecraft. It displays the current
state of Jelka FMF in real-time, and sends chat messages about currently running patterns.

The official Jelcraft server is hosted at `jelka.fmf.uni-lj.si:25570`. It supports any recent
Minecraft Java Edition and Bedrock Edition clients.

## Hosting

You can install Jelcraft by cloning this repository and building it with Gradle, or by running
the provided Docker image from [the container registry](https://github.com/Jelka-FMF/Jelcraft/pkgs/container/jelcraft).

The server can be configured using environment variables, prefixed with `JELCRAFT_`.
All configuration options are documented [in the source code](src/main/kotlin/Config.kt).

Required environment variables:

- `JELCRAFT_POSITIONS_FILE`: A path to a CSV file containing light positions.
- `JELCRAFT_SSE_STATUS_URL`: An URL to the status event stream endpoint.
- `JELCRAFT_SSE_DRIVER_URL`: An URL to the driver event stream endpoint.
