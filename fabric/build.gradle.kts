architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://maven.fabricmc.net")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://ladysnake.jfrog.io/artifactory/mods") {
        mavenContent {
            includeGroup("io.github.ladysnake")
            includeGroupByRegex("io\\.github\\.onyxstudios.*")
        }
    }
}

loom {
    accessWidenerPath.set(layout.projectDirectory.file("../fabric/src/main/resources/ingameime.accessWidener"))
}


dependencies {
    //Fabric
    modImplementation("net.fabricmc:fabric-loader:0.13.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.48.0+1.18.2")
    //REI
    modImplementation("me.shedaniel:RoughlyEnoughItems-fabric:7.3.+")
    //Cloth Api
    modImplementation("me.shedaniel.cloth.api:cloth-client-events-v0:2.0.+")
    //Stain
    modCompileOnly("io.github.ladysnake:satin:1.6.+")
    //Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.+")
    //Cloth Config
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:6.2.+") {
        exclude("net.fabricmc.fabric-api")
    }
    //ModMenu
    modImplementation("com.terraformersmc:modmenu:3.1.+")

    implementation(project(path = ":common")) { isTransitive = false }
    add("developmentFabric", project(path = ":common")) { isTransitive = false }
    shadowC(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        inputs.property("version", version)
    }
    jar {
        archiveClassifier.set("dev")
    }
}

val changeLog: String by rootProject

curseforge {
    apiKey = rootProject.ext["apiKey"]
    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "440032"
        releaseType = "release"
        changelog = changeLog
        mainArtifact(tasks["remapJar"])
        addArtifact(tasks["shadowJar"])
        addGameVersion("Fabric")
        addGameVersion("Java 16")
        addGameVersion("1.17")
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("fabric-language-kotlin")
            requiredDependency("fabric-api")
            requiredDependency("cloth-api")
            requiredDependency("cloth-config")
            optionalDependency("satin-api")
        })
    })

    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}