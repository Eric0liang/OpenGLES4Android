package com.test.opengl4android;

import android.content.Context;
import android.opengl.GLES20;
import android.view.View;


import com.test.opengl4android.base.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eric
 * @description 球体
 * @date: 2023/5/30 15:27
 */
public class Cylinder extends Shape {

    private View view;

    private List<Circle> ovalList;

    //切割份数
    private int n = 360;
    //圆柱高度
    private float height = 1.0f;
    //圆柱底面半径
    private float radius = 0.5f;
    private Context mContext;

    private int vSize;

    public Cylinder(View view) {
        this.view = view;
        this.mContext = view.getContext();
    }

    @Override
    public void init() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // 初始化
        ovalList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            //通过矩阵计算中间8个圆的位置
            ovalList.add(new Circle(mContext, height, i == 0 ? 0.4f : 0.25f, 0, i*45, new float[]{((int)(Math.random()*10))*0.1f, ((int)(Math.random()*10))*0.1f, ((int)(Math.random()*10))*0.1f, 1.0f},
                    i == 0 ? 0 : 1));
        }
        for (int i = 0; i < 4; i++) {
            //通过矩阵计算次底部4个圆的位置
            ovalList.add(new Circle(mContext, height, 0.25f, 45, i*90, new float[]{((int)(Math.random()*10))*0.1f, 1, ((int)(Math.random()*10))*0.1f, 1.0f},
                    1));
        }
        for (int i = 0; i < 4; i++) {
            //通过矩阵计算次顶部4个圆的位置
            ovalList.add(new Circle(mContext, height, 0.25f, -45, i*90, new float[]{1f, ((int)(Math.random()*10))*0.1f, ((int)(Math.random()*10))*0.1f, 1.0f},
                    1));
        }
        for (int i = 0; i < 2; i++) {
            //通过矩阵计算上下顶部底部4个圆的位置
            ovalList.add(new Circle(mContext, height, 0.25f, i ==0 ? -90: 90, 0, new float[]{0.3f, 0.3f, 1f, 1.0f}, 1));
        }
        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        for (Circle c : ovalList) {
            c.init();
        }
    }

    @Override
    public void onDraw(float[] mMVPMatrix) {
        GLES20.glUseProgram(mProgram);
        int mMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mMVPMatrix, 0);
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        for (Circle c : ovalList) {
            c.onDraw(mMVPMatrix);
        }
    }
}
