import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm")
    id("forgified-fabric-loom")
    id("com.matthewprenger.cursegradle")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://files.minecraftforge.net/maven")
    maven("https://dl.bintray.com/shedaniel/shedaniel-mods")
    maven("https://jitpack.io")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

loom {
    silentMojangMappingsLicense()
    mixinConfig = "IngameIME-forge.mixins.json"
}

//General
val minecraft_version: String by rootProject
val forge_version: String by project
//Mod Props
val archives_base_name: String by project
val mod_version: String by rootProject
val maven_group: String by rootProject
//Kotlin
val forge_kotlin_version: String by project

version = mod_version
group = maven_group
base {
    archivesBaseName = "$archives_base_name-$name-$minecraft_version"
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(minecraft.officialMojangMappings())
    forge("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    
    //Forge Kotlin
    modImplementation("thedarkcolour:kotlinforforge:${forge_kotlin_version}")
}

sourceSets {
    main {
        java {
            srcDirs(
                "src/main/java",
                "src/main/kotlin",
                "../common/src/main/kotlin"
            )
        }
        resources {
            srcDirs(
                "src/main/resources",
                "../common/src/main/resources"
            )
        }
    }
}

tasks {
    withType<Jar> {
        manifest {
            attributes(
                "Specification-Title" to "IngameIME",
                "Specification-Vendor" to "Windmill_City",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to "${version}",
                "Implementation-Vendor" to "Windmill_City",
                "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            )
        }
    }
}

curseforge {
    apiKey = rootProject.ext["apiKey"]
    project(closureOf<CurseProject> {
        id = "440032"
        releaseType = "release"
        changelog = "first release"
        addGameVersion("1.16-Snapshot")
        addGameVersion("1.16")
        addGameVersion("1.16.1")
        addGameVersion("1.16.2")
        addGameVersion("1.16.3")
        addGameVersion("1.16.4")
        addGameVersion("1.16.5")
        relations(closureOf<CurseRelation> {
            requiredDependency("kotlin-for-forge")
        })
    })
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}