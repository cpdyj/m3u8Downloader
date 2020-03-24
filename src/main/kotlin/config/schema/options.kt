package config.schema


data class ProxyOptions(
    val type: Type = Type.NOPROXY,
    val host: String = "",
    val port: Int = 0,
    val username: String = "",
    val password: String = ""
) {
    enum class Type {
        NOPROXY, SOCKS5, HTTP
    }

    override fun toString(): String {
        return if (type == Type.NOPROXY) "[$type]" else "[$type]$host:$port ${if (username.isNotBlank()) "auth" else ""}"
    }
}

