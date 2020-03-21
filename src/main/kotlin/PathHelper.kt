import java.io.File

fun configPath() = File(System.getProperty("user.home"), ".m3u8Downloader/config.json")
fun configPathTemp() = File(System.getProperty("user.home"), ".m3u8Downloader/config.json.temp")
