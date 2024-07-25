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
    private int screenHeight;
    private int velocity = 10;
    public boolean isTopPipe;
    private Rect rect;
    private static final int GAP_HEIGHT = 100;

    public Pipe(Context context, int screenWidth, int screenHeight, boolean isTopPipe, int gapPosition) {
        this.isTopPipe = isTopPipe;
        this.screenHeight = screenHeight;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Load the pipe images
        Bitmap originalTopPipeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipes, options);
        Bitmap originalBottomPipeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipesdown, options);

        if (originalTopPipeBitmap == null || originalBottomPipeBitmap == null) {
            throw new RuntimeException("Pipe bitmaps could not be loaded.");
        }

        // Scale the bitmaps to fit the screen
        if (isTopPipe) {
            topPipeBitmap = Bitmap.createScaledBitmap(originalTopPipeBitmap, originalTopPipeBitmap.getWidth(), gapPosition, false);
            y = 0; // Top pipe starts from the top of the screen
        } else {
            bottomPipeBitmap = Bitmap.createScaledBitmap(originalBottomPipeBitmap, originalBottomPipeBitmap.getWidth(), screenHeight - gapPosition, false);
            y = gapPosition; // Bottom pipe starts from the bottom of the gap
        }

        width = originalTopPipeBitmap.getWidth(); // Assuming both images have the same width
        height = isTopPipe ? topPipeBitmap.getHeight() : bottomPipeBitmap.getHeight(); // Use the height of the respective pipe
        x = screenWidth;

        updateRect(); // Initialize the rectangle
    }

        public void update() {

        x -= velocity;
            updateRect();
        }

    private void updateRect() {
        if (isTopPipe) {
            rect = new Rect(x, 0, x + width, topPipeBitmap.getHeight());
        } else {
            rect = new Rect(x, y, x + width, y + bottomPipeBitmap.getHeight());
        }
    }

        public void draw(Canvas canvas) {
            if (isTopPipe) {
                canvas.drawBitmap(topPipeBitmap, x - 50, y, null);
            } else {
                canvas.drawBitmap(bottomPipeBitmap, x, y, null);
            }
        }

    public Rect getRect() {
        return rect;
    }

        public int getX() {
            return x;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }