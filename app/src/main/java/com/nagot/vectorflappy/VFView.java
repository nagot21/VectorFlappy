package com.nagot.vectorflappy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Esta classe desenha todos os elementos na tela e controla alguns outros elementos
 */
public class VFView extends SurfaceView implements Runnable {

    volatile boolean playing;
    boolean loaded = false;
    Thread gameThread = null;
    private PlayerShip player;
    public EnemyShip enemy1, enemy2, enemy3, enemy4;

    //Array abaixo foi criado para armazenar os diversos objetos SpaceDust

    public ArrayList<SpaceDust> dustList = new ArrayList<>();
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    // Variáveis criadas para mostrar os valores no HUD

    private float distanceRemaining;
    private long timeTaken, timeStarted, fastestTime;
    private int screenX, screenY;
    private int score, maxScore;
    private int difficulty;
    private int auxExplosion = 0;
    private int auxBoost = 0;
    private final int EXPLOSION_FPS = 200;
    private Context context;
    private boolean gameEnded;
    private boolean isReady = false;
    private boolean isFirst;

    // Variáveis para colocar som no game

    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int destroyedStreamId = 0;
    int win = -1;

    // Variáveis para persistir o high score

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    /*
    Neste construtor criamos um SurfaceHolder ourHolder para travar nossa canvas quando for desenha-la.
    Criamos também um objeto paint para desenhar na tela.
    Repare que os argumentos recebidos pelo construtor são além de seu contexto, os valores da dimensão do
    smartphone obtidas na classe GameActivity.

    Como se pode ver, logo após a variável screenY é chamado o método startGame(). Nele são inicializados
    os objetos player, enemy*, spec e também as cordenadas, para que se possa posicionar de maneira correta
    os objetos na tela.

    Por fim, instanciamos o objeto SoundPool para colocar a trilha sonora do game
     */

    public VFView(Context context, int x, int y, int difficulty) {
        super(context);
        this.context = context;
        this.difficulty = difficulty;

        Log.i("dificuldade", "dificuldade: " + difficulty);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0); // Instancia o objeto

        /*
        Para o podermos especificar os arquivos que serão utilizados, precisamos colocá-los em um
        try/catch para que o erro seja tratado caso o arquivo não seja encontrado
         */

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error", "failed to load sound file or file is missing");
        }

        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;

        /*
         Na linha abaixo, o parâmetro pega as referência de um arquivo chamado HighScores. Caso ele
         não exista será criado
          */

        prefs = context.getSharedPreferences("HighScores", context.MODE_PRIVATE);

        // Inicializa o editor

        editor = prefs.edit();

        // Carrega o valor do arquivo HighScores. Caso não tenha nenhum valor, irá colocar o default de 1000000

        //fastestTime = prefs.getLong("FastestTime", 1000000);
        maxScore = prefs.getInt("MaxScore", 20);

        isFirst = true;

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
            if (enemy1.getAux() <= 1) {
                if (enemy1.isNoHit()) {
                    score++;
                }
            }

            if (enemy2.getAux() <= 1) {
                if (enemy2.isNoHit()) {
                    score++;
                }
            }

            if (enemy3.getAux() <= 1) {
                if (enemy3.isNoHit()) {
                    score++;
                }
            }

            if (screenX > 1000) {

                if (enemy4.getAux() <= 1) {
                    if (enemy4.isNoHit()) {
                        score++;
                    }
                }
            }

            /*if (screenX > 1200) {

                if (enemy5.getAux() <= 1) {
                    if (enemy5.isNoHit()) {
                        score++;
                    }
                }
            }*/
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
        isReady = false;
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY, difficulty);
        enemy1.setEnemyOne(context);
        enemy2 = new EnemyShip(context, screenX, screenY, difficulty);
        enemy3 = new EnemyShip(context, screenX, screenY, difficulty);
        score = 0;
        auxExplosion = 0;

        if (screenX > 1000) {
            enemy4 = new EnemyShip(context, screenX, screenY, difficulty);
        }

        Log.i("screenX", "screenX: " + screenX);

        /*if (screenX > 1200) {
            enemy5 = new EnemyShip(context, screenX, screenY, difficulty);
        }*/

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


        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        if (loaded) {
            soundPool.play(start, 0.1f, 0.1f, 0, 0, 1);
        }
    }

    private void update() {

        /*
        Os 3 bloco de IF's abaixo fazem o teste de colisão dos objetos. Caso eles colidam, a nave
        inimiga terá seu X mudado para -200. Isto é, será retirada automáticamente da tela.
         */

        // Caso o jogador passe o inimigo sem bater score ganha +1

        if (enemy1.getX() < player.getX()) {
            enemy1.setNoHit(true);
            enemy1.setAux(1);
        }

        if (enemy2.getX() < player.getX()) {
            enemy2.setNoHit(true);
            enemy2.setAux(1);
        }

        if (enemy3.getX() < player.getX()) {
            enemy3.setNoHit(true);
            enemy3.setAux(1);
        }

        if (screenX > 1000) {
            if (enemy4.getX() < player.getX()) {
                enemy4.setNoHit(true);
                enemy4.setAux(1);
            }
        }

        /*if (screenX > 1200) {
            if (enemy5.getX() < player.getX()) {
                enemy5.setNoHit(true);
                enemy5.setAux(1);
            }
        }*/

        boolean hitDetected = false; // Variável criada para ver se o jogador foi atingido

        // Caso o jogador tenha sido atingido, retira as naves de cena

        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            hitDetected = true; // Caso atingido, a variável ganha o valor true
            enemy1.setX(-400);
        }

        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            hitDetected = true; // Caso atingido, a variável ganha o valor true
            enemy2.setX(-400);
        }

        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            hitDetected = true; // Caso atingido, a variável ganha o valor true
            enemy3.setX(-400);
        }

        if (screenX > 1000) {
            if (Rect.intersects(player.getHitBox(), enemy4.getHitBox())) {
                hitDetected = true; // Caso atingido, a variável ganha o valor true
                enemy4.setX(-400);
            }
        }

        /*if (screenX > 1200) {
            if (Rect.intersects(player.getHitBox(), enemy5.getHitBox())) {
                hitDetected = true; // Caso atingido, a variável ganha o valor true
                enemy5.setX(-400);
            }
        }*/

        // Caso o valor de getShieldStrenght seja menor que zero o game finaliza

        if (player.getY() == player.getMaxY()) {
            player.setAuxSound(1); // Esta variável serve de controle para que o som de explosão toque somente uma vez
            if ((player.getAuxSound() >= 1) && (player.getAuxSound() < 2)) {
                gameEnded = true;
                if (gameEnded) {
                    destroyedStreamId = soundPool.play(destroyed, 0.1f, 0.1f, 0, 0, 1); // Se a nave for destruida, tocará o som destroyed
                    player.setX(-500);
                }
            }
        }

        if (hitDetected) {
            if (player.getAuxSound() < 1) { // Usa a variável de controle. Caso seja menor que 1 ira tocar o som de colisao
                soundPool.play(bump, 0.1f, 0.1f, 0, 0, 1); // Se a nave bater, será tocado o som de bump
                player.reduceShieldStrenght();
            }
            if (player.getShieldStrenght() < 0) {
                player.setAuxSound(1); // Esta variável serve de controle para que o som de explosão toque somente uma vez
                if ((player.getAuxSound() >= 1) && (player.getAuxSound() < 2)) {
                    gameEnded = true;
                    if (gameEnded) {
                        destroyedStreamId = soundPool.play(destroyed, 0.1f, 0.1f, 0, 0, 1); // Se a nave for destruida, tocará o som destroyed
                    }
                }
            }
        }

        if (gameEnded) {
            isFirst = false;
        }

        if (!isReady && isFirst) {
            player.getY();
        } else {
            player.update();
            enemy1.update(player.getSpeed());
            enemy1.setScore(score);
            enemy2.update(player.getSpeed());
            enemy2.setScore(score);
            enemy3.update(player.getSpeed());
            enemy3.setScore(score);

            // Caso a resolução da tela seja maior que 1000 no eixo x coloca mais um inimigo na tela

            if (screenX > 1000) {
                enemy4.update(player.getSpeed());
                enemy4.setScore(score);
            }
        }

        // Caso a resolução da tela seja maior que 1200 no eixo x coloca mais um inimigo na tela

        /*if (screenX > 1200) {
            enemy4.update(player.getSpeed());
        }*/

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

       /* if (!gameEnded) {
            distanceRemaining -= player.getSpeed();
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        /*
        Caso o tempo que o jogador demorou para finalizar o game seja menor que o fastestTime, a
        variável é atualizada e gameEnded setado como true
         */

        /*if (distanceRemaining < 0) {
            soundPool.play(win, 1, 1, 0, 0, 1); // Se o jogador finalizar o game tocará a a música win
            if (timeTaken < fastestTime) {
                editor.putLong("fastestTime", timeTaken); // Coloca o novo valor no buffer
                editor.commit(); // Joga o novo valor no arquivo
                fastestTime = timeTaken;
            }
            distanceRemaining = 0;
            gameEnded = true;
        }*/
        if (gameEnded) {
            if (score > maxScore) {
                editor.putInt("MaxScore", score);
                editor.commit();
                maxScore = score;
            }
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

           /* paint.setColor(Color.argb(255, 255, 255, 255));

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

            // Fim do bloco de teste */

            if (gameEnded && player.getAuxExplosion() < 1) {
                player.stopBoosting();
                int aux = 0;
                int x = player.getX();
                int y = player.getY();
                while (aux < EXPLOSION_FPS) {
                    canvas.drawBitmap(
                            player.getExplosion().get(auxExplosion),
                            x,
                            y,
                            paint);
                    aux++;
                }
                auxExplosion++;

                if (auxExplosion == 7) {
                    auxExplosion = 0;
                    player.setX(-500);
                    player.setAuxExplosion(1);
                }

            } else {
                canvas.drawBitmap(
                        player.getBitmap(),
                        player.getX(),
                        player.getY(),
                        paint);
            }

            if (player.isBoosting()) {
                canvas.drawBitmap(
                        player.getBitmapTurbo().get(auxBoost),
                        player.getX(),
                        player.getY(),
                        paint);
                auxBoost++;

                if (auxBoost > 1) {
                    auxBoost = 0;
                }
            }

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

            // Caso a resolução da tela seja maior que 1000 em seu eixo x coloca mais um inimigo

            if (screenX > 1000) {
                canvas.drawBitmap(
                        enemy4.getBitmap(),
                        enemy4.getX(),
                        enemy4.getY(),
                        paint);
            }

            // Caso a resolução da tela seja maior que 1200 em seu eixo x coloca mais um inimigo

            /*if (screenX > 1200) {
                canvas.drawBitmap(
                        enemy5.getBitmap(),
                        enemy5.getX(),
                        enemy5.getY(),
                        paint);
            }*/

            paint.setColor(Color.argb(255, 255, 255, 255));

            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            /*
             Aqui criamos o HUD do game.
              */

            // HUD avisando que o game está para começar

            if (!isReady && isFirst) {
                paint.setTextAlign(Paint.Align.CENTER); // Alinhamos o texto no centro
                paint.setTextSize(100); // Tamanho do texto
                Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/spaceranger.otf");
                paint.setTypeface(font);
                paint.setColor(Color.argb(255, 255, 255, 0)); // Dizemos que a cor do texto será amarelo e sem alpha
                canvas.drawText("GET READY AND TAP TO FLY!", screenX / 2, screenY / 2, paint);
            } else if (!gameEnded) {
                if (screenX <= 800) {
                    paint.setTextAlign(Paint.Align.LEFT); // Alinhamos o texto a esquerda
                    paint.setColor(Color.argb(255, 255, 255, 255)); // Dizemos que a cor do texto será branca e sem alpha
                    paint.setTextSize(20); // Tamanho do texto
                    Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/spaceranger.otf");
                    paint.setTypeface(font);

                    canvas.drawText("Shield: " + player.getShieldStrenght(), 10, 40, paint); // Repete o mesmo procedimento mencionado anteriormente
                    canvas.drawText("Score: " + score, screenX / 2, 40, paint); // Repete o mesmo procedimento mencionado anteriormente
                } else {
                    paint.setTextAlign(Paint.Align.LEFT); // Alinhamos o texto a esquerda
                    paint.setColor(Color.argb(255, 255, 255, 255)); // Dizemos que a cor do texto será branca e sem alpha
                    paint.setTextSize(40); // Tamanho do texto
                    Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/spaceranger.otf");
                    paint.setTypeface(font);

                    canvas.drawText("Shield: " + player.getShieldStrenght(), 10, 40, paint); // Repete o mesmo procedimento mencionado anteriormente
                    canvas.drawText("Score: " + score, screenX / 2, 40, paint); // Repete o mesmo procedimento mencionado anteriormente
                }
            } else {
                if ((screenX > 2500) && (screenY >= 1600)) {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER); // Alinhamos o texto no centro
                        paint.setTextSize(350); // Tamanho do texto
                        canvas.drawText("Game Over", screenX / 2, 500, paint);
                        paint.setTextSize(100);
                        canvas.drawText("Score: " + score, screenX / 2, 700, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 800, paint);
                        paint.setTextSize(220); // Tamanho do texto
                        paint.setColor(Color.argb(255, 255, 255, 0)); // Dizemos que a cor do texto será amarelo e sem alpha
                        canvas.drawText("Tap to replay", screenX / 2, 1100, paint);
                    }
                } else if (screenX <= 800) {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER); // Alinhamos o texto no centro
                        paint.setTextSize(100); // Tamanho do texto
                        canvas.drawText("Game Over", screenX / 2, 125, paint);
                        paint.setTextSize(25);
                        canvas.drawText("Score: " + score, screenX / 2, 175, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 205, paint);
                        paint.setTextSize(50); // Tamanho do texto
                        paint.setColor(Color.argb(255, 255, 255, 0)); // Dizemos que a cor do texto será branca e sem alpha
                        canvas.drawText("Tap to replay", screenX / 2, 280, paint);
                    }
                } else {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER); // Alinhamos o texto no centro
                        paint.setTextSize(200); // Tamanho do texto
                        canvas.drawText("Game Over", screenX / 2, 250, paint);
                        paint.setTextSize(50);
                        canvas.drawText("Score: " + score, screenX / 2, 350, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 410, paint);
                        paint.setTextSize(100); // Tamanho do texto
                        paint.setColor(Color.argb(255, 255, 255, 0)); // Dizemos que a cor do texto será amarela e sem alpha
                        canvas.drawText("Tap to replay", screenX / 2, 560, paint);
                    }
                }
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // Formata o tempo da variável timeTaken para ser legível por humanos METODO ABAIXO SERA INUTILIZADO

    private String formatTime(long time) {
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100) {
            strThousandths = "0" + thousandths;
        }
        if (thousandths < 10) {
            strThousandths = "0" + strThousandths;
        }
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
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
                isReady = true;
                player.setBoosting();
                /*if (player.isBoosting()) {
                    player.getBitmapTurbo();
                } else {
                    player.setBitmap(context);
                } */
                if (gameEnded && player.getX() == -500) {
                    soundPool.stop(destroyedStreamId);
                    startGame();
                }
                break;
        }
        return true;
    }
}


