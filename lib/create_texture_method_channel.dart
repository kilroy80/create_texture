import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'create_texture_platform_interface.dart';

/// An implementation of [CreateTexturePlatform] that uses method channels.
class MethodChannelCreateTexture extends CreateTexturePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('create_texture');

  // @override
  // int? textureId;

  @override
  Future<int> initialize(double width, double height) async {
    textureId = await methodChannel.invokeMethod('create', {
      'width': width,
      'height': height,
    });
    return textureId!;
  }

  @override
  Future<void> draw(List<Uint8List> buffers) async {
    await methodChannel.invokeMethod('draw', {
      'textureId': textureId,
      'image': buffers,
    });
  }

  @override
  Future<void> dispose() =>
      methodChannel.invokeMethod('dispose', {'textureId': textureId});

  @override
  bool get isInitialized => textureId != null;
}
