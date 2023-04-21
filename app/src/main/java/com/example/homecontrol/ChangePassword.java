package com.example.homecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    ImageView btnBack;
    TextInputEditText ENewPassword, EConfirmPassword, EOldPassword;
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
        EOldPassword = findViewById(R.id.Txt_old_Password_Profile);
        submitBtn = findViewById(R.id.btn_Submit_Change_Password_profile);

        submitBtn();
        btnBack();
    }

    public void CheckPassword() {

        String gNewPassword = Objects.requireNonNull(ENewPassword.getText()).toString();
        String gRePassword = Objects.requireNonNull(EConfirmPassword.getText()).toString();
        String gOldPassword = Objects.requireNonNull(EOldPassword.getText()).toString();

        if (gNewPassword.isEmpty() || gRePassword.isEmpty()) {

            Toast.makeText(this, "Vui Lòng Nhập Đầy Đủ Thông Tin", Toast.LENGTH_SHORT).show();

        } else {

            if (gNewPassword.length() >= 6) {

                if (gNewPassword.equals(gRePassword)) {

                    FirebaseUser userUID = FirebaseAuth.getInstance().getCurrentUser();
                    assert userUID != null;
                    String email = userUID.getEmail();
                    if (email != null) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, gOldPassword);
                        user.reauthenticate(credential)
                                .addOnCompleteListener(task -> new Handler().postDelayed(() -> {
                                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                    progressLoading.show();
                                    user1.updatePassword(gNewPassword)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    progressLoading.dismiss();
                                                    Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(ChangePassword.this, Login.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }
                                            });
                                }, 2000));
                    }
                } else {

                    Toast.makeText(this, "Vui Lòng Nhập Hai Mật Khẩu Giống Nhau", Toast.LENGTH_SHORT).show();

                }

            } else {

                Toast.makeText(this, "Vui lòng nhập mật khẩu dài hơn 6 ký tự", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void submitBtn() {

        submitBtn.setOnClickListener(view -> CheckPassword());

    }

    private void btnBack() {

        btnBack.setOnClickListener(view -> ChangePassword.super.onBackPressed());
    }
}