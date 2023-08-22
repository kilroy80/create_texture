package com.example.create_texture.open

import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.max
import kotlin.math.min

class YUVRenderWorker(private val textureId: Int) : CreateRenderer.Worker {

    private val tag = "YUVRenderWorker"

    private val VERTEX_SHADER_STRING =
        "varying vec2 interp_tc;\n" +
        "attribute vec4 in_pos;\n" +
        "attribute vec2 in_tc;\n" +
        "\n" +
        "void main() {\n" +
        "  gl_Position = in_pos;\n" +
        "  interp_tc = in_tc;\n" +
        "}\n"

    private val YUV_FRAGMENT_SHADER_STRING =
        "precision mediump float;\n" +
        "varying vec2 interp_tc;\n" +
        "\n" +
        "uniform sampler2D y_tex;\n" +
        "uniform sampler2D u_tex;\n" +
        "uniform sampler2D v_tex;\n" +
        "\n" +
        "void main() {\n" +
        // CSC according to http://www.fourcc.org/fccyvrgb.php
        "  float y = texture2D(y_tex, interp_tc).r;\n" +
        "  float u = texture2D(u_tex, interp_tc).r - 0.5;\n" +
        "  float v = texture2D(v_tex, interp_tc).r - 0.5;\n" +
        "  gl_FragColor = vec4(y + 1.403 * v, " +
        "                      y - 0.344 * u - 0.714 * v, " +
        "                      y + 1.77 * u, 1);\n" +
        "}\n"

    private val OES_FRAGMENT_SHADER_STRING =
        "#extension GL_OES_EGL_image_external : require\n" +
        "precision mediump float;\n" +
        "varying vec2 interp_tc;\n" +
        "\n" +
        "uniform samplerExternalOES oes_tex;\n" +
        "\n" +
        "void main() {\n" +
        "  gl_FragColor = texture2D(oes_tex, interp_tc);\n" +
        "}\n"

    private var yuvProgram = 0
    private var oesProgram = 0
    private val yuvTextures = intArrayOf(-1, -1, -1)

    // Texture vertices.
    private var texLeft = 0f
    private var texRight = 0f
    private var texTop = 0f
    private var texBottom = 0f
    private var textureVertices: FloatBuffer? = null

    // Texture UV coordinates.
    private var textureCoords: FloatBuffer? = null


    override fun onCreate() {

        texLeft = (0 - 50) / 50.0f  // -1.0f
        texTop = (50 - 0) / 50.0f   // 1.0f
        texRight = min(1.0f, (0 + 720 - 50) / 50.0f)    // 1.0f
        texBottom = max(-1.0f, (50 - 0 - 480) / 50.0f)  // -1.0f
        val textureVeticesFloat = floatArrayOf(
            texLeft, texTop,
            texLeft, texBottom,
            texRight, texTop,
            texRight, texBottom
        )
        textureVertices = directNativeFloatBuffer(textureVeticesFloat)

        val textureCoordinatesFloat = floatArrayOf(
            0f, 0f,     // left top
            0f, 1f,     // left bottom
            1f, 0f,     // right top
            1f, 1f      // right bottom
        )
        textureCoords = directNativeFloatBuffer(textureCoordinatesFloat)

        yuvProgram = createProgram(VERTEX_SHADER_STRING, YUV_FRAGMENT_SHADER_STRING)
        createTextures(yuvProgram)

        GLES20.glClearColor(0.15f, 0.15f, 0.15f, 1.0f)

//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
//            720, 480, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, null)
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
    }

    override fun updateTextureYUV(
        byteArray: List<ByteArray>,
        width: Int,
        height: Int,
        strides: IntArray
    ): Boolean {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(yuvProgram)

//        glTexSubImage2D

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // Unbind the texture as a precaution.
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())

        for (i in 0..2) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[i])
            if (byteArray != null) {
                val w: Int = if (i == 0) width else width / 2
                val h: Int = if (i == 0) height else height / 2

                val buffer = ByteBuffer.allocateDirect(strides[i] * height)
//                var buffer = ByteBuffer.allocateDirect(byteArray[i].size)
                buffer.put(byteArray[i])
                buffer.position(0)

                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
                    w, h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    buffer
                )
            }
        }

        GLES20.glUniform1i(GLES20.glGetUniformLocation(yuvProgram, "y_tex"), 0)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(yuvProgram, "u_tex"), 1)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(yuvProgram, "v_tex"), 2)

        val posLocation = GLES20.glGetAttribLocation(yuvProgram, "in_pos")
        if (posLocation == -1) {
            throw Exception("Could not get attrib location for in_pos")
        }
        GLES20.glEnableVertexAttribArray(posLocation)
        GLES20.glVertexAttribPointer(
            posLocation, 2, GLES20.GL_FLOAT, false, 0, textureVertices
        )
        val texLocation = GLES20.glGetAttribLocation(yuvProgram, "in_tc")
        if (texLocation == -1) {
            throw Exception("Could not get attrib location for in_tc")
        }
        GLES20.glEnableVertexAttribArray(texLocation)
        GLES20.glVertexAttribPointer(
            texLocation, 2, GLES20.GL_FLOAT, false, 0, textureCoords
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

//        GLES20.glDisableVertexAttribArray(posLocation)
//        GLES20.glDisableVertexAttribArray(texLocation)

        checkNoGLES2Error()

        return true
    }

    override fun updateTexture(data: ByteArray, width: Int, height: Int): Boolean {
        return false
    }

    override fun onDispose() {}


    private fun createTextures(yuvProgram: Int) {
        this.yuvProgram = yuvProgram
        // Generate 3 texture ids for Y/U/V and place them into |yuvTextures|.
        GLES20.glGenTextures(3, yuvTextures, 0)
        for (i in 0..2) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[i])
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
                128, 128, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, null
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
        }
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

    private fun directNativeFloatBuffer(array: FloatArray): FloatBuffer? {
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