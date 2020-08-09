package com.makienkovs.birdie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Hero {

    private int x;
    private int y;
    private int xSet;
    private int ySet;
    private int speed;
    private boolean jump = true;
    private int countJump = 0;
    private int countFly;
    private static Bitmap center;
    private static Bitmap down;
    private static Bitmap up;

    public Hero(Context context) {
        int height = context.getResources().getDisplayMetrics().heightPixels;
        int width = context.getResources().getDisplayMetrics().widthPixels;
        speed = 10;
        Bitmap center = BitmapFactory.decodeResource(context.getResources(), R.drawable.birdie_center);
        Bitmap down = BitmapFactory.decodeResource(context.getResources(), R.drawable.birdie_down);
        Bitmap up = BitmapFactory.decodeResource(context.getResources(), R.drawable.birdie_up);
        int multiplier = (int) (width / 7.2);
        Hero.center = Bitmap.createScaledBitmap(center, multiplier, multiplier, true);
        Hero.down = Bitmap.createScaledBitmap(down, multiplier, multiplier, true);
        Hero.up = Bitmap.createScaledBitmap(up, multiplier, multiplier, true);
        xSet = width / 10;
        ySet = height / 2 - getWidth();
        this.x = xSet;
        this.y = ySet;
        countFly = 0;
    }

    void reset() {
        speed = 10;
        x = xSet;
        y = ySet;
        countFly = 0;
        setJump();
    }

    void speedUp(){
        speed++;
    }

    Bitmap getImage() {
        if (countJump == 0 && countFly != 0)
            return down;
        else if (countFly == 0 && countJump > 3 && countJump < 17)
            return up;
        else return center;
    }

    void changeY() {
        if (jump) {
            countFly = 0;
            countJump++;
            y -= Math.floor(0.1 * Math.pow((20 - countJump), 1.6));
            if (countJump == 20) {
                jump = false;
                countJump = 0;
            }
        } else {
            countJump = 0;
            countFly++;
            y += Math.floor(Math.pow(countFly, 1.4));
        }
    }

    int getWidth() {
        return center.getWidth();
    }

    int getX() {
        return x;
    }

    int getY() { return y; }

    void setJump() {
        this.jump = true;
        countJump = 0;
    }

    void falling() {
        countFly = speed;
        x += Math.floor(Math.pow(countFly, 1.2));
        y += Math.floor(Math.pow(countFly, 2));
    }
}