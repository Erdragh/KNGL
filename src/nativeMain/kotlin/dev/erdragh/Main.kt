package dev.erdragh

import dev.erdragh.context.Context
import glew.*
import glfw.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.invoke
import okio.Path.Companion.toPath

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    Context.init()

    val vertexArrayId = GL.genVertexArrays(1)
    GL.bindVertexArray(vertexArrayId)

    val buffer = GL.genBuffers(1)
    GL.bindBuffer(GL_ARRAY_BUFFER.toUInt(), buffer)
    GL.bufferData(GL_ARRAY_BUFFER.toUInt(), arrayOf(-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f), GL_STATIC_DRAW.toUInt())
    glVertexAttribPointer!!(0u, 3, GL_FLOAT.toUInt(), GL_FALSE.toUByte(), 0, null)
    glEnableVertexAttribArray!!(0.toUInt())

    val programId = Shaders.load("./src/shaders/vertex.glsl".toPath(), "./src/shaders/fragment.glsl".toPath())

    do {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())

        glUseProgram!!(programId)

        GL.bindVertexArray(vertexArrayId)
        glDrawArrays(GL_TRIANGLES.toUInt(), 0, 3)

        glUseProgram!!(0u)

        glfwSwapBuffers(Context.glfwWindow)
        glfwPollEvents()
    } while (glfwGetKey(Context.glfwWindow, GLFW_KEY_ESCAPE) != GLFW_PRESS &&
        glfwWindowShouldClose(Context.glfwWindow) == 0)

    GL.deleteBuffers(1, buffer)
    GL.deleteVertexArrays(1, vertexArrayId)
    glDeleteProgram!!(programId)

    Context.uninit()
}