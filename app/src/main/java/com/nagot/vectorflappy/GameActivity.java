package com.nagot.vectorflappy;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

    private VFView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new VFView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
