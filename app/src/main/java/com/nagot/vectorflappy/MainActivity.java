package com.nagot.vectorflappy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
