FROM eclipse-temurin:25 AS base

FROM base AS build

WORKDIR /work

COPY . .

RUN ./gradlew shadowJar

FROM base as runtime

EXPOSE 25565

WORKDIR /server

COPY --from=build /work/build/libs/Jelcraft-*-SNAPSHOT.jar /server/server.jar

CMD [
    "java",
    "-Dminestom.chunk-view-distance=16",
    "-Dminestom.entity-view-distance=16",
    "-jar",
    "/server/server.jar"
]
