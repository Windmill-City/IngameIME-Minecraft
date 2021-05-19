import java.time.Instant
import java.time.format.DateTimeFormatter

architectury {
    platformSetupLoomIde()
    forge()
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://files.minecraftforge.net/maven")
    maven("https://maven.shedaniel.me/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
}

minecraft {
    mixinConfig("IngameIME-forge.mixins.json")
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.architectury.minecraft}-31.2.47")

    //Forge Kotlin
    modImplementation("thedarkcolour:kotlinforforge:1.11.1")
    //ClothConfig2-3.0
    modImplementation("curse.maven:ClothConfig2-348521:2938583")

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
        addGameVersion("1.15")
        addGameVersion("1.15.1")
        addGameVersion("1.15.2")
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("kotlin-for-forge")
            requiredDependency("cloth-config-forge")
            optionalDependency("mixinbootstrap")
        })
    })
    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}