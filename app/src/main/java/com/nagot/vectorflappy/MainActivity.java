package com.nagot.vectorflappy;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

// Begin improvements

/*
Classe de entrada do Game. Quando inicializar, será chamado o método
onCreate() da activity
*/
public class MainActivity extends Activity {

    private MediaPlayer player;
    private int x;
    Thread gameThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        x = size.x;

        // Troca a fonte da title screen

        final TextView title = (TextView) findViewById(R.id.title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
        title.setTypeface(customFont);

        // Instancia botão e coloca sua fonte

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        Typeface buttonPlayFont = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
        buttonPlay.setTypeface(buttonPlayFont);

        // Instancia botão de créditos e sua fonte

        final Button buttonCredits = (Button) findViewById(R.id.buttonCredits);
        Typeface buttonCreditsFont = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
        buttonCredits.setTypeface(buttonCreditsFont);

        if (x <= 800) {
            title.setTextSize(50);
            title.setY(title.getY() - 20);
            buttonPlay.setY(buttonPlay.getY() - 40);
            buttonPlay.setTextSize(20);
        }

        // Cria um MediaPlayer para tocar a música de abertura.

        try {
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("title.ogg");
            player = new MediaPlayer();
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            player.setVolume(0.1f, 0.1f);
            player.setLooping(true);
            player.prepare();
            player.start();

        } catch (IOException e) {
            Log.e("error", "failed to load sound file or file is missing");
        }

        // Seta o listener no botão. Ao ser clicado a Activity GameActivity.class será acionada

        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        // Checa se o arquivo HighScores existe. Caso não o cria

        prefs = getSharedPreferences("HighScores", MODE_PRIVATE);

        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);

        Typeface textFastestTimeFont = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
        textFastestTime.setTypeface(textFastestTimeFont);

        // Coloca a tag MaxScore no arquivo e lhe atribui o valor de 20 caso não tenha valor algum

        int maxScore = prefs.getInt("MaxScore", 20);

        // Muda o valor do text view para o encontrado no arquivo

        textFastestTime.setText("Max Score: " + maxScore);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Chama a dialog box ao clicar no botao start

                final Dialog dialog = new Dialog(MainActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.dialog_main);

                // Seta fonte do título

                final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);

                Typeface dialogTitle = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
                title.setTypeface(dialogTitle);

                // Seta a fonte do modo easy

                final Button btnEasy = (Button) dialog.findViewById(R.id.btnEasy);

                Typeface dialogBtnEasy = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
                btnEasy.setTypeface(dialogBtnEasy);

                // Seta a fonte do modo medium

                final Button btnMedium = (Button) dialog.findViewById(R.id.btnMedium);

                Typeface dialogBtnMedium = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
                btnMedium.setTypeface(dialogBtnMedium);

                // Seta a fonte do modo hard

                final Button btnHard = (Button) dialog.findViewById(R.id.btnHard);

                Typeface dialogBtnHard = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");
                btnHard.setTypeface(dialogBtnHard);

                if (dialog != null) {

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = (size.x / 2) + 100;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setLayout(width, height);
                }

                dialog.show();

                // Ao clicar no botao easy inicia o game

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
            }
        });

        // Faz com que apareça um dialog box com os créditos

        buttonCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Chama a dialog box ao clicar no botao start

                final Dialog dialog = new Dialog(MainActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.dialog_credits);

                // Seta fonte do título do dialog box

                Typeface dialogFont = Typeface.createFromAsset(getAssets(), "fonts/spaceranger.otf");

                // Seta título

                final TextView creditTitle = (TextView) dialog.findViewById(R.id.dialogCreditTitle);
                creditTitle.setTypeface(dialogFont);

                // Seta subtítulo

                final TextView subTitle = (TextView) dialog.findViewById(R.id.dialogTapToInfo);
                //subTitle.setTypeface(dialogFont);

                // Seta fonte do botão de créditos nagot

                final Button nagotButton = (Button) dialog.findViewById(R.id.btnNagot);
                //nagotButton.setTypeface(dialogFont);

                // Seta fonte do botão de créditos da autora dos sprites

                final Button spritesButton = (Button) dialog.findViewById(R.id.btnSprites);
                //spritesButton.setTypeface(dialogFont);

                // Seta fonte do botão back e o instancia

                final Button backButton = (Button) dialog.findViewById(R.id.btnBack);
                backButton.setTypeface(dialogFont);


                if (dialog != null) {

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = (size.x / 2) + 300;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setLayout(width, height);
                }

                dialog.show();

                // Ao clicar no botão nagot, vai para o linkedin

                nagotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent nagotLinkedin = new Intent(Intent.ACTION_VIEW);
                        nagotLinkedin.setData(Uri.parse("https://uk.linkedin.com/in/inagot"));
                        startActivity(nagotLinkedin);
                    }
                });

                // Ao clicar no botão sprite, vai para o link provido pela artista

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

        // Cria um movimento do background através da classe ObjectAnimator

        ObjectAnimator backgroundAnimation =
                ObjectAnimator.ofFloat(findViewById(R.id.backgroundImageView), "x", 40, -40);
        backgroundAnimation.setDuration(5000);
        backgroundAnimation.setRepeatCount(ValueAnimator.INFINITE);
        backgroundAnimation.setRepeatMode(ValueAnimator.REVERSE);
        backgroundAnimation.start();

    }


    // Caso o jogador aperte o botão back do smartphone o game será finalizado

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
