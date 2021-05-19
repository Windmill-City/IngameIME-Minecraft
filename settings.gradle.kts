pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://files.minecraftforge.net/maven")
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
include("forge")

rootProject.name = "IngameIME"
