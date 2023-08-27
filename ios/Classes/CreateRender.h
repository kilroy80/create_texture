//
//  CreateRender.h
//  create_texture
//

#import <Flutter/Flutter.h>

@protocol OpenGLRenderWorker<NSObject>
- (void)onCreate;
- (BOOL)updateTexture:(long)textureId :(double)width :(double)height :(NSData*)data;
- (void)onDispose;
@end

@interface CreateRender : NSObject<FlutterTexture>

- (instancetype)initWithSize:(CGSize)renderSize
                      worker:(id<OpenGLRenderWorker>)worker
                  onNewFrame:(void(^)(void))onNewFrame;

- (void)updateTexture:(long)textureId :(double)width :(double)height :(NSData*)data;
- (void)dispose;

@end
