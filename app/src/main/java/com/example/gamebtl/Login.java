package com.example.gamebtl;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Login extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private ImageButton Btn;
    private ProgressBar progressbar;
    private TextView linkSignup;
    private FirebaseAuth mAuth;

    public void CheckPassword(View view) {
        EditText matKhauEditText = findViewById(R.id.etPassword);
        CheckBox checkBoxShowPassword = findViewById(R.id.CheckPassword);
        ImageView mouseCheckImageView = findViewById(R.id.mouse_check);

        if (checkBoxShowPassword.isChecked()) {
            matKhauEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mouseCheckImageView.setVisibility(View.VISIBLE); // Show the mouse_check ImageView
        } else {
            matKhauEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mouseCheckImageView.setVisibility(View.GONE); // Hide the mouse_check ImageView
        }
        matKhauEditText.setSelection(matKhauEditText.length());

        // Use ResourcesCompat to be compatible with lower API versions
        Typeface typeface = ResourcesCompat.getFont(this, R.font.minecrafter_3);
        matKhauEditText.setTypeface(typeface);
        matKhauEditText.setTextSize(5);
        matKhauEditText.setTextColor(getResources().getColor(R.color.white));
        matKhauEditText.setShadowLayer(3, 3, 3, getResources().getColor(R.color.black));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.etEmail);
        passwordTextView = findViewById(R.id.etPassword);
        Btn = findViewById(R.id.start_login);
        progressbar = findViewById(R.id.progressBar);
        linkSignup = findViewById(R.id.Link_Signup);

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        // Set on Click Listener on Register text
        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SignUp activity
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    // Thêm các biến để lưu tài khoản Admin mặc định
    private final String adminUsername = "Admin";
    private final String adminPassword = "Admin";

    private void loginUserAccount() {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            progressbar.setVisibility(View.GONE);
            return;
        }

        // Kiểm tra nếu tài khoản là Admin
        if (email.equals(adminUsername) && password.equals(adminPassword)) {
            // Chuyển hướng tới trang quản lý
            Toast.makeText(getApplicationContext(),
                            "Admin login successful!! Redirecting to management page...",
                            Toast.LENGTH_LONG)
                    .show();
            progressbar.setVisibility(View.GONE);

            // Chuyển hướng tới trang quản lý
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://translate.google.com/?sl=en&tl=vi&op=translate"));
            startActivity(browserIntent);
            return; // Kết thúc hàm ở đây để không thực hiện các logic phía dưới
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                                    "Login successful!!",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    // hide the progress bar
                                    progressbar.setVisibility(View.GONE);

                                    // if sign-in is successful
                                    // intent to home activity
                                    Intent intent
                                            = new Intent(Login.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                } else {

                                    // sign-in failed
                                    if (task.getException() != null) {
                                        String errorMessage = task.getException().getMessage();
                                        if (errorMessage.contains("password is invalid")) {
                                            Toast.makeText(getApplicationContext(),
                                                            "Invalid password. Please try again!!",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } else if (errorMessage.contains("no user record")) {
                                            Toast.makeText(getApplicationContext(),
                                                            "Email address not found. Please sign up!!",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                            "Login failed!! Please try again later",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                        "Login failed!! Please try again later",
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }

                                    // hide the progress bar
                                    progressbar.setVisibility(View.GONE);
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
