import java.time.Instant
import java.time.format.DateTimeFormatter

architectury {
    platformSetupLoomIde()
    forge()
}

repositories {
    maven("https://files.minecraftforge.net/maven")
    maven("https://maven.shedaniel.me/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

minecraft {
    mixinConfig("IngameIME-forge.mixins.json")
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.architectury.minecraft}-36.1.4")

    //Forge Kotlin
    modImplementation("thedarkcolour:kotlinforforge:1.11.1")
    //Cloth Config
    modImplementation("me.shedaniel.cloth:cloth-config-forge:4.11.+")

    implementation(project(path = ":common")) { isTransitive = false }
    add("developmentForge", project(path = ":common")) { isTransitive = false }
    shadowC(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }
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
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
        addGameVersion("Forge")
        addGameVersion("Java 8")
        addGameVersion("1.16")
        addGameVersion("1.16.1")
        addGameVersion("1.16.2")
        addGameVersion("1.16.3")
        addGameVersion("1.16.4")
        addGameVersion("1.16.5")
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("kotlin-for-forge")
            requiredDependency("cloth-config-forge")
        })
    })
    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}