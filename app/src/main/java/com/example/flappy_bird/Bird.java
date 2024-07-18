package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bird {

    private Bitmap birdBitmap;
    private int x, y, width, height;
    private int velocity = 0;
    private int gravity = 2;

    public Bird(Context context, int screenWidth, int screenHeight) {
        birdBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grumpy);
        width = birdBitmap.getWidth();
        height = birdBitmap.getHeight();
        x = screenWidth / 2 - width / 2;
        y = screenHeight / 2 - height / 2;
    }

    public void update() {
        velocity += gravity;
        y += velocity;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(birdBitmap, x, y, null);
    }

    public void flap() {
        velocity = -20;
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }
}