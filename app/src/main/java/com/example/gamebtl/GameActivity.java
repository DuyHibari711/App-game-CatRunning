package com.example.gamebtl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.media.MediaPlayer;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.graphics.drawable.AnimationDrawable;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private ImageView background1, background2, background3, background4, background5, background6, heart1, heart2, heart3;
    private RelativeLayout gameLayout, level_1Layout, level_2Layout, level_3Layout, exitLayout;
    private MediaPlayer mediaPlayer, coinSound;
    private boolean isInvincible = false; // Biến để kiểm tra trạng thái miễn sát thương
    private final int INVINCIBLE_DURATION = 3000; // Thời gian miễn sát thương (3 giây)
    private int screenWidth, backgroundSpeed = 10;
    private int savedBackgroundSpeed;
    private static final int MAX_BG_SPEED = 20;
    private Map game_Map;
    private Player player;
    private List<Enemy> enemies;
    private final Random random=new Random();
    private AnimationDrawable runAnimation;
    private TextView scoreTextView;
    private Handler handler = new Handler();
    private int score = 0;
    private int hearts = 3;
    private TextView coinTextView;
    private int coinCount = 0;
    private List<Coin> coins = new ArrayList<>();
    private boolean isGameOver = false;
    private boolean isJumping = false;
    private int jumpCount = 0;
    private Handler jumpHandler = new Handler();
    Coin coin;
    private Runnable jumpRunnable = new Runnable() {
        @Override
        public void run() {
            if (jumpCount == 1) {
                if (lastTouchX < screenWidth / 2) {
                    player.jumpLeft();
                } else {
                    player.jumpRight();
                }
            } else if (jumpCount >= 2) {
                if (lastTouchX < screenWidth / 2) {
                    player.x2jumpLeft();
                } else {
                    player.x2jumpRight();
                }
            }
            jumpCount = 0;
        }
    };

    private float lastTouchX; // Biến lưu trữ tọa độ x của lần chạm cuối cùng
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        boolean useSkinBlack= getIntent().getBooleanExtra("useSkinBlack", true);
        boolean useSkinYellow = getIntent().getBooleanExtra("useSkinYellow", false);
        boolean useSkinSilver = getIntent().getBooleanExtra("useSkinSilver", false);
        boolean useSkinGray = getIntent().getBooleanExtra("useSkinGray", false);

        gameLayout = findViewById(R.id.game_layout);
        exitLayout = findViewById(R.id.ExitNotification);
        level_1Layout = findViewById(R.id.Level_1);
        level_2Layout = findViewById(R.id.Level_2);
        level_3Layout = findViewById(R.id.Level_3);
        background1 = findViewById(R.id.background1);
        background2 = findViewById(R.id.background2);
        background3 = findViewById(R.id.background3);
        background4 = findViewById(R.id.background4);
        background5 = findViewById(R.id.background5);
        background6 = findViewById(R.id.background6);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        background1.setX(0);
        background1.setY(0);
        background2.setX(screenWidth);
        background2.setY(0);
        background3.setX(0);
        background3.setY(0);
        background4.setX(screenWidth);
        background4.setY(0);
        background5.setX(0);
        background5.setY(0);
        background6.setX(screenWidth);
        background6.setY(0);
        moveBackground();
        coinSound = MediaPlayer.create(this, R.raw.coin);
        heart1 = findViewById(R.id.Heart_1);
        heart2 = findViewById(R.id.Heart_2);
        heart3 = findViewById(R.id.Heart_3);


        game_Map = new Map(this);
        gameLayout.addView(game_Map);

        player = new Player(this);
        gameLayout.addView(player);

        player.setUseSkinYellow(useSkinYellow);
        player.setUseSkinSilver(useSkinSilver);
        player.setUseSkinGray(useSkinGray);

        if (useSkinYellow) {
            runAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.yellow_run);
        } else if (useSkinSilver) {
            runAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.silver_run);
        } else if (useSkinGray) {
            runAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.gray_run);
        } else if (useSkinBlack) {
            runAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.cat_run);
        } else {
            runAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.cat_run);
        }
        startAnimation(player, runAnimation);

        if (game_Map != null) {
            player.setMap(game_Map);
        }

        // Khởi tạo Firebase Auth và Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        enemies = new ArrayList<>();
        EnemyDog enemydog = new EnemyDog(this);
        enemydog.setMap(game_Map);
        enemies.add(enemydog);

        EnemyTurtle enemyturtle = new EnemyTurtle(this);
        enemyturtle.setMap(game_Map);
        enemies.add(enemyturtle);


        EnemyFrog enemyfrog = new EnemyFrog(this);
        enemyfrog.setMap(game_Map);
        enemies.add(enemyfrog);

        EnemyBird enemybird = new EnemyBird(this);
        enemybird.setMap(game_Map);
        enemies.add(enemybird);

        for (Enemy enemy : enemies) {
            gameLayout.addView(enemy);
        }

        coinTextView = findViewById(R.id.coin);

        coin = new Coin(this);
        coin.setMap(game_Map);
        coin.setX(gameLayout.getWidth() - coin.getWidth());
        gameLayout.addView(coin);

        scoreTextView = findViewById(R.id.score);
        updateScore();
        startMusic();

        RelativeLayout settingLayout = findViewById(R.id.Setting);
        settingLayout.setVisibility(View.INVISIBLE);

        RelativeLayout notificationLayout = findViewById(R.id.Notification);
        notificationLayout.setVisibility(View.INVISIBLE);

        ImageButton homeButton = findViewById(R.id.home);
        ImageButton redoButton = findViewById(R.id.redo);
        ImageButton exitButton = findViewById(R.id.exit);
        ImageButton pauseButton = findViewById(R.id.btn_pause);
        ImageButton musicoffButton = findViewById(R.id.music_off);
        ImageButton musicmidButton = findViewById(R.id.music_mid);
        ImageButton musicfullButton = findViewById(R.id.music_full);
        ImageButton bgmusicoffButton = findViewById(R.id.bg_music_off);
        ImageButton bgmusicmidButton = findViewById(R.id.bg_music_mid);
        ImageButton bgmusicfullButton = findViewById(R.id.bg_music_full);
        ImageButton bgpickmusicoffButton = findViewById(R.id.bg_music_off_pick);
        ImageButton bgpickmusicmidButton = findViewById(R.id.bg_music_mid_pick);
        ImageButton bgpickmusicfullButton = findViewById(R.id.bg_music_full_pick);
        ImageView yesExit = findViewById(R.id.YesExit);
        ImageView noExit = findViewById(R.id.NoExit);
        ImageButton homesettingButton = findViewById(R.id.home_setting);
        ImageButton resumeButton = findViewById(R.id.resume);
        ImageButton redosettingButton = findViewById(R.id.redo_setting);


        gameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastTouchX = event.getX(); // Lưu tọa độ x của lần chạm cuối cùng
                    jumpCount++;
                    jumpHandler.removeCallbacks(jumpRunnable);
                    jumpHandler.postDelayed(jumpRunnable, 200);
                }
                return true;
            }
        });

        musicoffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgmusicoffButton.setVisibility(View.GONE);
                bgmusicmidButton.setVisibility(View.VISIBLE);
                bgmusicfullButton.setVisibility(View.VISIBLE);

                bgpickmusicoffButton.setVisibility(View.VISIBLE);
                bgpickmusicmidButton.setVisibility(View.GONE);
                bgpickmusicfullButton.setVisibility(View.GONE);
                setVolume(0);
            }
        });

        musicmidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgmusicoffButton.setVisibility(View.VISIBLE);
                bgmusicmidButton.setVisibility(View.GONE);
                bgmusicfullButton.setVisibility(View.VISIBLE);

                bgpickmusicoffButton.setVisibility(View.GONE);
                bgpickmusicmidButton.setVisibility(View.VISIBLE);
                bgpickmusicfullButton.setVisibility(View.GONE);
                setVolume(0.5f);
            }
        });

        musicfullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgmusicoffButton.setVisibility(View.VISIBLE);
                bgmusicmidButton.setVisibility(View.VISIBLE);
                bgmusicfullButton.setVisibility(View.GONE);

                bgpickmusicoffButton.setVisibility(View.GONE);
                bgpickmusicmidButton.setVisibility(View.GONE);
                bgpickmusicfullButton.setVisibility(View.VISIBLE);
                setVolume(1.0f);
            }
        });

        yesExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finishAffinity();}
        });

        noExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitLayout.setVisibility(View.GONE);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
        homesettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        redosettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitGame();
            }
        });

        // Đọc điểm cao nhất hiện tại từ Firebase khi bắt đầu hoạt động
        getCurrentHighScore(new OnHighScoreReceivedListener() {
            @Override
            public void onHighScoreReceived(int currentHighScore) {
                // Hiển thị điểm cao nhất lên giao diện người dùng
                TextView highScoreTextView = findViewById(R.id.score_end);
                highScoreTextView.setText(String.valueOf(currentHighScore));
            }
        });
    }

    // Phương thức đọc điểm cao nhất hiện tại từ Firebase
    private void getCurrentHighScore(final OnHighScoreReceivedListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("highScore").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer currentHighScore = snapshot.getValue(Integer.class);
                    if (currentHighScore == null) {
                        currentHighScore = 0;
                    }
                    listener.onHighScoreReceived(currentHighScore);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Lỗi khi đọc điểm cao nhất.", error.toException());
                    listener.onHighScoreReceived(0);
                }
            });
        } else {
            listener.onHighScoreReceived(0);
        }
    }

    interface OnHighScoreReceivedListener {
        void onHighScoreReceived(int currentHighScore);
    }
    // Phương thức cập nhật điểm cao nhất vào Firebase
    private void updateHighScoreInFirebase(final int score) {
        getCurrentHighScore(new OnHighScoreReceivedListener() {
            @Override
            public void onHighScoreReceived(int currentHighScore) {
                if (score > currentHighScore) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        mDatabase.child("users").child(userId).child("highScore").setValue(score)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Điểm cao nhất đã được cập nhật thành công.");
                                } else {
                                    Log.e("Firebase", "Lỗi khi cập nhật điểm cao nhất.", task.getException());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.game_music);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
        }
        mediaPlayer.start();
    }

    private void startAnimation(ImageView imageView, AnimationDrawable animation) {
        imageView.setImageDrawable(animation);
        animation.start();
        isJumping = !isJumping;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAnimation(player, runAnimation);
    }

    private void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
    // Áp dụng hiệu ứng fade in
    private void applyFadeIn(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            view.startAnimation(fadeIn);
            view.setVisibility(View.VISIBLE);
        }
    }

    // Áp dụng hiệu ứng fade out
    private void applyFadeOut(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            view.startAnimation(fadeOut);
            view.setVisibility(View.GONE);
        }
    }
    private void moveBackground() {
        gameLayout.post(new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    if (score < 30) {
                        background1.setX(background1.getX() - backgroundSpeed);
                        background2.setX(background2.getX() - backgroundSpeed);
                        applyFadeIn(background1);
                        applyFadeIn(background2);
                        applyFadeIn(level_1Layout);

                        if (background1.getX() + background1.getWidth() <= 0) {
                            background1.setX(background2.getX() + background2.getWidth() - backgroundSpeed);
                            increaseScore(10);
                        }

                        if (background2.getX() + background2.getWidth() <= 0) {
                            background2.setX(background1.getX() + background1.getWidth() - backgroundSpeed);
                            increaseScore(10);
                            if (backgroundSpeed < MAX_BG_SPEED) {
                                backgroundSpeed += 1;
                            }
                        }
                    } else if (score < 50 && score >= 30) {
                        background3.setX(background3.getX() - backgroundSpeed);
                        background4.setX(background4.getX() - backgroundSpeed);
                        applyFadeIn(background3);
                        applyFadeIn(background4);
                        applyFadeIn(level_2Layout);
                        applyFadeOut(background1);
                        applyFadeOut(background2);
                        applyFadeOut(level_1Layout);
                        if (background3.getX() + background3.getWidth() <= 0) {
                            background3.setX(background4.getX() + background4.getWidth() - backgroundSpeed);
                            increaseScore(10);
                        }

                        if (background4.getX() + background4.getWidth() <= 0) {
                            background4.setX(background3.getX() + background3.getWidth() - backgroundSpeed);
                            increaseScore(10);
                            if (backgroundSpeed < MAX_BG_SPEED) {
                                backgroundSpeed += 1;
                            }
                        }
                    } else {
                        background5.setX(background5.getX() - backgroundSpeed);
                        background6.setX(background6.getX() - backgroundSpeed);
                        applyFadeIn(background5);
                        applyFadeIn(background6);
                        applyFadeIn(level_3Layout);
                        applyFadeOut(background3);
                        applyFadeOut(background4);
                        applyFadeOut(level_2Layout);

                        if (background5.getX() + background5.getWidth() <= 0) {
                            background5.setX(background6.getX() + background6.getWidth() - backgroundSpeed);
                            increaseScore(10);
                        }

                        if (background6.getX() + background6.getWidth() <= 0) {
                            background6.setX(background5.getX() + background5.getWidth() - backgroundSpeed);
                            increaseScore(10);
                            if (backgroundSpeed < MAX_BG_SPEED) {
                                backgroundSpeed += 1;
                            }
                        }
                    }

                    for (Enemy enemy : enemies) {
                        enemy.setBackgroundSpeed(backgroundSpeed);
                    }

                    for (Coin coin : coins) {
                        coin.setBackgroundSpeed(backgroundSpeed);
                    }

                    checkCollision();
                    if (!isGameOver) {
                        moveBackground(); // Gọi lại moveBackground để di chuyển background tiếp
                    }
                }
            }
        });
    }

    private void updateScore() {
        scoreTextView.setText(String.valueOf(score));
    }

    private void increaseScore(int points) {
        score += points;
        updateScore();
    }

    private void pauseGame() {
        ImageButton pauseButton = findViewById(R.id.pause_game);
        pauseButton.setVisibility(View.INVISIBLE);

        ImageButton pauseBackground = findViewById(R.id.btn_pause);
        pauseBackground.setVisibility(View.INVISIBLE);

        RelativeLayout settingLayout = findViewById(R.id.Setting);
        settingLayout.setVisibility(View.VISIBLE);

        savedBackgroundSpeed = backgroundSpeed;
        backgroundSpeed = 0;

        setGameControlsEnabled(false);

        handler.removeCallbacksAndMessages(null);
        mediaPlayer.pause();
    }

    private void resumeGame() {
        RelativeLayout settingLayout = findViewById(R.id.Setting);
        settingLayout.setVisibility(View.INVISIBLE);

        ImageButton pauseBackground = findViewById(R.id.btn_pause);
        pauseBackground.setVisibility(View.VISIBLE);

        ImageButton pauseButton = findViewById(R.id.pause_game);
        pauseButton.setVisibility(View.VISIBLE);

        backgroundSpeed = savedBackgroundSpeed;

        setGameControlsEnabled(true);
        mediaPlayer.start();
    }


    private void setGameControlsEnabled(boolean enabled) {
        gameLayout.setEnabled(enabled);
        for (int i = 0; i < gameLayout.getChildCount(); i++) {
            View child = gameLayout.getChildAt(i);
            if (child.getId() != R.id.Setting && child.getId() != R.id.Notification) {
                child.setEnabled(enabled);
            }
        }
    }



    private void updateCoinCount() {
        coinTextView.setText(String.valueOf(coinCount));
    }

    private void increaseCoinCount() {
        coinCount++;
        updateCoinCount();
    }

    private void checkCollision() {
        for (Enemy enemy : enemies) {
            int enemyX = (int) enemy.getX();
            int enemyY = (int) enemy.getY();
            int playerX = (int) player.getX();
            int playerY = (int) player.getY();

            int enemyWidth = enemy.getWidth();
            int enemyHeight = enemy.getHeight();
            int playerWidth = player.getWidth();
            int playerHeight = player.getHeight();

            if (enemyX < playerX + playerWidth &&
                    enemyX + enemyWidth > playerX &&
                    enemyY < playerY + playerHeight &&
                    enemyY + enemyHeight > playerY) {
                if (!isInvincible) {
                    hearts--;
                    updateHeartsDisplay();

                    // Nếu nhân vật hết hearts, kết thúc game
                    if (hearts == 0) {
                        Endgame();
                    } else {
                        // Bật trạng thái miễn sát thương và khởi động thời gian chờ
                        isInvincible = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isInvincible = false; // Sau 3 giây, có thể nhận sát thương lại
                            }
                        }, INVINCIBLE_DURATION);
                    }
                }
                break;
            }
        }

        if (coin.getVisibility()==View.VISIBLE) {
            int coinX = (int) coin.getX();
            int coinY = (int) coin.getY();
            int playerX = (int) player.getX();
            int playerY = (int) player.getY();
            int coinWidth = coin.getWidth();
            int coinHeight = coin.getHeight();
            int playerWidth = player.getWidth();
            int playerHeight = player.getHeight();

            if (coinX < playerX + playerWidth &&
                    coinX + coinWidth > playerX &&
                    coinY < playerY + playerHeight &&
                    coinY + coinHeight > playerY && coin.getVisibility() == View.VISIBLE) {
                coin.setVisibility(View.GONE);
                synchronized (coin) {
                    coinSound.start();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        coin.setX(gameLayout.getWidth() - coin.getWidth());
                        coin.setY(gameLayout.getHeight()-random.nextInt(500)-300);
                        coin.setVisibility(View.VISIBLE);
                    }, random.nextInt(1000)+1000);

                }
                increaseCoinCount();
            }
            if(coinX>0 ){
                coin.setX(coinX-backgroundSpeed);
            }
            else {
                coin.setX(gameLayout.getWidth() - coin.getWidth());
            }
        }
    }

    private void updateHeartsDisplay() {
        if (hearts == 2) {
            heart3.setVisibility(View.INVISIBLE);
        } else if (hearts == 1) {
            heart2.setVisibility(View.INVISIBLE);
        } else if (hearts == 0) {
            heart1.setVisibility(View.INVISIBLE);
        }
    }
    private void Endgame() {
        stopGame();
        isGameOver = true;
        showGameOverDialog();
        RelativeLayout gameLayout = findViewById(R.id.game_layout);
        setGameControlsEnabled(false);
        updateCoinsInFirebase(coinCount);
        updateHighScoreInFirebase(score);
    }

    private void stopGame() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);

        player.die = true;
        if (player.useSkinYellow) {
            player.setImageResource(R.drawable.yellow_thua);
        } else if (player.useSkinSilver) {
            player.setImageResource(R.drawable.silver_thua);
        }  else if (player.useSkinGray) {
            player.setImageResource(R.drawable.gray_thua);
        }  else if (player.useSkinBlack) {
            player.setImageResource(R.drawable.black_thua);
        }  else {
        player.setImageResource(R.drawable.black_thua);
    }

        ImageButton pauseButton = findViewById(R.id.pause_game);
        pauseButton.setVisibility(View.INVISIBLE);

        ImageButton pauseBackground = findViewById(R.id.btn_pause);
        pauseBackground.setVisibility(View.INVISIBLE);

        backgroundSpeed = 0;
        for (Enemy enemy : enemies) {
            enemy.setBackgroundSpeed(backgroundSpeed);
        }
    }

    private void showGameOverDialog() {
        RelativeLayout notificationLayout = findViewById(R.id.Notification);
        notificationLayout.setVisibility(View.VISIBLE);
        TextView scoreEndNotificationTextView = findViewById(R.id.score_end);
        scoreEndNotificationTextView.setText(String.valueOf(score));
        TextView coinEndNotificationTextView = findViewById(R.id.coin_end);
        coinEndNotificationTextView.setText(String.valueOf(coinCount));
    }

    private void restartGame() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void exitGame() {
        exitLayout.setVisibility(View.VISIBLE);
    }

    // Phương thức đọc số vàng hiện tại từ Firebase
    private void getCurrentCoins(final OnCoinsReceivedListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer currentCoins = snapshot.getValue(Integer.class);
                    if (currentCoins == null) {
                        currentCoins = 0;
                    }
                    listener.onCoinsReceived(currentCoins);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Lỗi khi đọc số tiền.", error.toException());
                    listener.onCoinsReceived(0);
                }
            });
        } else {
            listener.onCoinsReceived(0);
        }
    }

    interface OnCoinsReceivedListener {
        void onCoinsReceived(int currentCoins);
    }

    // Phương thức cập nhật số tiền vào Firebase cộng dồn
    private void updateCoinsInFirebase(final int coins) {
        getCurrentCoins(new OnCoinsReceivedListener() {
            @Override
            public void onCoinsReceived(int currentCoins) {
                int newCoins = currentCoins + coins;
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    mDatabase.child("users").child(userId).child("coins").setValue(newCoins)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "Số tiền đã được cập nhật thành công.");
                            } else {
                                Log.e("Firebase", "Lỗi khi cập nhật số tiền.", task.getException());
                            }
                        }
                    });
                }
            }
        });
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
