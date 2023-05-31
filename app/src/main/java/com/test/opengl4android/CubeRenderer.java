package com.test.opengl4android;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.test.opengl4android.base.Shape;
import com.test.opengl4android.base.ShapeRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author eric
 * @description 球体渲染renderer
 * @date: 2023/5/30 16:16
 */
public class CubeRenderer extends ShapeRenderer {
    public static float mAngleY = 0;
    public static float mAngleX = 0;

    /**
     * 立方体
     */
    private Shape shape;
    private PlaneGlSurfaceView view;

    public CubeRenderer(PlaneGlSurfaceView view) {
        this.view = view;
    }

    public CubeRenderer() {
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        if (shape == null) {
            shape = new Cylinder(view);
        }
        // 初始化球体
        shape.init();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, -0.2f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        shape.onDraw(mMVPMatrix);
    }

    public PlaneGlSurfaceView.OnTouchEventListener getTouchEventListener() {
        return touchEventListener;
    }

    /**
     * 触摸回调
     */
    PlaneGlSurfaceView.OnTouchEventListener touchEventListener = new PlaneGlSurfaceView.OnTouchEventListener() {
        @Override
        public void onTouchEvent(float dx, float dy) {
            if (CubeRenderer.this.view != null) {
                mAngleY = dx;
                mAngleX = dy;
                Matrix.rotateM(mMVPMatrix, 0, -mAngleY, 0, 1, 0);
                view.requestRender();//重绘画面
            }
        }
    };
}
