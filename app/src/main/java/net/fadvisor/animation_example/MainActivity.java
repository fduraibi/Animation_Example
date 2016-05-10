/*
 * Copyright (c) 2016 Fahad Alduraibi
 */

package net.fadvisor.animation_example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    AnimGuy animGuy;
    boolean animGuyRunning = false;

    AnimGame animGame;
    boolean animGameRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animGuy = new AnimGuy(this);
        animGame = new AnimGame(this);
    }

    @Override
    protected void onPause() {
        if (animGuyRunning) {
            animGuy.pause();
        }

        if (animGameRunning) {
            animGame.pause();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (animGuyRunning) {
            animGuy.resume();
        }

        if (animGameRunning) {
            animGame.resume();
        }
    }

    public void animateBoy(View view) {
        Button btn = (Button) findViewById(view.getId());

        if ( btn.getText() == getResources().getString(R.string.move) ) {
            animGuy.resume();
            animGuyRunning = true;
            btn.setText( getResources().getString(R.string.stop) );
        } else {
            animGuy.pause();
            animGuyRunning = false;
            btn.setText( getResources().getString(R.string.move) );
        }
    }

    public void animateGame(View view) {
        Button btn = (Button) findViewById(view.getId());

        if ( btn.getText() == getResources().getString(R.string.move) ) {
            animGame.resume();
            btn.setText( getResources().getString(R.string.stop) );
        } else {
            animGame.pause();
            btn.setText( getResources().getString(R.string.move) );
        }
    }



}
