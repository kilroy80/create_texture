package com.example.create_texture.open

import android.graphics.SurfaceTexture
import android.opengl.GLUtils
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.util.concurrent.Semaphore
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class CreateRenderer(
    private val texture: SurfaceTexture,
    private val width: Double,
    private val height: Double
) {

    private lateinit var egl: EGL10
    private lateinit var eglDisplay: EGLDisplay
    private lateinit var eglContext: EGLContext
    private var eglSurface: EGLSurface? = null
    private var running = true

    private lateinit var worker: SampleRenderWorker

    var renderThread: HandlerThread? = null
    var renderHandler : Handler? = null

    companion object {
        private const val LOG_TAG = "OpenGL.Worker"
    }

    init {
        if (renderThread == null) {
            renderThread = HandlerThread("flutterGlCustomRender")
            renderThread!!.start()

            renderHandler = Handler(renderThread!!.looper)
        }
        this.executeSync {
            initGL()

            worker = SampleRenderWorker()
            worker.onCreate()

            Log.d(LOG_TAG, "OpenGL init OK.")
        }
    }

    private fun executeSync(task: () -> Unit) {
        val semaphore = Semaphore(0)
        renderHandler!!.post {
            task.invoke()
            semaphore.release()
        }
        semaphore.acquire()
    }

    private fun execute(task: () -> Unit) {
        renderHandler!!.post {
            task.invoke()
        }
    }

//    override fun run() {
//        initGL()
//        worker.onCreate()
//        Log.d(LOG_TAG, "OpenGL init OK.")
//        while (running) {
////            val loopStart = System.currentTimeMillis()
//            if (worker.onDraw()) {
//                if (!egl.eglSwapBuffers(eglDisplay, eglSurface)) {
//                    Log.d(LOG_TAG, egl.eglGetError().toString())
//                }
//            }
////            val waitDelta = 16 - (System.currentTimeMillis() - loopStart)
////            if (waitDelta > 0) {
////                try {
////                    Thread.sleep(waitDelta)
////                } catch (e: InterruptedException) {
////                }
////            }
//        }
////        worker.onDispose()
////        deInitGL()
//    }

    fun draw(byteArray: List<ByteArray>): Boolean {
        this.execute {
            if (worker.onDraw(byteArray)) {
                if (!egl.eglSwapBuffers(eglDisplay, eglSurface)) {
                    Log.d(LOG_TAG, egl.eglGetError().toString())
                }
            }

//            Log.d(LOG_TAG, "OpenGL draw")
        }

        return true
    }

    private fun initGL() {
        egl = EGLContext.getEGL() as EGL10
        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        if (eglDisplay === EGL10.EGL_NO_DISPLAY) {
            throw RuntimeException("eglGetDisplay failed")
        }
        val version = IntArray(2)
        if (!egl.eglInitialize(eglDisplay, version)) {
            throw RuntimeException("eglInitialize failed")
        }
        val eglConfig = chooseEglConfig()
        eglContext = createContext(egl, eglDisplay, eglConfig)
        eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, texture, null)
        if (eglSurface == null || eglSurface === EGL10.EGL_NO_SURFACE) {
            throw RuntimeException(
                "GL Error: " + GLUtils.getEGLErrorString(
                    egl.eglGetError()
                )
            )
        }
        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw RuntimeException(
                "GL make current error: " + GLUtils.getEGLErrorString(
                    egl.eglGetError()
                )
            )
        }
    }

    private fun deInitGL() {
        egl.eglMakeCurrent(
            eglDisplay,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )
        egl.eglDestroySurface(eglDisplay, eglSurface)
        egl.eglDestroyContext(eglDisplay, eglContext)
        egl.eglTerminate(eglDisplay)
        Log.d(LOG_TAG, "OpenGL deInit OK.")
    }

    private fun createContext(
        egl: EGL10,
        eglDisplay: EGLDisplay,
        eglConfig: EGLConfig?
    ): EGLContext {
        val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        val attribList = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attribList)
    }

    private fun chooseEglConfig(): EGLConfig? {
        val configsCount = IntArray(1)
        val configs = arrayOfNulls<EGLConfig>(1)
        val configSpec = config
        require(egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, configsCount)) {
            "Failed to choose config: " + GLUtils.getEGLErrorString(
                egl.eglGetError()
            )
        }
        return if (configsCount[0] > 0) {
            configs[0]
        } else null
    }

    private val config: IntArray
        private get() = intArrayOf(
            EGL10.EGL_RENDERABLE_TYPE, 4,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_SAMPLE_BUFFERS, 1,
            EGL10.EGL_SAMPLES, 4,
            EGL10.EGL_NONE
        )

    @Throws(Throwable::class)
    protected fun finalize() {
        running = false
    }

    fun onDispose() {
        running = false

        worker.onDispose()
        deInitGL()
    }

    interface Worker {
        fun onCreate()
        fun onDraw(byteArray: List<ByteArray>): Boolean
        fun onDispose()
    }
}