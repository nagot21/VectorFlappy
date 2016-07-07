package com.nagot.vectorflappy;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

/*
Entry point of the game
*/
public class MainActivity extends Activity {

    private MediaPlayer player;
    private int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        x = size.x;

        // Change app font
        final Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");

        // Instantiate widgets
        final TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(customFont);

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setTypeface(customFont);

        final Button buttonCredits = (Button) findViewById(R.id.buttonCredits);
        buttonCredits.setTypeface(customFont);

        // If device x axis <= 800 resize widgets size and position
        if (x <= 800) {
            title.setTextSize(50);
            title.setY(title.getY() - 20);
            buttonPlay.setY(buttonPlay.getY() - 40);
            buttonPlay.setTextSize(20);
        }

        // Creates a MediaPlayer to play the title bgm
        try {
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("title.ogg");
            player = new MediaPlayer();
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            player.setVolume(0.2f, 0.2f);
            player.setLooping(true);
            player.prepare();
            player.start();

        } catch (IOException e) {
            Log.e("error", "failed to load sound file or file is missing");
        }

        // Check if the HighScores files exist. If not creates it.
        // Also put the tag MaxScore in the file and in case it has no value attributes the value 20

        SharedPreferences prefs;
        prefs = getSharedPreferences("HighScores", MODE_PRIVATE);
        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);
        textFastestTime.setTypeface(customFont);
        int maxScore = prefs.getInt("MaxScore", 20);

        // Change the value of the textView

        textFastestTime.setText(getText(R.string.max_score) + " " + maxScore);

        // Set buttons listeners

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Creates a Dialog Box when start button is clicked

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_main);

                // Instantiate widgets

                final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
                title.setTypeface(customFont);

                final Button btnEasy = (Button) dialog.findViewById(R.id.btnEasy);
                btnEasy.setTypeface(customFont);

                final Button btnMedium = (Button) dialog.findViewById(R.id.btnMedium);
                btnMedium.setTypeface(customFont);

                final Button btnHard = (Button) dialog.findViewById(R.id.btnHard);
                btnHard.setTypeface(customFont);

                final Button btnback = (Button) dialog.findViewById(R.id.btnBackMain);
                btnback.setTypeface(customFont);

                if (dialog != null) {

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = (size.x / 2) + 100;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setLayout(width, height);
                }

                dialog.show();

                // Tap the button and initialize the game

                btnEasy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        i.putExtra("difficulty", "1");
                        startActivity(i);
                        player.stop();
                        dialog.dismiss();
                        finish();
                    }
                });

                btnMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        i.putExtra("difficulty", "2");
                        startActivity(i);
                        player.stop();
                        dialog.dismiss();
                        finish();
                    }
                });

                btnHard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        i.putExtra("difficulty", "3");
                        startActivity(i);
                        player.stop();
                        dialog.dismiss();
                        finish();
                    }
                });

                btnback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // credits button Dialog Box

        buttonCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_credits);

                // Instantiate widgets

                final TextView creditTitle = (TextView) dialog.findViewById(R.id.dialogCreditTitle);
                creditTitle.setTypeface(customFont);

                final Button nagotButton = (Button) dialog.findViewById(R.id.btnNagot);
                final Button spritesButton = (Button) dialog.findViewById(R.id.btnSprites);

                final Button backButton = (Button) dialog.findViewById(R.id.btnBack);
                backButton.setTypeface(customFont);

                if (dialog != null) {

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = (size.x / 2) + 300;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setLayout(width, height);
                }

                dialog.show();

                // Click the button nagot to go to linkedin

                nagotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent nagotLinkedin = new Intent(Intent.ACTION_VIEW);
                        nagotLinkedin.setData(Uri.parse("https://uk.linkedin.com/in/inagot"));
                        startActivity(nagotLinkedin);
                    }
                });

                // Click the button sprite to go to the artist provided link

                spritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent spritesSite = new Intent(Intent.ACTION_VIEW);
                        spritesSite.setData(Uri.parse("http://www.freepik.com/free-photos-vectors/cartoon"));
                        startActivity(spritesSite);
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // Creates a background movement using ObjectAnimator

        ObjectAnimator backgroundAnimation = ObjectAnimator.ofFloat(findViewById(R.id.backgroundImageView), "x", 40, -40);
        backgroundAnimation.setDuration(5000);
        backgroundAnimation.setRepeatCount(ValueAnimator.INFINITE);
        backgroundAnimation.setRepeatMode(ValueAnimator.REVERSE);
        backgroundAnimation.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            player.stop();
            finish();
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        player.start();
    }
}