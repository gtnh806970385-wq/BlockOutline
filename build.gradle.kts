plugins {
    id("java-library")
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

tasks.named<JavaCompile>("compileJava") {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

val modId = "blockoutline"
val modName = "BlockOutline"
val modGroup = "com.blockoutline"
val modVersion = "1.0.0"

dependencies {
    implementation(files("D:/GTNH/GT New Horizons 2.9.0-beta-1/.minecraft/mods/+unimixins-all-1.7.10-0.3.1.jar"))
}

tasks {
    build {
        doFirst {
            println("modVersion: $$modVersion")
        }
    }
}

minecraft {
    injectedTags.put("MODID", modId)
    injectedTags.put("MODNAME", modName)
    injectedTags.put("MODGROUP", modGroup)
    injectedTags.put("VERSION", modVersion)
    lwjgl3Version = "3.3.2"
    username = "Developer"
}
