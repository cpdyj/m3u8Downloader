package config

import JsonMapper
import config.schema.ConfigKey
import java.lang.ref.WeakReference
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

open class ConfigManagerBase {
    val weakListeners: MutableMap<ConfigKey<*>, MutableSet<WeakReference<(Any?, Any?) -> Unit>>> = mutableMapOf()
    val listeners: MutableMap<ConfigKey<*>, MutableSet<(Any?, Any?) -> Unit>> = mutableMapOf()
    val mapper = JsonMapper.kindMapper.copy()
    val data = mutableMapOf<ConfigKey<*>, Any?>()
    fun <T> onChange(key: ConfigKey<T>, weak: Boolean = false, handler: (old: T?, new: T) -> Unit): () -> Boolean {
        if (weak) {
            val ref = WeakReference(handler as (Any?, Any?) -> Unit)
            weakListeners.getOrPut(key) { mutableSetOf() }.add(ref)
            return { weakListeners[key]?.remove(ref) ?: false }
        } else {
            listeners.getOrPut(key) { mutableSetOf() }.add(handler as (Any?, Any?) -> Unit)
            return { listeners[key]?.remove(handler) ?: false }
        }
    }

    private fun fireUpdate(key: ConfigKey<*>, old: Any?, new: Any?) {
        listeners[key]?.forEach { h -> runCatching { h.invoke(old, new) } }
        weakListeners[key]?.forEach { h -> runCatching { h.get()?.invoke(old, new) ?: weakListeners[key]?.remove(h) } }
    }

    fun getContext(): String {
        val errorList = mutableListOf<Throwable>()
        val a = data.map { (t, u) ->
            kotlin.runCatching {
                val typeName = t::class.supertypes.find { it.jvmErasure.let { it == ConfigKey::class } }
                    ?.arguments?.get(0)?.type?.javaType?.typeName?.replace("? extends", "")
                val map = mutableMapOf(
                    "key" to t::class.qualifiedName,
                    "dataType" to typeName,
                    "data" to u
                )
                if (t::class.objectInstance == null) {
                    map["keyData"] = mapper.writeValueAsString(t)
                }
                map
            }.onFailure(errorList::add).getOrNull()
        }.filterNotNull()
        return mapper.writeValueAsString(a)!!
    }

    fun setContent(json: String) {
        val errorList = mutableListOf<Throwable>()
        val a = mapper.readTree(json)
        check(a.isArray)
        data.clear()
        val result = a.asSequence().map { node ->
            runCatching {
                val keyType = Class.forName(node["key"].asText())
                val key = keyType.kotlin.objectInstance ?: run { mapper.treeToValue(node["keyData"], keyType) }
                val dataType = mapper.typeFactory.constructFromCanonical(node["dataType"].asText())
                val data = mapper.convertValue<Any?>(node["data"], dataType)
                key as ConfigKey<*> to data
            }.onFailure(errorList::add).getOrNull()
        }.filterNotNull().forEach { (k, v) ->
            fireUpdate(k, null, v)
            data[k] = v
        }
    }

    operator fun <T> get(key: ConfigKey<T>): T {
        return (data[key] as? T) ?: key.default().also { set(key, it) }
    }

    operator fun <T> set(key: ConfigKey<T>, value: T) {
        val o = data[key]
        data[key] = value
        fireUpdate(key, o, value)
    }
}

object ConfigManager : ConfigManagerBase() {

}


