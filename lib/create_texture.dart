
import 'dart:typed_data';

import 'create_texture_platform_interface.dart';

class CreateTexture {
}

enum TextureType {
  rgb, yuv,
}

class OpenGLTextureController {

  int textureId = CreateTexturePlatform.instance.textureId ?? 0;

  Future<int> initialize(TextureType type, double width, double height) async {
    textureId = await CreateTexturePlatform.instance.initialize(type.index, width, height);
    return textureId;
  }

  Future<void> updateTexture(Uint8List data, int width, int height) async {
    return CreateTexturePlatform.instance.updateTexture(data, width, height);
  }

  Future<void> updateTextureByList(
      List<Uint8List> data, int width, int height, List<int> strides) async {
    return CreateTexturePlatform.instance.updateTextureByList(
        data, width, height, strides);
  }

  Future<void> dispose() async {
    return CreateTexturePlatform.instance.dispose(textureId);
  }

  bool get isInitialized => CreateTexturePlatform.instance.isInitialized;
}