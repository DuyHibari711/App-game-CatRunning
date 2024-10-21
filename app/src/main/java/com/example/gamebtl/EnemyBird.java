package com.example.gamebtl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.Random;

public class EnemyBird extends Enemy{

    private int xBird = new Random().nextInt(10000)+5000;
    private int yBird = new Random().nextInt(80)+200;

    public EnemyBird(Context context) {
        super(context);
        setAnimation();
    }

    public EnemyBird(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAnimation();
    }

    @Override
    protected void init() {
        super.init();
        x = xBird;
        setX(x);
        y = yBird;
        setY(y);
    }

    @Override
    protected void move() {
        x-= backgroundSpeed * 2;
        setX(x);
    }

    @Override
    protected void setAnimation() {
        setBackgroundResource(R.drawable.enemy_bird);
        animation = (AnimationDrawable) getBackground();
        animation.start();
    }
}
