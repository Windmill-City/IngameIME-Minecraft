architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://maven.fabricmc.net")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
}

loom {
    accessWidener = file("src/main/resources/ingameime.accessWidener")
}

minecraft {
    mixinConfig("IngameIME-fabric.mixins.json")
}

dependencies {
    //Fabric
    modImplementation("net.fabricmc:fabric-loader:0.11.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.32.5+1.16")
    //REI
    modImplementation("me.shedaniel:RoughlyEnoughItems:5.11.+")
    //Cloth Api
    modImplementation("me.shedaniel.cloth.api:cloth-client-events-v0:1.5.+")
    //Stain
    modImplementation("io.github.ladysnake:Satin:1.5.1")
    //Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:1.5.+")
    //Cloth Config
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:4.11.+") {
        exclude("net.fabricmc.fabric-api")
    }
    //ModMenu
    modImplementation("com.terraformersmc:modmenu:1.16.9")

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
        addGameVersion("Java 8")
        addGameVersion("1.16")
        addGameVersion("1.16.1")
        addGameVersion("1.16.2")
        addGameVersion("1.16.3")
        addGameVersion("1.16.4")
        addGameVersion("1.16.5")
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