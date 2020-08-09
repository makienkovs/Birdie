package com.makienkovs.birdie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class Sound {
    private SoundPool sounds;
    private int hitSound;
    private int jumpSound;
    private int tapSound;
    private Context context;

    private int volume = 10;
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_VOLUME = "volume";
    private SharedPreferences settings;

    @SuppressLint("CommitPrefEdits")
    Sound(Context context) {
        this.context = context;
        settings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        readParams();
        createSoundPool();
    }

    private void readParams() {
        if (settings.contains(APP_PREFERENCES_VOLUME)) {
            volume = settings.getInt(APP_PREFERENCES_VOLUME, 10);
        }
    }

    private void createSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(10)
                .build();

        hitSound = sounds.load(context, R.raw.hit, 1);
        jumpSound = sounds.load(context, R.raw.jump, 1);
        tapSound = sounds.load(context, R.raw.tap, 1);
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    void playHit() {
        try {
            sounds.play(hitSound, volume / 100f, volume/ 100f, 1, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void playJump() {
        try {
            sounds.play(jumpSound, volume / 100f, volume / 100f, 1, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void playTap() {
        try {
            sounds.play(tapSound, volume / 100f, volume / 100f, 1, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void release() {
        sounds.release();
        sounds = null;
    }
}
