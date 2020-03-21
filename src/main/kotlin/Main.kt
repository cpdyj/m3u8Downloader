import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.Vertx
import tornadofx.App
import tornadofx.launch
import view.MainView


class MyApp : App(MainView::class)
//class MyApp : App(view.ProxySettingsView::class)

lateinit var vertx: Vertx

fun main(vararg args: String) {
    io.vertx.core.json.jackson.DatabindCodec.prettyMapper().registerKotlinModule()
    io.vertx.core.json.jackson.DatabindCodec.mapper().registerKotlinModule()
//    ConfigCenter.init()

//    vertx = Vertx.vertx()
//    ConfigCenter.setupAutoSaver(vertx)

//    Thread.sleep(10000)
    launch<MyApp>(*args)
//    vertx.close()
}
