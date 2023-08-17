package com.example.create_texture.open

import android.R.attr.rotation
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLUtils
import android.renderscript.Allocation
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class SampleRenderWorker : CreateRenderer.Worker {

//    constructor(bytes: ByteArray) {
//        byteArray = bytes
//    }

    private var _tick = 0.0

//    private var byteArray: ByteArray

    private lateinit var verticesBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var hProgram = 0

    private val vss = "attribute vec2 vPosition;\n" +
            "attribute vec2 vTexCoord;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  texCoord = vTexCoord;\n" +
            "  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );\n" +
            "}"

    private val fss = "precision mediump float;\n" +
            "uniform sampler2D sTexture;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture,texCoord);\n" +
            "}"

    override fun onCreate() {
//        val vertices = floatArrayOf(1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f)
        val vertices = floatArrayOf(1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f)
//        val textureVertices = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)
        val textureVertices = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)

        verticesBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        verticesBuffer.put(vertices)
        verticesBuffer.position(0)

        textureBuffer = ByteBuffer.allocateDirect(textureVertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer.put(textureVertices)
        textureBuffer.position(0)

        hProgram = loadShader(vss, fss)

//        GLES20.glClearColor(0f, 0f, 0f, 1f)

//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//
//        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Color.GRAY)
//
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    }

    override fun onDraw(byteArray: List<ByteArray>): Boolean {
//        _tick += Math.PI / 60
//        val green = ((sin(_tick) + 1) / 2).toFloat()
//        GLES20.glClearColor(0f, green, 0f, 1f)
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_STENCIL_BUFFER_BIT)

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val id = 0

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glUseProgram(hProgram)

        val ph = GLES20.glGetAttribLocation(hProgram, "vPosition")
        val tch = GLES20.glGetAttribLocation(hProgram, "vTexCoord")
        val th = GLES20.glGetUniformLocation(hProgram, "sTexture")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        GLES20.glUniform1i(th, 0)

        GLES20.glVertexAttribPointer(ph, 2, GLES20.GL_FLOAT, false, 4 * 2, verticesBuffer)
        GLES20.glVertexAttribPointer(tch, 2, GLES20.GL_FLOAT, false, 4 * 2, textureBuffer)
        GLES20.glEnableVertexAttribArray(ph)
        GLES20.glEnableVertexAttribArray(tch)

        // draw
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

//        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
//        bitmap.eraseColor(Color.RED)

        val bitmap = BitmapFactory.decodeByteArray( byteArray[0], 0, byteArray[0].size )

//        val bitmap = convert(byteArray)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFlush()

        return true
    }

    override fun onDispose() {}

    private fun loadShader(vss: String, fss: String): Int {
        var vshader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vshader, vss)
        GLES20.glCompileShader(vshader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(vshader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(vshader)
            vshader = 0
        }

        var fshader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fshader, fss)
        GLES20.glCompileShader(fshader)
        GLES20.glGetShaderiv(fshader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(fshader)
            fshader = 0
        }

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vshader)
        GLES20.glAttachShader(program, fshader)
        GLES20.glLinkProgram(program)

        return program
    }

    fun convert(bytesList: List<ByteArray>): Bitmap {
        val Y = ByteBuffer.wrap(bytesList[0])
        val U = ByteBuffer.wrap(bytesList[1])
        val V = ByteBuffer.wrap(bytesList[2])

        val Yb = Y.remaining()
        val Ub = U.remaining()
        val Vb = V.remaining()

        val data = ByteArray(Yb + Ub + Vb)

        Y[data, 0, Yb]
        V[data, Yb, Vb]
        U[data, Yb + Vb, Ub]

        val bitmapRaw = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        bitmapRaw.eraseColor(Color.RED)

        return bitmapRaw

//        val bitmapRaw = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
//        val bmData: Allocation = renderScriptNV21ToRGBA888(
//            mRegistrar.context(),
//            imageWidth,
//            imageHeight,
//            data
//        )
//        bmData.copyTo(bitmapRaw)
//
//        val matrix = Matrix()
//        matrix.postRotate(rotation)
//        val finalbitmapRaw =
//            Bitmap.createBitmap(bitmapRaw, 0, 0, bitmapRaw.width, bitmapRaw.height, matrix, true)
    }
}