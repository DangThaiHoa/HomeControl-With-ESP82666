package com.example.homecontrol;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

public class ProgessLoading extends Dialog {

    public ProgessLoading(@NonNull Context context){
        super(context);

        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog,null);
        setContentView(view);

    }
}
