plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.kotlin.formver") version "0.1.0-SNAPSHOT"
}

group = "snakt-program-proofs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin.formver:formver.annotations:0.1.0-SNAPSHOT")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(24)
    sourceSets {
        main {
            kotlin {
                srcDirs(
                    "introduction",
                    "part0_learning_the_ropes",
                    "part1_functional_programs",
                    "part2_imperative_programs",
                )
            }
        }
    }
}

formver {
}