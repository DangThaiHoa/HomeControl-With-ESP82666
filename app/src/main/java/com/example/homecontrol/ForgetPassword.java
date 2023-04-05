package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gEmail = Email.getText().toString();
                if (gEmail.isEmpty()) {

                    Toast.makeText(ForgetPassword.this, "Vui Lòng Nhập Emai", Toast.LENGTH_SHORT).show();

                } else {
                    if (Patterns.EMAIL_ADDRESS.matcher(gEmail).matches()) {

                        sendVerifyEmail(gEmail);
                    } else {

                        Toast.makeText(ForgetPassword.this, "Vui Lòng Nhập Đúng Định Dạng Email", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        backbtn();
    }

    private void sendVerifyEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressLoading.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ForgetPassword.this, CompleteSendEmail.class);
                                    startActivity(intent);
                                    progressLoading.dismiss();
                                }
                            }, 2000);
                        }
                    }
                });
    }

    private void backbtn() {

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

    }

}