package dev.erdragh

import glew.*
import kotlinx.cinterop.*
import okio.BufferedSource
import okio.FileSystem
import okio.Path

@OptIn(ExperimentalForeignApi::class)
object Shaders {
    private fun printLog(id: UInt) = memScoped {
        val logLength = GL.getShaderiv(id, GL_INFO_LOG_LENGTH.toUInt())
        val log = allocArray<ByteVar>(logLength + 1)
        glGetShaderInfoLog!!(id, logLength, null, log)

        val string = log.toKString()
        println(string)
    }
    private fun printProgramLog(id: UInt) = memScoped {
        val logLength = GL.getProgramiv(id, GL_INFO_LOG_LENGTH.toUInt())
        val log = allocArray<ByteVar>(logLength + 1)
        glGetProgramInfoLog!!(id, logLength, null, log)

        val string = log.toKString()
        println(string)
    }

    fun load(vertexPath: Path, fragmentPath: Path): GLuint {
        val vertexId = glCreateShader!!(GL_VERTEX_SHADER.toUInt())
        val fragmentId = glCreateShader!!(GL_FRAGMENT_SHADER.toUInt())

        val vertexSource = FileSystem.SYSTEM.read(vertexPath, BufferedSource::readByteString)
        val fragmentSource = FileSystem.SYSTEM.read(fragmentPath, BufferedSource::readByteString)

        println("Compiling shader from $vertexPath")
        GL.shaderSource(vertexId, 1, vertexSource)
        glCompileShader!!(vertexId)
        var result = GL.getShaderiv(vertexId, GL_COMPILE_STATUS.toUInt())
        printLog(vertexId)
        if (result == GL_FALSE) throw IllegalStateException("Shader Compile Failed")

        println("Compiling shader from $fragmentPath")
        GL.shaderSource(fragmentId, 1, fragmentSource)
        glCompileShader!!(fragmentId)
        result = GL.getShaderiv(fragmentId, GL_COMPILE_STATUS.toUInt())
        printLog(fragmentId)
        if (result == GL_FALSE) throw IllegalStateException("Shader Compile Failed")

        println("Linking program")
        val programId = glCreateProgram!!()
        glAttachShader!!(programId, vertexId)
        glAttachShader!!(programId, fragmentId)
        glLinkProgram!!(programId)

        result = GL.getProgramiv(programId, GL_LINK_STATUS.toUInt())
        printProgramLog(programId)
        if (result == GL_FALSE) throw IllegalStateException("Program Linking Failed")

        glDetachShader!!(programId, vertexId)
        glDetachShader!!(programId, fragmentId)
        glDeleteShader!!(vertexId)
        glDeleteShader!!(fragmentId)

        return programId
    }
}