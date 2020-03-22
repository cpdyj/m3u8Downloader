import javafx.beans.property.SimpleStringProperty
import options.GlobalOptions
import tornadofx.stringBinding
import view.MainView


class MainViewController(val view: MainView) : BaseController() {
    val primaryStatus = SimpleStringProperty("Init.")
    val secondaryStatus = SimpleStringProperty("Idle.")
    val devToolsUrl=GlobalOptions::devToolsUrl.observableProperty()
    val proxy=GlobalOptions::proxyOptions.observableProperty().also { println(it.hashCode()) }
}

