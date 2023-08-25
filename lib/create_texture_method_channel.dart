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
  Future<int> initialize(int type, double width, double height) async {
    textureId = await methodChannel.invokeMethod('create', {
      'type': type,
      'width': width,
      'height': height,
    });
    return textureId!;
  }

  @override
  Future<void> updateTexture(Uint8List data, int width, int height, [List<int>? strides]) async {
    await methodChannel.invokeMethod('updateTexture', {
      'textureId': textureId,
      'data': data,
      'width': width,
      'height': height,
      'strides': strides
    });
  }

  @override
  Future<void> updateTextureByList(
      List<Uint8List> data, int width, int height, List<int> strides) async {
    await methodChannel.invokeMethod('updateTextureByList', {
      'textureId': textureId,
      'data': data,
      'width': width,
      'height': height,
      'strides': strides
    });
  }

  @override
  Future<void> dispose() =>
      methodChannel.invokeMethod('dispose', {'textureId': textureId});

  @override
  bool get isInitialized => textureId != null;
}
