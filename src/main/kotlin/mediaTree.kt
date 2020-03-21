import javafx.scene.control.TreeItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

sealed class MediaTreeElement()

class StandaloneMedia(val title: String):MediaTreeElement(){
val treeItem by lazy { TreeItem(this) }
}

class M3U8MediaList(val title: String, val url:String):MediaTreeElement(){
    private val lazyLoadPlaceholder by lazy { TreeItem<MediaTreeElement>(LoadingPlaceholder) }
    val treeItem by lazy {
        TreeItem<MediaTreeElement>(this).also {
            it.children.add(lazyLoadPlaceholder)
            it.addEventHandler(TreeItem.branchExpandedEvent<MediaTreeElement>()){
                fetchMediaList(this)
            }
        }
    }
}

class RootMedia()

object LoadingPlaceholder : MediaTreeElement()

fun fetchMediaList(mediaTreeElement:MediaTreeElement) =GlobalScope.async<List<MediaTreeElement>> { TODO() }
