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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// This is a surface view class that runs in a thread to animate the character
public class AnimGame extends SurfaceView implements Runnable {

    Thread thread = null;
    SurfaceView surfaceViewGame;
    SurfaceHolder surfaceHolder;
    int canvasHeight;
    int canvasWidth;

    boolean animate = false;

    int boyFrame = 0;
    int boyTotalFrames = 6; // the total number of frames in the sprite
    Bitmap boyImage;
    int boyHeight;
    int boyWidth;

    int groundFrame = 0;
    int groundTotalFrames = 3;  // Each tile is divided into this number of steps (more steps = slower motion)
    Bitmap groundImage;
    int groundHeight;
    int groundWidth;
    int groundTileWidth;

    int backgroundFrame = 0;
    int backgroundTotalFrames = 240;  // Each tile is divided into this number of steps (more steps = slower motion)
    Bitmap backgroundImage;
    int backgroundHeight;
    int backgroundWidth;
    int backgroundTileWidth;

    public AnimGame(Context context) {
        super(context);

        boyImage = BitmapFactory.decodeResource(getResources(), R.drawable.runningboy);
        boyHeight = boyImage.getHeight();
        boyWidth = boyImage.getWidth() / boyTotalFrames;   // Find the width of a single frame by dividing whole width by the total number of frames

        surfaceViewGame = (SurfaceView) ((Activity) context).findViewById(R.id.surfaceViewGame);
        surfaceHolder = surfaceViewGame.getHolder();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                getCanvasSize();
                makeGroundImage();
                scaleBackgroundImage();
                runOnce();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    private void getCanvasSize() {
        canvasHeight = surfaceViewGame.getHeight();
        canvasWidth = surfaceViewGame.getWidth();
    }

    private void makeGroundImage() {
        Bitmap groundTileImage = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        groundHeight = groundTileImage.getHeight();
        groundTileWidth = groundTileImage.getWidth();

        int groundTiles = (int) (Math.ceil(canvasWidth / (double) groundTileWidth) + 1);

        groundWidth = groundTileWidth * groundTiles;
        groundImage = Bitmap.createBitmap(groundWidth, groundHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(groundImage);
        Paint paint = new Paint();
        for (int i = 0; i < groundTiles; i++) {
            canvas.drawBitmap(groundTileImage, i * groundTileWidth, 0, paint);
        }
    }

    private void scaleBackgroundImage() {
        Bitmap backgroundTileImage = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        backgroundHeight = backgroundTileImage.getHeight();
        backgroundTileWidth = backgroundTileImage.getWidth();

        // Scale background to fit canvas height
        backgroundTileWidth = (int) (canvasHeight / (double) backgroundHeight * backgroundTileWidth);
        backgroundHeight = canvasHeight;
        Bitmap backgroundScaledTileImage = Bitmap.createScaledBitmap(backgroundTileImage, backgroundTileWidth,  backgroundHeight, false);

        int backgroundTiles = (int) (Math.ceil(canvasWidth / (double) backgroundTileWidth) + 1);

        backgroundWidth = backgroundTileWidth * backgroundTiles;
        backgroundImage = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundImage);
        Paint paint = new Paint();
        for (int i = 0; i < backgroundTiles; i++) {
            canvas.drawBitmap(backgroundScaledTileImage, i * backgroundTileWidth, 0, paint);
        }
    }

    private void runOnce() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            int backgroundX = 0;
            int backgroundY = 0;
            drawOnCanvas(canvas, backgroundImage, backgroundWidth, backgroundHeight, 0, 0, backgroundX, backgroundY);

            int groundX = 0;
            int groundY = canvasHeight - groundHeight;
            drawOnCanvas(canvas, groundImage, groundWidth, groundHeight, 0, 0, groundX, groundY);

            int boyX = canvasWidth/2 - boyWidth /2;
            int boyY = groundY - boyHeight;
            drawOnCanvas(canvas, boyImage, boyWidth, boyHeight, 0, 0, boyX, boyY);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawOnCanvas(Canvas canvas, Bitmap imageSource, int imageWidth, int imageHeight, int srcX, int srcY, int dstX, int dstY) {
        Rect src = new Rect(srcX, srcY, srcX + imageWidth, srcY + imageHeight);
        Rect dst = new Rect(dstX,  dstY, dstX + imageWidth, dstY + imageHeight);
        canvas.drawBitmap(imageSource, src, dst, null);
    }

    @Override
    public void run() {
        while (animate) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();

                // Clear any existing drawing
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                int backgroundXshift = backgroundFrame * backgroundTileWidth / backgroundTotalFrames;
                int backgroundX = 0;
                int backgroundY = 0;
                drawOnCanvas(canvas, backgroundImage, backgroundWidth, backgroundHeight, backgroundXshift, 0, backgroundX, backgroundY);
                backgroundFrame = ++backgroundFrame % backgroundTotalFrames;

                int groundXshift = groundFrame * groundTileWidth / groundTotalFrames;
                int groundX = 0;
                int groundY = canvasHeight - groundHeight;
                drawOnCanvas(canvas, groundImage, groundWidth, groundHeight, groundXshift, 0, groundX, groundY);
                groundFrame = ++groundFrame % groundTotalFrames;


                int boyXshift = boyFrame * boyWidth;
                int boyX = canvasWidth/2 - boyWidth /2;
                int boyY = groundY - boyHeight;;
                drawOnCanvas(canvas, boyImage, boyWidth, boyHeight, boyXshift, 0, boyX, boyY);
                // This is a modulus operation to loop through all the frames and restart over
                boyFrame = ++boyFrame % boyTotalFrames;

                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            try {
                Thread.sleep(60);
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