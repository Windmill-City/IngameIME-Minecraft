pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            // RetroFuturaGradle
            name = "GTNH Maven"
            url = uri("http://jenkins.usrv.eu:8081/nexus/content/groups/public/")
            isAllowInsecureProtocol = true
            mavenContent {
                includeGroup("com.gtnewhorizons.retrofuturagradle")
            }
        }
    }
}

rootProject.name = "IngameIME-Minecraft"
include(":IngameIME-Native", ":1.7.10")
