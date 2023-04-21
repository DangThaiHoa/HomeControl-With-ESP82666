package com.example.homecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {

    ImageView backbtn;
    Button confirmBtn;
    TextInputEditText Email;
    ProgressLoading progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_forget_password);

        progressLoading = new ProgressLoading(this);

        backbtn = findViewById(R.id.forgetPassword_button_back);
        Email = findViewById(R.id.email_forgetPassword);
        confirmBtn = findViewById(R.id.confirm_btn);

        confirmBtn.setOnClickListener(view -> {
            String gEmail = Objects.requireNonNull(Email.getText()).toString();
            if (gEmail.isEmpty()) {

                Toast.makeText(ForgetPassword.this, "Vui Lòng Nhập Emai", Toast.LENGTH_SHORT).show();

            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(gEmail).matches()) {

                    sendVerifyEmail(gEmail);
                } else {

                    Toast.makeText(ForgetPassword.this, "Vui Lòng Nhập Đúng Định Dạng Email", Toast.LENGTH_SHORT).show();

                }
            }
        });

        backbtn();
    }

    private void sendVerifyEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressLoading.show();
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(ForgetPassword.this, CompleteSendEmail.class);
                            startActivity(intent);
                            progressLoading.dismiss();
                        }, 2000);
                    }
                });
    }

    private void backbtn() {

        backbtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });

    }

}