package com.example.create_texture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLES32.*
import android.opengl.GLUtils
import android.util.Log
import java.nio.FloatBuffer

class RenderWorker {

    private lateinit var vertexBuffer4FBO: FloatBuffer
    private lateinit var textureBuffer4FBO: FloatBuffer

    private var program: Int = 0

    private val openGLProgram: OpenGLProgram = OpenGLProgram()

    fun setup() {
        setupVBO4FBO()
        program = openGLProgram.getProgram()
    }

    fun draw(byteArray: ByteArray) {

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glUseProgram(program)

        val ph = GLES20.glGetAttribLocation(program, "vPosition")
        val tch = GLES20.glGetAttribLocation(program, "vTexCoord")
        val th = GLES20.glGetUniformLocation(program, "sTexture")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUniform1i(th, 0)

        GLES20.glVertexAttribPointer(ph, 2, GLES20.GL_FLOAT, false, 4 * 2, vertexBuffer4FBO)
        GLES20.glVertexAttribPointer(tch, 2, GLES20.GL_FLOAT, false, 4 * 2, textureBuffer4FBO)
        GLES20.glEnableVertexAttribArray(ph)
        GLES20.glEnableVertexAttribArray(tch)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
//        GLES20.glFlush()

        // draw
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

//        val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Color.RED)

        val bitmap = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.size )

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()
    }

    fun renderTexture(texture: Int, matrix: FloatArray?) {
        drawTexture(texture, vertexBuffer4FBO, textureBuffer4FBO, matrix)
    }

    fun drawTexture(
        texture: Int,
        vertexBuffer: FloatBuffer,
        textureBuffer: FloatBuffer,
        matrix: FloatArray?
    ) {
        val program = getProgram()
        glUseProgram(program)

        glActiveTexture(GL_TEXTURE10)
        glBindTexture(GL_TEXTURE_2D, texture)
        glUniform1i(glGetUniformLocation(program, "Texture0"), 10)

        var resultMatrix = floatArrayOf(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )

        if (matrix != null) {
            resultMatrix = matrix
        }
        val matrixUniform = glGetUniformLocation(program, "matrix")
        glUniformMatrix4fv(matrixUniform, 1, false, resultMatrix, 0)

        val positionSlot = 0
        val textureSlot = 1

        glEnableVertexAttribArray(positionSlot)
        glEnableVertexAttribArray(textureSlot)

        vertexBuffer.position(0)
        glVertexAttribPointer(positionSlot, 3, GL_FLOAT, false, 0, vertexBuffer)

        textureBuffer.position(0)
        glVertexAttribPointer(textureSlot, 2, GL_FLOAT, false, 0, textureBuffer)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun getProgram(): Int {
        if (program == null) {
            program = openGLProgram.getProgram()
        }
        return program as Int
    }

    private fun setupVBO4FBO() {
        val w = 1.0f
        val h = 1.0f

//        val verticesPoints = floatArrayOf(-w, -h, 0.0f, w, -h, 0.0f, -w, h, 0.0f, w, h, 0.0f)
        val verticesPoints = floatArrayOf(1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f)
//        val texturesPoints = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f)
        val texturesPoints = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)

        vertexBuffer4FBO = BufferUtils.createFloatBuffer(verticesPoints)
        textureBuffer4FBO = BufferUtils.createFloatBuffer(texturesPoints)
    }

    fun dispose() {
        vertexBuffer4FBO.clear()
        textureBuffer4FBO.clear()
    }

}