import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options

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
    accessWidener = file("src/main/resources/ingameime.accessWidener")
}

//General
val minecraft_version: String by rootProject
//Mod Props
val archives_base_name: String by project
val mod_version: String by rootProject
val maven_group: String by rootProject
//Fabric
val fabric_api_version: String by project
val fabric_loader_version: String by project
val cloth_client_events_v0_version: String by project
val satin_version: String by project
val roughlyenoughitems: String by project
val fabric_kotlin_version: String by project

version = mod_version
group = maven_group
base {
    archivesBaseName = "$archives_base_name-$name-$minecraft_version"
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(minecraft.officialMojangMappings())
    
    //Fabric
    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    //REI
    modImplementation("me.shedaniel:RoughlyEnoughItems:${roughlyenoughitems}")
    //Cloth Api
    modImplementation("me.shedaniel.cloth.api:cloth-client-events-v0:${cloth_client_events_v0_version}")
    //Stain
    modImplementation("io.github.ladysnake:Satin:${satin_version}")
    //Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")
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
    processResources {
        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
        inputs.property("version", version)
    }
}

val changeLog: String by rootProject

curseforge {
    apiKey = rootProject.ext["apiKey"]
    project(closureOf<CurseProject> {
        id = "440032"
        releaseType = "release"
        changelog = changeLog
        mainArtifact(tasks["remapJar"])
        addArtifact(tasks["jar"])
        addGameVersion("Fabric")
        addGameVersion("Java 8")
        addGameVersion("1.16")
        addGameVersion("1.16.1")
        addGameVersion("1.16.2")
        addGameVersion("1.16.3")
        addGameVersion("1.16.4")
        addGameVersion("1.16.5")
        relations(closureOf<CurseRelation> {
            requiredDependency("fabric-language-kotlin")
            requiredDependency("fabric-api")
            requiredDependency("satin-api")
            requiredDependency("cloth-api")
        })
    })
    
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}