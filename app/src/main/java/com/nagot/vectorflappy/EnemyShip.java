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
Class responsible for EnemyShip object
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
    In the constructor we pass a context, x and y coordinates and the difficulty of the game
     */

    public EnemyShip(Context context, int screenX, int screenY, int difficulty) {
        this.difficulty = difficulty;
        Random generator = new Random();
        int whichBitmap = generator.nextInt(2);

        // This switch will choose one random bitmap

        switch (whichBitmap){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy4);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy5);
                break;
        }

        // scaleBitmap will decrease bitmap size to smaller screens

        scaleBitmap(screenX);

        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 50;

        // Set the initial speed of the enemy

        speed = generator.nextInt(6) + 10;

        x = screenX;

        // Take some value of y to don't let enemy ships pass over the score and shield draws
        y = generator.nextInt(maxY - 50) - bitmap.getHeight();
        if (y < 50) {
            y = 250;
        }

        // Instantiate the object hitbox to calculate the collision

        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    // Method resize bitmap

    public void scaleBitmap(int x) {
        if (x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
        } else if (x < 1200) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4 * 3, bitmap.getHeight() / 4 * 3, false);
        }
    }

    // Return the value of the variable bitmap

    public Bitmap getBitmap() {
        return bitmap;
    }

    // Return the value of the variable x

    public int getX() {
        return x;
    }

    // Return the value of the variable y

    public int getY() {
        return y;
    }

    // Return the value of the variable hitBox

    public Rect getHitBox() {
        return hitBox;
    }

    // Set the value of the variable x

    public void setX(int x) {
        this.x = x;
    }

    // This method will update the enemyShip position based on the player ship speed

    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;

        // After the enemy is outside the screen, it will add a new speed based on game difficulty and score

        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();

            switch (difficulty) {
                case 1:
                    if (score > 0 && score <= 20) {
                        speed = generator.nextInt(6) + 10;
                    } else if (score > 20 && score <= 40) {
                        speed = generator.nextInt(10) + 15;
                    } else if (score > 40 && score <= 60) {
                        speed = generator.nextInt(10) + 20;
                    } else if (score > 60 && score <= 80) {
                        speed = generator.nextInt(10) + 25;
                    } else {
                        speed = generator.nextInt(10) + 30;
                    }
                    break;
                case 2:
                    if (score > 0 && score <= 15) {
                        speed = generator.nextInt(6) + 10;
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
                        speed = generator.nextInt(6) + 10;
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
            x = maxX;
            y = generator.nextInt(maxY - 100) - bitmap.getHeight();
            if (y < 50) {
                y = 250;
            }
            this.setNoHit(false);
            this.aux = 0;
        }

        // Give the parameters to hitBox variable

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    // Return the value of the variable noHit

    public boolean isNoHit() {
        return noHit;
    }

    // Set the value of the variable noHit

    public void setNoHit(boolean noHit) {
        this.noHit = noHit;
    }

    // Return the value of the variable aux

    public int getAux() {
        return aux;
    }

    // Set the value of the variable aux

    public void setAux(int aux) {
        this.aux = this.aux + aux;
    }

    // Pick always enemy1 bitmap and scale it

    public void setEnemyOne(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1);
        scaleBitmap(x);
    }

    // Pick always enemy2 bitmap and scale it

    public void setEnemyTwo(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
        scaleBitmap(x);
    }

    // Pick always enemy3 bitmap and scale it

    public void setEnemyThree(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
        scaleBitmap(x);
    }

    // Set the value of the variable score

    public void setScore(int score) {
        this.score = score;
    }
}
