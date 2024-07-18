package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Pipe {


    private Bitmap pipeBitmap;
    private int x, y, width, height;
    private int velocity = 10;

    public Pipe(Context context, int screenWidth, int screenHeight) {
        pipeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipes);
        width = pipeBitmap.getWidth();
        height = pipeBitmap.getHeight();
        x = screenWidth;
        y = screenHeight - height; // Position at the bottom of the screen
    }

    public void update() {
        x -= velocity;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(pipeBitmap, x, y, null);
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