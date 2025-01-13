package dev.erdragh

import glew.GLsizei
import glew.glBindVertexArray
import glew.glDeleteVertexArrays
import glew.glGenVertexArrays
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
object GL {
    inline fun genVertexArrays(p1: GLsizei): UInt = memScoped {
        val id = alloc<UIntVar>()
        glGenVertexArrays!!(p1, id.ptr)
        id.value
    }
    inline fun bindVertexArray(vaoId: UInt) = glBindVertexArray!!(vaoId)
    inline fun deleteVertexArrays(p1: GLsizei, vaoId: UInt) = memScoped {
        val id = alloc<UIntVar>()
        id.value = vaoId
        glDeleteVertexArrays!!(p1, id.ptr)
    }
}