plugins {
    kotlin("multiplatform")
}

group = "net.codinux.util"
version = "2.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {
    jvm {
//        jvmToolchain(8)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
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

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    val coroutinesVersion: String by project
    val slf4jVersion: String by project
    val kotestVersion: String by project

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("net.codinux.log:kmp-log:1.0.0")

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

        val jsMain by getting
        val jsTest by getting

        val nativeMain by getting
        val nativeTest by getting
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


ext["artifactName"] = "stopwatch"
ext["sourceCodeRepositoryBaseUrl"] = "https://github.com/codinux-gmbh/Stopwatch"

ext["useNewSonatypeRepo"] = true
ext["packageGroup"] = "net.codinux"

ext["projectDescription"] = "Simple stopwatch to measure and format durations"

ext["developerId"] = "codinux"
ext["developerName"] = "codinux GmbH & Co. KG"
ext["developerMail"] = "git@codinux.net"

ext["licenseName"] = "The Apache License, Version 2.0"
ext["licenseUrl"] = "http://www.apache.org/licenses/LICENSE-2.0.txt"