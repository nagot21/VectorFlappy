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
    private int difficulty;
    private int score;
    private Rect hitBox;
    private boolean noHit = false;
    private int aux = 0;

     /*
    No construtor damos as cordenadas iniciais da nave inimiga.
    Repare que ele herda da primeira classe, a GameActivity, as coordenadas de X e Y da tela
    do dispositivo. Perceba também que ela inicializa uma variável chamada hitBox, que irá determinar
    o tamanho do nosso objeto para realizar o teste de colisão.

    É criado também um gerador de números random para pegar uma imagem de inimigo aleatória
     */

    public EnemyShip(Context context, int screenX, int screenY, int difficulty) {
        this.difficulty = difficulty;
        Random generator = new Random();
        int whichBitmap = generator.nextInt(41);

        if ((whichBitmap >= 0) && (whichBitmap <= 10)) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
        } else if ((whichBitmap > 10) && (whichBitmap <= 20)) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
        } else if ((whichBitmap > 20) && (whichBitmap <= 30)) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy4);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy5);
        }


        // Método irá modificar o tamanho dos inimigos baseados no tamanho da tela

        scaleBitmap(screenX);

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 50;

        /*
        Repare no trecho abaixo que o valor do speed da nave é gerado inicialmente de
        forma aleatória através da classe Random
         */

        /*switch (difficulty) {
            case 1:
                speed = generator.nextInt(6) + 10;
                break;
            case 2:
                speed = generator.nextInt(6) + 12;
                break;
            case 3:
                speed = generator.nextInt(6) + 14;
        }*/
        //Random generator = new Random();
        speed = generator.nextInt(6) + 10;

        /*
        Aqui dizemos que o valor inicial de x igual ao tamanho da tela em seu eixo
        X. Ou seja, a nave virá, neste caso, da direita para esqueda.
         */
        x = screenX;

        /*
        Sua posição no eixo Y será o cálculo aletório do seu tamanho máximo no eixo
        Y menos o tamanho do objeto bitmap. Caso o valor seja menor que 50, será colocado um
        valor inicial fixo
         */
        y = generator.nextInt(maxY - 50) - bitmap.getHeight();
        if (y < 50) {
            y = 250;
        }

        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    // Da resize no tamanho do bitmap do inimigo

    public void scaleBitmap(int x) {
        if (x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
        } else if (x < 1200) {
            //bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4 * 3, bitmap.getHeight() / 4 * 3, false);
        }
    }

    // Retornamos o valor da variável bitmap

    public Bitmap getBitmap() {
        return bitmap;
    }

    // Retornamos o valor da variável speed

    public int getX() {
        return x;
    }

    // Retornamos o valor da variável x

    public int getY() {
        return y;
    }

    // Retorna o valor da variável hitBox

    public Rect getHitBox() {
        return hitBox;
    }

    // Seta o valor da variável hitBox

    public void setX(int x) {
        this.x = x;
    }

    /*
    O método recebe como parametro a velocidade da nave do jogador. Com base nisso é feito um cálculo
    de quão veloz a nave inimiga será. Perceba também que ela inicializa uma variável chamada hitBox,
    que irá determinar o tamanho do nosso objeto para realizar o teste de colisão.
     */

    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;

        /*
        O trecho abaixo são as velocidades aleatórias que a nave inimiga terá juntamente com sua
        posição randomica no eixo Y caso X seja menor que minX menos a altura da imagem bitmap
         */

        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();

            switch (difficulty) {
                case 1:
                    if (score > 0) {
                        speed = generator.nextInt(10) + 10;
                    } else if (score > 20) {
                        speed = generator.nextInt(10) + 15;
                    } else if (score > 40) {
                        speed = generator.nextInt(10) + 20;
                    } else if (score > 60) {
                        speed = generator.nextInt(10) + 25;
                    } else {
                        speed = generator.nextInt(10) + 30;
                    }
                    break;
                case 2:
                    if (score > 0 && score <= 15) {
                        speed = generator.nextInt(10) + 10;
                    } else if (score > 15 && score <= 30) {
                        speed = generator.nextInt(10) + 15;
                    } else if (score > 30 && score <= 45) {
                        speed = generator.nextInt(10) + 20;
                    } else if (score > 45 && score <= 60) {
                        speed = generator.nextInt(10) + 25;
                    } else {
                        speed = generator.nextInt(10) + 30;
                    }
                    break;
                case 3:
                    if (score > 0 && score <= 10) {
                        speed = generator.nextInt(10) + 10;
                    } else if (score > 10 && score <= 20) {
                        speed = generator.nextInt(10) + 15;
                    } else if (score > 20 && score <= 30) {
                        speed = generator.nextInt(10) + 20;
                    } else if (score > 30 && score <= 40) {
                        speed = generator.nextInt(10) + 25;
                    } else {
                        speed = generator.nextInt(10) + 30;
                    }
                    break;
            }

            //speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY - 50) - bitmap.getHeight();
            if (y < 50) {
                y = 250;
            }
            this.setNoHit(false);
            this.aux = 0;
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

    public boolean isNoHit() {
        return noHit;
    }

    public void setNoHit(boolean noHit) {
        this.noHit = noHit;
    }

    public int getAux() {
        return aux;
    }

    public void setAux(int aux) {
        this.aux = this.aux + aux;
    }

    // enemy1 será sempre escolhido

    public void setEnemyOne(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1);
        scaleBitmap(x);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
