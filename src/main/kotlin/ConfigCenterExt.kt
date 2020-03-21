import io.vertx.core.Vertx

fun ConfigCenter.store() {
    runCatching {
        val cpt = configPathTemp()
        cpt.mkdirIfNotExists().writeText(getValues())
        cpt.copyTo(configPath(), true)
    }.onFailure { println("save config fail: ${it.localizedMessage}") }
}

fun ConfigCenter.load() {
    runCatching {
        configPath().readText().let(::setValues)
    }.onFailure { println("load config fail: ${it.localizedMessage}") }
}

fun ConfigCenter.init() {
    configPath()
    load()
    registerShutdownHook { store() }
}

fun ConfigCenter.setupAutoSaver(vertx: Vertx) {
    var lastVersion = version
    vertx.setPeriodic(10000) {
        if (lastVersion != version) {
            lastVersion = version
            store()
            println("Config stored. version: $lastVersion")
        }
    }
}