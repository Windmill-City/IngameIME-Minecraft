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

repositories {
    maven {
        name = "Overmind forge repo mirror"
        url = uri("https://gregtech.overminddl1.com/")
    }
    maven {
        name = "GTNH Maven"
        url = uri("http://jenkins.usrv.eu:8081/nexus/content/groups/public/")
        isAllowInsecureProtocol = true
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        content {
            includeGroup("org.lwjgl")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "CurseMaven"
                url = uri("https://cursemaven.com")
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    implementation(project(":IngameIME-Native"))

    annotationProcessor("org.ow2.asm:asm-debug-all:5.0.3")
    annotationProcessor("com.google.guava:guava:24.1.1-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.6")
    annotationProcessor("io.github.legacymoddingmc:unimixins:0.1.13:dev")
    implementation(
        modUtils.enableMixins(
            "io.github.legacymoddingmc:unimixins:0.1.13:dev",
            "mixins.ingameime.refmap.json"
        )
    )
}

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

    //LWJGL
    lwjgl3Version = "3.3.2"
}

fun getManifestAttributes(): MutableMap<String, String> {
    val manifestAttributes = mutableMapOf<String, String>()
    manifestAttributes["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
    manifestAttributes["MixinConfigs"] = "mixins.ingameime.json"
    manifestAttributes["ForceLoadAsMod"] = "true"
    return manifestAttributes
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
        manifest {
            attributes(getManifestAttributes())
        }
    }
    processResources {
        filesMatching("mcmod.info") {
            expand("modVersion" to modVersion)
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}