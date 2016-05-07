/*
 * Copyright (c) 2016 Fahad Alduraibi
 */

package net.fadvisor.animation_example;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// This is a surface view class that runs in a thread to animate the character
public class AnimGuy extends SurfaceView implements Runnable {

    Thread thread = null;
    SurfaceHolder surfaceHolder;
    boolean animate = false;
    int x;
    int frame = 0;
    int totalFrames = 6; // the total number of frames in the sprite

    SurfaceView surfaceViewBoy;
    Bitmap guyImage;
    int imgHeight;
    int imgWidth;

    public AnimGuy(Context context) {
        super(context);

        guyImage = BitmapFactory.decodeResource(getResources(), R.drawable.runningboy);
        imgHeight = guyImage.getHeight();
        imgWidth = guyImage.getWidth() / totalFrames;   // Find the width of a single frame by dividing whole width by the total number of frames

        surfaceViewBoy = (SurfaceView) ((Activity) context).findViewById(R.id.surfaceViewBoy);

        // Move the view to the top and make it transparent.
        surfaceViewBoy.setZOrderOnTop(true);
        surfaceHolder = surfaceViewBoy.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                runOnce();  // This is just to draw a single frame when starting the app
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    // This is just to draw a single frame when starting the app (so it won't be empty)
    private void runOnce() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            // Clear any existing drawing
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Rect src = new Rect(0, 0, imgWidth, imgHeight);
            Rect dst = new Rect(0, 0, imgWidth, imgHeight);
            canvas.drawBitmap(guyImage, src, dst, null);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while (animate) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();

                // Clear any existing drawing
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                x = frame * imgWidth;
                Rect src = new Rect(x, 0, x + imgWidth, imgHeight);
                Rect dst = new Rect(0, 0, imgWidth, imgHeight);
                canvas.drawBitmap(guyImage, src, dst, null);

                // This is a modulus operation to loop through all the frames and restart over
                frame = ++frame % totalFrames;

                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        animate = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //thread = null; // do i need this?
    }

    public void resume() {
        animate = true;
        thread = new Thread(this);
        thread.start();
    }
}