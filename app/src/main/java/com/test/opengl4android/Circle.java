package com.test.opengl4android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.test.opengl4android.base.Shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eric
 * @description 圆形
 * @date: 2023/5/30 14:24
 */
public class Circle extends Shape {

    //顶点之间的偏移量
    private final int vertexStride = 0; // 每个顶点四个字节

    private int mMatrixHandler;

    public float radius = 0.25f;
    public double thetaX = 0;
    public double thetaY = 0;
    public Context mContext;
    public static int n = 180;  //切割份数

    private float[] shapePos;
    private float[] texCoords;
    public float height = 0.0f;
    public static List<List<double[]>> cPoints = new ArrayList<>(); //圆的点击范围
    public static int index = 0;
    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {0f, 0f, 0f, 1.0f};

    public Circle() {
    }

    public Circle(Context context, float height, float radius, double thetaX, double thetaY, float[] color, int resId) {
        this.height = height;
        this.radius = radius;
        this.thetaX = thetaX;
        this.thetaY = thetaY;
        this.color = color;
        this.mContext = context;
        initTexture(resId);
    }


    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float[] createPositions() {
        if (index == 0) {
            cPoints = new ArrayList();
        }
        List cPoint = new ArrayList();
        ArrayList<Float> data = new ArrayList<>();
        //设置圆心坐标
        double[] vector = new double[]{0.0f, 0.0f, height};
        double[] xyz = MatrixUtil.calXY(vector, this.thetaX, this.thetaY);
        data.add((float) xyz[0]);
        data.add((float) xyz[1]);
        data.add((float) xyz[2]);
        cPoint.add(xyz);
        float angDegSpan = 360f / n;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            vector = new double[]{radius * Math.sin(i * Math.PI / 180f), radius * Math.cos(i * Math.PI / 180f), height};
            xyz = MatrixUtil.calXY(vector, this.thetaX, this.thetaY);
            data.add((float) xyz[0]);
            data.add((float) xyz[1]);
            data.add((float) xyz[2]);
            cPoint.add(xyz);
        }
        index++;
        index = index == 18? 0 : index;
        float[] f = new float[data.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = data.get(i);
        }
        cPoints.add(cPoint);
        return f;
    }

    private static float[] mTexCoords;

    public static float[] getTextureCoords() {
        if (mTexCoords == null) {
            ArrayList<Float> data = new ArrayList<>();
            float centerX = 0.5f;
            float centerY = 0.5f;
            float radius = 0.5f;

            // 中心点的纹理坐标为(0.5, 0.5)
            data.add(centerX);
            data.add(centerY);

            float angleIncrement = (float) (2.0 * Math.PI / n);
            float currentAngle = -(float) (Math.PI / 2.0); // 旋转90度的起始角度
            float angDegSpan = 360f / n;
            for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
                float x = centerX + radius * (float) Math.cos(currentAngle);
                float y = centerY + radius * (float) Math.sin(currentAngle);
                data.add(x);
                data.add(y);
                currentAngle += angleIncrement;
            }
            mTexCoords = new float[data.size()];
            for (int i = 0; i < mTexCoords.length; i++) {
                mTexCoords[i] = data.get(i);
            }
        }
        return mTexCoords;
    }

    @Override
    public void init() {
        shapePos = createPositions();
        ByteBuffer bb = ByteBuffer.allocateDirect(
                shapePos.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(shapePos);
        vertexBuffer.position(0);

        texCoords = getTextureCoords();
        bb = ByteBuffer.allocateDirect(
                texCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        texCoordsBuffer = bb.asFloatBuffer();
        texCoordsBuffer.put(texCoords);
        texCoordsBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onDraw(float[] mMVPMatrix) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        //设置纹理
        int aTexture = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLES20.glEnableVertexAttribArray(aTexture);
        GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 获取纹理的句柄
        int textureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glUniform1i(textureHandle, 0);

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        setColor();
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, shapePos.length / 3);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        //纹理加载成功后释放内存中的纹理图
        bitmapTemp.recycle();
    }


    public void setColor() {
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
    }

    public void matrix(float[] mMVPMatrix) {
        //Matrix.setRotateM(mMVPMatrix, 0, 45, 0, 1, 0);
    }

    private int textureId;
    private Bitmap bitmapTemp;

    private void initTexture(int id) {
        if (bitmapTemp == null) {
            int[] textures = new int[1];
            GLES20.glGenTextures(
                    1, //产生的纹理id数量
                    textures,//纹理id的数组
                    0);//偏移量
            //获取产生的纹理id
            textureId = textures[0];
            //绑定纹理id
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            //设置MIN采样方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            //设置MAG采样方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置S轴拉伸方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置T轴拉伸方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            if (id == 0) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.i9, null);
                view.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                bitmapTemp = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapTemp);
                view.draw(canvas);

            } else if(id == 1) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.i1, null);
                int color = Color.RED;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    color = Color.argb(this.color[3], this.color[0],this.color[1],this.color[2]);
                }
                view.setBackgroundColor(color); //随机颜色
                view.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                bitmapTemp = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapTemp);
                view.draw(canvas);
            }else {
                Drawable drawable = mContext.getResources().getDrawable(id);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmapTemp = bd.getBitmap();
            }
            //实际加载纹理进显存
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTemp, 0);
        }

    }


    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "attribute vec2 aTexCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vTexCoord = aTexCoord;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "  gl_FragColor = texture2D(u_Texture, vTexCoord);" +
                    "}";

}
