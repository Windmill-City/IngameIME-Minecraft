architectury { common(false); injectInjectables = false }

repositories {
    maven("https://maven.fabricmc.net")
}

loom {
    accessWidenerPath.set(layout.projectDirectory.file("../fabric/src/main/resources/ingameime.accessWidener"))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:0.13.3")
    //Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.+")
    //Cloth Config
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:6.2.+") {
        exclude("net.fabricmc.fabric-api")
    }
}