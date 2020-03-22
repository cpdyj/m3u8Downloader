import BaseController
import options.GlobalOptions
import options.ProxyOptions

class ProxySettingsController(val option: ProxyOptions) : BaseController() {
    init {
        check(option!=GlobalOptions.proxyOptions)
    }

    val type = option::type.observableProperty()
    val host = option::host.observableProperty()
    val port = option::port.observableProperty()
    val username = option::username.observableProperty()
    val password = option::password.observableProperty()
}