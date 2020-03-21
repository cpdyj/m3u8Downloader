import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.*
import kotlin.reflect.KProperty

typealias ChangeListenerCanceler = () -> Boolean
typealias ChangeListener<T> = (old: T, new: T) -> Unit

open class ConfigCenterBase {

    private val list = LinkedList<Property<*>>()

    private val watchMap: MutableMap<Int, MutableSet<ChangeListener<*>>> = mutableMapOf()

    fun <T> watch(kProperty: KProperty<T>, listener: ChangeListener<T>): ChangeListenerCanceler {
        val key = kProperty.hashCode()
        watchMap.getOrPut(key) { mutableSetOf() }.add(listener)
        return { watchMap[key]?.remove(listener) ?: false }
    }

    private fun fireUpdate(kProperty: KProperty<*>,old:Any?,new:Any?){
        watchMap[kProperty.hashCode()]?.forEach {
            runCatching { (it as ChangeListener<Any?>).invoke(old, new) }
        }
    }

    var version = 0
        private set

    fun setValues(json: String) {
        val tree = json.decodeJson()
        list.forEach { p ->
            tree.get(p.key)?.let { (p as Property<Any?>).field = it mapTo p.javaType }
        }
    }

    fun getValues(): String {
        val node = ObjectNode(JsonNodeFactory.instance)
        list.forEach { p ->
            node.set(p.key, p.field.toJsonTree())
        }
        return node.encode()
    }


    inner class Property<T>(var field: T, val javaType: Class<*>, val key: String) {
        init {
            list.add(this)
        }

        operator fun getValue(thisRef: Any?, kProperty: KProperty<*>): T {
            check(thisRef == null || thisRef == this@ConfigCenterBase)
            return field
        }

        operator fun setValue(thisRef: Any?, kProperty: KProperty<*>, value: T) {
            fireUpdate(kProperty,field,value)
            field = value
            version++
        }

    }

    @OptIn(ExperimentalStdlibApi::class)
    protected inline fun <reified T> obj(key: String) =
        Property<T?>(null, T::class.java, key)

    @OptIn(ExperimentalStdlibApi::class)
    protected inline fun <reified T> obj(key: String, noinline l: (() -> T)) =
        Property<T>(l.invoke(), T::class.java, key)
}
