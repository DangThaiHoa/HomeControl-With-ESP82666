package com.example.homecontrol;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AppInformation extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);

        backBtn = findViewById(R.id.back_btn);

        backBtn();
    }

    private void backBtn() {

        backBtn.setOnClickListener(view -> AppInformation.super.onBackPressed());

    }
}