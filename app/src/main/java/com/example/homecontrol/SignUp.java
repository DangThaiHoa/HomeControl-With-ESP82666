package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    Button signup,login, ConfirmBtnDia, CancelBtnDia;
    TextView ContentDia;
    ImageView backbtn;
    TextInputEditText email, password, rePassword;
    Dialog dialog;
    ProgressLoading progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressLoading = new ProgressLoading(SignUp.this);

        backbtn = findViewById(R.id.signup_back_button);
        login = findViewById(R.id.signup_login_button);
        signup = findViewById(R.id.sigun_button);
        email = findViewById(R.id.Email_signup);
        password = findViewById(R.id.Password_signup);
        rePassword = findViewById(R.id.rePassword_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gPassword = password.getText().toString();
                String grePassword = rePassword.getText().toString();
                String gEmail = email.getText().toString();
                if (gPassword.isEmpty() || grePassword.isEmpty() || gEmail.isEmpty()) {

                    Toast.makeText(SignUp.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();

                } else {

                    if (Patterns.EMAIL_ADDRESS.matcher(gEmail).matches()) {

                        if (gPassword.equals(grePassword)) {

                            if (gPassword.length() >= 6) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.createUserWithEmailAndPassword(gEmail, gPassword)
                                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    progressLoading.show();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dialog.show();
                                                            progressLoading.dismiss();
                                                        }
                                                    }, 2000);

                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(SignUp.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {

                                password.forceLayout();
                                Toast.makeText(SignUp.this, "Vui lòng nhập Password nhiều hơn 6 ký tự", Toast.LENGTH_SHORT).show();

                            }

                        } else {

                            password.forceLayout();
                            Toast.makeText(SignUp.this, "Vui lòng nhập hai mật khẩu giống nhau", Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        email.forceLayout();
                        Toast.makeText(SignUp.this, "Vui Lòng Nhập Đúng Đinh Dạng Email", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        login();
        btnBack();
        ShowDiaLog();
    }

    private void login() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

    }

    private void btnBack() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp.super.onBackPressed();
            }
        });
    }

    public void ShowDiaLog() {

        dialog = new Dialog(SignUp.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_dialog));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ConfirmBtnDia = dialog.findViewById(R.id.Confirm_dialog_btn);
        CancelBtnDia = dialog.findViewById(R.id.Cancel_dialog_btn);
        ContentDia = dialog.findViewById(R.id.tv_Content_dialog);

        ContentDia.setText("Đăng Ký Thành Công");

        ConfirmBtnDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        CancelBtnDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
}