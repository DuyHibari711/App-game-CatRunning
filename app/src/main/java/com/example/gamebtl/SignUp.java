package com.example.gamebtl;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private EditText usenameTextView, emailTextView, passwordTextView;
    private ImageButton Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public void CheckPassword(View view) {
        EditText matKhauEditText = findViewById(R.id.etPassword);
        CheckBox checkBoxShowPassword = findViewById(R.id.CheckPassword);
        ImageView mouseCheckImageView = findViewById(R.id.mouse_check);

        if (checkBoxShowPassword.isChecked()) {
            matKhauEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mouseCheckImageView.setVisibility(View.VISIBLE);
        } else {
            matKhauEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mouseCheckImageView.setVisibility(View.GONE);
        }
        matKhauEditText.setSelection(matKhauEditText.length());

        Typeface typeface = ResourcesCompat.getFont(this, R.font.minecrafter_3);
        matKhauEditText.setTypeface(typeface);
        matKhauEditText.setTextSize(5);
        matKhauEditText.setTextColor(getResources().getColor(R.color.white));
        matKhauEditText.setShadowLayer(3, 3, 3, getResources().getColor(R.color.black));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        usenameTextView = findViewById(R.id.etUsername);
        emailTextView = findViewById(R.id.etEmail);
        passwordTextView = findViewById(R.id.etPassword);
        Btn = findViewById(R.id.start_signup);
        progressbar = findViewById(R.id.progressbar);

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        progressbar.setVisibility(View.VISIBLE);

        String username, email, password;
        username = usenameTextView.getText().toString();
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please enter username!!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters!!", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        mDatabase.child("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "Username already exists!!", Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            String userId = currentUser.getUid();
                                            HashMap<String, Object> userData = new HashMap<>();
                                            userData.put("username", username);
                                            userData.put("email", email);
                                            userData.put("highScore", 0);
                                            userData.put("coins", 0);
                                            userData.put("ownedSkins", new HashMap<String, Boolean>() {{
                                                put("blackSkin", true);
                                                put("yellowSkin", false);
                                                put("silverSkin", false);
                                                put("graySkin", false);
                                            }});

                                            mDatabase.child("users").child(userId).setValue(userData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                                                progressbar.setVisibility(View.GONE);
                                                                Intent intent = new Intent(SignUp.this, Login.class);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Failed to save user data!", Toast.LENGTH_LONG).show();
                                                                progressbar.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Registration failed!! Please try again later", Toast.LENGTH_LONG).show();
                                        progressbar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database error!! Please try again later", Toast.LENGTH_LONG).show();
                progressbar.setVisibility(View.GONE);
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
