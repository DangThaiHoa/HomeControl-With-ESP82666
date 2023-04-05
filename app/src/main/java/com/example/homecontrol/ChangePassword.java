package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    ImageView btnBack;
    TextInputEditText ENewPassword, EConfirmPassword;
    Button submitBtn;
    ProgressLoading progressLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        progressLoading = new ProgressLoading(this);

        btnBack = findViewById(R.id.back_btn);
        ENewPassword = findViewById(R.id.Txt_new_Password_Profile);
        EConfirmPassword = findViewById(R.id.Txt_confirm_Password_Profile);
        submitBtn = findViewById(R.id.btn_Submit_Change_Password_profile);

        submitBtn();
        btnBack();
    }

    public void CheckPassword() {

        String gNewPassword = ENewPassword.getText().toString();
        String gRePassword = EConfirmPassword.getText().toString();

        if (gNewPassword.isEmpty() || gRePassword.isEmpty()) {

            Toast.makeText(this, "Vui Lòng Nhập Đầy Đủ Thông Tin", Toast.LENGTH_SHORT).show();

        } else {

            if (gNewPassword.length() >= 6) {

                if (gNewPassword.equals(gRePassword)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential("user@example.com", "password1234");

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String newPassword = gNewPassword;

                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressLoading.dismiss();
                                                Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(ChangePassword.this, Login.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                        }
                                    });
                        }
                    }, 2000);

                } else {

                    Toast.makeText(this, "Vui Lòng Nhập Hai Mật Khẩu Giống Nhau", Toast.LENGTH_SHORT).show();

                }

            } else {

                Toast.makeText(this, "Vui lòng nhập mật khẩu dài hơn 6 ký tự", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void submitBtn() {

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPassword();
                }
        });

    }

    private void btnBack() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword.super.onBackPressed();
            }
        });
    }
}