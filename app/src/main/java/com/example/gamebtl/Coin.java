package com.example.gamebtl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatImageView;

public class Coin extends AppCompatImageView {

    private Map map;
    private int x;
    public int backgroundSpeed;
    private AnimationDrawable animation;

    public void setBackgroundSpeed(int speed) {
        this.backgroundSpeed = speed;
    }

    public Coin(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.coin_animation);
        animation = (AnimationDrawable) getBackground();
        animation.start();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100,100);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin=50;
        setLayoutParams(layoutParams);

    }

    public void setMap(Map map) {
        this.map = map;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        setLayoutParams(layoutParams);
    }

}
