plugins {
    kotlin("jvm") version "1.9.0"
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.2.1"
}

kotlin {
    jvmToolchain(8)
}

group = "ch.rethab"
version = "0.0.1"

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://github.com/rethab/semantic-gradle"
    vcsUrl = "https://github.com/rethab/semantic-gradle"
    plugins {
        create("semanticGradle") {
            id = "ch.rethab.semantic-gradle"
            implementationClass = "ch.rethab.semanticgradle.SemanticGradlePlugin"
            displayName = "Semantic Gradle"
            description = "Semantic Releases based on Conventional Commits for Gradle"
            tags = listOf("publish", "release", "semantic-version", "conventional-commits")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
