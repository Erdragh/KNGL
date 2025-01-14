package dev.erdragh.kngl.context

import cnames.structs.GLFWwindow
import glew.glViewport
import glfw.GLFW_KEY_ESCAPE
import glfw.glfwSetWindowShouldClose
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

private fun callbackSafe(action: () -> Unit) {
    try {
        action()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalForeignApi::class)
fun keyCallback(window: CPointer<GLFWwindow>?, key: Int, scandcode: Int, action: Int, mods: Int) = callbackSafe {
    if (key == GLFW_KEY_ESCAPE)
        glfwSetWindowShouldClose(window, 1)
    Context.userKeyCallback?.let { it(key, scandcode, action, mods) }
}
@OptIn(ExperimentalForeignApi::class)
fun mouseCallback(window: CPointer<GLFWwindow>?, x: Double, y: Double) = callbackSafe {
    Context.userMouseCallback?.let { it(x, y) }
}
@OptIn(ExperimentalForeignApi::class)
fun mouseButtonCallback(window: CPointer<GLFWwindow>?, button: Int, action: Int, mods: Int) = callbackSafe {
    Context.userMouseButtonCallback?.let { it(button, action, mods) }
}
@OptIn(ExperimentalForeignApi::class)
fun mouseScrollCallback(window: CPointer<GLFWwindow>?, x: Double, y: Double) = callbackSafe {
    Context.userMouseScrollCallback?.let { it(x, y) }
}
@OptIn(ExperimentalForeignApi::class)
fun resizeCallback(window: CPointer<GLFWwindow>?, w: Int, h: Int) = callbackSafe {
    glViewport(0, 0, w, h)
    Context.userResizeCallback?.let { it(w, h) }
}
@OptIn(ExperimentalForeignApi::class)
fun charCallback(window: CPointer<GLFWwindow>?, c: UInt) = callbackSafe {
    Context.userCharCallback?.let { it(c) }
}