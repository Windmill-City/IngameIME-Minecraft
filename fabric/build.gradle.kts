architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://maven.fabricmc.net")
    maven("https://maven.shedaniel.me/")
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
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.28.5+1.14")
    //REI
    modImplementation("me.shedaniel:RoughlyEnoughItems:3.3.0")
    //Cloth Api
    modImplementation("me.shedaniel.cloth:cloth-events:0.6.0")
    //Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:1.5.+")
    //Cloth Config
    modImplementation("me.shedaniel.cloth:config-2:1.8") {
        exclude("net.fabricmc.fabric-api")
    }
    //ModMenu
    modImplementation("io.github.prospector:modmenu:1.7.17+build.1")

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
        addGameVersion("1.14.1")
        addGameVersion("1.14.2")
        addGameVersion("1.14.3")
        addGameVersion("1.14.4")
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("fabric-language-kotlin")
            requiredDependency("fabric-api")
            requiredDependency("cloth-api")
            requiredDependency("cloth-config")
        })
    })

    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}