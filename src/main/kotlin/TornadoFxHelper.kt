import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.util.StringConverter
import tornadofx.ChangeListener
import kotlin.reflect.KProperty0

/**
 * Bind SimpleObjectProperty<Enum<*>> to BooleanProperty.
 * __Attention memory leaks.__
 *
 * @param[list] vararg argument, pair of `object` and [BooleanProperty]. When `this == object`, the `booleanProperty` will be true.
 *
 * @return Cancel function. when you need cancel contract, invoke it.
 */
fun <A : Enum<*>, B : BooleanProperty> SimpleObjectProperty<A>.bindBooleanPropertyBidirectional(vararg list: Pair<A, B>): () -> Unit {
    val cancleList = mutableListOf<() -> Unit>()
    list.forEach { (a, b) ->
        val listenB: ChangeListener<Boolean> =
            ChangeListener { _, _, it -> if (it) this.value = a }
        val listenA: ChangeListener<A> =
            ChangeListener { _, _, it -> b.value = it == a }
        b.addListener(listenB)
        this.addListener(listenA)
        b.value = this.value == a
        cancleList.add {
            this.removeListener(listenA)
            b.removeListener(listenB)
        }
    }
    return {
        cancleList.forEach { it() }
    }
}

