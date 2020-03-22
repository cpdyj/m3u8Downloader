import java.io.File

fun configPath() = File(System.getProperty("user.home"), ".m3u8Downloader/config.json")
fun configPathBackup() = File(System.getProperty("user.home"), ".m3u8Downloader/config.json.backup")
