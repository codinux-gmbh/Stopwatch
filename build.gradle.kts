plugins {
    kotlin("multiplatform")
}

group = "net.codinux.util"
version = "2.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {
    // Enable the default target hierarchy:
    targetHierarchy.default()

    jvm {
//        jvmToolchain(8)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        moduleName = "stopwatch"
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {
//                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
//                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {

        }
    }


    linuxX64()
    mingwX64()


    ios {
        binaries {
            framework {
                baseName = "stopwatch"
            }
        }
    }
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchos()
    watchosSimulatorArm64()
    tvos()
    tvosSimulatorArm64()


    sourceSets {
        val coroutinesVersion: String by project
        val slf4jVersion: String by project
        val kotestVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation("net.codinux.log:kmp-log:1.5.1")

                implementation("org.jetbrains.kotlinx:atomicfu:0.20.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                compileOnly("org.slf4j:slf4j-api:$slf4jVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine")

                implementation("org.mockito:mockito-junit-jupiter:3.12.2")
                implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")

                implementation("org.assertj:assertj-core:3.20.2")

                implementation("org.slf4j:slf4j-simple:$slf4jVersion")
            }
        }
    }
}


tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}


ext["customArtifactId"] = "stopwatch"
ext["sourceCodeRepositoryBaseUrl"] = "github.com/codinux-gmbh/Stopwatch"

ext["projectDescription"] = "Simple stopwatch to measure and format durations"

apply(from = File("./gradle/scripts/publish-codinux.gradle.kts"))