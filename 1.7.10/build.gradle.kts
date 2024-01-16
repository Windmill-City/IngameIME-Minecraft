import com.gtnewhorizons.retrofuturagradle.minecraft.RunMinecraftTask
import com.gtnewhorizons.retrofuturagradle.shadow.de.undercouch.gradle.tasks.download.Download
import java.util.*
import com.gtnewhorizons.retrofuturagradle.util.Distribution as DistributionGTNH

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

val mixinSpec by extra { "io.github.legacymoddingmc:unimixins:0.1.13:dev" }
dependencies {
    shadowCompileOnly(project(":IngameIME-Native"))

    annotationProcessor("org.ow2.asm:asm-debug-all:5.0.3")
    annotationProcessor("com.google.guava:guava:24.1.1-jre")
    annotationProcessor("com.google.code.gson:gson:2.8.6")
    annotationProcessor(mixinSpec)
    implementation(
        modUtils.enableMixins(
            mixinSpec,
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
    // Enable assertions in the current mod
    extraRunJvmArguments.add("-ea:${modGroup}")
    // Debug Mixin
    extraRunJvmArguments.addAll(
        listOf(
            "-Dmixin.debug.countInjections=true",
            "-Dmixin.debug.verbose=true",
            "-Dmixin.debug.export=true"
        )
    )
}

fun getManifestAttributes(): MutableMap<String, String> {
    val manifestAttributes = mutableMapOf<String, String>()
    manifestAttributes["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
    manifestAttributes["MixinConfigs"] = "mixins.ingameime.json"
    manifestAttributes["ForceLoadAsMod"] = "true"
    manifestAttributes["FMLCorePluginContainsFMLMod"] = "true"
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
        minimize()
    }
    reobfJar {
        inputJar.set(shadowJar.flatMap { it.archiveFile })
    }
}

/**
 * Upload Tasks
 */
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
        return System.getenv("CURSE_API_KEY") ?: ""
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

curseforge {
    apiKey = curseApiKey
    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "440032"
        releaseType = "release"
        mainArtifact(tasks["reobfJar"])
        addArtifact(tasks["shadowJar"])
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

/**
 * Java 17 Tasks
 */
val java17Dependencies by extra {
    configurations.create("java17Dependencies") {
        extendsFrom(configurations.getByName("runtimeClasspath")) // Ensure consistent transitive dependency resolution
        isCanBeConsumed = false
    }
}
val java17PatchDependencies by extra {
    configurations.create("java17PatchDependencies") {
        isCanBeConsumed = false
    }
}
val java17Toolchain by extra {
    Action<JavaToolchainSpec> { ->
        this.languageVersion.set(JavaLanguageVersion.of(17))
        this.vendor.set(JvmVendorSpec.matching("jetbrains"))
    }
}

dependencies {
    val lwjgl3ifyVersion = "1.5.7"
    java17Dependencies("com.github.GTNewHorizons:lwjgl3ify:${lwjgl3ifyVersion}")
    java17Dependencies("com.github.GTNewHorizons:Hodgepodge:2.3.35")
    java17PatchDependencies("com.github.GTNewHorizons:lwjgl3ify:${lwjgl3ifyVersion}:forgePatches") {
        isTransitive = false
    }
}

val setupHotswapAgentTask = tasks.register("setupHotswapAgent") {
    group = "GTNH Buildscript"
    description = "Installs a recent version of HotSwapAgent into the Java 17 JetBrains runtime directory"
    val hsaUrl =
        "https://github.com/HotswapProjects/HotswapAgent/releases/download/1.4.2-SNAPSHOT/hotswap-agent-1.4.2-SNAPSHOT.jar"
    val targetFolderProvider =
        javaToolchains.launcherFor(java17Toolchain)
            .map { it.metadata.installationPath.dir("lib/hotswap") }
    val targetFilename = "hotswap-agent.jar"
    onlyIf {
        !targetFolderProvider.get().file(targetFilename).asFile.exists()
    }
    doLast {
        val targetFolder = targetFolderProvider.get()
        targetFolder.asFile.mkdirs()
        task<Download>("download") {
            src(hsaUrl)
            dest(targetFolder.file(targetFilename).asFile)
            overwrite(false)
            tempAndMove(true)
        }
    }
}

abstract class RunHotSwappableMinecraftTask @Inject constructor(
    side: DistributionGTNH,
    superTask: String,
    gradle: Gradle
) : RunMinecraftTask(side, gradle) {
    private val java17JvmArgs = listOf(
        // Java 9+ support
        "--illegal-access=warn",
        "-Djava.security.manager=allow",
        "-Dfile.encoding=UTF-8",
        "--add-opens", "java.base/jdk.internal.loader=ALL-UNNAMED",
        "--add-opens", "java.base/java.net=ALL-UNNAMED",
        "--add-opens", "java.base/java.nio=ALL-UNNAMED",
        "--add-opens", "java.base/java.io=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.text=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/jdk.internal.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-opens", "jdk.naming.dns/com.sun.jndi.dns=ALL-UNNAMED,java.naming",
        "--add-opens", "java.desktop/sun.awt.image=ALL-UNNAMED",
        "--add-modules", "jdk.dynalink",
        "--add-opens", "jdk.dynalink/jdk.dynalink.beans=ALL-UNNAMED",
        "--add-modules", "java.sql.rowset",
        "--add-opens", "java.sql.rowset/javax.sql.rowset.serial=ALL-UNNAMED"
    )
    private val hotswapJvmArgs = listOf(
        // DCEVM advanced hot reload
        "-XX:+AllowEnhancedClassRedefinition",
        "-XX:HotswapAgent=fatjar"
    )

    // IntelliJ doesn't seem to allow commandline arguments, so we also support an env variable
    private var enableHotswap = System.getenv("HOTSWAP").toBoolean()

    private val java17Toolchain: Action<JavaToolchainSpec> by project.extra
    private val java17Dependencies: Configuration by project.extra
    private val java17PatchDependencies: Configuration by project.extra
    private val mixinSpec: String by project.extra

    @Input
    fun getEnableHotswap(): Boolean {
        return enableHotswap
    }

    @Option(option = "hotswap", description = "Enables HotSwapAgent for enhanced class reloading under a debugger")
    fun setEnableHotswap(enable: Boolean) {
        enableHotswap = enable
    }

    init {
        lwjglVersion = 3
        javaLauncher = project.javaToolchains.launcherFor(java17Toolchain)
        // JVM Args
        extraJvmArgs.addAll(java17JvmArgs)
        if (enableHotswap)
            extraJvmArgs.addAll(hotswapJvmArgs)

        // ClassPaths
        this.classpath(java17PatchDependencies)
        if (side == DistributionGTNH.CLIENT)
            this.classpath(project.minecraftTasks.lwjgl3Configuration)
        this.classpath(project.provider {
            project.tasks.named<RunMinecraftTask>(superTask).get().classpath
        })
        this.classpath.filter {
            !it.path.contains("2.9.4-nightly-20150209")
        }
        this.classpath(java17Dependencies)
    }

    override fun setup(project: Project) {
        super.setup(project)
        if (enableHotswap) {
            val mixinCfg = project.configurations.detachedConfiguration(project.dependencies.create(mixinSpec))
            mixinCfg.isTransitive = false
            mixinCfg.isCanBeConsumed = false
            extraJvmArgs.addAll("-javaagent:${mixinCfg.singleFile.absolutePath}")
        }
    }
}

val runClient17 = tasks.register(
    "runClient17",
    RunHotSwappableMinecraftTask::class.java,
    DistributionGTNH.CLIENT,
    "runClient",
    gradle
)
runClient17.configure {
    setup(project)
    group = "Modded Minecraft"
    description = "Runs the modded client using Java 17, lwjgl3ify and Hodgepodge"
    dependsOn(
        setupHotswapAgentTask,
        mcpTasks.launcherSources.classesTaskName,
        minecraftTasks.taskDownloadVanillaAssets,
        mcpTasks.taskPackagePatchedMc,
        tasks.shadowJar
    )
    mainClass = "GradleStart"
    username = minecraft.username
    userUUID = minecraft.userUUID
}

val runServer17 = tasks.register(
    "runServer17",
    RunHotSwappableMinecraftTask::class.java,
    DistributionGTNH.DEDICATED_SERVER,
    "runServer",
    gradle
)
runServer17.configure {
    setup(project)
    group = "Modded Minecraft"
    description = "Runs the modded server using Java 17, lwjgl3ify and Hodgepodge"
    dependsOn(
        setupHotswapAgentTask,
        mcpTasks.launcherSources.classesTaskName,
        minecraftTasks.taskDownloadVanillaAssets,
        mcpTasks.taskPackagePatchedMc,
        tasks.shadowJar
    )
    mainClass = "GradleStartServer"
    extraArgs.add("nogui")
}
