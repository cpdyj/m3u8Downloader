package controller

import Media
import config.ConfigManager
import config.getBindedProperty
import config.schema.*
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.stringBinding

object GlobalStatus {
    val devToolsHost=ConfigManager.getBindedProperty(DevToolsHost)
    val devToolsPort=ConfigManager.getBindedProperty(DevToolsPort)
    val devToolsUrl = devToolsHost.stringBinding(devToolsPort){ "http://$it:${devToolsPort.value}/json" }
    val proxyOptions = ConfigManager.getBindedProperty(ProxyConfig)
    val aria2ClientOptions = ConfigManager.getBindedProperty(Aria2ClientConfig)

    val tabs = observableListOf<Tab>()
    val medias = observableListOf<Media>().also { it.onChange { println(it) } }

    val aria2ClientConn = SimpleBooleanProperty(false)
    val devToolsConn = SimpleBooleanProperty(false)
    val devToolsWebsocketEndpoint = SimpleStringProperty("")

    val primaryStatus = SimpleStringProperty("init.")
}

fun pStatus(string: String) {
    Platform.runLater { GlobalStatus.primaryStatus.value = string }
}

data class Tab(val title: String, val url: String, val webSocketDebuggerUrl: String)