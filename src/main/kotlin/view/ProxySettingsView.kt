package view

import ProxyOptions
import ProxyType
import bindBooleanPropertyBidirectional
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.stage.Modality
import javafx.stage.Screen
import javafx.util.converter.NumberStringConverter
import tornadofx.*

class ProxySettingsView(val proxyOptions: ProxyOptions=ProxyOptions(),val callback: (ProxyOptions?) -> Unit = {}) : View() {
    init {
        title = "Proxy Settings"
    }

    val proxyTypeProperty = SimpleObjectProperty(ProxyType.NONE)
    var proxyType by proxyTypeProperty
    val hostProperty = SimpleStringProperty(proxyOptions.host)
    var host by hostProperty
    val portProperty = SimpleIntegerProperty(proxyOptions.port)
    var port by portProperty
    val usernameProperty = SimpleStringProperty(proxyOptions.username)
    var username by usernameProperty
    val passwordProperty = SimpleStringProperty(proxyOptions.password)
    var password by passwordProperty

    val proxyTypeList = listOf("No Proxy", "Http", "Socks5")
    val proxyTypeNeedCheck = setOf("Http", "Socks5")
    override val root = form {
        this.alignment = Pos.CENTER
        fieldset("Proxy Settings") {
            this.alignment = Pos.CENTER
            field("Type") {
                togglegroup {
                    proxyTypeProperty.bindBooleanPropertyBidirectional(
                        ProxyType.NONE to radiobutton("No Proxy").selectedProperty(),
                        ProxyType.SOCKS5 to radiobutton("SOCKS5").selectedProperty(),
                        ProxyType.HTTP to radiobutton("HTTP").selectedProperty()
                    )
                }
            }
            field("Host & Port") {
                textfield().bind(hostProperty)
                textfield {
                    this
                    bind(portProperty,converter = NumberStringConverter())
                    this.prefWidth = 15.0
                }
            }
            field("Username") { textfield().bind(usernameProperty) }
            field("Password") { passwordfield().bind(passwordProperty) }
        }
        hbox {
            spacing = 8.0
            this.alignment = Pos.CENTER
            button("Test").action(::popupTestWindow)
            button("Save").action { tryProxyOption()?.let(callback)?.run { close() } }
            button("Cancel").action {
                callback.invoke(null)
                close()
            }
        }
    }

    private fun popupTestWindow() {
        tryProxyOption()?.let {
            ProxyTestView(it).openWindow(
                owner = currentStage,
                modality = Modality.WINDOW_MODAL,
                resizable = true
            )
        }
    }

    private fun tryProxyOption() = try {
        ProxyOptions(
            type = proxyType,
            host = host.trim()
                .also { check(proxyType == ProxyType.NONE || it.isNotBlank()) { "Host invalid" } },
            port = port.also { check(proxyType == ProxyType.NONE || it in 1..65535) { "Port must in range[1, 65535]" } }
        )
    } catch (e: Exception) {
        error(header = "出错了", content = e.localizedMessage)
        null
    }

    override fun onDock() {
        setWindowMinSize(root.width * Screen.getPrimary().outputScaleY, root.height * Screen.getPrimary().outputScaleX)
        setWindowMaxSize(root.width * Screen.getPrimary().outputScaleY, root.height * Screen.getPrimary().outputScaleX)
    }
}

