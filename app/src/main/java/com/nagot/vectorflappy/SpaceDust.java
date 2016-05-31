package com.nagot.vectorflappy;

import java.util.Random;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Esta classe é responsável por definir o objeto SpaceDust
 */
public class SpaceDust {
    private int x, y;
    private int speed;

    private int maxX;
    private int maxY;
    private int minX;
    private int minY;

      /*
    No construtor damos as cordenadas iniciais da space dust.
    Repare que ele herda da primeira classe, a GameActivity, as coordenadas de X e Y da tela
    do dispositivo
     */

    public SpaceDust(int screenX, int screenY) {
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        /*
        Repare no trecho abaixo que o valor do speed bem com X e Y são gerados inicialmente de
        forma aleatória através da classe Random
         */

        Random generator = new Random();
        speed = generator.nextInt(10);

        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
    }

        /*
    O método recebe como parametro a velocidade da nave do jogador. Com base nisso é feito um cálculo
    de quão veloz a space dust será.
     */

    public void update(int playerSpeed){
        x -= playerSpeed;
        x -= speed;

         /*
        O trecho abaixo são as velocidades aleatórias que o space dust terá juntamente com sua
        posição randomica no eixo Y caso X seja menor que zero
         */

        if(x < 0) {
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(15);
        }
    }

    // Retornamos o valor da variável x

    public int getX() {
        return x;
    }
    // Retornamos o valor da variável y

    public int getY() {
        return y;
    }
}
