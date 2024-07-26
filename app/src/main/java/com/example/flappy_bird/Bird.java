package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;


public class Bird {

    private Bitmap birdBitmap;
    private int x, y, width, height;
    private int gravity = 3;
    private int lift = -50;
    private int velocity = 0;

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
        if (y < 0) {
            y = 0;
            velocity = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(birdBitmap, x, y, null);
    }

    public Rect getRect() {
        int Hpadding = 150;
        int Wpadding = 300;
        return new Rect(x + Wpadding, y + Hpadding, x + width - Wpadding, y + height - Hpadding);
    }

    public void flap() {
        velocity = lift;
    }

    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
}