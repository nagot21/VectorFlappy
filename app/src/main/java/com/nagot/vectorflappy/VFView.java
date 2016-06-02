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

    // Variáveis criadas para mostrar os valores no HUD

    private float distanceRemaining;
    private long timeTaken, timeStarted, fastestTime;
    private int screenX, screenY;
    private Context context;
    private boolean gameEnded;

    /*
    Neste construtor criamos um SurfaceHolder ourHolder para travar nossa canvas quando for desenha-la.
    Criamos também um objeto paint para desenhar na tela.
    Repare que os argumentos recebidos pelo construtor são além de seu contexto, os valores da dimensão do
    smartphone obtidas na classe GameActivity.

    Como se pode ver, logo após a variável screenY é chamado o método startGame(). Nele são inicializados
    os objetos player, enemy*, spec e também as cordenadas, para que se possa posicionar de maneira correta
    os objetos na tela.
     */

    public VFView(Context context, int x, int y) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;

        startGame();
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
     os parametros de posição do objeto na tela e também sem comportamento quando colidirem
      */

    /*
    Este método ira agir quase como o construtor. Porém, ele será chamado quando o game for recomeçar
    para não termos que resetá-lo toda vez que quisermos jogar novamente.
    Note que foi trocado x e y por screenX e screenY. Isso ocorre pois os valores de x e y estão no
    construtor, impossibilitando assim de acessá-los.
     */

    private void startGame() {
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        int numSpecs = 40;

        // Na linha abaixo são criados 40 objetos SpaceDust

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        distanceRemaining = 10000; // Quanto teremos de percorrer para finalizar o game
        timeTaken = 0; // O tempo que levamos finalizar
        timeStarted = System.currentTimeMillis(); // Age com um cronometro para armazenar o relógio

        gameEnded = false; // Variável checa se o game finalizou ou não
    }

    private void update() {

        /*
        Os 3 bloco de IF's abaixo fazem o teste de colisão dos objetos. Caso eles colidam, a nave
        inimiga terá seu X mudado para -200. Isto é, será retirada automáticamente da tela.
         */
        boolean hitDetected = false; // Variável criada para ver se o jogador foi atingido

        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            hitDetected = true; // Caso atingido, a variável ganha o valor true
            enemy1.setX(-200);
        }

        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            hitDetected = true; // Caso atingido, a variável ganha o valor true
            enemy2.setX(-200);
        }

        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            hitDetected = true; // Caso atingido, a variável ganha o valor true
            enemy3.setX(-200);
        }

        // Caso o valor de getShieldStrenght seja menor que zero o game finaliza

        if (hitDetected) {
            player.reduceShieldStrenght();
            if (player.getShieldStrenght() < 0) {
                gameEnded = true;
            }
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

        /*
        Checa se o game finalizou. Senão, retira valor da variável distanceRemaining baseado na
        speed do jogador.

        Retira também tempo do cronometro
         */

        if (!gameEnded) {
            distanceRemaining -= player.getSpeed();
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        /*
        Caso o tempo que o jogador demorou para finalizar o game seja menor que o fastestTime, a
        variável é atualizada e gameEnded setado como true
         */

        if (distanceRemaining < 0) {
            if (timeTaken < fastestTime) {
                fastestTime = timeTaken;
            }
            distanceRemaining = 0;
            gameEnded = true;
        }
    }

    /*
    Este método desenha os elementos na tela. Ele checa se o SurfaceHolder é válido. Se sim,
    Ele irá travar o canvas para que possamos desenhar nele.

    Chamamos o objeto canvas.drawColor e atribuimos uma cor a ele juntamente com seu alfa.
    Por fim, chamamos o canvas.drawBitmap especificando a imagem .png pelo método player.getBitmap(),
    pegando a posição x por player.getX(), de y por player.getY() e, por fim, desenhamos chamando paint.
    Fazemos o mesmo com as naves inimigas.

    Em seguida, fazemos um procedimento diferente com os objetos armazenados em dustList. Ao invés de usar
    um bitmap, setamos a cor que queremos utilizar de fundo por intermédio do método paint.setColor()
    e criamos o nosso desenho através do método canvas.drawPoint().

    Por fim, desenhamos na tela o HUD do game.

    Por último, destravamos o canvas por intermédio de nossa variável ourHolder

     */

    private void draw() {
        if (ourHolder.getSurface().isValid()) {

            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Código abaixo é para fins de debug. Ele irá criar um retangulo branco atrás do bitmap

            paint.setColor(Color.argb(255, 255, 255, 255));

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

            /*
             Aqui criamos o HUD do game.
              */

            paint.setTextAlign(Paint.Align.LEFT); // Alinhamos o texto a esquerda
            paint.setColor(Color.argb(255, 255, 255, 255)); // Dizemos que a cor do texto será branca e sem alpha
            paint.setTextSize(25); // Tamanho do texto
            canvas.drawText("Fastest: " + fastestTime + "s", 10, 20, paint); // Aqui temos 4 parâmetros. O primeiro é o texto, seguido da coordenada X, Y, comando para escrever
            canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 20, paint); // Repete o mesmo procedimento mencionado anteriormente
            canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM", screenX / 2, screenY - 80, paint); // Repete o mesmo procedimento mencionado anteriormente
            canvas.drawText("Shield: " + player.getShieldStrenght(), 10, screenY - 80, paint); // Repete o mesmo procedimento mencionado anteriormente
            canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 80, paint); // Repete o mesmo procedimento mencionado anteriormente

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


