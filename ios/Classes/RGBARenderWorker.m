//
//  SampleRender.m
//  create_texture
//

#import "RGBARenderWorker.h"
#import <OpenGLES/ES2/gl.h>
#import "ShaderUtilities.h"

#if !defined(_STRINGIFY)
#define __STRINGIFY( _x )   # _x
#define _STRINGIFY( _x )   __STRINGIFY( _x )
#endif

static const char * kPassThruVertex = _STRINGIFY(
    attribute vec4 position;
    attribute mediump vec4 texturecoordinate;
    varying mediump vec2 coordinate;

    void main() {
        gl_Position = position;
        coordinate = texturecoordinate.xy;
    }
);

static const char * kPassThruFragment = _STRINGIFY(
    precision mediump float;
    varying highp vec2 coordinate;
    uniform sampler2D sTexture;

    void main() {
        gl_FragColor = texture2D(sTexture, coordinate);
    }
);

enum {
    ATTRIB_VERTEX,
    ATTRIB_TEXTUREPOSITON,
    NUM_ATTRIBUTES
};

@interface RGBARenderWorker() {
    
    GLint width;
    GLint height;

    GLuint frameBufferHandle;
    GLuint colorBufferHandle;
    
    GLuint program;
    GLint _frame;
    
}
@end
@implementation RGBARenderWorker

- (void)onCreate:(double)width :(double)height {
    
    GLint attribLocation[NUM_ATTRIBUTES] = {
        ATTRIB_VERTEX,
        ATTRIB_TEXTUREPOSITON,
    };
    GLchar *attribName[NUM_ATTRIBUTES] = {
        "position",
        "texturecoordinate",
    };
            
    glueCreateProgram(
                      kPassThruVertex,
                      kPassThruFragment,
                      NUM_ATTRIBUTES,
                      (const GLchar **)&attribName[0], attribLocation,
                      0,
                      0,
                      0,
                      &program
                      );

    if (!program) {
        NSLog(@"Error creating the program");
    }
    
    glViewport(0, 0, width, height);
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
}

- (BOOL)updateTexture:(long)textureId :(double)width :(double)height :(NSData*)data {
    
    if (textureId <= 0 || !program) return NO;

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
//    glViewport(0, 0, width, height);

    glUseProgram( program );
    glActiveTexture( GL_TEXTURE0 );
//    glBindTexture( CVOpenGLESTextureGetTarget( texture ), CVOpenGLESTextureGetName( texture ) );
    glBindTexture(GL_TEXTURE_2D, (GLuint)textureId);
    
    // Set texture parameters
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
    
    
    glUniform1i( glueGetUniformLocation(program, "sTexture"), 0 );
    
    NSUInteger len = [data length];
    void *buffer = malloc(len);
    memcpy(buffer, [data bytes], len);
    
    glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGBA,
                width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, buffer
                 );
    
    free(buffer);
    
    static const GLfloat squareVertices[] = {
        -1.0f, -1.0f, // bottom left
        1.0f, -1.0f, // bottom right
        -1.0f,  1.0f, // top left
        1.0f,  1.0f, // top right
    };
    
    glVertexAttribPointer( ATTRIB_VERTEX, 2, GL_FLOAT, 0, 0, squareVertices );
    glEnableVertexAttribArray( ATTRIB_VERTEX );
    
    GLfloat passThroughTextureVertices[] = {
        1.0f, 0.0f,     // left top
        0.0f, 0.0f,     // left bottom
        1.0f, 1.0f,     // right top
        0.0f, 1.0f      // right bottom
    };
    
    glVertexAttribPointer( ATTRIB_TEXTUREPOSITON, 2, GL_FLOAT, 0, 0, passThroughTextureVertices);
    glEnableVertexAttribArray( ATTRIB_TEXTUREPOSITON );
    
    glDrawArrays( GL_TRIANGLE_STRIP, 0, 4 );
    
    glDisableVertexAttribArray(ATTRIB_VERTEX);
    glDisableVertexAttribArray(ATTRIB_TEXTUREPOSITON);
    
    return YES;
}

- (void)onDispose {

}

@end
