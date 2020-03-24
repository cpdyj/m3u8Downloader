package config.schema

import Aria2ClientOptions

object ProxyConfig : ConfigKey<ProxyOptions> {
    override fun default() = ProxyOptions()
}

object Aria2ClientConfig : ConfigKey<Aria2ClientOptions> {
    override fun default() = Aria2ClientOptions()
}

object DevToolsHost : ConfigKey<String> {
    override fun default() = "127.0.0.1"
}

object DevToolsPort : ConfigKey<Int> {
    override fun default() = 9222
}


interface ConfigKey<T> {
    fun default(): T
}
