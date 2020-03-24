import config.initConfig
import io.vertx.core.Vertx
import tornadofx.App
import tornadofx.launch
import view.MainFrameView

class MainApp: App(primaryView = MainFrameView::class)

lateinit var vertx: Vertx
fun main(){
    vertx=Vertx.vertx()
    initConfig()
    launch<MainApp>()
    vertx.close()
}
