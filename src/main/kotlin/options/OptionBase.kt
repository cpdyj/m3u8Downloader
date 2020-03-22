package options

import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


typealias ChangeListener<T> = (new: T) -> Unit
typealias ChangeListenerCanceller = () -> Boolean

open class OptionBase<R : OptionBase<R>> {
//    private val mutationListenerSet= mutableSetOf<()->Unit>()
    protected fun <T : OptionBase<T>> ids(initializer: () -> T): OptionValueDelegate<T> {
        return OptionValueDelegate(initializer.invoke())
    }

    protected fun <T> id(initializer: () -> T): SimpleValueDelegate<T> {
        return initializer.invoke().let { return SimpleValueDelegate(it) }
    }
//    fun fireMutate(){
//        mutationListenerSet.forEach { it.invoke() }
//    }
//    fun onMutate(handler:()->Unit){
//
//    }
    abstract inner class OptionDelegateBase<T> {

        abstract operator fun getValue(thisRef: Any, kProperty: KProperty<*>): T
        abstract operator fun setValue(thisRef: Any, kProperty: KProperty<*>, value: T)
        private val set = mutableSetOf<ChangeListener<T>>()
        fun onChange(produce: ChangeListener<T>): ChangeListenerCanceller {
            set.add(produce)
            return { set.remove(produce) }
        }

        fun fireChange(new: T) {
            set.forEach { runCatching { it.invoke(new) } }
        }
    }

    inner class OptionValueDelegate<T : OptionBase<T>>(initValue: T) : OptionDelegateBase<T>() {
        private val valueField: T = initValue
        override fun getValue(thisRef: Any, kProperty: KProperty<*>): T {
            return valueField
        }

        override fun setValue(thisRef: Any, kProperty: KProperty<*>, value: T) {
            if (valueField !== value) {
                val b = value::class.memberProperties.asSequence()
                    .map { it.also { it.isAccessible = true } as KProperty1<T, Any?> }
                    .map { it.name to it }.toMap()
                valueField::class.memberProperties.asSequence()
                    .map { it.also { it.isAccessible = true } as? KMutableProperty1<T, Any?> }
                    .filterNotNull().forEach {
                        it.set(valueField, b[it.name]?.get(value) ?: error("copy fail. source property not found"))
                        println("copy succeed: $it")
                    }
                fireChange(valueField)
            }
//            fireMutate()
        }
    }

    inner class SimpleValueDelegate<T>(initValue: T) : OptionDelegateBase<T>() {
        private var valueField: T = initValue
        override fun getValue(thisRef: Any, kProperty: KProperty<*>): T {
            return valueField
        }

        override fun setValue(thisRef: Any, kProperty: KProperty<*>, value: T) {
            valueField = value
            fireChange(value)
//            fireMutate()
        }

    }

    fun readValueFrom(source: R) {
        val map = source::class.memberProperties.asSequence()
            .map { it.also { it.isAccessible = true }.name to it as KProperty1<R, Any?> }.toMap()
        this::class.memberProperties.asSequence()
            .map { it.also { it.isAccessible=true } as KProperty1<OptionBase<R>,*> }
            .forEach {
                val d=it.getDelegate(this) as? OptionValueDelegate<*>
                if (d!=null){
                    val v=it.get(this) as OptionBase<*>
                    (v::readValueFrom as KFunction1<Any?,Unit>).invoke(map[it.name])
                }else{
                    if (it is KMutableProperty1<*,*>){
                        (it as KMutableProperty1<OptionBase<R>,Any>).set(this,map[it.name]?.get(source)?: error("property not found"))
                    }
                }
            }
    }

    fun fork():R{
        val instance=this::class.createInstance() as R
        instance.readValueFrom(this as R)
        return instance
    }

    fun merge(r:R){
        this.readValueFrom(r)
    }
}