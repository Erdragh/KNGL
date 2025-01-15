package dev.erdragh.kngl

import glew.*
import kotlinx.cinterop.*
import okio.ByteString

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
    fun bufferData(type: GLenum, data: FloatArray, hint: GLenum) = memScoped {
        val allocated = allocArray<FloatVar>(data.size * Float.SIZE_BYTES)
        for ((i, x) in data.withIndex()) {
            allocated[i] = x
        }
        glBufferData!!(type, data.size * Float.SIZE_BYTES.toLong(), allocated, hint)
    }
    fun bufferData(type: GLenum, data: UIntArray, hint: GLenum) = memScoped {
        val allocated = allocArray<UIntVar>(data.size * UInt.SIZE_BYTES)
        for ((i, x) in data.withIndex()) {
            allocated[i] = x
        }
        glBufferData!!(type, data.size * UInt.SIZE_BYTES.toLong(), allocated, hint)
    }

    // Texture
    fun genTextures(p1: GLsizei): UInt = memScoped {
        val id = alloc<UIntVar>()
        glGenTextures(p1, id.ptr)
        id.value
    }
    fun deleteTextures(p1: GLsizei, texId: UInt) = memScoped {
        val id = alloc<UIntVar>()
        id.value = texId
        glDeleteTextures(p1, id.ptr)
    }

    fun shaderSource(id: UInt, p2: Int, source: ByteString) = memScoped {
        source.toByteArray().usePinned { pinned ->
            val doublePointer = alloc<CPointerVar<ByteVar>>()
            doublePointer.value = pinned.addressOf(0)

            glShaderSource!!(id, p2, doublePointer.ptr, null)
        }
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

    fun getString(what: UInt) = glGetString(what)?.reinterpret<ByteVar>()?.toKString()
}