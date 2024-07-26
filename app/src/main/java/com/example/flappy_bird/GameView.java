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
    private boolean isPaused = true;
    private Paint paint;
    private int screenWidth, screenHeight;
    private Bird bird;
    private List<Pipe> pipesL = new ArrayList<>();
    private Random random = new Random();
    private Bitmap background;
    private Bitmap ployImage;
    private float backgroundOffset = 0;
    private float backgroundSpeed = 10;
    private static final int PIPE_GAP = 800;
    private int score = 0;
    private Paint collisionPaint;

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        paint = new Paint();
        collisionPaint = new Paint();
        collisionPaint.setColor(Color.RED);
        collisionPaint.setStyle(Paint.Style.STROKE);
        collisionPaint.setStrokeWidth(5);
        bird = new Bird(context, screenWidth, screenHeight);
        addPipePair();


        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);


        ployImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ploy);

        int ployImageWidth = ployImage.getWidth();
        int ployImageHeight = ployImage.getHeight();
        float aspectRatio = (float) ployImageWidth / ployImageHeight;
        int scaledHeight = Math.round(screenWidth / aspectRatio);
        if (scaledHeight > screenHeight) {
            scaledHeight = screenHeight;
            screenWidth = Math.round(scaledHeight * aspectRatio);
        }
        ployImage = Bitmap.createScaledBitmap(ployImage, screenWidth / 3, scaledHeight / 3, false);
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (!isPaused) {
                update();
            }
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
            updateBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBackground() {
        backgroundOffset -= backgroundSpeed;
        if (backgroundOffset <= -screenWidth) {
            backgroundOffset = 0;
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            try {
                Canvas canvas = getHolder().lockCanvas();


                canvas.drawBitmap(background, backgroundOffset, 0, paint);
                if (backgroundOffset < 0) {
                    canvas.drawBitmap(background, backgroundOffset + screenWidth, 0, paint);
                }


                bird.draw(canvas);
                for (Pipe pipe : pipesL) {
                    pipe.draw(canvas);
                }

                // Uncomment the following lines to draw collision boxes
                // canvas.drawRect(bird.getRect(), collisionPaint); // Draw bird's collision box
                // for (Pipe pipe : pipesL) {
                //     canvas.drawRect(pipe.getRect(), collisionPaint); // Draw pipe's collision box
                // }

                paint.setColor(Color.BLACK);
                paint.setTextSize(500);
                paint.setFakeBoldText(true);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(score), screenWidth / 2, 600, paint);


                if (isPaused) {

                    int ployImageWidth = ployImage.getWidth();
                    int ployImageHeight = ployImage.getHeight();
                    int left = (screenWidth - ployImageWidth) / 2;
                    int top = (screenHeight - ployImageHeight) - 400;
                    canvas.drawBitmap(ployImage, left, top, paint);


                    paint.setColor(Color.BLACK);
                    paint.setTextSize(200);
                    paint.setFakeBoldText(true);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("Press to Play", screenWidth / 2, top + ployImageHeight + 150, paint); // Adjust the y-position as needed
                }

                getHolder().unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        isPaused = true;
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
            if (isPaused) {
                isPaused = false;
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
        bird = new Bird(getContext(), screenWidth, screenHeight);
        pipesL.clear();
        addPipePair();
        score = 0;
        isPaused = true;
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
            } else {

                if (pipe.getX() + pipe.getWidth() < bird.getX() && !pipe.isScored() && pipe.isTopPipe) {
                    score++;
                    pipe.setScored(true);
                }
            }
        }
    }

    private void addPipePair() {

        int pipeHeight = pipesL.isEmpty() ? 500 : pipesL.get(0).getHeight();
        int screenHeightPadding = 100;


        int minGapPosition = screenHeightPadding + pipeHeight / 2;
        int maxGapPosition = screenHeight - screenHeightPadding - (PIPE_GAP + pipeHeight / 2);

        if (maxGapPosition <= minGapPosition) {
            maxGapPosition = minGapPosition + 1;
        }


        int gapPosition = random.nextInt(maxGapPosition - minGapPosition + 1) + minGapPosition;


        pipesL.add(new Pipe(getContext(), screenWidth, screenHeight, true, gapPosition));
        pipesL.add(new Pipe(getContext(), screenWidth, screenHeight, false, gapPosition + PIPE_GAP));
    }
}