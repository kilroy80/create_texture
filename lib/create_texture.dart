
import 'dart:typed_data';

import 'create_texture_platform_interface.dart';

class CreateTexture {
}

class OpenGLTextureController {

  int textureId = CreateTexturePlatform.instance.textureId ?? 0;

  Future<int> initialize(double width, double height) async {
    textureId = await CreateTexturePlatform.instance.initialize(width, height);
    return textureId;
  }

  Future<void> updateTexture(Uint8List data, int width, int height) async {
    return CreateTexturePlatform.instance.updateTexture(data, width, height);
  }

  Future<void> updateTextureYUV(
      List<Uint8List> data, int width, int height, List<int> strides) async {
    return CreateTexturePlatform.instance.updateTextureYUV(
        data, width, height, strides);
  }

  Future<void> dispose() async {
    return CreateTexturePlatform.instance.dispose();
  }

  bool get isInitialized => CreateTexturePlatform.instance.isInitialized;
}