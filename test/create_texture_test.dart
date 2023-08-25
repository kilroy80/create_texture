import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:create_texture/create_texture_platform_interface.dart';
import 'package:create_texture/create_texture_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCreateTexturePlatform
    with MockPlatformInterfaceMixin
    implements CreateTexturePlatform {

  @override
  Future<void> dispose() {
    // TODO: implement dispose
    throw UnimplementedError();
  }

  @override
  Future<int> initialize(int type, double width, double height) {
    // TODO: implement initialize
    throw UnimplementedError();
  }

  @override
  // TODO: implement isInitialized
  bool get isInitialized => throw UnimplementedError();

  @override
  int? textureId;

  @override
  Future<void> updateTexture(Uint8List data, int width, int height) {
    // TODO: implement updateTexture
    throw UnimplementedError();
  }

  @override
  Future<void> updateTextureByList(List<Uint8List> data, int width, int height, List<int> strides) {
    // TODO: implement updateTextureByList
    throw UnimplementedError();
  }
}

void main() {
  final CreateTexturePlatform initialPlatform = CreateTexturePlatform.instance;

  test('$MethodChannelCreateTexture is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCreateTexture>());
  });

  // test('getPlatformVersion', () async {
  //   CreateTexture createTexturePlugin = CreateTexture();
  //   MockCreateTexturePlatform fakePlatform = MockCreateTexturePlatform();
  //   CreateTexturePlatform.instance = fakePlatform;
  //
  //   expect(await createTexturePlugin.getPlatformVersion(), '42');
  // });
}
