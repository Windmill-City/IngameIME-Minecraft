import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import java.util.*

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
val modType = "forge"

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

val shadowCompileOnly: Configuration by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
}

configurations.all {
    afterEvaluate {
        attributes {
            attribute(ModUtils.DEOBFUSCATOR_TRANSFORMED, true)
        }
    }
}

dependencies {
    shadowCompileOnly(project(":IngameIME-Native"))

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
    compileOnly("com.github.GTNewHorizons:NotEnoughItems:2.5.3-GTNH:dev")
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
        // Replaced by shadowJar
        enabled = false
    }
    processResources {
        filesMatching("mcmod.info") {
            expand("modVersion" to modVersion)
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    shadowJar {
        archiveBaseName = "$modName-$mcVersion-$modVersion"
        archiveClassifier = "dev"
        manifest {
            attributes(getManifestAttributes())
        }
        configurations = listOf(shadowCompileOnly)
    }
    reobfJar {
        inputJar.set(shadowJar.flatMap { it.archiveFile })
    }
}

class Version(version: String) : Comparable<Version> {
    @Suppress("PropertyName")
    val VERSION_REGEX: Regex = Regex("""(\d+)(?:.(\d+)(?:.(\d+))?)?""")
    var major: Int = 0
    var minor: Int = 0
    var revision: Int = 0

    init {
        VERSION_REGEX.matchEntire(version)?.apply {
            major = this.groupValues[1].toInt()
            minor = this.groupValues[2].toIntOrNull() ?: 0
            revision = this.groupValues[3].toIntOrNull() ?: 0
        } ?: throw IllegalArgumentException("Invalid version string:$version")
    }

    override fun compareTo(other: Version): Int {
        if (this == other) return 0
        if (this.major > other.major) return 1
        if (this.major == other.major) {
            if (this.minor > other.minor) return 1
            if (this.minor == other.minor && this.revision > other.revision) return 1
        }
        return -1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Version

        if (major != other.major) return false
        if (minor != other.minor) return false
        if (revision != other.revision) return false

        return true
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + revision
        return result
    }

    override fun toString(): String {
        return "$major.$minor.$revision"
    }
}

val curseApiKey: String
    get() {
        with(file("../local.properties")) {
            if (exists()) {
                val props = Properties()
                props.load(inputStream())
                return (props["curse_api_key"]!!) as String
            }
        }
        return System.getenv("CURSE_API_KEY")
    }

curseforge {
    apiKey = curseApiKey
    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "440032"
        releaseType = "release"
        addGameVersion("Forge")
        addGameVersion("Java 8")
        addGameVersion("1.7.10")
        relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
            requiredDependency("unimixins")
        })
    })
    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}

afterEvaluate {
    tasks {
        withType<com.matthewprenger.cursegradle.CurseUploadTask> {
            onlyIf {
                val curseforgeFile = file("../CurseForgeLatest.json")
                @Suppress("UNCHECKED_CAST") val versionInfo =
                    (groovy.json.JsonSlurper().parse(curseforgeFile) as Map<String, String>).toMutableMap()
                val uploadedVersion = Version(versionInfo["$modType-$mcVersion"] ?: "0.0.0")
                val currentVersion = Version(modVersion)
                println("Uploaded:$uploadedVersion")
                println("Current:$currentVersion")
                return@onlyIf uploadedVersion < currentVersion
            }
            doLast {
                val curseforgeFile = file("../CurseForgeLatest.json")
                @Suppress("UNCHECKED_CAST") val versionInfo =
                    (groovy.json.JsonSlurper().parse(curseforgeFile) as Map<String, String>).toMutableMap()
                //Uploaded, update json file
                versionInfo["$modType-$mcVersion"] = modVersion
                groovy.json.JsonOutput.toJson(versionInfo).let {
                    curseforgeFile
                        .outputStream()
                        .bufferedWriter().apply {
                            write(groovy.json.JsonOutput.prettyPrint(it))
                            flush()
                            close()
                        }
                }
            }
        }
    }
}