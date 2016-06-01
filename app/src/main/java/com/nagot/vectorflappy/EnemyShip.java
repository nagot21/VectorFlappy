package com.nagot.vectorflappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Esta classe é responsável por definir o objeto EnemyShip
 */
public class EnemyShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;
    private Rect hitBox;

     /*
    No construtor damos as cordenadas iniciais da nave inimiga.
    Repare que ele herda da primeira classe, a GameActivity, as coordenadas de X e Y da tela
    do dispositivo REESCREVER...
     */

    public EnemyShip(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        /*
        Repare no trecho abaixo que o valor do speed da nave é gerado inicialmente de
        forma aleatória através da classe Random
         */

        Random generator = new Random();
        speed = generator.nextInt(6) + 10;

        /*
        Aqui dizemos que o valor inicial de x igual ao tamanho da tela em seu eixo
        X. Ou seja, a nave virá, neste caso, da direita para esqueda.
         */
        x = screenX;

        /*
        Sua posição no eixo Y será o cálculo aletório do seu tamanho máximo no eixo
        Y menos o tamanho do objeto bitmap
         */
        y = generator.nextInt(maxY) - bitmap.getHeight();

        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    // Retornamos o valor da variável bitmap

    public Bitmap getBitmap(){
        return bitmap;
    }

    // Retornamos o valor da variável speed

    public int getX(){
        return x;
    }

    // Retornamos o valor da variável x

    public int getY() {
        return y;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    /*
    O método recebe como parametro a velocidade da nave do jogador. Com base nisso é feito um cálculo
    de quão veloz a nave inimiga será. REESCREVER...
     */

    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;

        /*
        O trecho abaixo são as velocidades aleatórias que a nave inimiga terá juntamente com sua
        posição randomica no eixo Y caso X seja menor que minX menos a altura da imagem bitmap
         */

        if (x < minX - bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }
}
