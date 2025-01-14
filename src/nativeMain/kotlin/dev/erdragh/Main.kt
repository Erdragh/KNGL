package dev.erdragh

import dev.erdragh.context.Context
import glew.GL_COLOR_BUFFER_BIT
import glew.GL_DEPTH_BUFFER_BIT
import glew.glClear
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) = Context.initialized {
    val drawShader = Shader("draw", "shaders/vertex.glsl".toPath(), "shaders/fragment.glsl".toPath())
    while (running) {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())
        drawShader.bind()
        drawShader.unbind()
        swapBuffers()
    }
}