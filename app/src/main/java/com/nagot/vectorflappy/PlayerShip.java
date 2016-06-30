package com.nagot.vectorflappy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Esta classe é responsável por definir o objeto Ship
 */
public class PlayerShip {
    private Bitmap bitmap;
    private ArrayList<Bitmap> bitmapExplosionArray;
    private ArrayList<Bitmap> bitmapTurbo;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    //private final int GRAVITY = -12;
    private final int GRAVITY = -16;
    private int maxY;
    private int minY;
    private int auxSound = 0;
    private int auxExplosion = 0;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 25; // era 20
    private int shieldStrenght; // Responsável pelo shield da nave
    private Rect hitBox; // Esta variável é a responsável pelo teste de colisão

    /*
    No construtor damos as cordenadas iniciais da nave, seu speed inicial e seu sprite.
    Repare que ele herda da primeira classe, a GameActivity, as coordenadas de X e Y da tela
    do dispositivo. Perceba também que ela inicializa uma variável chamada hitBox, que irá determinar
    o tamanho do nosso objeto para realizar o teste de colisão.
     */

    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        //y = 50;
        y = screenY / 2;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
        // O valor abaixo determina o máximo que o eixo Y pode chegar baseado nas dimensões do dispositivo
        maxY = screenY - bitmap.getHeight() + 40;
        minY = 50;
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        shieldStrenght = 2;

        scaleBitmap(screenX);

        bitmapExplosionArray = new ArrayList<Bitmap>();
        for(int resId : new int[]{
                R.drawable.explosion1,
                R.drawable.explosion2,
                R.drawable.explosion3,
                R.drawable.explosion4,
                R.drawable.explosion5,
                R.drawable.explosion6,
                R.drawable.explosion7,
                R.drawable.explosion8
        }) {
            bitmapExplosionArray.add(BitmapFactory.decodeResource(context.getResources(),resId));
        }

        bitmapTurbo = new ArrayList<Bitmap>();
        for(int resId : new int[]{
                R.drawable.ship_turbo,
                R.drawable.ship
        }) {
            bitmapTurbo.add(BitmapFactory.decodeResource(context.getResources(),resId));
        }
    }

    /*
    No método update() especificamos os parametros da nave, como seu boost, hitBox, etc.
     */

    public void update() {
        if (boosting) {
            //speed += 2;
            speed += 5;
        } else {
            speed -= 5;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }

        /*
        Abaixo, será passado o parametro de onde o objeto se encontra na tela para a variável
        hitBox
         */
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public void scaleBitmap(int x) {
        if (x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
        } else if (x < 1200) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4 * 3, bitmap.getHeight() / 4 * 3, false);
        }
    }

    // Retornamos o valor da variável bitmap

    public Bitmap getBitmap() {
        return bitmap;
    }

    // Retornamos o valor da variável speed

    public int getSpeed() {
        return speed;
    }

    // Coloca um valor para variável x

    public void setX(int x) {
        this.x = x;
    }

    // Retornamos o valor da variável x

    public int getX() {
        return x;
    }

    // Coloca um valor para variável x

    public void setY(int y) {
        this.y = maxY / y;
    }

    public void setYDefault(int y) {
        this.y = y;
    }

    // Retornamos o valor da variável y

    public int getY() {
        return y;
    }

    public int getMaxY(){
        return maxY;
    }

    // Retorna o valor da variável hitBox

    public Rect getHitBox() {
        return hitBox;
    }

    // Seta o valor da variável boosting = true

    public void setBoosting() {
        boosting = true;
    }

    // Retorna o valor da variável boosting

    public boolean isBoosting() {
        return boosting;
    }

    // Retorna o valor da variável shieldStrenght

    public int getShieldStrenght() {
        return shieldStrenght;
    }

    // Reduz o valor da variável shieldStrenght

    public void reduceShieldStrenght() {
        shieldStrenght --;
    }

    // Seta o valor da variável boosting = false

    public void stopBoosting() {
        boosting = false;
    }

    public void setAuxSound(int auxSound) {
        this.auxSound = this.auxSound + auxSound;
    }

    public int getAuxSound() {
        return auxSound;
    }

    public int getAuxExplosion() {
        return auxExplosion;
    }

    public void setAuxExplosion(int auxExplosion) {
        this.auxExplosion = auxExplosion;
    }

    public ArrayList<Bitmap> getExplosion() {
        return bitmapExplosionArray;
    }

    public ArrayList<Bitmap> getBitmapTurbo() {
        return bitmapTurbo;
    }
}
