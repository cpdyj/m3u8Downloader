object ConfigCenter : ConfigCenterBase() {
    var proxyOptions by obj("proxyOptions") { ProxyOptions() }
    var webdevUrl by obj("webdevUrl"){ "https://127.0.0.1:9222/json" }
}

