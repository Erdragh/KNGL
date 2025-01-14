package dev.erdragh

import dev.erdragh.context.Context
import glew.GL_COLOR_BUFFER_BIT
import glew.GL_DEPTH_BUFFER_BIT
import glew.glClear
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    Context.init()
    
    while (Context.running) {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT).toUInt())
        Context.swapBuffers()
    }

    Context.uninit()
}