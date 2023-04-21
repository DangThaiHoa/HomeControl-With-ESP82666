package com.example.homecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    ImageView backbtn;
    TextInputEditText email, password;
    Button loginBtn,signupBtn;
    TextView forgetPassword;

    ProgressLoading progressLoading;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressLoading = new ProgressLoading(Login.this);

        auth = FirebaseAuth.getInstance();

        backbtn = findViewById(R.id.login_back_button);
        loginBtn = findViewById(R.id.button_login);
        signupBtn = findViewById(R.id.button_signup_login);
        password = findViewById(R.id.Password_login);
        forgetPassword = findViewById(R.id.forgetPassword_button);
        email = findViewById(R.id.Email_login);

        loginBtn.setOnClickListener(view -> {
            String gEmail = Objects.requireNonNull(email.getText()).toString();
            String gPassword = Objects.requireNonNull(password.getText()).toString();
            if (gEmail.isEmpty() || gPassword.isEmpty()) {

                Toast.makeText(Login.this, "Vui Lòng Nhập Đầy Đủ Thông Tin", Toast.LENGTH_SHORT).show();

            } else {

                if (Patterns.EMAIL_ADDRESS.matcher(gEmail).matches()) {
                    progressLoading.show();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        progressLoading.dismiss();
                        auth.signInWithEmailAndPassword(gEmail, gPassword)
                                .addOnCompleteListener(Login.this, task -> {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Sai Email Hoặc Mật Khẩu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }, 2000);
                }else{

                    email.forceLayout();
                    Toast.makeText(Login.this, "Vui Lòng Nhập Đúng Đinh Dạng Email", Toast.LENGTH_SHORT).show();

                }
            }
        });

        signupBtn();
        backBtn();
        forgetPassword();
    }

    private void forgetPassword() {

        forgetPassword.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, ForgetPassword.class);
            startActivity(intent);
        });

    }

    private void signupBtn() {

        signupBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),SignUp.class);
            startActivity(intent);
        });

    }

    private void backBtn() {

        backbtn.setOnClickListener(view -> Login.super.onBackPressed());

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}