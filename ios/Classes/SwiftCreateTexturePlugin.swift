import Flutter
import UIKit

public class SwiftCreateTexturePlugin: NSObject, FlutterPlugin {

  var registry: FlutterTextureRegistry;
  var textureId: Int64?;
//   static var messenger : FlutterBinaryMessenger? = nil;

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

      let type = args["type"] as? Int;
      let width = args["width"] as? Double;
      let height = args["height"] as? Double;

      let render = CreateRender(
        size: CGSize(width: width!, height: height!),
        worker: SampleRenderWorker(),
        onNewFrame: {() -> Void in
    //          print(" self.registry.textureFrameAvailable(self.textureId!): \(self.textureId) ")
          self.registry.textureFrameAvailable(self.textureId!)
        }
      );
        
      self.textureId = self.registry.register(render!);

      result(textureId!)

    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
