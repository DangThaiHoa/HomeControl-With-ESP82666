package com.example.homecontrol;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class ProgressLoading extends Dialog {

    public ProgressLoading(@NonNull Context context){
        super(context);

        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = View.inflate(context, R.layout.custom_loading_dialog, null);
        setContentView(view);

    }
}
