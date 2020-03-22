import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.Vertx
import options.GlobalOptions
import options.ProxyOptions
import options.initConfig
import options.loadConfig
import tornadofx.App
import tornadofx.launch
import view.MainView


class MyApp : App(MainView::class)
//class MyApp : App(view.ProxySettingsView::class)

lateinit var vertx: Vertx

fun main(vararg args: String) {
    io.vertx.core.json.jackson.DatabindCodec.prettyMapper().registerKotlinModule()
    io.vertx.core.json.jackson.DatabindCodec.mapper().registerKotlinModule()

//    vertx = Vertx.vertx()

    initConfig()

    println(GlobalOptions.proxyOptions)
    val b=GlobalOptions.proxyOptions.fork()
    b.port=12345
    println(GlobalOptions.proxyOptions)
    GlobalOptions.proxyOptions.merge(b)
    println(GlobalOptions.proxyOptions)


//    ProxyOptions().also {
//        it.readValueFrom(GlobalOptions.proxyOptions)
//        it.fork()
//    }
    launch<MyApp>(*args)
//    vertx.close()
}
