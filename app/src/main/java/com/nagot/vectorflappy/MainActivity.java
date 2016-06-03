package com.nagot.vectorflappy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
Classe de entrada do Game. Quando inicializar, será chamado o método
onCreate() da activity
*/
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Seta o listener no botão. Ao ser clicado a Activity GameActivity.class será acionada

        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        // Checa se o arquivo HighScores existe. Caso não o cria

        prefs = getSharedPreferences("HighScores", MODE_PRIVATE);

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);

        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);

        // Coloca a tag fastestTime no arquivo e lhe atribui o valor de 1000000 caso não tenha valor algum

        long fastestTime = prefs.getLong("fastestTime", 1000000);

        // Muda o valor do text view para o encontrado no arquivo

        textFastestTime.setText("Fastest Time: " + fastestTime);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    // Caso o jogador aperte o botão back do smartphone o game será finalizado

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
