package view

import ConfigCenter
import MainViewController
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.util.Callback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.*

class MainView : View() {

    val controller=MainViewController(this)


    override val root = borderpane {
        top = menubar {
            menu("File")
            menu("Settings") {
                item("Proxy Settings...").action(::popupProxySettings)
            }
            menu("About") {
                item("About").action(::popupAboutWindow)
            }
        }
        center = vbox {
            this.padding = tornadofx.insets(16)
            gridpane {
                this.hgap = 8.0
                this.vgap = 8.0
                this.hgrow=Priority.ALWAYS
                row {
                    label("WebDev url:").gridpaneConstraints { hAlignment = HPos.RIGHT }
//                    textfield(controller.webdevUrl){
//                        gridpaneColumnConstraints { this.hgrow=Priority.ALWAYS }
//                        this.useMaxWidth=true
//                    }
                    button("Get"){
                        this.useMaxWidth=true
                    }
                }
                row {
                    label("Tab:").gridpaneConstraints { hAlignment = HPos.RIGHT }
                    choicebox<String> {
                        hgrow=Priority.ALWAYS
                        this.useMaxWidth=true
                    }
                }
                row {
                    label("Action:").gridpaneConstraints { hAlignment = HPos.RIGHT }
                    hbox {
                        spacing=4.0
                        button("Connect")
                        button("Disconnect")
                    }

                }
            }
            splitpane{
                this.vgrow=Priority.ALWAYS
                vbox {
                    this.vgrow=Priority.ALWAYS
                    label("Video sources:")
                    treeview<String> {
//                        isShowRoot=false
//                        root.isExpanded=true
                    }
                }
                vbox {
                    this.vgrow=Priority.ALWAYS
                    label("Video segments:")
                    tableview<String> {
                    }
                }
            }
        }
        bottom = stackpane {
            this.padding = insets(4)
            label {
                bind(controller.primaryStatus)
                stackpaneConstraints { this.alignment = Pos.CENTER_LEFT }
            }
            label {
                bind(controller.secondaryStatus)
                stackpaneConstraints { this.alignment = Pos.CENTER_RIGHT }
            }
        }
    }

    private fun popupProxySettings() {
        ProxySettingsView(ConfigCenter.proxyOptions) {
            it?.let { ConfigCenter.proxyOptions = it }
        }.openWindow(resizable = false, owner = this.currentStage,modality = Modality.WINDOW_MODAL)
    }

    private fun popupAboutWindow() {
        alert(
            type = Alert.AlertType.INFORMATION,
            header = "About...",
            content = "build by iseki",
            title = "About..."
        )
    }

    override fun onUndock() {
        controller.onDestory()
        super.onUndock()
    }
}

