package dev.erdragh.kngl

open class Named<T> {
    private val container: MutableMap<String, T> = mutableMapOf()
    fun find(name: String) = container[name]
    operator fun get(name: String) = find(name)

    protected fun registerNamed(name: String, value: T) {
        container[name] = value
    }
}