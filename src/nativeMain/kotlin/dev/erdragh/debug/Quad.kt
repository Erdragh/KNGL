package dev.erdragh.debug

import dev.erdragh.GL
import glew.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
object Quad {
    private val vao: GLuint
    private val vbo: GLuint
    private val ibo: GLuint
    init {
        val quad = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f, 1f, 1f, 1f, 1f, 0f, 1f, 1f, 0f, 1f)
        val idx = uintArrayOf(0u, 1u, 2u, 2u, 3u, 0u)

        vao = GL.genVertexArrays(1)
        GL.bindVertexArray(vao)
        vbo = GL.genBuffers(1)
        GL.bindBuffer(GL_ARRAY_BUFFER.toUInt(), vbo)
        GL.bufferData(GL_ARRAY_BUFFER.toUInt(), quad, GL_STATIC_DRAW.toUInt())
        ibo = GL.genBuffers(1)
        GL.bindBuffer(GL_ELEMENT_ARRAY_BUFFER.toUInt(), ibo)
        GL.bufferData(GL_ELEMENT_ARRAY_BUFFER.toUInt(), idx, GL_STATIC_DRAW.toUInt())

        glEnableVertexAttribArray!!(0u)
        glVertexAttribPointer!!(0u, 3, GL_FLOAT.toUInt(), GL_FALSE.toUByte(), Float.SIZE_BYTES * 5, null)
        glEnableVertexAttribArray!!(1u)
        glVertexAttribPointer!!(1u, 2, GL_FLOAT.toUInt(), GL_FALSE.toUByte(), Float.SIZE_BYTES * 5, (Float.SIZE_BYTES * 3).toLong().toCPointer())

        GL.bindVertexArray(0u)
        GL.bindBuffer(GL_ARRAY_BUFFER.toUInt(), 0u)
        GL.bindBuffer(GL_ELEMENT_ARRAY_BUFFER.toUInt(), 0u)
    }

    fun cleanup() {
        GL.deleteVertexArrays(1, vao)
        GL.deleteBuffers(1, ibo)
        GL.deleteBuffers(1, vbo)
    }

    fun draw() {
        GL.bindVertexArray(vao)
        glDrawElements(GL_TRIANGLES.toUInt(), 6, GL_UNSIGNED_INT.toUInt(), null)
        GL.bindVertexArray(0u)
    }
}