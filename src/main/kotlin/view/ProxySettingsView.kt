package view

import ProxySettingsController
import bindBooleanPropertyBidirectional
import javafx.geometry.Pos
import javafx.stage.Modality
import javafx.stage.Screen
import options.ProxyOptions
import options.ProxyType
import tornadofx.*

class ProxySettingsView(proxyOptions: ProxyOptions = ProxyOptions(), val callback: (ProxyOptions?) -> Unit = {}) :
    View() {

    val controller = ProxySettingsController(proxyOptions)

    init {
        title = "Proxy Settings"
    }

    override val root = form {
        this.alignment = Pos.CENTER
        fieldset("Proxy Settings") {
            this.alignment = Pos.CENTER
            field("Type") {
                togglegroup {
                    controller.type.bindBooleanPropertyBidirectional(
                        ProxyType.NONE to radiobutton("No Proxy").selectedProperty(),
                        ProxyType.SOCKS5 to radiobutton("SOCKS5").selectedProperty(),
                        ProxyType.HTTP to radiobutton("HTTP").selectedProperty()
                    )
                }
            }
            field("Host & Port") {
                textfield().bind(controller.host)
                textfield {
                    bind(controller.port)
                    this.prefWidth = 15.0
                }
            }
            field("Username") { textfield().bind(controller.username) }
            field("Password") { passwordfield().bind(controller.password) }
        }
        hbox {
            spacing = 8.0
            this.alignment = Pos.CENTER
            button("Test").action(::popupTestWindow)
            button("Save").action { controller.option.let(callback).run { close() } }
            button("Cancel").action {
                callback.invoke(null)
                close()
            }
        }
    }

    private fun popupTestWindow() {
        controller.option.let {
            ProxyTestView(it).openWindow(
                owner = currentStage,
                modality = Modality.WINDOW_MODAL,
                resizable = true
            )
        }
    }

    override fun onDock() {
        setWindowMinSize(root.width * Screen.getPrimary().outputScaleY, root.height * Screen.getPrimary().outputScaleX)
        setWindowMaxSize(root.width * Screen.getPrimary().outputScaleY, root.height * Screen.getPrimary().outputScaleX)
    }

    override fun onUndock() {
        controller.destroy()
    }
}

