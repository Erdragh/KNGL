package dev.erdragh.kngl.math

typealias Vec4f = Vec4<Float>
typealias Vec4d = Vec4<Double>
typealias Vec4i = Vec4<Int>

class Vec4<T : Number> {
    private val array: Array<T>

    constructor(init: T) : this(init, init, init, init)
    constructor(x: T, y: T, z: T, w: T) : this(arrayOf(x, y, z, w))
    constructor(array: Array<T>) {
        require(array.size == 4) { "Vec4 Backing Array must be 4 elements in size" }
        this.array = array
    }

    val x get() = array[0]
    val y get() = array[1]
    val z get() = array[2]
    val w get() = array[2]

    val r get() = x
    val g get() = y
    val b get() = z
    val a get() = w

    val repr get() = array
}