plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("app.cash.sqldelight")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = false
        }
    }

    sourceSets {
        val ktorVersion = "2.3.5"
        val coroutinesVersion = "1.7.3"
        val sqlDelightVersion = "2.0.0"
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("co.touchlab:kermit:2.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
                implementation("app.cash.turbine:turbine:1.0.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation("app.cash.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("app.cash.sqldelight:sqlite-driver:$sqlDelightVersion")
            }
        }
    }
}

android {
    namespace = "com.remote.ghibli"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.remote.ghibli.database")
        }
    }
}