package com.makienkovs.birdie;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int volume = 10;
    private int difficulty = 10;
    private boolean day = true;
    private boolean night = false;
    private boolean fps = false;
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_VOLUME = "volume";
    public static final String APP_PREFERENCES_DIFFICULTY = "difficulty";
    public static final String APP_PREFERENCES_DAY = "day";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private int fpsCount;
    private Sound sound;

    @SuppressLint({"SourceLockedOrientationActivity", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();
        readParams();
        fpsCount = 0;
        sound = new Sound(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Pacifico-Regular.ttf");
        TextView header = findViewById(R.id.header);
        header.setTypeface(typeface);
        Button start = findViewById(R.id.start);
        start.setTypeface(typeface);
        Button param = findViewById(R.id.param);
        param.setTypeface(typeface);
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeParams();
        sound.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readParams();
        sound = new Sound(this);
        fpsCount = 0;
    }

    public void startGame(View v) {
        sound.playTap();
        Intent game = new Intent(MainActivity.this, GameActivity.class);
        game.putExtra("fps", fps);
        startActivity(game);
    }

    private void readParams() {
        if (settings.contains(APP_PREFERENCES_VOLUME)) {
            volume = settings.getInt(APP_PREFERENCES_VOLUME, 10);
        }
        if (settings.contains(APP_PREFERENCES_DIFFICULTY)) {
            difficulty = settings.getInt(APP_PREFERENCES_DIFFICULTY, 10);
        }
        if (settings.contains(APP_PREFERENCES_DAY)) {
            day = settings.getBoolean(APP_PREFERENCES_DAY, true);
            night = !settings.getBoolean(APP_PREFERENCES_DAY, true);
        }
    }

    private void writeParams() {
        editor.putInt(APP_PREFERENCES_VOLUME, volume);
        editor.putInt(APP_PREFERENCES_DIFFICULTY, difficulty);
        editor.putBoolean(APP_PREFERENCES_DAY, day);
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    public void param(View v) {
        sound.playTap();
        final View settingsLayout = getLayoutInflater().inflate(R.layout.settings, null);

        final Switch daySwitch = settingsLayout.findViewById(R.id.day);
        final Switch nightSwitch = settingsLayout.findViewById(R.id.night);
        final SeekBar volumeSeekBar = settingsLayout.findViewById(R.id.volume);
        final SeekBar difficultySeekBar = settingsLayout.findViewById(R.id.difficulty);
        final TextView percentVolume = settingsLayout.findViewById(R.id.percentVolume);
        final TextView percentDifficulty = settingsLayout.findViewById(R.id.percentDifficulty);

        volumeSeekBar.setProgress(volume);
        difficultySeekBar.setProgress(difficulty);
        daySwitch.setChecked(day);
        nightSwitch.setChecked(night);
        percentVolume.setText("" + volume + "%");
        percentDifficulty.setText("" + difficulty + "%");

        daySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sound.playTap();
                day = isChecked;
                night = !isChecked;
                nightSwitch.setChecked(night);
            }
        });

        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                night = isChecked;
                day = !isChecked;
                daySwitch.setChecked(day);
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                percentVolume.setText("" + value + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                volume = value;
                sound.setVolume(volume);
                percentVolume.setText("" + volume + "%");
            }
        });

        difficultySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                percentDifficulty.setText("" + value + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                difficulty = value;
                percentDifficulty.setText("" + difficulty + "%");
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.Settings)
                .setPositiveButton(R.string.Ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sound.playTap();
                                writeParams();
                            }
                        })
                .setView(settingsLayout)
                .setIcon(R.drawable.settings)
                .setCancelable(false)
                .create()
                .show();
    }

    public void fps(View v) {
        fpsCount++;
        if (fpsCount == 10) {
            fps = !fps;
            fpsCount = 0;
            if (fps) {
                Toast toast = Toast.makeText(this, getText(R.string.Fpson), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, getText(R.string.Fpsoff), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }
}