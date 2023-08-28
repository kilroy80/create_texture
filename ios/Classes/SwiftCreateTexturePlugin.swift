import Flutter
import UIKit

public class SwiftCreateTexturePlugin: NSObject, FlutterPlugin {

  var registry: FlutterTextureRegistry;
//  var textureId: Int64?;
//   static var messenger : FlutterBinaryMessenger? = nil;
  var renders = [Int: CreateRender]();

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "create_texture", binaryMessenger: registrar.messenger())
    let instance = SwiftCreateTexturePlugin(textureRegistry: registrar.textures())
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  init(textureRegistry: FlutterTextureRegistry) {
    self.registry = textureRegistry;
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)
    case "create":
      guard let args = call.arguments as? [String : Any] else {
        result("error arguments")
        return
      }
        
      var textureId: Int64?

      let type = args["type"] as? Int
      let width = args["width"] as? Double
      let height = args["height"] as? Double

      let render = CreateRender(
        size: CGSize(width: width!, height: height!),
        worker: RGBARenderWorker(),
        onNewFrame: {() -> Void in
    //          print(" self.registry.textureFrameAvailable(self.textureId!): \(self.textureId) ")
            
          self.registry.textureFrameAvailable(textureId!)
        }
      );

//      self.textureId = self.registry.register(render!);
      textureId = self.registry.register(render!);
      if (textureId == 1) {
        self.registry.unregisterTexture(textureId!);
        textureId = self.registry.register(render!);
      }

      self.renders[Int(textureId!)] = render!;

      result(textureId!)

    case "updateTexture":
      guard let args = call.arguments as? [String : Any] else {
        result("error arguments")
        return
      }

      guard let textureId = args["textureId"] as? Int else {
        print("error data arguments")
        result("error data arguments")
        return
      }
      guard let data = args["data"] as? FlutterStandardTypedData else {
        print("error data arguments")
        result("error data arguments")
        return
      }
      guard let width = args["width"] as? Double else {
        print("error width arguments")
        result("error width arguments")
        return
      }
      guard let height = args["height"] as? Double else {
        print("error height arguments")
        result("error height arguments")
        return
      }

      guard let render = self.renders[textureId] else {
        print("error render")
        result("error render")
        return
      }
      
      render.updateTexture(textureId, width, height, data.data);
      result(nil)

    case "dispose":
        
      guard let args = call.arguments as? [String : Any] else {
        result("error arguments")
        return
      }

      guard let textureId = args["textureId"] as? Int else {
        print("error textureId arguments")
        result("error data arguments")
        return
      }
      
      guard let render = self.renders[textureId] else {
        print("error render")
        result("error render")
        return
      }
      render.dispose()
      renders.removeValue(forKey: textureId)
        
      result(nil)
       
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
