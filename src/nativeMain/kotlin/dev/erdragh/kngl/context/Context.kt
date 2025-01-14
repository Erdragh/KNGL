package dev.erdragh.kngl.context

import cnames.structs.GLFWwindow
import dev.erdragh.kngl.GL
import dev.erdragh.kngl.util.Quad
import glew.*
import glfw.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
object Context {
    fun <T> initialized(action: Context.() -> T): T {
        init()
        val ret = action()
        uninit()
        return ret
    }

    private var glfwWindow: CPointer<GLFWwindow>? = null
    var userKeyCallback: ((key: Int, scandcode: Int, action: Int, mods: Int) -> Unit)? = null
    var userMouseCallback: ((x: Double, y: Double) -> Unit)? = null
    var userMouseButtonCallback: ((button: Int, action: Int, mods: Int) -> Unit)? = null
    var userMouseScrollCallback: ((x: Double, y: Double) -> Unit)? = null
    var userResizeCallback: ((w: Int, h: Int) -> Unit)? = null
    var userCharCallback: ((c: UInt) -> Unit)? = null

    private fun init() {
        if (glfwInit() == null)
            throw RuntimeException("Failed to initialize GLFW")

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
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

        enableGLDebugOutput()

        // TODO: Find out how to replace null with this
        glfwSetWindowUserPointer(glfwWindow, null)

        // setup callbacks
        glfwSetKeyCallback(glfwWindow, staticCFunction(::keyCallback))
        glfwSetCursorPosCallback(glfwWindow, staticCFunction(::mouseCallback))
        glfwSetMouseButtonCallback(glfwWindow, staticCFunction(::mouseButtonCallback))
        glfwSetScrollCallback(glfwWindow, staticCFunction(::mouseScrollCallback))
        glfwSetFramebufferSizeCallback(glfwWindow, staticCFunction(::resizeCallback))
        glfwSetCharCallback(glfwWindow, staticCFunction(::charCallback))

        // set input mode
        glfwSetInputMode(glfwWindow, GLFW_STICKY_KEYS, 1)
        glfwSetInputMode(glfwWindow, GLFW_STICKY_MOUSE_BUTTONS, 1)

        // TODO: ImGui

        glEnable(GL_DEPTH_TEST.toUInt())
        glCullFace(GL_BACK.toUInt())
        glEnable(GL_CULL_FACE.toUInt())
        glClearColor(0f, 0f, 0f, 1f)
        glClearDepth(1.0)
    }

    private fun uninit() {
        Quad.cleanup()
        println("Resetting GLFW Input Mode")
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        println("Terminating GLFW")
        glfwTerminate()
        println("Successfully terminated Context")
    }

    fun swapBuffers() {
        glfwSwapBuffers(glfwWindow)
        glfwPollEvents()
    }

    fun show() = glfwShowWindow(glfwWindow)
    fun hide() = glfwHideWindow(glfwWindow)

    val running: Boolean
        get() = glfwWindowShouldClose(glfwWindow) == 0

    private fun enableGLDebugOutput() {
        glEnable(GL_DEBUG_OUTPUT.toUInt())
        glDebugMessageCallback!!(staticCFunction(::debugCallback), null)
        disableGLNotifications()
    }

    private fun disableGLNotifications() {
        glDebugMessageControl!!(GL_DEBUG_SOURCE_API.toUInt(), GL_DEBUG_TYPE_OTHER.toUInt(), GL_DEBUG_SEVERITY_NOTIFICATION.toUInt(), 0, null, GL_FALSE.toUByte())
    }
}