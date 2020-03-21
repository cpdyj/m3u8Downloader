pluginManagement {
    repositories {

        mavenCentral()

        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "m3u8Downloader"
include("aria2client")
