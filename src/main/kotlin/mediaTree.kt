sealed class Media(val title: String) {
    override fun toString() = title
}

class SingleFile(title: String, val url: String) : Media(title)

class FileList(title: String, val url: String) : Media(title) {
}
