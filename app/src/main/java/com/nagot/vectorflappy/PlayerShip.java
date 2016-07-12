package com.nagot.vectorflappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Nagot on 31/05/2016.
 */

/*
Class responsible to define Ship object
 */
public class PlayerShip {
    private Bitmap bitmap;
    private Bitmap fZero, fOne, fTwo, fThree, fFour, fFive, fSix, fSeven, fEight;
    private ArrayList<Bitmap> bitmapExplosionArray;
    private ArrayList<Bitmap> bitmapTurbo;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -16;
    private final int GRAVITY_LOWY = -7;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 25;
    private final int MAX_SPEED_LOWY = 12;
    private int maxY;
    private int minY;
    private int screenY;
    private int auxSound = 0;
    private int auxExplosion = 0;
    private int shieldStrenght;
    private Rect hitBox;

    /*
    In the constructor we pass a context and x and y coordinates of the game
    */

    public PlayerShip(Context context, int screenX, int screenY) {
        this.screenY = screenY;
        x = 50;
        y = 50;
        speed = 1;

        // Variables concerning the bitmap sprites

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        fZero = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        fOne = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion1);
        fTwo = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion2);
        fThree = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion3);
        fFour = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion4);
        fFive = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion5);
        fSix = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion6);
        fSeven = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion7);
        fEight = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion8);

        // The method below resize the explosion bitmap case the screenX < 1000

        scaleBitmapExplosion(screenX);

        boosting = false;

        // Value will set the maximum value in the Y axis based in the device dimension
        maxY = screenY - bitmap.getHeight() + 40;
        minY = 50;
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        shieldStrenght = 2;

        // Put the explosion bitmap in a ArrayList

        bitmapExplosionArray = new ArrayList<Bitmap>();
        bitmapExplosionArray.add(0, fZero);
        bitmapExplosionArray.add(1, fOne);
        bitmapExplosionArray.add(2, fTwo);
        bitmapExplosionArray.add(3, fThree);
        bitmapExplosionArray.add(4, fFour);
        bitmapExplosionArray.add(5, fFive);
        bitmapExplosionArray.add(6, fSix);
        bitmapExplosionArray.add(7, fSeven);
        bitmapExplosionArray.add(8, fEight);

        // Put the turbo bitmap in a ArrayList

        bitmapTurbo = new ArrayList<Bitmap>();
        for (int resId : new int[]{
                R.drawable.ship_turbo,
                R.drawable.ship
        }) {
            bitmapTurbo.add(BitmapFactory.decodeResource(context.getResources(), resId));
        }

        // Scale the bitmaps to fit on smaller screens

        scaleBitmap(screenX);
    }

    // This method will update the ship position

    public void update() {
        if (screenY < 500) {
            if (boosting) {
                speed += 5;
            } else {
                speed -= 5;
            }

            if (speed > MAX_SPEED_LOWY) {
                speed = MAX_SPEED_LOWY;
            }

            if (speed < MIN_SPEED) {
                speed = MIN_SPEED;
            }

            y -= speed + GRAVITY_LOWY;


            if (y < minY) {
                y = minY;
            }

            if (y > maxY) {
                y = maxY;
            }
        } else {
            if (boosting) {
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
        }
        // Give the parameters to hitBox variable

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    // Method resize bitmap for the explosion

    public void scaleBitmapExplosion(int x) {
        if (x < 1000) {
            fZero = Bitmap.createScaledBitmap(fZero, fZero.getWidth() / 2, fZero.getHeight() / 2, false);
            fOne = Bitmap.createScaledBitmap(fOne, fOne.getWidth() / 2, fOne.getHeight() / 2, false);
            fTwo = Bitmap.createScaledBitmap(fTwo, fTwo.getWidth() / 2, fTwo.getHeight() / 2, false);
            fThree = Bitmap.createScaledBitmap(fThree, fThree.getWidth() / 2, fThree.getHeight() / 2, false);
            fFour = Bitmap.createScaledBitmap(fFour, fFour.getWidth() / 2, fFour.getHeight() / 2, false);
            fFive = Bitmap.createScaledBitmap(fFive, fFive.getWidth() / 2, fFive.getHeight() / 2, false);
            fSix = Bitmap.createScaledBitmap(fSix, fSix.getWidth() / 2, fSix.getHeight() / 2, false);
            fSeven = Bitmap.createScaledBitmap(fSeven, fSeven.getWidth() / 2, fSeven.getHeight() / 2, false);
            fEight = Bitmap.createScaledBitmap(fEight, fEight.getWidth() / 2, fEight.getHeight() / 2, false);
        } else if (x < 1200) {
            fZero = Bitmap.createScaledBitmap(fZero, fZero.getWidth() / 4 * 3, fZero.getHeight() / 4 * 3, false);
            fOne = Bitmap.createScaledBitmap(fOne, fOne.getWidth() / 4 * 3, fOne.getHeight() / 4 * 3, false);
            fTwo = Bitmap.createScaledBitmap(fTwo, fTwo.getWidth() / 4 * 3, fTwo.getHeight() / 4 * 3, false);
            fThree = Bitmap.createScaledBitmap(fThree, fThree.getWidth() / 4 * 3, fThree.getHeight() / 4 * 3, false);
            fFour = Bitmap.createScaledBitmap(fFour, fFour.getWidth() / 4 * 3, fFour.getHeight() / 4 * 3, false);
            fFive = Bitmap.createScaledBitmap(fFive, fFive.getWidth() / 4 * 3, fFive.getHeight() / 4 * 3, false);
            fSix = Bitmap.createScaledBitmap(fSix, fSix.getWidth() / 4 * 3, fSix.getHeight() / 4 * 3, false);
            fSeven = Bitmap.createScaledBitmap(fSeven, fSeven.getWidth() / 4 * 3, fSeven.getHeight() / 4 * 3, false);
            fEight = Bitmap.createScaledBitmap(fEight, fEight.getWidth() / 4 * 3, fEight.getHeight() / 4 * 3, false);
        }
    }

    // Method resize bitmap

    public void scaleBitmap(int x) {
        Bitmap auxBitmapOne, auxBitmapTwo;
        if (x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            auxBitmapOne = bitmapTurbo.get(0);
            auxBitmapOne = Bitmap.createScaledBitmap(auxBitmapOne, auxBitmapOne.getWidth() / 2, auxBitmapOne.getHeight() / 2, false);
            bitmapTurbo.add(0, auxBitmapOne);
            auxBitmapTwo = bitmapTurbo.get(1);
            auxBitmapTwo = Bitmap.createScaledBitmap(auxBitmapTwo, auxBitmapTwo.getWidth() / 2, auxBitmapTwo.getHeight() / 2, false);
            bitmapTurbo.add(1, auxBitmapTwo);
        } else if (x < 1200) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4 * 3, bitmap.getHeight() / 4 * 3, false);
            auxBitmapOne = bitmapTurbo.get(0);
            auxBitmapOne = Bitmap.createScaledBitmap(auxBitmapOne, auxBitmapOne.getWidth() / 4 * 3, auxBitmapOne.getHeight() / 4 * 3, false);
            bitmapTurbo.add(0, auxBitmapOne);
            auxBitmapTwo = bitmapTurbo.get(1);
            auxBitmapTwo = Bitmap.createScaledBitmap(auxBitmapTwo, auxBitmapTwo.getWidth() / 4 * 3, auxBitmapTwo.getHeight() / 4 * 3, false);
            bitmapTurbo.add(1, auxBitmapTwo);
        }
    }

    // Return the value of the variable bitmap

    public Bitmap getBitmap() {
        return bitmap;
    }

    // Return the value of the variable speed

    public int getSpeed() {
        return speed;
    }

    // Set the value of the variable x

    public void setX(int x) {
        this.x = x;
    }

    // Return the value of the variable x

    public int getX() {
        return x;
    }

    // Return the value of the variable y

    public int getY() {
        return y;
    }

    // Return the value of the variable maxY

    public int getMaxY() {
        return maxY;
    }

    // Return the value of the variable hitBox

    public Rect getHitBox() {
        return hitBox;
    }

    // Set the value of the variable boosting

    public void setBoosting() {
        boosting = true;
    }

    // Return the value of the variable boosting

    public boolean isBoosting() {
        return boosting;
    }

    // Return the value of the variable shieldStrenght

    public int getShieldStrenght() {
        return shieldStrenght;
    }

    // Reduce the value of the variable shieldStrenght

    public void reduceShieldStrenght() {
        shieldStrenght--;
    }

    // Set the value of the variable boosting to false

    public void stopBoosting() {
        boosting = false;
    }

    // Set the value of the variable auxSound

    public void setAuxSound(int auxSound) {
        this.auxSound = this.auxSound + auxSound;
    }

    // Return the value of the variable auxSound

    public int getAuxSound() {
        return auxSound;
    }

    // Return the value of the variable auxExplosion

    public int getAuxExplosion() {
        return auxExplosion;
    }

    // Set the value of the variable auxExplosion

    public void setAuxExplosion(int auxExplosion) {
        this.auxExplosion = auxExplosion;
    }

    // Return the value of the variable bitmapExplosionArray

    public ArrayList<Bitmap> getExplosion() {
        return bitmapExplosionArray;
    }

    // Return the value of the variable bitmapTurbo

    public ArrayList<Bitmap> getBitmapTurbo() {
        return bitmapTurbo;
    }
}
