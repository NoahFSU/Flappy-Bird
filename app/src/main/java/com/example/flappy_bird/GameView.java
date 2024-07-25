package com.example.flappy_bird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
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
    private List<Pipe> pipesL = new ArrayList<>();
    private Random random = new Random();
    private Bitmap background;
    private static final int PIPE_GAP = 800;
    private int score = 0;


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
        try {
            bird.update();
            for (Pipe pipe : pipesL) {
                pipe.update();
                if (Rect.intersects(bird.getRect(), pipe.getRect())) {
                    Log.d("Collision", "Collision detected between bird: " + bird.getRect().toString() + " and pipe: " + pipe.getRect().toString());
                    restart();
                    return;
                }
            }
            if (bird.getY() > screenHeight) {
                Log.d("Game", "Bird went below the screen. Restarting...");
                restart();
                return;
            }

            managePipes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void draw() {
        if (getHolder().getSurface().isValid()) {
            try {
                Canvas canvas = getHolder().lockCanvas();
                canvas.drawBitmap(background, 0, 0, paint);
                bird.draw(canvas);
                for (Pipe pipe : pipesL) {
                    pipe.draw(canvas);
                }
                //paint.setColor(Color.RED);
                //canvas.drawRect(bird.getRect(), paint);
                //paint.setColor(Color.GREEN);
                //for (Pipe pipe : pipesL) {
                //    canvas.drawRect(pipe.getRect(), paint);
                //}

                paint.setColor(Color.BLACK);
                paint.setTextSize(500);
                paint.setFakeBoldText(true);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(score), screenWidth / 2, 600, paint);

                getHolder().unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            bird.flap();
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
        bird = new Bird(getContext(), screenWidth, screenHeight);
        pipesL.clear();
        addPipePair();
        score = 0;
    }

    private void managePipes() {
        if (!pipesL.isEmpty() && pipesL.get(pipesL.size() - 1).getX() < screenWidth / 4) {
            addPipePair();
        }

        Iterator<Pipe> iterator = pipesL.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
                if (pipe.isTopPipe) {
                    score++;
                }
            }
        }
    }

    private void addPipePair() {
        // Assuming both pipes (top and bottom) have the same height
        int pipeHeight = pipesL.isEmpty() ? 500 : pipesL.get(0).getHeight(); // Replace 500 with actual pipe height or use a placeholder
        int screenHeightPadding = 100; // Optional padding from the top and bottom of the screen

        // Calculate minimum and maximum Y positions for the top of the gap
        int minGapPosition = screenHeightPadding + pipeHeight / 2;
        int maxGapPosition = screenHeight - screenHeightPadding - (PIPE_GAP + pipeHeight / 2);

        if (maxGapPosition <= minGapPosition) {
            maxGapPosition = minGapPosition + 1; // Ensure a valid range
        }

        // Randomly determine the Y position for the top of the gap within the calculated range
        int gapPosition = random.nextInt(maxGapPosition - minGapPosition + 1) + minGapPosition;

        // Add top and bottom pipes
        pipesL.add(new Pipe(getContext(), screenWidth, screenHeight, true, gapPosition));
        pipesL.add(new Pipe(getContext(), screenWidth, screenHeight, false, gapPosition + PIPE_GAP));
    }
}
