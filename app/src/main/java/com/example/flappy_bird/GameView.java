package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private List<Pipe> pipesl = new ArrayList<>();
    private boolean isGameOver = false;
    private Random random = new Random();
    private Bitmap background;
    private static final int PIPE_GAP = 400;

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        paint = new Paint();
        bird = new Bird(context, screenWidth, screenHeight);
        addPipePair();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
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
        for (Pipe pipe : pipesl) {
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
            canvas.drawBitmap(background, 0, 0, paint);
            bird.draw(canvas);
            for (Pipe pipe : pipesl) {
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
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void restart() {
        isGameOver = false;
        bird = new Bird(getContext(), screenWidth, screenHeight);
        pipesl.clear();
        addPipePair();
    }

    private void managePipes() {
        if (!pipesl.isEmpty() && pipesl.get(pipesl.size() - 1).getX() < screenWidth / 2) {
            addPipePair();
        }

        Iterator<Pipe> iterator = pipesl.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
            }
        }
    }

    private void addPipePair() {
        int gapPosition = random.nextInt(screenHeight - PIPE_GAP) + PIPE_GAP / 2;
        pipesl.add(new Pipe(getContext(), screenWidth, screenHeight, true, gapPosition));
        pipesl.add(new Pipe(getContext(), screenWidth, screenHeight, false, gapPosition));
    }
}
