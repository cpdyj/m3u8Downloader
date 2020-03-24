package controller

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.onChange
import tornadofx.stringBinding

class MainFrameController : BaseController() {
//    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.JavaFx).also { addDestoryHook { it.cancel() } }

    val selectedTab = SimpleObjectProperty<Tab>()
        .also { it.onChange { GlobalStatus.devToolsWebsocketEndpoint.value=it?.webSocketDebuggerUrl?:"" } }
    val primaryStatus = GlobalStatus.primaryStatus
//    val secondaryStatus = SimpleStringProperty("secondaryStatus")

//    val devToolsUrl = GlobalStatus.devToolsUrl
    val devToolsConnected = GlobalStatus.devToolsConn.stringBinding { if (it!!) "[Connected]" else "[Disconnected]" }

    val proxyStatus = GlobalStatus.proxyOptions.stringBinding { it.toString() }
    val aria2Status = GlobalStatus.aria2ClientOptions.stringBinding(GlobalStatus.aria2ClientConn) {
        "[${if (GlobalStatus.aria2ClientConn.value) "Connected" else "Disconnected"}] ${it?.url} ${if (!it?.secret.isNullOrBlank()) "[secret]" else ""}"
    }



}