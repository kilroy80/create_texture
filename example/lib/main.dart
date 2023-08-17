import 'dart:async';

import 'package:create_texture/create_texture.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _controller = OpenGLTextureController();
  final _width = 300.0;
  final _height = 300.0;

  @override
  initState() {
    super.initState();

    initializeController();
  }

  @override
  void dispose() {
    _controller.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('OpenGL via Texture widget example'),
        ),
        body: Center(
          child: SizedBox(
            width: _width,
            height: _height,
            child: _controller.isInitialized
                ? Texture(textureId: _controller.textureId)
                : null,
          ),
        ),
      ),
    );
  }

  Future<void> initializeController() async {

    final ByteData bytes = await rootBundle.load('assets/images/img_avatar1.png');
    var buffers = bytes.buffer.asUint8List();

    var result = await _controller.initialize(_width, _height);
    // while (true) {
    //   _controller.draw(buffers);
    // }

    setState(() {});
  }
}