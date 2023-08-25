package com.example.create_texture

import android.graphics.SurfaceTexture
import android.util.Log
import android.util.LongSparseArray
import androidx.annotation.NonNull
import androidx.collection.ArrayMap
import com.example.create_texture.open.CreateRenderer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.view.TextureRegistry

/** CreateTexturePlugin */
class CreateTexturePlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private lateinit var textures: TextureRegistry
  private var surfaceTextures: ArrayMap<Long, SurfaceTexture> = ArrayMap()
  private val openRenders: LongSparseArray<CreateRenderer> = LongSparseArray()

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "create_texture")
    channel.setMethodCallHandler(this)

    textures = flutterPluginBinding.textureRegistry
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
//    val arguments = call.arguments as Map<String, Number>
//    Log.d("create_texture", call.method + " " + call.arguments.toString())

    if (call.method.equals("create")) {

      val entry: TextureRegistry.SurfaceTextureEntry = textures.createSurfaceTexture()
      val surfaceTexture: SurfaceTexture = entry.surfaceTexture()

      val type: Int = call.argument("type") ?: 0
      val width: Double = call.argument("width") ?: 0.0
      val height: Double = call.argument("height") ?: 0.0

      val textureId = entry.id()

      surfaceTexture.setDefaultBufferSize(width.toInt(), height.toInt())

      val render = CreateRenderer(surfaceTexture, textureId.toInt(), type, width, height)
      openRenders.put(entry.id(), render)
      result.success(entry.id())

    } else if (call.method.equals("updateTexture")) {

      val textureId: Long = (call.argument("textureId") ?: 0).toLong()
      val data: ByteArray = call.argument("data")!!
      val width: Int = call.argument("width") ?: 0
      val height: Int = call.argument("height") ?: 0

      this.openRenders[textureId]?.updateTexture(data, width, height)

//      val surfaceTexture: SurfaceTexture? = surfaceTextures[textureId]
//      if (surfaceTexture != null) {
//        val worker = SampleRenderWorker(image)
//        val render = OpenGLRenderer(surfaceTexture, worker)
//        renders.put(textureId, render)

        result.success(null)
//      }

    } else if (call.method.equals("updateTextureByList")) {

      val textureId: Long = (call.argument("textureId") ?: 0).toLong()
      val data: List<ByteArray> = call.argument("data")!!
      val width: Int = call.argument("width") ?: 0
      val height: Int = call.argument("height") ?: 0
      val strides: IntArray = call.argument("strides") ?: IntArray(0)

      this.openRenders[textureId]?.updateTextureByList(data, width, height, strides)

      result.success(null)

    } else if (call.method.equals("dispose")) {

      val textureId: Long = (call.argument("textureId") ?: 0).toLong()
      this.openRenders[textureId]?.onDispose()
      openRenders.remove(textureId)

      result.success(null)

    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
