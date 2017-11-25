package cz.polabskageostezka.utils.ar_utils;

public class Shaders
{
    
    public static final String CUBE_MESH_VERTEX_SHADER = " \n" +
         "attribute vec4 vertexPosition; \n" +
         "attribute vec2 vertexTexCoord; \n" +
         "varying vec2 texCoord; \n" +
         "uniform mat4 modelViewProjectionMatrix; \n" +
         "void main() \n" + "{ \n" +
         "   gl_Position = modelViewProjectionMatrix * vertexPosition; \n" +
         "   texCoord = vertexTexCoord; \n" +
         "}";
    
    public static final String CUBE_MESH_FRAGMENT_SHADER = " \n" +
        "precision mediump float; \n" +
        "varying vec2 texCoord; \n" +
        "uniform sampler2D texSampler2D; \n" +
        "void main() \n" +
        "{ \n" + "   gl_FragColor = texture2D(texSampler2D, texCoord); \n" +
        "}";
    
}
