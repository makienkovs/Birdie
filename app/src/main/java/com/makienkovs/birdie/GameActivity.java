package com.makienkovs.birdie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private Surface surface;
    private Timer timer;

    private static int difficulty = 10;
    private static int best = 0;
    private static boolean day = true;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_DIFFICULTY = "difficulty";
    public static final String APP_PREFERENCES_BEST = "best";
    public static final String APP_PREFERENCES_DAY = "day";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        boolean fps = getIntent().getBooleanExtra("fps", false);
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();
        readParams();
        surface = new Surface(this, difficulty, day, fps);
        surface.setWillNotDraw(false);
        setContentView(surface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        surface.onResume();
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        surface.onPause();
        stopTimer();
        writeParams();
    }

    private void writeParams() {
        editor.putInt(APP_PREFERENCES_BEST, best);
        editor.apply();
    }

    private void readParams() {
        if (settings.contains(APP_PREFERENCES_DIFFICULTY)) {
            difficulty = settings.getInt(APP_PREFERENCES_DIFFICULTY, 10);
        }
        if (settings.contains(APP_PREFERENCES_BEST)) {
            best = settings.getInt(APP_PREFERENCES_BEST, 0);
        }
        if (settings.contains(APP_PREFERENCES_DAY)) {
            day = settings.getBoolean(APP_PREFERENCES_DAY, true);
        }
    }

    public static int getBest() {
        return best;
    }

    public static void setBest(int best) { GameActivity.best = best; }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                surface.postInvalidate();
            }
        }, 1, 1);
    }

    private void stopTimer() {
        timer.cancel();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        surface.touch(event.getAction() == MotionEvent.ACTION_DOWN);
        return super.onTouchEvent(event);
    }
}