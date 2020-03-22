import javafx.beans.property.SimpleObjectProperty
import options.OptionBase
import tornadofx.markDirty
import tornadofx.onChange
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

open class BaseController {
    val clrSet = mutableSetOf<() -> Any>()
    fun <T> KProperty0<T>.observableProperty(readOnly:Boolean=false): SimpleObjectProperty<T> {
        val delegate =
            runCatching { this.also { isAccessible = true }.getDelegate() as OptionBase<*>.OptionDelegateBase<T?> }
                .getOrElse {
                    throw RuntimeException("Get delegate fail. Maybe options not delegate by OptionDelegate?", it)
                }
        val p = SimpleObjectProperty(this.get())
        if (!readOnly){
            p.onChange { (this as KMutableProperty0<T?>).set(it) }
        }
        val c = delegate.onChange { n -> p.value=n ;println("change: ${p.hashCode()} $n");p.markDirty()}
        clrSet.add(c)
        return p
    }

    fun destroy() {
        clrSet.forEach { it.invoke() }
        clrSet.clear()
        onDestroy()
    }

    open fun onDestroy() {}
}