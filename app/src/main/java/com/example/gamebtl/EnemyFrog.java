package com.example.gamebtl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import java.util.Random;

public class EnemyFrog extends Enemy {

    private int xFrog = new Random().nextInt(10000) + 30000;

    public EnemyFrog(Context context) {
        super(context);
        setAnimation();
    }

    public EnemyFrog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation();
    }

    @Override
    protected void init() {
        super.init();
        x = xFrog;
        setX(x);
    }

    @Override
    protected void move() {
        x -= backgroundSpeed;
        setX(x);
    }

    @Override
    protected void setAnimation() {
        setBackgroundResource(R.drawable.enemy_frog);
        animation = (AnimationDrawable) getBackground();
        animation.start();
    }
}
