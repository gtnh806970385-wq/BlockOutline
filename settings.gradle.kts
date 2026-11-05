pluginManagement {
    repositories {
        maven {
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.gtnewhorizons.retrofuturagradle") {
                useModule(
                    "com.gtnewhorizons.retrofuturagradle:com.gtnewhorizons.retrofuturagradle.gradle.plugin:2.0.2"
                )
            }
        }
    }
}

rootProject.name = "BlockOutline"
