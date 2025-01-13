package dev.erdragh

import gl.*
import gl.GL_COLOR_BUFFER_BIT
import gl.GL_DEPTH_BUFFER_BIT
import gl.GL_STATIC_DRAW
import gl.GL_TRUE
import gl.GLenum
import gl.glClear
import gl.glClearColor
import glew.*
import glfw.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.invoke
import okio.Path.Companion.toPath

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {

    if (glfwInit() == 0) {
        throw RuntimeException("Failed to initialize GLFW")
    }

    glfwWindowHint(GLFW_SAMPLES, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val window = glfwCreateWindow(1280, 720, "Test", null, null)
    if (window == null) {
        glfwTerminate()
        throw RuntimeException("Failed to create the GLFW window")
    }

    glfwMakeContextCurrent(window)

    if (glewInit().toInt() != GLEW_OK) {
        glfwTerminate()
        throw RuntimeException("Failed to initialize GLEW")
    }

    glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE)
    glClearColor(0f, 0f, 0.4f, 1f)

    val vertexArrayId = GL.genVertexArrays(1)
    GL.bindVertexArray(vertexArrayId)

    val buffer = GL.genBuffers(1)
    GL.bindBuffer(glew.GL_ARRAY_BUFFER.toUInt(), buffer)
    GL.bufferData(glew.GL_ARRAY_BUFFER.toUInt(), arrayOf(-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f), GL_STATIC_DRAW.toUInt())

    val programId = Shaders.load("./src/shaders/vertex.glsl".toPath(), "./src/shaders/fragment.glsl".toPath())

    do {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())

        glUseProgram!!(programId)

        glEnableVertexAttribArray!!(0.toUInt())
        GL.bindBuffer(glew.GL_ARRAY_BUFFER.toUInt(), buffer)
        glVertexAttribPointer!!(0u, 3, gl.GL_FLOAT.toUInt(), glew.GL_FALSE.toUByte(), 0, null)

        gl.glDrawArrays(gl.GL_TRIANGLES.toUInt() as GLenum, 0, 3)
        glDisableVertexAttribArray!!(0.toUInt())

        glfwSwapBuffers(window)
        glfwPollEvents()
    } while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS &&
        glfwWindowShouldClose(window) == 0)

    GL.deleteBuffers(1, buffer)
    GL.deleteVertexArrays(1, vertexArrayId)
    glDeleteProgram!!(programId)

    glfwTerminate()
}