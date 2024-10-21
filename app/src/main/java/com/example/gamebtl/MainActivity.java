package com.example.gamebtl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private DatabaseReference databaseReference;
    private TextView coinTextView;
    private FirebaseAuth mAuth;
    private RelativeLayout buyFailedLayout, buyItemYellowLayout, buyItemSilverLayout, buyItemGrayLayout
            , groupPriceYellow, groupPriceSilver, groupPriceGray
            , groupUseYellow, groupUseSilver, groupUseGray;
    private TextView priceYellowTextView, priceSilverTextView, priceGrayTextView;
    private ImageView  yesBuyFailButton;
    private long userCoins;
    private ImageView backgroundReturnImageView, backgroundReturnRankImageView;
    private RelativeLayout groupPlay, groupShop, groupRank, shopRelativeLayout, rankRelativeLayout;
    private ImageButton buttonShop, buttonRank;
    public boolean useSkinYellow, useSkinSilver, useSkinGray, useSkinBlack;
    private TextView name1, score1, name2, score2, name3, score3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupPlay = findViewById(R.id.group_play);
        groupShop = findViewById(R.id.group_shop);
        groupRank = findViewById(R.id.group_rank);
        backgroundReturnImageView = findViewById(R.id.Background_Return);
        shopRelativeLayout = findViewById(R.id.Shop);
        buttonShop = findViewById(R.id.button_shop);
        buttonRank = findViewById(R.id.button_rank);
        backgroundReturnRankImageView = findViewById(R.id.Background_ReturnRank);
        rankRelativeLayout = findViewById(R.id.Rank);
        coinTextView = findViewById(R.id.coin);
        buyItemYellowLayout = findViewById(R.id.BuyItemYellow);
        buyItemSilverLayout = findViewById(R.id.BuyItemSilver);
        buyItemGrayLayout = findViewById(R.id.BuyItemGray);
        buyFailedLayout = findViewById(R.id.BuyFailed);
        groupPriceYellow = findViewById(R.id.group_price_yellow);
        groupPriceSilver = findViewById(R.id.group_price_silver);
        groupPriceGray = findViewById(R.id.group_price_gray);
        groupUseYellow = findViewById(R.id.group_use_yellow);
        groupUseSilver = findViewById(R.id.group_use_silver);
        groupUseGray = findViewById(R.id.group_use_gray);
        name1 = findViewById(R.id.name_1);
        score1 = findViewById(R.id.score_1);
        name2 = findViewById(R.id.name_2);
        score2 = findViewById(R.id.score_2);
        name3 = findViewById(R.id.name_3);
        score3 = findViewById(R.id.score_3);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        if (intent.getBooleanExtra("updateLeaderboard", false)) {
            updateLeaderboard();
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("coins");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userCoins = dataSnapshot.getValue(Long.class);
                        coinTextView.setText(String.valueOf(userCoins));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }
            });
        } else {
            // Xử lý trường hợp người dùng chưa đăng nhập
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child("ownedSkins").exists()) {
                        databaseReference.child("ownedSkins").setValue(new HashMap<String, Boolean>() {{
                            put("blackSkin", true);
                            put("yellowSkin", false);
                            put("silverSkin", false);
                            put("graySkin", false);
                        }});
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }
            });

            // Phần còn lại của code đăng nhập...
        }


        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.child("coins").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userCoins = dataSnapshot.getValue(Long.class);
                        coinTextView.setText(String.valueOf(userCoins));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }
            });

            databaseReference.child("ownedSkins").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        boolean yellowSkinOwned = dataSnapshot.child("yellowSkin").getValue(Boolean.class);
                        boolean silverSkinOwned = dataSnapshot.child("silverSkin").getValue(Boolean.class);
                        boolean graySkinOwned = dataSnapshot.child("graySkin").getValue(Boolean.class);

                        if (yellowSkinOwned) {
                            groupPriceYellow.setVisibility(View.GONE);
                        }

                        if (silverSkinOwned) {
                            groupPriceSilver.setVisibility(View.GONE);
                        }

                        if (graySkinOwned) {
                            groupPriceGray.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }

            });
        } else {
            // Xử lý trường hợp người dùng chưa đăng nhập
        }

        priceYellowTextView = findViewById(R.id.PriceYellow);
        priceSilverTextView = findViewById(R.id.PriceSilver);
        priceGrayTextView = findViewById(R.id.PriceGray);

        ImageView yesButtonYellow = findViewById(R.id.YesYellow);
        ImageView noButtonYellow = findViewById(R.id.NoYellow);
        ImageView yesButtonSilver = findViewById(R.id.YesSilver);
        ImageView noButtonSilver = findViewById(R.id.NoSilver);
        ImageView yesButtonGray = findViewById(R.id.YesGray);
        ImageView noButtonGray = findViewById(R.id.NoGray);
        yesBuyFailButton = findViewById(R.id.YesBuyFail);

        yesButtonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndBuyYellow(priceYellowTextView.getText().toString());
            }
        });

        noButtonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.BuyItemYellow).setVisibility(View.GONE);
            }
        });

        yesButtonSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndBuySilver(priceSilverTextView.getText().toString());
            }
        });

        noButtonSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.BuyItemSilver).setVisibility(View.GONE);
            }
        });

        yesButtonGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndBuyGray(priceGrayTextView.getText().toString());
            }
        });

        noButtonGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.BuyItemGray).setVisibility(View.GONE);
            }
        });

        yesBuyFailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyFailedLayout.setVisibility(View.GONE);
            }
        });

        startAnimation(R.id.background_meo);
        startAnimation(R.id.background_gau);
        startMusic();

        buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupPlay.setVisibility(View.GONE);
                groupShop.setVisibility(View.GONE);
                groupRank.setVisibility(View.GONE);
                shopRelativeLayout.setVisibility(View.VISIBLE);
            }
        });

        backgroundReturnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupPlay.setVisibility(View.VISIBLE);
                groupShop.setVisibility(View.VISIBLE);
                groupRank.setVisibility(View.VISIBLE);
                shopRelativeLayout.setVisibility(View.GONE);
            }
        });

        buttonRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupPlay.setVisibility(View.GONE);
                groupShop.setVisibility(View.GONE);
                groupRank.setVisibility(View.GONE);
                rankRelativeLayout.setVisibility(View.VISIBLE);
                updateLeaderboard();
            }
        });

        backgroundReturnRankImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupPlay.setVisibility(View.VISIBLE);
                groupShop.setVisibility(View.VISIBLE);
                groupRank.setVisibility(View.VISIBLE);
                rankRelativeLayout.setVisibility(View.GONE);
            }
        });



    }
    public void openBuyItemYellowLayout(View view) {
        buyItemYellowLayout.setVisibility(View.VISIBLE);
    }
    public void openBuyItemSilverLayout(View view) {
        buyItemSilverLayout.setVisibility(View.VISIBLE);
    }
    public void openBuyItemGrayLayout(View view) {

        buyItemGrayLayout.setVisibility(View.VISIBLE);
    }

    public void openUseItemBlackLayout(View view) {
        mediaPlayer = MediaPlayer.create(this, R.raw.meow_black);
        mediaPlayer.start();
        groupUseYellow.setVisibility(View.VISIBLE);
        groupUseSilver.setVisibility(View.VISIBLE);
        groupUseGray.setVisibility(View.VISIBLE);
        useSkinYellow = false;
        useSkinSilver = false;
        useSkinGray = false;

    }
    public void openUseItemYellowLayout(View view) {
        mediaPlayer = MediaPlayer.create(this, R.raw.meow_yellow);
        mediaPlayer.start();
        groupUseYellow.setVisibility(View.GONE);
        groupUseSilver.setVisibility(View.VISIBLE);
        groupUseGray.setVisibility(View.VISIBLE);
        useSkinYellow = true;
        useSkinSilver = false;
        useSkinGray = false;

    }
    public void openUseItemSilverLayout(View view) {
        mediaPlayer = MediaPlayer.create(this, R.raw.meow_silver);
        mediaPlayer.start();
        groupUseYellow.setVisibility(View.VISIBLE);
        groupUseSilver.setVisibility(View.GONE);
        groupUseGray.setVisibility(View.VISIBLE);
        useSkinYellow = false;
        useSkinSilver = true;
        useSkinGray = false;
    }
    public void openUseItemGrayLayout(View view) {
        mediaPlayer = MediaPlayer.create(this, R.raw.meow_gray);
        mediaPlayer.start();
        groupUseYellow.setVisibility(View.VISIBLE);
        groupUseSilver.setVisibility(View.VISIBLE);
        groupUseGray.setVisibility(View.GONE);
        useSkinGray = true;
        useSkinYellow = false;
        useSkinSilver = false;
    }

    private void checkAndBuyYellow(String priceText) {
        long itemPrice = Long.parseLong(priceText);
        if (userCoins >= itemPrice) {
            userCoins -= itemPrice;
            coinTextView.setText(String.valueOf(userCoins));
            databaseReference.child("coins").setValue(userCoins);
            databaseReference.child("ownedSkins").child("yellowSkin").setValue(true); // Cập nhật skin đã sở hữu
            groupPriceYellow.setVisibility(View.GONE);
            buyItemYellowLayout.setVisibility(View.GONE);
        } else {
            buyFailedLayout.setVisibility(View.VISIBLE);
            buyItemYellowLayout.setVisibility(View.GONE);
        }
    }

    private void checkAndBuySilver(String priceText) {
        long itemPrice = Long.parseLong(priceText);
        if (userCoins >= itemPrice) {
            userCoins -= itemPrice;
            coinTextView.setText(String.valueOf(userCoins));
            databaseReference.child("coins").setValue(userCoins);
            databaseReference.child("ownedSkins").child("silverSkin").setValue(true); // Cập nhật skin đã sở hữu
            groupPriceSilver.setVisibility(View.GONE);
            buyItemSilverLayout.setVisibility(View.GONE);
        } else {
            buyFailedLayout.setVisibility(View.VISIBLE);
            buyItemSilverLayout.setVisibility(View.GONE);
        }
    }

    private void checkAndBuyGray(String priceText) {
        long itemPrice = Long.parseLong(priceText);
        if (userCoins >= itemPrice) {
            userCoins -= itemPrice;
            coinTextView.setText(String.valueOf(userCoins));
            databaseReference.child("coins").setValue(userCoins);
            databaseReference.child("ownedSkins").child("graySkin").setValue(true);
            groupPriceGray.setVisibility(View.GONE);
            buyItemGrayLayout.setVisibility(View.GONE);
        } else {
            buyFailedLayout.setVisibility(View.VISIBLE);
            buyItemGrayLayout.setVisibility(View.GONE);
        }
    }

    private void updateLeaderboard() {
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference("users");
        playersRef.orderByChild("highScore").limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    long highScore = snapshot.child("highScore").getValue(Long.class);
                    switch (i) {
                        case 3:
                            name1.setText(username);
                            score1.setText(String.valueOf(highScore));
                            break;
                        case 2:
                            name2.setText(username);
                            score2.setText(String.valueOf(highScore));
                            break;
                        case 1:
                            name3.setText(username);
                            score3.setText(String.valueOf(highScore));
                            break;
                        default:
                            break;
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void startAnimation(int imageViewId) {
        ImageView imageView = findViewById(imageViewId);
        if (imageView != null) {
            AnimationDrawable animation = (AnimationDrawable) imageView.getDrawable();
            if (animation != null) {
                animation.start();
            }
        }
    }

    private void startMusic() {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ost);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void startGame(View view) {
        stopMusic();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("updateLeaderboard", true);
        intent.putExtra("useSkinBlack", useSkinBlack);
        intent.putExtra("useSkinYellow", useSkinYellow);
        intent.putExtra("useSkinSilver", useSkinSilver);
        intent.putExtra("useSkinGray", useSkinGray);
        startActivity(intent);
        overridePendingTransition(R.anim.vao_game, R.anim.out_game);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}