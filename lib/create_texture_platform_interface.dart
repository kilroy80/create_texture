import 'dart:typed_data';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'create_texture_method_channel.dart';

abstract class CreateTexturePlatform extends PlatformInterface {
  /// Constructs a CreateTexturePlatform.
  CreateTexturePlatform() : super(token: _token);

  static final Object _token = Object();

  static CreateTexturePlatform _instance = MethodChannelCreateTexture();

  /// The default instance of [CreateTexturePlatform] to use.
  ///
  /// Defaults to [MethodChannelCreateTexture].
  static CreateTexturePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CreateTexturePlatform] when
  /// they register themselves.
  static set instance(CreateTexturePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  int? textureId;

  Future<int> initialize(double width, double height) async {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  Future<void> draw(Uint8List buffers) async {
    throw UnimplementedError('draw() has not been implemented.');
  }

  Future<void> dispose() async {
    throw UnimplementedError('dispose() has not been implemented.');
  }

  bool get isInitialized =>
      throw UnimplementedError('isInitialized has not been implemented.');
}
