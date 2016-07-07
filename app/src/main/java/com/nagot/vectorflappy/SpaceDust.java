package com.nagot.vectorflappy;

import java.util.Random;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Class responsible for object SpaceDust
 */
public class SpaceDust {
    private int x, y;
    private int speed;
    private int maxX;
    private int maxY;

      /*
    Pass screen coordinates to the constructor
     */

    public SpaceDust(int screenX, int screenY) {
        maxX = screenX;
        maxY = screenY;

        // Creates a random generator

        Random generator = new Random();
        speed = generator.nextInt(10);

        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
    }

    // Make the calculations of the dust speed based on the player speed

    public void update(int playerSpeed){
        x -= playerSpeed;
        x -= speed;

        if(x < 0) {
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(15);
        }
    }

    // Return the value of the variable x

    public int getX() {
        return x;
    }

    // Return the value of the variable y

    public int getY() {
        return y;
    }
}
