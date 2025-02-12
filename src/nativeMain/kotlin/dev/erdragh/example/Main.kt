package dev.erdragh.example

import dev.erdragh.kngl.Shader
import dev.erdragh.kngl.context.Context
import dev.erdragh.kngl.util.Quad
import glew.GL_COLOR_BUFFER_BIT
import glew.GL_DEPTH_BUFFER_BIT
import glew.glClear
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) = Context.initialized {
    Shader("draw", "shaders/vertex.glsl".toPath(), "shaders/fragment.glsl".toPath())
    while (running) {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())
        val drawShader = Shader.find("draw") ?: throw RuntimeException("Draw shader not found")
        drawShader.bind()
        Quad.draw()
        drawShader.unbind()
        swapBuffers()
    }
}