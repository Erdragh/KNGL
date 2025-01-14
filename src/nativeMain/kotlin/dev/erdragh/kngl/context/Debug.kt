package dev.erdragh.kngl.context

import glew.*
import kotlinx.cinterop.*
import platform.posix.stderr

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