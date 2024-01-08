buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            // GTNH RetroFuturaGradle and ASM Fork
            name = "GTNH Maven"
            url = uri("http://jenkins.usrv.eu:8081/nexus/content/groups/public/")
            isAllowInsecureProtocol = true
        }
    }
}
plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.palantir.git-version") version "3.0.0"
    // Publish
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    // ForgeGradle
    id("com.gtnewhorizons.retrofuturagradle") version "1.3.26"
}

val modId = "ingameime"
val modName = "IngameIME"
val modGroup = "city.windmill.ingameime"

val mcVersion = minecraft.mcVersion.get()
val gitVersion: groovy.lang.Closure<String> by extra
val modVersion = gitVersion()

tasks {
    build {
        doFirst {
            println("modVersion: $modVersion")
        }
    }
    injectTags {
        outputClassName = "$modGroup.Tags"
    }
}

minecraft {
    // Tags
    injectedTags.put("MODID", modId)
    injectedTags.put("MODNAME", modName)
    injectedTags.put("MODGROUP", modGroup)
    injectedTags.put("VERSION", modVersion)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
    jar {
        archiveBaseName = "$modName-$mcVersion-$modVersion"
    }
}