enum class ProxyType {
    NONE, SOCKS5, HTTP
}


data class ProxyOptions(
    val type: ProxyType = ProxyType.NONE,
    val host: String = "",
    val port: Int = 0,
    val username: String = "",
    val password: String = ""
)


data class Aria2ClientOption(
    val rpcUrl: String,
    val secret: String
)