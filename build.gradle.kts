plugins {
    val kotlinVersion = "2.3.0-Beta2"
    val shadowVersion = "8.3.0"

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
    val minestormVersion = "2025.10.31-1.21.10"
    val slf4jVersion = "2.0.17"
    val kotlinLoggingVersion = "7.0.3"
    val kotlinxSerializationVersion = "1.9.0"
    val kotlinxCoroutinesVersion = "1.7.3"
    val ktorVersion = "3.3.2"

    // Minestom
    implementation("net.minestom:minestom:$minestormVersion")

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
