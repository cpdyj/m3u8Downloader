package config

import configPath
import configPathBackup
import makeTreeIfNotExists
import registerShutdownHook
import java.io.File

fun initConfig() {
    runCatching { loadConfig(configPath()) }.onFailure {
        it.printStackTrace()
        runCatching { loadConfig(configPathBackup()) }.onFailure { it.printStackTrace() }
    }
    registerShutdownHook {
        runCatching { storeConfig(configPath().makeTreeIfNotExists()) }.onFailure { it.printStackTrace() }
        runCatching { storeConfig(configPathBackup().makeTreeIfNotExists()) }.onFailure { it.printStackTrace() }
    }
}

fun loadConfig(file: File) {
    ConfigManager.setContent(file.readText())
}

fun storeConfig(file: File) {
    ConfigManager.getContext().let(file::writeText)
}