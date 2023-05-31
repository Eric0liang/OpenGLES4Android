package com.test.opengl4android.base;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author eric
 * @description 图形基础类
 * @date: 2023/5/30 13:54
 */
public abstract class Shape {

    /**
     * 顶点数据
     */
    protected FloatBuffer vertexBuffer;

    /**
     * 纹理数据
     */
    protected FloatBuffer texCoordsBuffer;
    /**
     * 顶点数据索引
     */
    protected ShortBuffer indexBuffer;
    /**
     * 可执行程序
     */
    protected int mProgram;

    /**
     * 坐标个数（xyz）
     */
    protected static final int COORDS_PER_VERTEX = 3;

    protected int mPositionHandle;
    protected int mColorHandle;

    public abstract void init();

    public abstract void onDraw(float[] mMVPMatrix);

    public static int loadShader(int type, String shaderCode) {
        // 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
        // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // 添加上面编写的着色器代码并编译它
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

}
