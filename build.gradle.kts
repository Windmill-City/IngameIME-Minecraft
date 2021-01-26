import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    java
    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    kotlin("jvm") version "1.4.21" apply false
    id("forgified-fabric-loom") version "0.6.49" apply false
}

val curse_api_key: String
    get() {
        with(file("./local.properties")){
            if (exists()){
                val props = Properties()
                props.load(inputStream())
                return (props["curse_api_key"] ?: "") as String
            }
        }
        return ""
    }

rootProject.ext["apiKey"] = curse_api_key
println("Curse Api Key:$curse_api_key")

subprojects {
    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}