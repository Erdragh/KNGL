package dev.erdragh.kngl

import dev.erdragh.kngl.math.Vec3i
import glew.*
import kotlinx.cinterop.*
import okio.BufferedSource
import okio.FileSystem
import okio.Path
import kotlin.math.ceil

@OptIn(ExperimentalForeignApi::class)
private fun compileShader(type: GLenum, sources: Map<GLenum, Path>): GLuint {
    val path = sources[type] ?: throw IllegalArgumentException("No shader source for type: $type")
    println("Loading: $path...")
    val source = FileSystem.SYSTEM.read(path, BufferedSource::readByteString)

    if (source.size < 1) throw RuntimeException("Empty shader source: $path")

    // TODO: Handle include

    // actually compile shader
    val shader = glCreateShader!!(type)
    GL.shaderSource(shader, 1, source)
    glCompileShader!!(shader)

    // print error msg if failed
    if (GL.getShaderiv(shader, GL_COMPILE_STATUS.toUInt()) != GL_TRUE) memScoped {
        val logLength = GL.getShaderiv(shader, GL_INFO_LOG_LENGTH.toUInt())
        val log = allocArray<ByteVar>(logLength + 1)
        glGetShaderInfoLog!!(shader, logLength, null, log)

        val string = log.toKString()
        glDeleteShader!!(shader)
        throw RuntimeException("Shader compilation failed:\n$string")
    }

    return shader
}

@OptIn(ExperimentalForeignApi::class)
class Shader(val name: String) {
    var id: GLuint = 0u
    private val sourceFiles: MutableMap<GLenum, Path> = mutableMapOf()

    constructor(name: String, compute: Path) : this(name) {
        setComputeSource(compute)
        compile()
    }
    constructor(name: String, vertex: Path, fragment: Path) : this(name) {
        setVertexSource(vertex)
        setFragmentSource(fragment)
        compile()
    }
    constructor(name: String, vertex: Path, geometry: Path, fragment: Path) : this(name) {
        setVertexSource(vertex)
        setGeometrySource(geometry)
        setFragmentSource(fragment)
        compile()
    }

    fun bind() = glUseProgram!!(id)
    fun unbind() = glUseProgram!!(0u)

    private fun setVertexSource(path: Path) = setSource(GL_VERTEX_SHADER.toUInt(), path)
    private fun setGeometrySource(path: Path) = setSource(GL_GEOMETRY_SHADER.toUInt(), path)
    private fun setFragmentSource(path: Path) = setSource(GL_FRAGMENT_SHADER.toUInt(), path)
    private fun setComputeSource(path: Path) = setSource(GL_COMPUTE_SHADER.toUInt(), path)
    private fun setSource(type: GLenum, path: Path) {
        sourceFiles[type] = path
    }

    private fun compile() {
        // compile shaders
        val program = glCreateProgram!!()
        if (sourceFiles.containsKey(GL_COMPUTE_SHADER.toUInt())) {
            // it's a compute shader
            val shader = compileShader(GL_COMPUTE_SHADER.toUInt(), sourceFiles)
            if (shader == 0u) {
                glDeleteProgram!!(program)
                return
            }
            glAttachShader!!(program, shader)
        } else {
            // is pipeline
            if (sourceFiles.containsKey(GL_VERTEX_SHADER.toUInt())) {
                val shader = compileShader(GL_VERTEX_SHADER.toUInt(), sourceFiles)
                if (shader == 0u) {
                    glDeleteProgram!!(program)
                    return
                }
                glAttachShader!!(program, shader)
            }

            if (sourceFiles.containsKey(GL_GEOMETRY_SHADER.toUInt())) {
                val shader = compileShader(GL_GEOMETRY_SHADER.toUInt(), sourceFiles)
                if (shader == 0u) {
                    glDeleteProgram!!(program)
                    return
                }
                glAttachShader!!(program, shader)
            }

            if (sourceFiles.containsKey(GL_FRAGMENT_SHADER.toUInt())) {
                val shader = compileShader(GL_FRAGMENT_SHADER.toUInt(), sourceFiles)
                if (shader == 0u) {
                    glDeleteProgram!!(program)
                    return
                }
                glAttachShader!!(program, shader)
            }
        }

        // link program
        glLinkProgram!!(program)
        if (GL.getProgramiv(program, GL_LINK_STATUS.toUInt()) != GL_TRUE) memScoped {
            val logLength = GL.getProgramiv(id, GL_INFO_LOG_LENGTH.toUInt())
            val log = allocArray<ByteVar>(logLength + 1)
            glGetProgramInfoLog!!(id, logLength, null, log)

            val string = log.toKString()
            throw RuntimeException("Failed to link program from sources:\n${sourceFiles.values.joinToString("\n")}\n\n$string")
        }

        if (glIsProgram!!(id) == GL_TRUE.toUByte()) glDeleteProgram!!(id)
        id = program
    }

    fun dispatchCompute(width: UInt, height: UInt = 1u, depth: UInt = 1u, memoryBarrierBits: GLbitfield = GL_ALL_BARRIER_BITS) {
        val size = Vec3i(0)
        size.repr.toIntArray().usePinned { pinned ->
            glGetProgramiv!!(id, GL_COMPUTE_WORK_GROUP_SIZE.toUInt(), pinned.addressOf(0))
        }
        glDispatchCompute!!(ceil(width.toFloat() / size.x).toUInt(), ceil(height.toFloat() / size.y).toUInt(), ceil(depth.toFloat() / size.z).toUInt())
        if (memoryBarrierBits != 0u)
            glMemoryBarrier!!(memoryBarrierBits)
    }

    // Uniforms
    fun uniform(name: String, value: Int) = memScoped {
        val loc = glGetUniformLocation!!(id, name.cstr.ptr)
        glUniform1i!!(loc, value)
    }
    fun uniform(name: String, value: IntArray) = memScoped {
        value.usePinned { pinned ->
            val loc = glGetUniformLocation!!(id, name.cstr.ptr)
            glUniform1iv!!(loc, value.size, pinned.addressOf(0))
        }
    }

    fun uniform(name: String, value: UInt) = memScoped {
        val loc = glGetUniformLocation!!(id, name.cstr.ptr)
        glUniform1ui!!(loc, value)
    }
    fun uniform(name: String, value: UIntArray) = memScoped {
        value.usePinned { pinned ->
            val loc = glGetUniformLocation!!(id, name.cstr.ptr)
            glUniform1uiv!!(loc, value.size, pinned.addressOf(0))
        }
    }

    fun uniform(name: String, value: Float) = memScoped {
        val loc = glGetUniformLocation!!(id, name.cstr.ptr)
        glUniform1f!!(loc, value)
    }
    fun uniform(name: String, value: FloatArray) = memScoped {
        value.usePinned { pinned ->
            val loc = glGetUniformLocation!!(id, name.cstr.ptr)
            glUniform1fv!!(loc, value.size, pinned.addressOf(0))
        }
    }

    // TODO: Vector uniforms
}