package options

import Aria2ClientOptions
import configPath
import configPathBackup
import decodeJson
import encode
import makeTreeIfNotExists
import mapTo
import registerShutdownHook
import toJsonTree
import java.io.File

object GlobalOptions : OptionBase<GlobalOptions>() {
    var aria2ClientOptions by id { Aria2ClientOptions() }
    var proxyOptions by ids { ProxyOptions() }
    var devToolsUrl by id { "http://127.0.0.1:9222/json" }
}

fun loadConfig() {
    fun loadConfig(vararg files: File) {
        files.forEach { file ->
            runCatching {
                file.readText().decodeJson().mapTo(GlobalOptions, true)
            }.onSuccess {
                println("load config succeed")
                return
            }.onFailure {
                println("load config fail: ${file.absolutePath} -> $it")
                it.printStackTrace()
            }
        }
    }
    loadConfig(configPath(), configPathBackup())
}

fun storeConfig() {
    runCatching {
        val p1 = configPath()
        val p2 = configPathBackup()
        p1.makeTreeIfNotExists(thisIsFolder = false)
        p1.copyTo(p2, overwrite = true)
        p1.writeText(GlobalOptions.toJsonTree().encode())
        p2.delete()
    }.onFailure {
        println("save config fail: ${it.localizedMessage}")
        it.printStackTrace()
    }.onSuccess { println("store config succeed.") }
}

fun initConfig() {
    runCatching { loadConfig() }
    registerShutdownHook { storeConfig() }
}
