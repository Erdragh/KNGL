package dev.erdragh.kngl.math

typealias Vec2f = Vec2<Float>
typealias Vec2d = Vec2<Double>
typealias Vec2i = Vec2<Int>

class Vec2<T : Number> {
    private val array: Array<T>

    constructor(init: T) : this(init, init)
    constructor(x: T, y: T) : this(arrayOf(x, y))
    constructor(array: Array<T>) {
        require(array.size == 2) { "Vec2 Backing Array must be 2 elements in size" }
        this.array = array
    }

    val x get() = array[0]
    val y get() = array[1]

    val repr get() = array
}