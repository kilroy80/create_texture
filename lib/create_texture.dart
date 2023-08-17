
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

  Future<void> draw(List<Uint8List> buffers) async {
    return CreateTexturePlatform.instance.draw(buffers);
  }

  Future<void> dispose() async {
    return CreateTexturePlatform.instance.dispose();
  }

  bool get isInitialized => CreateTexturePlatform.instance.isInitialized;
}