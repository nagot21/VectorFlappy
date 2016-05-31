package com.nagot.vectorflappy;

import android.content.Context;
import android.view.SurfaceView;

/**
 * Created by Nagot on 31/05/2016.
 */
public class VFView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;

    public VFView(Context context) {
        super(context);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {

    }

    private void draw() {

    }

    private void control() {
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}


