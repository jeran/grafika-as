/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.grafika;

import android.app.Activity;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.WindowSurface;

/**
 * Activity based on {@link MultiSurfaceActivity} which adds and removes a second {@link SurfaceView}
 * when the user clicks a button.
 */
public class AddSecondSurfaceViewActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = MainActivity.TAG;

    private SurfaceView mSurfaceView1;
    private SurfaceView mSurfaceView2;

    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_second_surface_view);

        mSurfaceView1 = (SurfaceView) findViewById(R.id.add_second_surface_view_surface_view1);
        mSurfaceView1.getHolder().addCallback(this);

        mContainer = (FrameLayout) findViewById(R.id.add_second_surface_view_container);

        mSurfaceView2 = new SurfaceView(this);
        mSurfaceView2.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSurfaceView2.getHolder().addCallback(this);
        mSurfaceView2.setZOrderMediaOverlay(true);

        final TextView button = (TextView) findViewById(R.id.add_second_surface_view_button);
        button.setText(R.string.add_second_surface_view_add);
        findViewById(R.id.add_second_surface_view_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContainer.getChildCount() > 0) {
                    button.setText(R.string.add_second_surface_view_add);
                    mContainer.removeView(mSurfaceView2);
                } else {
                    button.setText(R.string.add_second_surface_view_remove);
                    mContainer.addView(mSurfaceView2);
                }
            }
        });
    }

    private int getSurfaceId(SurfaceHolder holder) {
        if (holder.equals(mSurfaceView1.getHolder())) {
            return 1;
        } else if (holder.equals(mSurfaceView2.getHolder())) {
            return 2;
        } else {
            return -1;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int id = getSurfaceId(holder);
        if (id < 0) {
            Log.w(TAG, "surfaceCreated UNKNOWN holder=" + holder);
        } else {
            Log.d(TAG, "surfaceCreated #" + id + " holder=" + holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged fmt=" + format + " size=" + width + "x" + height +
                " holder=" + holder);

        int id = getSurfaceId(holder);
        Surface surface = holder.getSurface();

        switch (id) {
            case 1:
                drawSolidColor(surface, 1, 0, 0);
                break;
            case 2:
                drawSolidColor(surface, 1, 1, 1);
                break;
            default:
                throw new RuntimeException("wha?");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed holder=" + holder);
    }

    private void drawSolidColor(Surface surface, float r, float g, float b) {
        EglCore eglCore = new EglCore();
        WindowSurface win = new WindowSurface(eglCore, surface, false);
        win.makeCurrent();

        GLES20.glClearColor(r, g, b, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        win.swapBuffers();
        win.release();
        eglCore.release();
    }
}
