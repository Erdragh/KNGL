plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "dev.erdragh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val glfw by creating
                val glew by creating
            }
        }
        binaries {
            executable {
                debuggable = true
                entryPoint = "dev.erdragh.example.main"
            }
        }
    }

    sourceSets {
        nativeMain.dependencies {
            implementation(libs.okio)
        }
    }
}