package com.nagot.vectorflappy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
Activity controls what will be shown in the game. There is no layout in this activity since VFView.java will draw all objects on the screen
 */

public class GameActivity extends Activity {

    private VFView gameView;
    private int difficulty = 0;
    private String auxDifficulty;
    private boolean gettingOut = false;

    /*
    No onCreate instanciamos o objeto gameView e o chamamos no setContentView.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            auxDifficulty = extras.getString("difficulty");
        }

        difficulty = Integer.parseInt(auxDifficulty);

        // Get device x and y coordinates
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Instantiate VFView class and set it as the contentView
        gameView = new VFView(this, size.x, size.y, difficulty);
        setContentView(gameView);
    }

    // Pause the thread in case there is a phone call, for instance

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    // Resume the thread

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    // If the back button is pressed in game a message will be shown and the player will have to press it again to exit the game
    // Case the back button is pressed when the game over screen is appearing, player will get back to title screen

    @Override
    public void onBackPressed() {
        if (gameView.isGameEnded()) {
            Intent i = new Intent(GameActivity.this, MainActivity.class);
            startActivity(i);
            gameView.stopExplosionSoundPool();
            finish();
        } else if (gettingOut && gameView.getAuxGetOut() == 1) {
            super.onBackPressed();
            finish();
        } else {
            gettingOut = true;
            gameView.setAuxGetOut(1);
            Toast toast = Toast.makeText(getBaseContext(), getText(R.string.get_out), Toast.LENGTH_SHORT);
            toastFormat(toast);
        }
    }

    // Method created to format toast

    public void toastFormat(Toast toast) {
        LinearLayout layout = (LinearLayout) toast.getView();
        if (layout.getChildCount() > 0) {
            TextView tv = (TextView) layout.getChildAt(0);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        }
        toast.show();
    }
}
