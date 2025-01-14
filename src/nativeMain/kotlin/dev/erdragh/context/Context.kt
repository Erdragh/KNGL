package dev.erdragh.context

import cnames.structs.GLFWwindow
import dev.erdragh.GL
import glew.*
import glfw.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString

@OptIn(ExperimentalForeignApi::class)
object Context {
    var glfwWindow: CPointer<GLFWwindow>? = null

    fun init() {
        if (glfwInit() == null)
            throw RuntimeException("Failed to initialize GLFW")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE)
        glfwWindowHint(GLFW_DECORATED, GLFW_TRUE)
        glfwWindowHint(GLFW_FLOATING, GLFW_TRUE)
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)

        glfwWindow = glfwCreateWindow(1280, 720, "KNGL", null, null)
        if (glfwWindow == null) {
            glfwTerminate()
            throw RuntimeException("Failed to create the GLFW window")
        }
        glfwMakeContextCurrent(glfwWindow)
        glfwSwapInterval(1)

        glewExperimental = GL_TRUE.toUByte()
        val err = glewInit();
        if (err != GLEW_OK.toUInt()) {
            glfwDestroyWindow(glfwWindow)
            glfwTerminate()
        }

        println("GLFW: ${glfwGetVersionString()?.toKString()}")
        println("OpenGL: ${GL.getString(GL_VERSION.toUInt())}, ${GL.getString(GL_RENDERER.toUInt())}")
        println("GLSL: ${GL.getString(GL_SHADING_LANGUAGE_VERSION.toUInt())}")

        // TODO: debugging output

        // TODO: Find out how to replace null with this
        glfwSetWindowUserPointer(glfwWindow, null)

        glfwSetInputMode(glfwWindow, GLFW_STICKY_KEYS, 1)
        glfwSetInputMode(glfwWindow, GLFW_STICKY_MOUSE_BUTTONS, 1)

        // TODO: ImGui

        glEnable(GL_DEPTH_TEST.toUInt())
        glCullFace(GL_BACK.toUInt())
        glEnable(GL_CULL_FACE.toUInt())
        glClearColor(0f, 0f, 0f, 1f)
        glClearDepth(1.0)
    }

    fun uninit() {
        println("Resetting GLFW Input Mode")
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        println("Terminating GLFW")
        glfwTerminate()
        println("Successfully terminated Context")
    }
}