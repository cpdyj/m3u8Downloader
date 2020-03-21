import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import view.MainView
import kotlin.reflect.KProperty


class MainViewController(val view: MainView) {


    val primaryStatus = SimpleStringProperty("Init.")
    val secondaryStatus = SimpleStringProperty("Idle.")

    //    val webdevUrl
    fun onDestory() {
        val aa=MainViewController(MainView())
        val a = aa::primaryStatus
    }
}

