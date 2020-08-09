package com.makienkovs.birdie;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Enemy {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int x3;
    private int y3;
    private int x4;
    private int y4;
    private int x;
    private int y;
    private int distance;
    private int speed;
    private int hero;
    private int width;
    private int height;
    private int frame;
    private Bitmap pipeUp;
    private Bitmap pipeDown;
    private int pipeHeight;

    Enemy(Context context, int difficulty, boolean day, int hero) {
        Resources resources = context.getResources();
        speed = 10;
        this.hero = hero;
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        distance = width / 2;
        frame = hero * (4 - difficulty / 50);
        x1 = width;
        x2 = x1 + distance;
        x3 = x2 + distance;
        x4 = x3 + distance;
        y1 = (height - frame) / 2;
        y2 = addRandom(y1);
        y3 = addRandom(y2);
        y4 = addRandom(y3);

        x = x1;
        y = y1;

        int multiplier = (int) (width / 7.2);
        if (day) {
            pipeUp = BitmapFactory.decodeResource(resources, R.drawable.daypipeup);
            pipeDown = BitmapFactory.decodeResource(resources, R.drawable.daypipedown);
        } else {
            pipeUp = BitmapFactory.decodeResource(resources, R.drawable.nightpipeup);
            pipeDown = BitmapFactory.decodeResource(resources, R.drawable.nightpipedown);
        }

        pipeUp = Bitmap.createScaledBitmap(pipeUp, multiplier, height, true);
        pipeDown = Bitmap.createScaledBitmap(pipeDown, multiplier, height, true);

        pipeHeight = pipeUp.getHeight();
    }

    Bitmap getPipeUp() {
        return pipeUp;
    }

    Bitmap getPipeDown() {
        return pipeDown;
    }

    int getPipeHeight() {
        return pipeHeight;
    }

    void speedUp(){
        speed++;
    }

    void reset() {
        speed = 10;
        x1 = width;
        x2 = x1 + distance;
        x3 = x2 + distance;
        x4 = x3 + distance;
        y1 = (height - frame) / 2;
        y2 = addRandom(y1);
        y3 = addRandom(y2);
        y4 = addRandom(y3);

        x = x1;
        y = y1;
    }

    private int addRandom(int i) {
        int result;
        do {
            double dif = frame * Math.random() * 8 / speed;
            if (Math.random() > 0.5)
                result = i + (int) dif;
            else
                result = i - (int) dif;
        } while (result < hero || result > height - (frame + hero));
        return result;
    }

    void decrease() {
        x1 -= speed;
        x2 -= speed;
        x3 -= speed;
        x4 -= speed;
        x -= speed;

        if (x1 < -hero) {
            x1 = x4;
            y1 = y4;
        }

        if (x2 < -hero) {
            x2 = x1 + distance;
            y2 = addRandom(y1);
        }

        if (x3 < -hero) {
            x3 = x2 + distance;
            y3 = addRandom(y2);
        }

        if (x4 < -hero) {
            x4 = x3 + distance;
            y4 = addRandom(y3);
        }

        if (x1 > -hero && x1 < width / 3) {
            x = x1;
            y = y1;
        } else if (x2 > -hero && x2 < width / 3) {
            x = x2;
            y = y2;
        } else if (x3 > -hero && x3 < width / 3) {
            x = x3;
            y = y3;
        } else if (x4 > -hero && x4 < width / 3) {
            x = x4;
            y = y4;
        }
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getFrame() {
        return frame;
    }

    int getX1() {
        return x1;
    }

    int getY1() {
        return y1;
    }

    int getX2() {
        return x2;
    }

    int getY2() {
        return y2;
    }

    int getX3() {
        return x3;
    }

    int getY3() {
        return y3;
    }

    int getX4() {
        return x4;
    }

    int getY4() {
        return y4;
    }
}