package com.example.create_texture.worker

import android.opengl.GLES20
import android.util.Log
import com.example.create_texture.CreateRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class RGBRenderWorker(private val textureId: Int) : CreateRenderer.Worker {

    private val tag = "RGBRenderWorker"

    private lateinit var verticesBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private var rgbProgram = 0

    private val VERTEX_SHADER_STRING =
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vTexCoord;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  texCoord = vTexCoord;\n" +
            "  gl_Position = vPosition;\n" +
            "}"

    private val RGB_FRAGMENT_SHADER_STRING =
            "precision mediump float;\n" +
            "uniform sampler2D sTexture;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture, texCoord);\n" +
            "}"

    override fun onCreate() {
//        val vertices = floatArrayOf(1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f)
////        val vertices = floatArrayOf(-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f)
//        val textureVertices = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)
////        val textureVertices = floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f)

        val vertices = floatArrayOf(
            -1.0f, 1.0f,    // left top
            -1.0f, -1.0f,   // left bottom
            1.0f, 1.0f,     // right top
            1.0f, -1.0f     // right bottom
        )
        verticesBuffer = directNativeFloatBuffer(vertices)

        val textureVertices = floatArrayOf(
            1.0f, 0.0f,     // left top
            0.0f, 0.0f,     // left bottom
            1.0f, 1.0f,     // right top
            0.0f, 1.0f      // right bottom
        )
        textureBuffer = directNativeFloatBuffer(textureVertices)

        rgbProgram = createProgram(VERTEX_SHADER_STRING, RGB_FRAGMENT_SHADER_STRING)
        createTextures(rgbProgram)
    }

    override fun updateTexture(byteArray: ByteArray,
       width: Int,
       height: Int,
    ): Boolean {

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glUseProgram(rgbProgram)

        // texture
        GLES20.glUniform1i(GLES20.glGetUniformLocation(rgbProgram, "sTexture"), 0)

        val buffer = ByteBuffer.allocateDirect(byteArray.size)
        buffer.put(byteArray)
        buffer.position(0)

        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
            width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, buffer
        )

//        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Color.RED)

//        val bitmap = BitmapFactory.decodeByteArray( byteArray[0], 0, byteArray[0].size )
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

//        bitmap.recycle()

        // vertex
        val posLocation = GLES20.glGetAttribLocation(rgbProgram, "vPosition")
        val texLocation = GLES20.glGetAttribLocation(rgbProgram, "vTexCoord")

        GLES20.glVertexAttribPointer(
            posLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, verticesBuffer)
        GLES20.glVertexAttribPointer(
            texLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, textureBuffer)

        GLES20.glEnableVertexAttribArray(posLocation)
        GLES20.glEnableVertexAttribArray(texLocation)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(posLocation)
        GLES20.glDisableVertexAttribArray(texLocation)

        return true
    }

    override fun updateTextureByList(
        byteArray: List<ByteArray>,
        width: Int,
        height: Int,
        strides: IntArray
    ): Boolean {
        return false
    }

    override fun onDispose() {}

    private fun createTextures(program: Int) {
        this.rgbProgram = program

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        checkNoGLES2Error()
    }

    private fun abortUnless(condition: Boolean, msg: String) {
        if (!condition) {
            throw RuntimeException(msg)
        }
    }

    private fun checkNoGLES2Error() {
        val error = GLES20.glGetError()
        abortUnless(error == GLES20.GL_NO_ERROR, "GLES20 error: $error")
    }

    private fun loadShader(shaderType: Int, source: String): Int {
        val result = intArrayOf(
            GLES20.GL_FALSE
        )
        val shader = GLES20.glCreateShader(shaderType)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, result, 0)
        if (result[0] != GLES20.GL_TRUE) {
            Log.e(
                tag, "Could not compile shader " + shaderType + ":" +
                        GLES20.glGetShaderInfoLog(shader)
            )
            throw Exception(GLES20.glGetShaderInfoLog(shader))
        }
        checkNoGLES2Error()
        return shader
    }

    private fun directNativeFloatBuffer(array: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size * 4).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()
        buffer.put(array)
        buffer.flip()
        return buffer
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        val program = GLES20.glCreateProgram()
        if (program == 0) {
            throw Exception("Could not create program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        val linkStatus = intArrayOf(
            GLES20.GL_FALSE
        )
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(
                tag, "Could not link program: " +
                        GLES20.glGetProgramInfoLog(program)
            )
            throw Exception(GLES20.glGetProgramInfoLog(program))
        }
        checkNoGLES2Error()
        return program
    }
}