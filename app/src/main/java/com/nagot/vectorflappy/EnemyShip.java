package com.nagot.vectorflappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by Nagot on 31/05/2016.
 */
public class EnemyShip {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;

    public EnemyShip(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        Random generator = new Random();
        speed = generator.nextInt(6) + 10;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public int getX(){
        return x;
    }

    public int getY() {
        return y;
    }
}
