package controller

import tornadofx.Controller

open class BaseController : Controller(){
    private val set = mutableSetOf<() -> Unit>()

    fun destroy() {
        set.forEach { it.invoke() }
    }

    fun addDestoryHook(handler: () -> Unit) {
        set.add(handler)
    }

}