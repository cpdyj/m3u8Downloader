package options

enum class ProxyType {
    NONE, SOCKS5, HTTP
}


class ProxyOptions : OptionBase<ProxyOptions>() {
    var type by id { ProxyType.NONE }
    var host by id { "" }
    var port by id { 0 }
    var username by id { "" }
    var password by id { "" }
    override fun toString(): String = "[${type.name}]$host:$port ${if (username.isNotBlank()) "username is set." else ""}"
}
