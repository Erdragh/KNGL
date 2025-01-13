package dev.erdragh

import glew.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
object GL {
    fun genVertexArrays(p1: GLsizei): UInt = memScoped {
        val id = alloc<UIntVar>()
        glGenVertexArrays!!(p1, id.ptr)
        id.value
    }
    fun bindVertexArray(vaoId: UInt) = glBindVertexArray!!(vaoId)
    fun deleteVertexArrays(p1: GLsizei, vaoId: UInt) = memScoped {
        val id = alloc<UIntVar>()
        id.value = vaoId
        glDeleteVertexArrays!!(p1, id.ptr)
    }

    fun genBuffers(p1: GLsizei): GLuint = memScoped {
        val id = alloc<UIntVar>()
        glGenBuffers!!(p1, id.ptr)
        id.value
    }
    fun bindBuffer(type: GLenum, id: GLuint) {
        glBindBuffer!!(type, id)
    }
    fun deleteBuffers(p1: GLsizei, bufferId: GLuint) = memScoped {
        val id = alloc<UIntVar>()
        id.value = bufferId
        glDeleteBuffers!!(p1, id.ptr)
    }
    fun bufferData(type: GLenum, data: Array<GLfloat>, hint: GLenum) = memScoped {
        val allocated = allocArray<FloatVar>(data.size)
        for ((i, x) in data.withIndex()) {
            allocated[i] = x
        }
        glBufferData!!(type, data.size.toLong(), allocated, hint)
    }

    fun shaderSource(id: UInt, p2: Int, source: String) = memScoped {
        val cString = source.cstr.getPointer(this)
        val doublePointer = alloc<CPointerVar<ByteVar>>()
        doublePointer.value = cString

        glShaderSource!!(id, p2, doublePointer.ptr, null)
    }
    fun getShaderiv(id: UInt, what: UInt): Int = memScoped {
        val result: IntVar = alloc()
        result.value = GL_FALSE

        glGetShaderiv!!(id, what, result.ptr)

        result.value
    }
    fun getProgramiv(id: UInt, what: UInt): Int = memScoped {
        val result: IntVar = alloc()
        result.value = GL_FALSE

        glGetProgramiv!!(id, what, result.ptr)

        result.value
    }
}