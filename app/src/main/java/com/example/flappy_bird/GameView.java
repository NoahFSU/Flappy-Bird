package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    private Paint paint;
    private int screenWidth, screenHeight;
    private Bird bird;
    private List<Pipe> pipes = new ArrayList<>();
    private boolean isGameOver = false;
    private Random random = new Random();

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        paint = new Paint();
        bird = new Bird(context, screenWidth, screenHeight);
        pipes.add(new Pipe(context, screenWidth, screenHeight));
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        bird.update();
        for (Pipe pipe : pipes) {
            pipe.update();
            if (Rect.intersects(bird.getRect(), pipe.getRect())) {
                isGameOver = true;
            }
        }
        managePipes();
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.BLUE); // Set background color
            bird.draw(canvas);
            for (Pipe pipe : pipes) {
                pipe.draw(canvas);
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17); // Approximately 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            if (isGameOver) {
                restart();
            } else {
                bird.flap();
            }
            return true;
        }
        return false;
    }

    private void restart() {
        isGameOver = false;
        bird = new Bird(getContext(), screenWidth, screenHeight);
        pipes.clear();
        pipes.add(new Pipe(getContext(), screenWidth, screenHeight));
    }

    private void managePipes() {
        if (!pipes.isEmpty() && pipes.get(pipes.size() - 1).getX() < screenWidth / 2) {
            pipes.add(new Pipe(getContext(), screenWidth, screenHeight));
        }

        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
            }
        }
    }
}
