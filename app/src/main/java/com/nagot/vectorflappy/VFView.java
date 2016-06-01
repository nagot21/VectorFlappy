package com.nagot.vectorflappy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Esta classe desenha todos os elementos na tela e controla alguns outros elementos
 */
public class VFView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    public EnemyShip enemy1, enemy2, enemy3;

    //Array abaixo foi criado para armazenar os diversos objetos SpaceDust

    public ArrayList<SpaceDust> dustList = new ArrayList<>();
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    /*
    Neste construtor criamos um SurfaceHolder ourHolder para travar nossa canvas quando for desenha-la.
    Criamos também um objeto paint para desenhar na tela.
    Repare que os argumentos recebidos pelo construtor são além de seu contexto, os valores da dimensão do
    smartphone obtidas na classe GameActivity.

    Como se pode ver, são passados aos objetos player, enemy* e spec também as cordenadas, para que se possa
    posicionar de maneira correta os objetos na tela
     */

    public VFView(Context context, int x, int y) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        player = new PlayerShip(context, x, y);
        enemy1 = new EnemyShip(context, x, y);
        enemy2 = new EnemyShip(context, x, y);
        enemy3 = new EnemyShip(context, x, y);

        int numSpecs = 40;

    // Na linha abaixo são criados 40 objetos SpaceDust

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }
    }

    /*
    Aqui damos override no método run, que será executado toda vez que esta classe for chamada.
    Quando ela for executada, chamará o método update(), draw() e control().
     */
    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    /*
     Chama o método update() da classe PlayerShip, EnemyShip e SpaceDust. Estes métodos irão ditar
     os parametros de posição do objeto na tela. REESCREVER...
      */

    private void update() {
        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())){
            enemy1.setX(-200);
        }

        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())){
            enemy2.setX(-200);
        }

        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())){
            enemy3.setX(-200);
        }

        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        /*
        O enchanced FOR criado abaixo é utilizado pois com ele fica mais fácil percorer uma coleção
        de itens. O que ele está fazendo é dar um update para cada objeto no array
         */

        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }
    }

    /*
    Este método desenha os elementos na tela. Ele checa se o SurfaceHolder é válido. Se sim,
    Ele irá travar o canvas para que possamos desenhar nele.
    Chamamos o objeto canvas.drawColor e atribuimos uma cor a ele juntamente com seu alfa.
    Por fim, chamamos o canvas.drawBitmap especificando a imagem .png pelo método player.getBitmap(),
    pegando a posição x por player.getX(), de y por player.getY() e, por fim, desenhamos chamando paint.
    Fazemos o mesmo com as naves inimigas.
    Por fim, fazemos um procedimento diferente com os objetos armazenados em dustList. Ao invés de usar
    um bitmap, setamos a cor que queremos utilizar de fundo por intermédio do método paint.setColor()
    e criamos o nosso desenho através do método canvas.drawPoint()

    Por último, destravamos o canvas por intermédio de nossa variável ourHolder

     */

    private void draw() {
        if (ourHolder.getSurface().isValid()) {

            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Código abaixo é para fins de debug

            paint.setColor(Color.argb(255,255,255,255));

            canvas.drawRect(player.getHitBox().left,
                    player.getHitBox().top,
                    player.getHitBox().right,
                    player.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy1.getHitBox().left,
                    enemy1.getHitBox().top,
                    enemy1.getHitBox().right,
                    enemy1.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy2.getHitBox().left,
                    enemy2.getHitBox().top,
                    enemy2.getHitBox().right,
                    enemy2.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy3.getHitBox().left,
                    enemy3.getHitBox().top,
                    enemy3.getHitBox().right,
                    enemy3.getHitBox().bottom,
                    paint);

            // Fim do bloco de teste

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy1.getBitmap(),
                    enemy1.getX(),
                    enemy1.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy2.getBitmap(),
                    enemy2.getX(),
                    enemy2.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy3.getBitmap(),
                    enemy3.getX(),
                    enemy3.getY(),
                    paint);

            paint.setColor(Color.argb(255, 255, 255, 255));

            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    /*
    Chama o método da classe Thread sleep(). Este método trabalha com milisegundos. Para dar a sensação de 60 FPS,
     dividimos 1000/60 onde o resultado é aproximadamente 17.
     */

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    /*
    Aqui pausamos a thread. Caso playing = false chamaremos a classe join(), que espera a thread morrer.
     */

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    /*
    Caso playing = true, a thread é começa a rodar novamente.
     */

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        return true;
    }
}


