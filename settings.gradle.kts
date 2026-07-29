pluginManagement {
    repositories {
        flatDir {
            dirs("C:/Users/Admin/.gradle/caches/modules-2/files-2.1/com.gtnewhorizons.retrofuturagradle/com.gtnewhorizons.retrofuturagradle.gradle.plugin/2.0.2/c7e6f1abda8bba025971083c9e50309b1021b772")
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.gtnewhorizons.retrofuturagradle") {
                useModule("com.gtnewhorizons.retrofuturagradle:com.gtnewhorizons.retrofuturagradle.gradle.plugin:2.0.2")
            }
        }
    }
}

rootProject.name = "BlockOutline"
