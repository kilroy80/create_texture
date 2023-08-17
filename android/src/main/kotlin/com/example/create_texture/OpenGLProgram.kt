package com.example.create_texture

import android.opengl.GLES30
import android.util.Log

class OpenGLProgram {

    private val GLSL_VERTEX_SHADER = "attribute vec2 vPosition;\n" +
            "attribute vec2 vTexCoord;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  texCoord = vTexCoord;\n" +
            "  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );\n" +
            "}"

    private val GLSL_FRAGMENT_SHADER = "precision mediump float;\n" +
            "uniform sampler2D sTexture;\n" +
            "varying vec2 texCoord;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture,texCoord);\n" +
            "}"

//    fun getProgramOES() : Int {
//        return compileShaders(OES_GLSL_VERTEX_SHADER, OES_GLSL_FRAGMENT_SHADER);
//    }

    fun getProgram(): Int {
        return compileShaders(GLSL_VERTEX_SHADER, GLSL_FRAGMENT_SHADER);
    }

    fun getProgramByName(name: String): Int {
        return compileProgramByName(name);
    }

    private fun compileProgramByName(name: String): Int {

        if(name == "glsl") {
            return compileShaders(GLSL_VERTEX_SHADER, GLSL_FRAGMENT_SHADER);
        } else if(name == "glsl-fxaa") {
//            return compileShaders(FXAA_VERTEX_SHADER, FXAA_FRAGMENT_SHADER);
        }

        throw Exception(" name: $name is not support ");
    }


    private fun compileShaders(pVertexShader: String, pFragmentShader: String): Int {
        val vertexShader = this.compileShader(pVertexShader, GLES30.GL_VERTEX_SHADER)
        val fragmentShader = this.compileShader(pFragmentShader, GLES30.GL_FRAGMENT_SHADER)

        val programHandle = GLES30.glCreateProgram()
        GLES30.glAttachShader(programHandle, vertexShader)
        GLES30.glAttachShader(programHandle, fragmentShader)
        GLES30.glLinkProgram(programHandle)

        val linkSuccess = IntArray(1);
        GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkSuccess, 0)
        if (linkSuccess[0] == 0) {

            println(GLES30.glGetProgramInfoLog(programHandle));


            GLES30.glDeleteProgram(programHandle);
            throw Exception(" Linking of program failed. ")
        }

        return programHandle;
    }

    private fun compileShader(shader: String, shaderType: Int): Int {
        return compileShaderCode(shader, shaderType);
    }

    private fun compileShaderCode(shaderCode: String, shaderType: Int): Int {

        val shaderObjectId = GLES30.glCreateShader(shaderType)


        if (shaderObjectId == 0) {

            println("Could not create new shader.");

            return 0
        }

        GLES30.glShaderSource(shaderObjectId, shaderCode)

        GLES30.glCompileShader(shaderObjectId)

        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shaderObjectId, GLES30.GL_COMPILE_STATUS, compileStatus, 0)

        Log.e("TAG", "Results of compiling source:" + GLES30.glGetShaderInfoLog(shaderObjectId))

        if (compileStatus[0] == 0) {

            println(GLES30.glGetProgramInfoLog(shaderObjectId));
            println("Compilation of shader failed.")

            GLES30.glDeleteShader(shaderObjectId)

            return 0
        }

        return shaderObjectId
    }
}