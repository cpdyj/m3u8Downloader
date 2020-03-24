package view

import Media
import controller.DevToolsService.updateTabList
import controller.DevToolsService.watchIt
//import controller.DevToolsService.updateTabList
import controller.GlobalStatus
import controller.MainFrameController
import controller.Tab
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import tornadofx.*

class MainFrameView : View() {
    val controller: MainFrameController by inject()
    override val root = borderpane {
        top = menubar {
            menu("Welcome") { }
        }
        center = vbox {
            gridpane {
                this.vgap = 4.0
                this.hgap = 8.0
                constraintsForColumn(0).halignment = HPos.RIGHT
                constraintsForColumn(1).hgrow = Priority.ALWAYS
                constraintsForColumn(2).minWidth = 80.0
                row {
                    label("DevTools Endpoint: ")
                    hbox(spacing = 4.0,alignment = Pos.BASELINE_CENTER){
                        label("Host:")
                        textfield { bind(GlobalStatus.devToolsHost) }
                        label("Port:")
                        textfield { bind(GlobalStatus.devToolsPort) }
                    }
                    button {
                        this.useMaxSize = true
                        text = "Fetch tabs"
                        action { updateTabList() }
                    }
                }
                row {
                    label("Tab:") {
                    }
                    choicebox<Tab> {
                        this.useMaxWidth = true
                        this.items = GlobalStatus.tabs
                        this.converter = object : StringConverter<Tab?>() {
                            override fun toString(`object`: Tab?): String {
                                return `object`!!.title
                            }

                            override fun fromString(string: String?): Tab? {
                                TODO("Not yet implemented")
                            }
                        }
                        this.valueProperty().bindBidirectional(controller.selectedTab)
                    }
                    button {
                        this.useMaxSize = true
                        text = "Open"
                        action{ watchIt() }
                    }
                }
                row {
                    label("Proxy Status:")
                    label("......").bind(controller.proxyStatus)
                    button("Proxy Config") {
                        useMaxSize = true
                    }
                }
                row {
                    label("Aria2 Status:")
                    label("......").bind(controller.aria2Status)
                    button("Aria2 Config") {
                        useMaxSize = true
                    }
                }
                row {
                    label("DevTools Status:")
                    label("......").bind(controller.devToolsConnected)
                }
            }
            splitpane {
                vbox {
                    listview<Media> {
                        this.items = GlobalStatus.medias
                    }
                }
                vbox {
                    tableview<String> {
                        this.vgrow = Priority.ALWAYS
                    }
                }
                this.vgrow = Priority.ALWAYS
            }
        }

        bottom = stackpane {
            this.padding = insets(4)
            label("Primary Status") {
                bind(controller.primaryStatus)
                stackpaneConstraints {
                    alignment = Pos.CENTER_LEFT
                }
            }
        }
    }

    override fun onUndock() {
        controller.destroy()
        super.onUndock()
    }
}