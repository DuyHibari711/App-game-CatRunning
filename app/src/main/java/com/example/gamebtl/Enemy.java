package com.example.gamebtl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.Random;

public abstract class Enemy extends androidx.appcompat.widget.AppCompatImageView {

    protected Map map;
    protected int x, y;
    protected int backgroundSpeed;

    protected AnimationDrawable animation;

    public Enemy(Context context) {
        super(context);
        init();
    }

    public Enemy(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setBackgroundSpeed(int speed) {
        this.backgroundSpeed = speed;
    }
    protected void init() {
        // Đặt layout params để Enemy đứng trên map
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        if (map != null) {
            layoutParams.bottomMargin = map.getSurfaceY() + map.getLayoutParams().height;
        } else {
            layoutParams.bottomMargin = 0;
        }
        setLayoutParams(layoutParams);

        startMoving();
    }

    protected void startMoving() {
        post(new Runnable() {
            @Override
            public void run() {
                move();
                if (x + getWidth() < 0) {
                    init();
                    return;
                }
                post(this);
            }
        });
    }

    protected abstract void move();
    protected abstract void setAnimation();
}
