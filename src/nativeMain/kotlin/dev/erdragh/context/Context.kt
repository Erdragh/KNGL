package dev.erdragh.context

import cnames.structs.GLFWwindow
import dev.erdragh.GL
import dev.erdragh.debug.Quad
import glew.*
import glfw.*
import kotlinx.cinterop.*
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
private fun keyCallback(window: CPointer<GLFWwindow>?, key: Int, scandcode: Int, action: Int, mods: Int) {
    if (key == GLFW_KEY_ESCAPE)
        glfwSetWindowShouldClose(window, 1)
}
@OptIn(ExperimentalForeignApi::class)
private fun mouseCallback(window: CPointer<GLFWwindow>?, x: Double, y: Double) {
}
@OptIn(ExperimentalForeignApi::class)
private fun mouseButtonCallback(window: CPointer<GLFWwindow>?, button: Int, action: Int, mods: Int) {
}
@OptIn(ExperimentalForeignApi::class)
private fun mouseScrollCallback(window: CPointer<GLFWwindow>?, x: Double, y: Double) {
}
@OptIn(ExperimentalForeignApi::class)
private fun resizeCallback(window: CPointer<GLFWwindow>?, w: Int, h: Int) {
    glViewport(0, 0, w, h)
}
@OptIn(ExperimentalForeignApi::class)
private fun charCallback(window: CPointer<GLFWwindow>?, c: UInt) {
}

@OptIn(ExperimentalForeignApi::class)
fun debugCallback(source: GLenum, type: GLenum, id: GLuint, severity: GLenum, length: GLsizei, message: CPointer<GLcharVar>?, userProgram: COpaquePointer?) {
    val src = when (source.toInt()) {
        GL_DEBUG_SOURCE_API -> "API"
        GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW_SYSTEM"
        GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER_COMPILER"
        GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD_PARTY"
        GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION"
        GL_DEBUG_SOURCE_OTHER -> "OTHER"
        else -> "UNKNOWN"
    }

    val typ = when (type.toInt()) {
        GL_DEBUG_TYPE_ERROR -> "ERROR"
        GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED_BEHAVIOR"
        GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED_BEHAVIOR"
        GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY"
        GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE"
        GL_DEBUG_TYPE_OTHER -> "OTHER"
        GL_DEBUG_TYPE_MARKER -> "MARKER"
        GL_DEBUG_TYPE_PUSH_GROUP -> "PUSH_GROUP"
        GL_DEBUG_TYPE_POP_GROUP -> "POP_GROUP"
        else -> "UNKNOWN"
    }

    val sev = when (severity.toInt()) {
        GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION"
        GL_DEBUG_SEVERITY_LOW -> "LOW"
        GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM"
        GL_DEBUG_SEVERITY_HIGH -> "HIGH"
        else -> "UNKNOWN"
    }

    memScoped {
        platform.posix.fprintf(stderr, "GL_DEBUG: Severity: %s, Source: %s, Type: %s.\nMessage: %s\n", sev.cstr.ptr, src.cstr.ptr, typ.cstr.ptr, message)
    }
}

@OptIn(ExperimentalForeignApi::class)
object Context {
    fun <T> initialized(action: Context.() -> T): T {
        init()
        val ret = action()
        uninit()
        return ret
    }

    var glfwWindow: CPointer<GLFWwindow>? = null

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