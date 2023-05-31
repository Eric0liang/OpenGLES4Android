package com.test.opengl4android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Toast;

import java.util.List;

/**
 * @author eric
 * @description 平面GLSurfaceView
 * @date: 2023/5/30 13:58
 */
public class PlaneGlSurfaceView extends GLSurfaceView {

    private OnTouchEventListener touchListener;

    public PlaneGlSurfaceView(Context context) {
        super(context);
        init();
    }

    public PlaneGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
    }

    public void setOnTouchListener(OnTouchEventListener listener) {
        touchListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        this.setSceneWidthAndHeight(this.getMeasuredWidth(),
                this.getMeasuredHeight());
    }

    public void setSceneWidthAndHeight(float mSceneWidth, float mSceneHeight) {
        this.mSceneWidth = mSceneWidth;
        this.mSceneHeight = mSceneHeight;
    }

    // 宽
    private float mSceneWidth = 720;
    // 高
    private float mSceneHeight = 1280;

    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    public VelocityTracker velocityTracker;

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(e);
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                velocityTracker.computeCurrentVelocity(150);   //计算每150ms手指移动的像素
                float mouseX = velocityTracker.getXVelocity();
                float mouseY = velocityTracker.getYVelocity();
                if (mouseX == 0 && mouseY == 0) {
                    break;
                }
                //计算触控笔Y位移
                float dy = (mouseY / 400) * 8;
                //计算触控笔X位移
                float dx = (-mouseX / 400) * 8;
                //
                if (touchListener != null) {
                    touchListener.onTouchEvent(dx, dy);
                    for (int i = 0; i < Circle.cPoints.size(); i++) {
                        List<double[]> list = Circle.cPoints.get(i);
                        double[] o = list.get(0); //圆心
                        o = MatrixUtil.calXY(o,0, -CubeRenderer.mAngleY);
                        list.set(0, o);
                        for (int j = 1; j < list.size(); j++) {
                            double[] a = list.get(j); //圆上的点
                            a = MatrixUtil.calXY(a, 0, -CubeRenderer.mAngleY);
                            list.set(j, a);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.clear();
                velocityTracker.recycle();
                break;
            case MotionEvent.ACTION_UP:
                if (e.getEventTime() - e.getDownTime() < 150) {
                    // 将屏幕坐标转换为OpenGL ES的标准化设备坐标
                    float normalizedX = (x / getWidth()) * 2 - 1;
                    float normalizedY = (1 - (y / getHeight()) * 2) * (getHeight()/ (float) getWidth());
                    for (int i = 0; i < Circle.cPoints.size(); i++) {
                        List<double[]> list = Circle.cPoints.get(i);
                        double[] o = list.get(0); //圆心
                        double minX = 0;
                        double maxX = 0;
                        double minY = 0;
                        double maxY = 0;
                        for (int j = 1; j < list.size(); j++) {
                            double[] a = list.get(j); //圆上的点
                            minX = j ==1 ? a[0]: Math.min(minX, a[0]);
                            maxX = j ==1 ? a[0]: Math.max(maxX, a[0]);
                            minY = j ==1 ? a[1]: Math.min(minY, a[1]);
                            maxY = j ==1 ? a[1]: Math.max(maxY, a[1]);
                        }
                        if (o[2] > 0 && normalizedX >= minX && normalizedX <= maxX
                                && normalizedY >= minY && normalizedY <= maxY) {
                            Toast.makeText(getContext(), "click"+i, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                velocityTracker.clear();
                velocityTracker.recycle();
                break;
            default:
                break;

        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

    /**
     * 触摸监听接口
     */
    public interface OnTouchEventListener {

        void onTouchEvent(float dx, float dy);

    }


}
