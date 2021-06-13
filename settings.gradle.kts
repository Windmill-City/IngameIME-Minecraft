pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://files.minecraftforge.net/maven") {
            mavenContent { excludeGroupByRegex("""org\.eclipse.*""") }
        }
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
//include("forge")

rootProject.name = "IngameIME"
