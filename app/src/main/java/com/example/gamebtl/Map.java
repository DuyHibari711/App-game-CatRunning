package com.example.gamebtl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Color;
import android.widget.RelativeLayout;

public class Map extends View {

    private int surfaceY; // Vị trí Y của bề mặt

    public Map(Context context) {
        super(context);
        init();
    }

    public Map(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Vẽ map
    private void init() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                35
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        setLayoutParams(layoutParams);
    }

    // Lấy vị trí bề mặt
    public int getSurfaceY() {
        return surfaceY;
    }
    public void setSurfaceY(int surfaceY) {
        this.surfaceY = surfaceY;
    }
}