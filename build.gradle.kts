//buildscript {
//    repositories {
//        jcenter()
//    }
//    dependencies {
//        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
//    }
//}

plugins {
    kotlin("jvm") version "1.3.70"
    id("org.openjfx.javafxplugin") version "0.0.8"
    kotlin("plugin.serialization") version "1.3.70"
    application
//    id("com.github.johnrengelman.shadow") version "5.2.0"
}
allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    group = "space.iseki"
    version = "1.0-SNAPSHOT"

    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        mavenCentral()
        jcenter()
    }

    dependencies {
        implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.3.70")
        testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")

        val jacksonVersion = "2.10.2"
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

        implementation(vertx("core"))
        implementation(vertx("lang-kotlin"))
        implementation(vertx("lang-kotlin-coroutines"))


        implementation(coroutine("core"))
    }
    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+NewInference")
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+NewInference")
        }
    }
}



dependencies {

    //    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

    implementation("no.tornado:tornadofx:1.7.20")
    implementation(coroutine("javafx"))

    implementation(project(":aria2client"))
    implementation(kotlin("stdlib-jdk8"))
}





javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClassName = "MainKt"
}
repositories {
    mavenCentral()
}


fun DependencyHandler.vertx(module: String, version: String = "4.0.0-milestone4") =
    "io.vertx:vertx-$module:$version"

fun DependencyHandler.coroutine(module: String, version: String = "1.3.3") =
    "org.jetbrains.kotlinx:kotlinx-coroutines-$module:$version"