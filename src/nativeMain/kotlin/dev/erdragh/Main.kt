package dev.erdragh

import gl.*
import glew.GLEW_OK
import glew.glewInit
import glfw.*
import kotlinx.cinterop.*

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

    do {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())

        glfwSwapBuffers(window)
        glfwPollEvents()
    } while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS &&
        glfwWindowShouldClose(window) == 0)

    GL.deleteVertexArrays(1, vertexArrayId)

    glfwTerminate()
}