package com.example.homecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CompleteSendEmail extends AppCompatActivity {

    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_complete_send_email);

        login = findViewById(R.id.back_login);

        login.setOnClickListener(view -> {
            Intent intent = new Intent(CompleteSendEmail.this, Login.class);
            startActivity(intent);
        });

    }
}