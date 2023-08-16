
import 'create_texture_platform_interface.dart';

class CreateTexture {
}

class OpenGLTextureController {

  int textureId = CreateTexturePlatform.instance.textureId ?? 0;

  Future<int> initialize(double width, double height) async {
    return CreateTexturePlatform.instance.initialize(width, height);
  }

  Future<void> draw() async {
    return CreateTexturePlatform.instance.draw();
  }

  Future<void> dispose() async {
    return CreateTexturePlatform.instance.dispose();
  }

  bool get isInitialized => CreateTexturePlatform.instance.isInitialized;
}