//
//  CreateRender.h
//  create_texture
//

#import <Flutter/Flutter.h>

@protocol OpenGLRenderWorker<NSObject>
- (void)onCreate;
- (BOOL)onDraw;
- (void)onDispose;
@end

@interface CreateRender : NSObject<FlutterTexture>

- (instancetype)initWithSize:(CGSize)renderSize
                      worker:(id<OpenGLRenderWorker>)worker
                  onNewFrame:(void(^)(void))onNewFrame;

- (void)dispose;

@end
