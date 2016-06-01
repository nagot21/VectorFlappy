package com.nagot.vectorflappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Esta classe é responsável por definir o objeto Ship
 */
public class PlayerShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -12;
    private int maxY;
    private int minY;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;
    private Rect hitBox;

    /*
    No construtor damos as cordenadas iniciais da nave, seu speed inicial e seu sprite.
    Repare que ele herda da primeira classe, a GameActivity, as coordenadas de X e Y da tela
    do dispositivo REESCREVER...
     */

    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
        // O valor abaixo determina o máximo que o eixo Y pode chegar baseado nas dimensões do dispositivo
        maxY = screenY - bitmap.getHeight();
        minY = 0;
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    /*
    No método update() especificamos os parametros da nave, como seu boost, etc. REESCREVER
     */

    public void update() {
        if (boosting) {
            speed += 2;
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

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    // Retornamos o valor da variável bitmap

    public Bitmap getBitmap() {
        return bitmap;
    }

    // Retornamos o valor da variável speed

    public int getSpeed() {
        return speed;
    }

    // Retornamos o valor da variável x

    public int getX() {
        return x;
    }

    // Retornamos o valor da variável y

    public int getY() {
        return y;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    // Seta o valor da variável boosting = true

    public void setBoosting() {
        boosting = true;
    }

    // Seta o valor da variável boosting = false

    public void stopBoosting() {
        boosting = false;
    }
}
