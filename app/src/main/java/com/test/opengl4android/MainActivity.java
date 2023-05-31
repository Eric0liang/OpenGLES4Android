package com.test.opengl4android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private PlaneGlSurfaceView glSurfaceView;
    private CubeRenderer renderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.glsv_plane);
        renderer = new CubeRenderer(glSurfaceView);
        glSurfaceView.setOnTouchListener(renderer.getTouchEventListener());
        glSurfaceView.setRenderer(renderer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

}