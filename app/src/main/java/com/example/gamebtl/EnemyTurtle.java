package com.example.gamebtl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import java.util.Random;

public class EnemyTurtle extends Enemy {

    private int xTurtle = new Random().nextInt(10000)+5000;

    public EnemyTurtle(Context context) {
        super(context);
        setAnimation();
    }

    public EnemyTurtle(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation();
    }

    @Override
    protected void init() {
        super.init();
        x = xTurtle;
        setX(x);
    }

    @Override
    protected void move() {
        x -= backgroundSpeed;
        setX(x);
    }

    @Override
    protected void setAnimation() {
        setBackgroundResource(R.drawable.enemy_turtle);
        animation = (AnimationDrawable) getBackground();
        animation.start();
    }
}
