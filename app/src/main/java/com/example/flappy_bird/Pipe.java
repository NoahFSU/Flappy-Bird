package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

    public class Pipe {

        private Bitmap topPipeBitmap;
        private Bitmap bottomPipeBitmap;
        private int x, y, width, height;
        private int velocity = 10;
        private boolean isTopPipe;

        public Pipe(Context context, int screenWidth, int screenHeight, boolean isTopPipe, int gapPosition) {
            this.isTopPipe = isTopPipe;
            topPipeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipes);
            bottomPipeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipesdown);
            width = topPipeBitmap.getWidth(); // Assuming both images have the same width
            height = topPipeBitmap.getHeight(); // Assuming both images have the same height
            x = screenWidth;

            if (isTopPipe) {
                y = gapPosition - height; // Position the top pipe above the gap
            } else {
                y = gapPosition; // Position the bottom pipe below the gap
            }
        }

        public void update() {
            x -= velocity;
        }

        public void draw(Canvas canvas) {
            if (isTopPipe) {
                canvas.drawBitmap(topPipeBitmap, x, y, null);
            } else {
                canvas.drawBitmap(bottomPipeBitmap, x, y, null);
            }
        }

        public Rect getRect() {
            return new Rect(x, y, x + width, y + height);
        }

        public int getX() {
            return x;
        }

        public int getWidth() {
            return width;
        }
    }