package dev.erdragh.kngl.math

typealias Vec3f = Vec3<Float>
typealias Vec3d = Vec3<Double>
typealias Vec3i = Vec3<Int>

class Vec3<T : Number> {
    private val array: Array<T>

    constructor(init: T) : this(init, init, init)
    constructor(x: T, y: T, z: T) : this(arrayOf(x, y, z))
    constructor(array: Array<T>) {
        require(array.size == 3) { "Vec3 Backing Array must be 3 elements in size" }
        this.array = array
    }

    val x: T
        get() = array[0]
    val y: T
        get() = array[1]
    val z: T
        get() = array[2]

    val r get() = x
    val g get() = y
    val b get() = z

    val repr get() = array
}