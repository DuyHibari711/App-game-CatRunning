package com.example.gamebtl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import java.util.Random;

public class EnemyDog extends Enemy {

    private int xDog = new Random().nextInt(50000)+30000;

    public EnemyDog(Context context) {
        super(context);
        setAnimation();
    }

    public EnemyDog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation();
    }

    @Override
    protected void init() {
        super.init();
        x = xDog;
        setX(x);
    }

    @Override
    protected void move() {
        x -= backgroundSpeed;
        setX(x);
    }

    @Override
    protected void setAnimation() {
        setBackgroundResource(R.drawable.enemy_dog);
        animation = (AnimationDrawable) getBackground();
        animation.start();
    }
}

