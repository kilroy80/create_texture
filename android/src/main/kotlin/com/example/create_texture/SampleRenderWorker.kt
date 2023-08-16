package com.example.create_texture

import android.opengl.GLES20
import kotlin.math.sin

class SampleRenderWorker : OpenGLRenderer.Worker {

    private var _tick = 0.0

    override fun onCreate() {}

    override fun onDraw(): Boolean {
        _tick += Math.PI / 60
        val green = ((sin(_tick) + 1) / 2).toFloat()
        GLES20.glClearColor(0f, green, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        return true
    }

    override fun onDispose() {}
}