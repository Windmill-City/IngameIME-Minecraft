pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.fabricmc.net")
        maven("https://dl.bintray.com/shedaniel/cloth")
        maven("https://files.minecraftforge.net/maven")
    }
}
rootProject.name = "IngameIME"
include("common", "forge", "fabric")