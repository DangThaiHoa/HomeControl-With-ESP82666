package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gEmail = email.getText().toString();
                String gPassword = password.getText().toString();
                if (gEmail.isEmpty() || gPassword.isEmpty()) {

                    Toast.makeText(Login.this, "Vui Lòng Nhập Đầy Đủ Thông Tin", Toast.LENGTH_SHORT).show();

                } else {

                    if (Patterns.EMAIL_ADDRESS.matcher(gEmail).matches()) {
                        progressLoading.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressLoading.dismiss();
                                auth.signInWithEmailAndPassword(gEmail, gPassword)
                                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(Login.this, "Sai Email Hoặc Mật Khẩu", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }, 2000);
                    }else{

                        email.forceLayout();
                        Toast.makeText(Login.this, "Vui Lòng Nhập Đúng Đinh Dạng Email", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        signupBtn();
        backBtn();
    }

    private void signupBtn() {

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
            }
        });

    }

    private void backBtn() {

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login.super.onBackPressed();
            }
        });

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