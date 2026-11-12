plugins {
    val kotlinVersion = "2.4.21"
    val shadowVersion = "9.6.1"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.gradleup.shadow") version shadowVersion
}

group = "si.progklub.jelcraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val minestomVersion = "2026.10.07-26.2"
    val slf4jVersion = "2.0.20"
    val kotlinLoggingVersion = "8.0.7"
    val kotlinxSerializationVersion = "1.13.0"
    val kotlinxCoroutinesVersion = "1.11.0"
    val ktorVersion = "3.6.1"

    // Minestom
    implementation("net.minestom:minestom:$minestomVersion")

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    // Clients
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-encoding:$ktorVersion")
}

kotlin {
    jvmToolchain(25)
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "${project.group}.MainKt"
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
            attributes["Implementation-Vendor"] = "Programerski klub FMF"
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }
}
