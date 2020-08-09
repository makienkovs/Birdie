package com.makienkovs.birdie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class Surface extends SurfaceView {

    private Context context;

    private static final long FPS_CALC_INTERVAL = 1000L;
    private long lastFpsCalcUptime;
    private long frameCounter;
    private long fps;

    private int height;
    private int width;

    private boolean jump;

    private boolean fall;
    private boolean start;
    private boolean switchCount;
    private int passCount;
    private int tickCount;
    private int fallCount;

    private Bitmap back;
    private Bitmap paper;
    private Bitmap tap;
    Typeface tf;

    private Hero hero;
    private Enemy enemy;

    private int best;
    private int difficulty;
    private boolean day;
    private boolean fpsShow;

    private Sound sound;

    public Surface(Context context, int difficulty, boolean day, boolean fpsShow) {
        super(context);
        this.context = context;
        this.difficulty = difficulty;
        this.day = day;
        this.fpsShow = fpsShow;
        init();
    }

    private void init() {
        height = context.getResources().getDisplayMetrics().heightPixels;
        width = context.getResources().getDisplayMetrics().widthPixels;
        hero = new Hero(context);
        enemy = new Enemy(context, difficulty, day, hero.getWidth());
        jump = false;
        fall = false;
        start = false;
        switchCount = false;
        passCount = 0;
        tickCount = 0;
        fallCount = 0;
        best = GameActivity.getBest();

        if (day) {
            back = BitmapFactory.decodeResource(context.getResources(), R.drawable.day);
            tap = BitmapFactory.decodeResource(context.getResources(), R.drawable.daytap);
        } else {
            back = BitmapFactory.decodeResource(context.getResources(), R.drawable.night);
            tap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nighttap);
        }

        back = Bitmap.createScaledBitmap(back, width, height, true);
        paper = BitmapFactory.decodeResource(context.getResources(), R.drawable.paper);
        paper = Bitmap.createScaledBitmap(paper, (int) (width * 0.8), height / 2, true);
        tap = Bitmap.createScaledBitmap(tap, (int) (width * 0.5), (int) (width * 0.5), true);

        tf = Typeface.createFromAsset(context.getAssets(), "Pacifico-Regular.ttf");
        sound = new Sound(context);
    }

    public void onPause() {
        sound.release();
    }

    public void onResume() {
        prepare();
        init();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        measureFps();
        updateGame();
        drawGame(canvas);
    }

    private void updateGame() {
        tickCount++;
        boolean isBetween = hero.getY() > 0 && hero.getY() < height - hero.getWidth();
        boolean isCollision = enemy.getX() > (hero.getX() - hero.getWidth()) && enemy.getX() < (hero.getX() + hero.getWidth()) && (hero.getY() < enemy.getY() - hero.getWidth() * 0.1 || hero.getY() > enemy.getY() + enemy.getFrame() - hero.getWidth() * 0.9);

        if (tickCount == 600) {
            tickCount = 0;
            hero.speedUp();
            enemy.speedUp();
        }

        if (jump) {
            if (!start) {
                start = true;
            } else if (!fall) {
                hero.setJump();
                sound.playJump();
            } else if (fallCount > 21){
                start = false;
                fall = false;
            }
        }

        if (!start) {
            hero.reset();
            enemy.reset();
            passCount = 0;
            tickCount = 0;
            fallCount = 0;
            best = GameActivity.getBest();
        } else if (isBetween && !isCollision && !fall) {
            hero.changeY();
            enemy.decrease();
        } else {
            fall = true;
            if (fallCount == 0) {
                sound.playHit();
            }
            fallCount++;
        }

        if (fall && hero.getY() < height) {
            hero.falling();
        }

        if (enemy.getX() < hero.getX() + hero.getWidth() && enemy.getX() > hero.getX() - hero.getWidth()) {
            switchCount = true;
        }

        if (switchCount && enemy.getX() < hero.getX() - hero.getWidth() && !fall) {
            switchCount = false;
            passCount++;
        }

        jump = false;
    }

    private void drawGame(Canvas canvas) {
        Paint paint = new Paint();
        //paint.setFakeBoldText(true);
        paint.setTypeface(tf);

        canvas.drawBitmap(back, 0, 0, paint);

        canvas.drawBitmap(enemy.getPipeDown(), (float) enemy.getX1(), (float) enemy.getY1() - enemy.getPipeHeight(), paint);
        canvas.drawBitmap(enemy.getPipeUp(), (float) enemy.getX1(), (float) enemy.getY1() + (float) enemy.getFrame(), paint);
        canvas.drawBitmap(enemy.getPipeDown(), (float) enemy.getX2(), (float) enemy.getY2() - enemy.getPipeHeight(), paint);
        canvas.drawBitmap(enemy.getPipeUp(), (float) enemy.getX2(), (float) enemy.getY2() + (float) enemy.getFrame(), paint);
        canvas.drawBitmap(enemy.getPipeDown(), (float) enemy.getX3(), (float) enemy.getY3() - enemy.getPipeHeight(), paint);
        canvas.drawBitmap(enemy.getPipeUp(), (float) enemy.getX3(), (float) enemy.getY3() + (float) enemy.getFrame(), paint);
        canvas.drawBitmap(enemy.getPipeDown(), (float) enemy.getX4(), (float) enemy.getY4() - enemy.getPipeHeight(), paint);
        canvas.drawBitmap(enemy.getPipeUp(), (float) enemy.getX4(), (float) enemy.getY4() + (float) enemy.getFrame(), paint);

        canvas.drawBitmap(hero.getImage(), hero.getX(), hero.getY(), paint);

        if (fall && fallCount > 20) {
            int score = passCount + passCount * difficulty / 100;
            if (score > best) {
                best = score;
                GameActivity.setBest(best);
            }
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(height * 0.04f);
            paint.setColor(Color.BLACK);
            canvas.drawARGB(80, 250, 0, 0);
            canvas.drawBitmap(paper, width * 0.1f, height * 0.25f, paint);
            canvas.drawText(context.getResources().getString(R.string.Passscore) + passCount, width * 0.5f, height * 0.4f, paint);
            canvas.drawText(context.getResources().getString(R.string.Difficulty) + ":\u0020" + difficulty, width * 0.5f, height * 0.466f, paint);
            canvas.drawText(context.getResources().getString(R.string.Score) + score, width * 0.5f, height * 0.533f, paint);
            canvas.drawText(context.getResources().getString(R.string.Bestscore) + best, width * 0.5f, height * 0.6f, paint);
        }

        if (day) {
            paint.setColor(Color.BLACK);
        } else {
            paint.setColor(Color.LTGRAY);
        }

        if (!start) {
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(height * 0.1f);
            canvas.drawText(context.getResources().getString(R.string.Tap), width * 0.5f, height * 0.2f, paint);
            canvas.drawBitmap(tap, width * 0.25f, height * 0.6f, paint);
        }

        if (start) {
            paint.setTextSize(height * 0.1f);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("" + passCount, width * 0.025f, height * 0.075f, paint);
        }

        if (fpsShow && start) {
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("~" + fps, width * 0.025f, height * 0.95f, paint);
        }
    }

    private void measureFps() {
        frameCounter++;
        long now = SystemClock.uptimeMillis();
        long delta = now - lastFpsCalcUptime;
        if (delta > FPS_CALC_INTERVAL) {
            fps = frameCounter * FPS_CALC_INTERVAL / delta;
            frameCounter = 0;
            lastFpsCalcUptime = now;
        }
    }

    private void prepare() {
        lastFpsCalcUptime = SystemClock.uptimeMillis();
        frameCounter = 0;
    }

    public void touch(boolean jump) {
        this.jump = jump;
    }
}
