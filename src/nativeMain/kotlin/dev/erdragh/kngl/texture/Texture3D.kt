package dev.erdragh.kngl.texture

import dev.erdragh.kngl.GL
import dev.erdragh.kngl.Named
import glew.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.invoke
import okio.Path

@OptIn(ExperimentalForeignApi::class)
class Texture3D internal constructor(val name: String, var width: UInt, var height: UInt, var depth: UInt, val internalFormat: GLint, val format: GLenum, val type: GLenum) {
    private var id: GLuint = 0u

    constructor(name: String, width: UInt, height: UInt, depth: UInt, internalFormat: GLint, format: GLenum, type: GLenum, data: CPointer<*>?, mipmap: Boolean = false) : this(name, width, height, depth, internalFormat, format, type) {
        id = GL.genTextures(1)
        glBindTexture(GL_TEXTURE_3D.toUInt(), id)
        glTexParameteri(GL_TEXTURE_3D.toUInt(), GL_TEXTURE_WRAP_S.toUInt(), GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_3D.toUInt(), GL_TEXTURE_WRAP_T.toUInt(), GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_3D.toUInt(), GL_TEXTURE_WRAP_R.toUInt(), GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_3D.toUInt(), GL_TEXTURE_MAG_FILTER.toUInt(), GL_LINEAR)
        glTexParameteri(GL_TEXTURE_3D.toUInt(), GL_TEXTURE_MIN_FILTER.toUInt(), if (format.toInt() == GL_DEPTH_COMPONENT || format.toInt() == GL_DEPTH_STENCIL) GL_NEAREST else GL_LINEAR)

        glTexImage3D!!(GL_TEXTURE_3D.toUInt(), 0, internalFormat, width.toInt(), height.toInt(), depth.toInt(), 0, format, type, data)

        if (mipmap && data != null) glGenerateMipmap!!(GL_TEXTURE_3D.toUInt())

        glBindTexture(GL_TEXTURE_3D.toUInt(), 0u)

        registerNamed(name, this)
    }

    fun cleanup() {
        if (glIsTexture(id) == GL_TRUE.toUByte())
            GL.deleteTextures(1, id)
    }

    companion object : Named<Texture3D>()

    fun resize(w: UInt, h: UInt, d: UInt) {
        width = w
        height = h
        depth = d
        glBindTexture(GL_TEXTURE_3D.toUInt(), id)
        glTexImage3D!!(GL_TEXTURE_3D.toUInt(), 0, internalFormat, width.toInt(), height.toInt(), depth.toInt(), 0, format, type, null);
        glBindTexture(GL_TEXTURE_3D.toUInt(), 0u)
    }

    fun bind(unit: UInt) {
        glActiveTexture!!(GL_TEXTURE0.toUInt() + unit)
        glBindTexture(GL_TEXTURE_3D.toUInt(), id)
    }
    fun unbind() {
        glBindTexture(GL_TEXTURE_3D.toUInt(), 0u)
    }

    fun bindImage(unit: UInt, access: GLenum, format: GLenum) {
        glBindImageTexture!!(unit, id, 0, GL_FALSE.toUByte(), 0, access, format)
    }
    fun unbindImage(unit: UInt) {
        glBindImageTexture!!(unit, 0u, 0, GL_FALSE.toUByte(), 0, GL_READ_ONLY.toUInt(), GL_RGBA8.toUInt())
    }
}