package config

import config.schema.ConfigKey
import javafx.beans.property.SimpleObjectProperty
import tornadofx.onChange

fun <T> ConfigManagerBase.getBindedProperty(key: ConfigKey<T>, readOnly: Boolean = false): SimpleObjectProperty<T>{
    val v=this[key]
    val p=SimpleObjectProperty(v)
    this.onChange(key){ o,n->
        if (o!=n){
            p.value= n
        }
    }
    if (!readOnly){
        p.onChange {
            this[key]=it as T
        }
    }
    return p
}