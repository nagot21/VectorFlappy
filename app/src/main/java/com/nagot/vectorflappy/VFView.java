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
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
This class draw the elements on the screen and control the game
 */
public class VFView extends SurfaceView implements Runnable {

    volatile boolean playing;
    boolean loaded = false;
    Thread gameThread = null;
    private PlayerShip player;
    public EnemyShip enemy1, enemy2, enemy3, enemy4;
    public ArrayList<SpaceDust> dustList = new ArrayList<>();
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private int screenX, screenY;
    private int score, maxScore;
    private int difficulty;
    private int auxExplosion = 0;
    private int auxBoost = 0;
    private int auxGetOut = 0;
    private final int EXPLOSION_FPS = 200;
    private final float VOLUME = 0.2f;
    private Context context;
    private boolean gameEnded;
    private boolean isReady = false;
    private boolean isFirst;

    // SoundPool variables

    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int destroyedStreamId = 0;
    int win = -1;

    // Variables to persist the high score

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // In the constructor we pass a context, x and y coordinates and the difficulty of the game

    public VFView(Context context, int x, int y, int difficulty) {
        super(context);
        this.context = context;
        this.difficulty = difficulty;

        // Instantiate soundPool

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        // Get soundPool assets

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

        // Check if the HighScores files exist. If not creates it.

        prefs = context.getSharedPreferences("HighScores", context.MODE_PRIVATE);
        editor = prefs.edit();

        // Put the tag MaxScore in the file and in case it has no value attributes the value 20
        maxScore = prefs.getInt("MaxScore", 20);

        isFirst = true;

        // Instantiate 40 objects SpaceDust and add them to a ArrayList

        int numSpecs = 40;

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        // Calls method startGame

        startGame();
    }

    /*
    Control how the game will behave calling update() that calls the update method of other classes
    Also, controls the score, draw and control
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
            draw();
            control();
        }
    }

    //Instantiate theship and all te enemies. Also, set some variable attributes

    private void startGame() {
        setAuxGetOut(0);
        isReady = false;
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY, difficulty);
        enemy1.setEnemyOne(context);
        enemy2 = new EnemyShip(context, screenX, screenY, difficulty);
        enemy2.setEnemyTwo(context);
        enemy3 = new EnemyShip(context, screenX, screenY, difficulty);
        enemy3.setEnemyThree(context);
        score = 0;
        auxExplosion = 0;

        if (screenX > 1000) {
            enemy4 = new EnemyShip(context, screenX, screenY, difficulty);
        }

        // This variable controls if the game is over or not

        gameEnded = false;

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        if (loaded) {
            soundPool.play(start, VOLUME, VOLUME, 0, 0, 1);
        }
    }

    // This method will control all the update methods of other classes, sending them new values to their variables

    private void update() {
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

        // Variable controls if the player was hit or not

        boolean hitDetected = false;

        // In case of hit the following will be executed and the enemy will be taken away of the screen and the variable will be assigned with a new value

        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            hitDetected = true;
            enemy1.setX(-400);
        }

        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            hitDetected = true;
            enemy2.setX(-400);
        }

        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            hitDetected = true;
            enemy3.setX(-400);
        }

        if (screenX > 1000) {
            if (Rect.intersects(player.getHitBox(), enemy4.getHitBox())) {
                hitDetected = true;
                enemy4.setX(-400);
            }
        }

        // If the player goes out of the lower part of the screen he/she will loose and the sound of destroyed will play

        if (player.getY() == player.getMaxY()) {
            player.setAuxSound(1);
            if ((player.getAuxSound() >= 1) && (player.getAuxSound() < 2)) {
                gameEnded = true;
                if (gameEnded) {
                    destroyedStreamId = soundPool.play(destroyed, VOLUME, VOLUME, 0, 0, 1);
                    player.setX(-500);
                }
            }
        }

        // If the player gets a hit the hit bump sound will play
        // If the player shields in < 0 then the game is over and the destroyed sound will play

        if (hitDetected) {
            if (player.getAuxSound() < 1) {
                soundPool.play(bump, VOLUME, VOLUME, 0, 0, 1);
                player.reduceShieldStrenght();
            }
            if (player.getShieldStrenght() < 0) {
                player.setAuxSound(1);
                if ((player.getAuxSound() >= 1) && (player.getAuxSound() < 2)) {
                    gameEnded = true;
                    if (gameEnded) {
                        destroyedStreamId = soundPool.play(destroyed, VOLUME, VOLUME, 0, 0, 1);
                    }
                }
            }
        }

        // In case the game's already been played once the the variable will have a new value

        if (gameEnded) {
            isFirst = false;
        }

        // If the player is not playing yet the ship will stand still in a fixed position

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

            // If the screen resolution in the X axis is bigger then 1000 another enemyShip will be displayed

            if (screenX > 1000) {
                enemy4.update(player.getSpeed());
                enemy4.setScore(score);
            }
        }

        // Update the spaceDust update method

        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }

        // If the game is ended and the score archived  is higher then the previous one change it in the file created in the beginning of the game

        if (gameEnded) {
            if (score > maxScore) {
                editor.putInt("MaxScore", score);
                editor.commit();
                maxScore = score;
            }
        }
    }

    // Draw the objects on the screen by checking if the surface is valid. After that locks the canvas to draw and only unlocks at the end

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/spaceranger.otf");
            paint.setTypeface(font);
            canvas.drawColor(Color.argb(255, 0, 0, 0));

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

            if (screenX > 1000) {
                canvas.drawBitmap(
                        enemy4.getBitmap(),
                        enemy4.getX(),
                        enemy4.getY(),
                        paint);
            }

            paint.setColor(Color.argb(255, 255, 255, 255));

            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // Here we create the game HUD. It has some if inside to adjust the draw to different types of screens

            // The following is draw on the screen when the player ship is waiting for the first tap

            if (!isReady && isFirst) {
                if (screenX <= 800) {
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(50);
                    paint.setColor(Color.argb(255, 255, 255, 0));
                    canvas.drawText("GET READY AND TAP TO FLY!", screenX / 2, screenY / 2, paint);
                } else if (screenX >= 1100 && screenX <= 1280) {
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(80); // Tamanho do texto
                    paint.setColor(Color.argb(255, 255, 255, 0));
                    canvas.drawText("GET READY AND TAP TO FLY!", screenX / 2, screenY / 2, paint);
                } else if (screenX >= 2560 && screenY >= 1400) {
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(150);
                    paint.setColor(Color.argb(255, 255, 255, 0));
                    canvas.drawText("GET READY AND TAP TO FLY!", screenX / 2, screenY / 2, paint);
                } else {
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(100);
                    paint.setColor(Color.argb(255, 255, 255, 0));
                    canvas.drawText("GET READY AND TAP TO FLY!", screenX / 2, screenY / 2, paint);
                }

                // After the first tap, the game HUD is draw

            } else if (!gameEnded) {
                if (screenX <= 800) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(20);
                    canvas.drawText("Shield: " + player.getShieldStrenght(), 10, 40, paint);
                    canvas.drawText("Score: " + score, screenX / 2, 40, paint);
                } else if (screenX >= 2560 && screenY >= 1400) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(60);
                    canvas.drawText("Shield: " + player.getShieldStrenght(), 10, 40, paint);
                    canvas.drawText("Score: " + score, screenX / 2, 40, paint);
                } else {
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(40);
                    canvas.drawText("Shield: " + player.getShieldStrenght(), 10, 40, paint);
                    canvas.drawText("Score: " + score, screenX / 2, 40, paint);
                }

                // In case the game is over the following screen will be draw

            } else {
                if ((screenX > 2500) && (screenY >= 1600)) {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setTextSize(350);
                        canvas.drawText("Game Over", screenX / 2, 500, paint);
                        paint.setTextSize(100);
                        canvas.drawText("Score: " + score, screenX / 2, 700, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 800, paint);
                        paint.setTextSize(220);
                        paint.setColor(Color.argb(255, 255, 255, 0));
                        canvas.drawText("Tap to replay", screenX / 2, 1100, paint);
                        paint.setTextSize(200);
                        paint.setColor(Color.argb(255, 255, 140, 0));
                        canvas.drawText("Press back to title screen", screenX / 2, 1400, paint);
                    }
                } else if (screenX >= 2560 && screenY >= 1400) {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setTextSize(300);
                        canvas.drawText("Game Over", screenX / 2, 250, paint);
                        paint.setTextSize(70);
                        canvas.drawText("Score: " + score, screenX / 2, 400, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 460, paint);
                        paint.setTextSize(200);
                        paint.setColor(Color.argb(255, 255, 255, 0));
                        canvas.drawText("Tap to replay", screenX / 2, 680, paint);
                        paint.setTextSize(100);
                        paint.setColor(Color.argb(255, 255, 140, 0));
                        canvas.drawText("Press back to title screen", screenX / 2, 900, paint);
                    }
                } else if (screenX >= 1100 && screenX <= 1280) {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setTextSize(180);
                        canvas.drawText("Game Over", screenX / 2, 220, paint);
                        paint.setTextSize(40);
                        canvas.drawText("Score: " + score, screenX / 2, 300, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 360, paint);
                        paint.setTextSize(80);
                        paint.setColor(Color.argb(255, 255, 255, 0));
                        canvas.drawText("Tap to replay", screenX / 2, 460, paint);
                        paint.setTextSize(60);
                        paint.setColor(Color.argb(255, 255, 140, 0));
                        canvas.drawText("Press back to title screen", screenX / 2, 560, paint);
                    }
                } else if (screenX <= 800) {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setTextSize(100);
                        canvas.drawText("Game Over", screenX / 2, 125, paint);
                        paint.setTextSize(25);
                        canvas.drawText("Score: " + score, screenX / 2, 175, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 205, paint);
                        paint.setTextSize(50);
                        paint.setColor(Color.argb(255, 255, 255, 0));
                        canvas.drawText("Tap to replay", screenX / 2, 280, paint);
                        paint.setTextSize(30);
                        paint.setColor(Color.argb(255, 255, 140, 0));
                        canvas.drawText("Press back to title screen", screenX / 2, 350, paint);
                    }
                } else {
                    if (player.getX() == -500) {
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setTextSize(200);
                        canvas.drawText("Game Over", screenX / 2, 250, paint);
                        paint.setTextSize(50);
                        canvas.drawText("Score: " + score, screenX / 2, 350, paint);
                        canvas.drawText("Max Score: " + maxScore, screenX / 2, 410, paint);
                        paint.setTextSize(100);
                        paint.setColor(Color.argb(255, 255, 255, 0));
                        canvas.drawText("Tap to replay", screenX / 2, 560, paint);
                        paint.setTextSize(80);
                        paint.setColor(Color.argb(255, 255, 140, 0));
                        canvas.drawText("Press back to title screen", screenX / 2, 760, paint);
                    }
                }
            }

            // Now we've finished the draw the canvas can be unlocked

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    /*
     This method will control the frame rate of the game. Since it works with milliseconds, to
     have the sensation of 60 FPS we divide 1000/60 where the result will be approximately 17
     */

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    // Here we pause the thread. In case playing is false the class join() will be call to let the thread die.

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }


    // Case playing is true the thread is resumed

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Here we controls the tap on the screen. If it's been pressed then a boost will occur

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                isReady = true;
                player.setBoosting();

                if (gameEnded && player.getX() == -500) {
                    stopExplosionSoundPool();
                    startGame();
                }
                break;
        }
        return true;
    }

    // In case the game is interrupted all the sound concerning it will be ceased

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        soundPool.stop(destroyedStreamId);
        return false;
    }

    // Stop the explosion sound

    public void stopExplosionSoundPool() {
        soundPool.stop(destroyedStreamId);
    }

    // Return the value of the variable gameEnded

    public boolean isGameEnded() {
        return gameEnded;
    }

    // Return the value of the variable auxGetOut

    public int getAuxGetOut() {
        return auxGetOut;
    }

    // Set the value of the variable auxGetOut

    public void setAuxGetOut(int auxGetOut) {
        this.auxGetOut = auxGetOut;
    }
}