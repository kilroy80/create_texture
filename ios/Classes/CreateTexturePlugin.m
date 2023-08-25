#import "CreateTexturePlugin.h"
#if __has_include(<create_texture/create_texture-Swift.h>)
#import <create_texture/create_texture-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "create_texture-Swift.h"
#endif

@implementation CreateTexturePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftCreateTexturePlugin registerWithRegistrar:registrar];
}
@end
