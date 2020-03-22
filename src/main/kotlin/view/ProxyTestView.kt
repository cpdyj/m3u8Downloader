package view

import options.ProxyOptions
import options.ProxyType
import javafx.geometry.Pos
import tornadofx.*

class ProxyTestView(proxyOptions: ProxyOptions) : View() {
    init {
        title = "Proxy test"
    }

    override val root = vbox {
        padding = insets(8)
        alignment = Pos.CENTER
        val untestable = proxyOptions.type == ProxyType.NONE
        label(text = if (untestable) "没设置代理，没什么可测试的" else "输入要测试的 URL") { this.padding = insets(8) }
        if (!untestable) {
            textfield()
        }
        hbox {
            spacing = 8.0
            padding = insets(8)
            alignment = Pos.CENTER
            if (!untestable) {
                button("Test") { action { } }
            }
            button(if (untestable) "OK" else "Close") { action { close() } }
        }
    }
}