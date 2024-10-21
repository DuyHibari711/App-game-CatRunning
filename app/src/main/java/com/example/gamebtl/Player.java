package com.example.gamebtl;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class Player extends androidx.appcompat.widget.AppCompatImageView {

    private Map map;
    private int x;
    private MediaPlayer jumpSound;
    private boolean isJumping = false;
    public boolean die = false;
    public boolean useSkinYellow, useSkinSilver, useSkinGray, useSkinBlack;
    public void setUseSkinYellow(boolean useYellow) {
        this.useSkinYellow = useYellow;
        updateSkinDung();
        updateSkinNhay();
        updateSkinTiepDat();
        updateSkinChay();
    }
    public void setUseSkinSilver(boolean useSilver) {
        this.useSkinSilver = useSilver;
        updateSkinDung();
        updateSkinNhay();
        updateSkinTiepDat();
        updateSkinChay();
    }
    public void setUseSkinGray(boolean useGray) {
        this.useSkinGray = useGray;
        updateSkinDung();
        updateSkinNhay();
        updateSkinTiepDat();
        updateSkinChay();
    }
    public void setUseSkinBlack(boolean useBlack) {
        this.useSkinBlack = useBlack;
        updateSkinDung();
        updateSkinNhay();
        updateSkinTiepDat();
        updateSkinChay();
    }
    public void setMap(Map map) {
        this.map = map;
        init();
    }

    public Player(Context context) {
        super(context);
    }

    public Player(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void updateSkinDung() {
        if (useSkinYellow) {
            setImageResource(R.drawable.yellow_dung);
        } else if (useSkinSilver) {
            setImageResource(R.drawable.silver_dung);
        } else if (useSkinGray) {
            setImageResource(R.drawable.gray_dung);
        } else if (useSkinBlack) {
            setImageResource(R.drawable.black_dung);
        } else {
            setImageResource(R.drawable.black_dung);
        }
    }

    private void updateSkinNhay() {
        if (useSkinYellow){
            setImageResource(R.drawable.yellow_nhay);
        }
        else if (useSkinSilver){
            setImageResource(R.drawable.silver_nhay);
        }
        else if (useSkinGray){
            setImageResource(R.drawable.gray_nhay);
        } else if (useSkinBlack){
            setImageResource(R.drawable.black_nhay);
        } else {
            setImageResource(R.drawable.black_nhay);
        }
    }

    private void updateSkinChay() {
        if (useSkinYellow) {
            AnimationDrawable run = (AnimationDrawable) getResources().getDrawable(R.drawable.yellow_run);
            setImageDrawable(run);
            run.start();
        } else if (useSkinSilver) {
            AnimationDrawable run = (AnimationDrawable) getResources().getDrawable(R.drawable.silver_run);
            setImageDrawable(run);
            run.start();
        } else if (useSkinGray) {
            AnimationDrawable run = (AnimationDrawable) getResources().getDrawable(R.drawable.gray_run);
            setImageDrawable(run);
            run.start();
        } else if (useSkinBlack){
            AnimationDrawable run = (AnimationDrawable) getResources().getDrawable(R.drawable.cat_run);
            setImageDrawable(run);
            run.start();
        } else {
            AnimationDrawable run = (AnimationDrawable) getResources().getDrawable(R.drawable.cat_run);
            setImageDrawable(run);
            run.start();
        }
    }

    private void updateSkinTiepDat() {
        if (useSkinYellow){
            setImageResource(R.drawable.yellow_tiepdat);
        }
        else if (useSkinSilver){
            setImageResource(R.drawable.silver_tiepdat);
        }
        else if (useSkinGray){
            setImageResource(R.drawable.gray_tiepdat);
        } else if (useSkinBlack){
            setImageResource(R.drawable.black_tiepdat);
        } else {
            setImageResource(R.drawable.black_tiepdat);
        }
    }
    private void init() {
        // Đặt hình ảnh cho player
        updateSkinDung();
        jumpSound = MediaPlayer.create(this.getContext(), R.raw.jump);

        // Đặt layout params để player đứng trên map
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        x = 700;
        layoutParams.leftMargin = x;

        // Kiểm tra xem map đã được gán chưa
        if (map != null) {
            layoutParams.bottomMargin = map.getSurfaceY() + map.getLayoutParams().height;
        } else {
            layoutParams.bottomMargin = 0;
        }
        setLayoutParams(layoutParams);
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void jumpRight() {
        if (isJumping || getY() < map.getSurfaceY() + getLayoutParams().height) {
            return;
        }
        isJumping = true;
        updateSkinNhay();
        jumpSound.start();
        // Kiểm tra xem player có nhảy ra ngoài viền phải không
        int playerRightBound = (int) (getX() + getWidth());
        int mapRightBound = map.getWidth();
        int maxRight = mapRightBound - getWidth(); // Tính toán giới hạn phải

        int finalX = Math.min(playerRightBound + 100, maxRight); // Giới hạn vị trí phải sau khi nhảy

        animate().translationYBy(-500).translationXBy(finalX - playerRightBound).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc animation nhảy, player bắt đầu rơi xuống
                fallRight();
                updateSkinTiepDat();
            }
        });
    }

    private void fallRight() {
        animate().translationYBy(500).translationXBy(100).setDuration(700).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc rơi, player được cho phép nhảy lại
                if (!die) {
                    isJumping = false;
                    updateSkinChay();
                }
            }
        });
    }

    public void jumpLeft() {
        if (isJumping || getY() < map.getSurfaceY() + getLayoutParams().height) {
            return;
        }
        isJumping = true;
        updateSkinNhay();
        jumpSound.start();
        // Kiểm tra xem player có nhảy ra ngoài viền trái không
        int playerLeftBound = (int) getX();
        int maxLeft = 130; // Giới hạn trái của màn hình

        int finalX = Math.max(playerLeftBound - 150, maxLeft); // Giới hạn vị trí trái sau khi nhảy

        animate().translationYBy(-500).translationXBy(finalX - playerLeftBound).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc animation nhảy, player bắt đầu rơi xuống
                fallLeft();
                updateSkinTiepDat();
            }
        });
    }

    private void fallLeft() {
        animate().translationYBy(500).translationXBy(-150).setDuration(700).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc rơi, player được cho phép nhảy lại
                if (!die) {
                    isJumping = false;
                    updateSkinChay();
                }
            }
        });
    }

    public void x2jumpRight() {
        if (isJumping || getY() < map.getSurfaceY() + getLayoutParams().height) {
            return;
        }
        isJumping = true;
        updateSkinNhay();
        jumpSound.start();
        // Kiểm tra xem player có nhảy ra ngoài viền phải không
        int playerRightBound = (int) (getX() + getWidth());
        int mapRightBound = map.getWidth();
        int maxRight = mapRightBound - getWidth(); // Tính toán giới hạn phải

        int finalX = Math.min(playerRightBound + 200, maxRight); // Giới hạn vị trí phải sau khi nhảy

        animate().translationYBy(-700).translationXBy(finalX - playerRightBound).rotationBy(360).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc animation nhảy, player bắt đầu rơi xuống
                x2fallRight();
                updateSkinTiepDat();
            }
        });
    }

    private void x2fallRight() {
        animate().translationYBy(700).translationXBy(200).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc rơi, player được cho phép nhảy lại
                if (!die) {
                    isJumping = false;
                    updateSkinChay();
                }
            }
        });
    }

    public void x2jumpLeft() {
        if (isJumping || getY() < map.getSurfaceY() + getLayoutParams().height) {
            return;
        }
        isJumping = true;
        updateSkinNhay();
        jumpSound.start();
        // Kiểm tra xem player có nhảy ra ngoài viền trái không
        int playerLeftBound = (int) getX();
        int maxLeft = 300; // Tính toán giới hạn trái

        int finalX = Math.max(playerLeftBound - 300, maxLeft); // Giới hạn vị trí trái sau khi nhảy

        animate().translationYBy(-700).translationXBy(finalX - playerLeftBound).rotationBy(360).setDuration(800).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc animation nhảy, player bắt đầu rơi xuống
                x2fallLeft();
                updateSkinTiepDat();
            }
        });
    }

    private void x2fallLeft() {
        animate().translationYBy(700).translationXBy(-300).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Khi kết thúc rơi, player được cho phép nhảy lại
                if (!die) {
                    isJumping = false;
                    updateSkinChay();
                }
            }
        });
    }
}

