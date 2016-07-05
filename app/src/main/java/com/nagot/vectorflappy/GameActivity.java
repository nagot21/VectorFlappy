package com.nagot.vectorflappy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

/*
Esta atividade controla o que será mostrado no game. Não existe um layout mostrando esta activity. Ela
é totalmente gerenciada pela VFView.java que mostra os elementos na tela.
 */

public class GameActivity extends Activity {

    private VFView gameView;
    private int difficulty = 0;
    String auxDifficulty;

    /*
    No onCreate instanciamos o objeto gameView e o chamamos no setContentView.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            auxDifficulty = extras.getString("difficulty");
        }

        difficulty = Integer.parseInt(auxDifficulty);

        /*
         Na linha abaixo utilizamos a classe Display para pegar as dimensões da tela do smartphone.
         O display.getSize retorna as cordenadas x e y da tela e, para ser guardada precisa de um
         objeto do tipo Point. É justamente por isto que estamos instanciando um.
          */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        /*
        Ao criarmos o objeto gameView, passamos além de seu contexto as variáveis da posição x e y
        obtidas através do método display.getSize.
         */
        gameView = new VFView(this, size.x, size.y, difficulty);
        setContentView(gameView);
    }

    /*
    No onPause chamamos o método pause() da classe VGView. Este método pausa a thread caso
    o jogador pause ou caso algo de maior prioridade seja invocado, como uma ligação, por exemplo
     */

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    /*
    No onResume chamamos o método resume() da classe VGView. Este método despausa a thread
     */

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    // Caso o jogador aperte o botão back do smartphone o game será finalizado

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(GameActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
