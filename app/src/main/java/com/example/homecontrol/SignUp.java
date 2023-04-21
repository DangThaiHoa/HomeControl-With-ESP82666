package com.example.homecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    Button signup,login, ConfirmBtnDia, CancelBtnDia;
    TextView ContentDia;
    ImageView backbtn;
    TextInputEditText email, password, rePassword, name;
    Dialog dialog;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ProgressLoading progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressLoading = new ProgressLoading(SignUp.this);

        DatabaseReference refUser = database.getReference("HomeControl/Members/");

        backbtn = findViewById(R.id.signup_back_button);
        login = findViewById(R.id.signup_login_button);
        signup = findViewById(R.id.sigun_button);
        email = findViewById(R.id.Email_signup);
        password = findViewById(R.id.Password_signup);
        rePassword = findViewById(R.id.rePassword_signup);
        name = findViewById(R.id.Name_signup);

        signup.setOnClickListener(view -> {
            String gPassword = Objects.requireNonNull(password.getText()).toString();
            String grePassword = Objects.requireNonNull(rePassword.getText()).toString();
            String gEmail = Objects.requireNonNull(email.getText()).toString();
            String gName = Objects.requireNonNull(name.getText()).toString();
            if (gPassword.isEmpty() || grePassword.isEmpty() || gEmail.isEmpty() || gName.isEmpty()) {

                Toast.makeText(SignUp.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();

            } else {

                if (Patterns.EMAIL_ADDRESS.matcher(gEmail).matches()) {

                    if (gPassword.equals(grePassword)) {

                        if (gPassword.length() >= 6) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.createUserWithEmailAndPassword(gEmail, gPassword)
                                    .addOnCompleteListener(SignUp.this, task -> {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            progressLoading.show();
                                            DatabaseReference  usersRef = refUser.child("Users");
                                            Map<String, String> userData = new HashMap<>();
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if(user != null){
                                                String uid = user.getUid();
                                                usersRef = refUser.child("Users").child(uid);
                                                userData.put("name",gName);
                                                userData.put("email",gEmail);
                                            }
                                            usersRef.setValue(userData);
                                            new Handler().postDelayed(() -> {
                                                dialog.show();
                                                progressLoading.dismiss();
                                            }, 2000);

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(SignUp.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
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
        });

        login();
        btnBack();
        ShowDiaLog();
    }

    private void login() {

        login.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });

    }

    private void btnBack() {
        backbtn.setOnClickListener(view -> SignUp.super.onBackPressed());
    }

    public void ShowDiaLog() {

        dialog = new Dialog(SignUp.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(this,R.drawable.bg_dialog));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ConfirmBtnDia = dialog.findViewById(R.id.Confirm_dialog_btn);
        CancelBtnDia = dialog.findViewById(R.id.Cancel_dialog_btn);
        ContentDia = dialog.findViewById(R.id.tv_Content_dialog);

        ContentDia.setText(R.string.success_sign_up);

        ConfirmBtnDia.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
            finishAffinity();
        });

        CancelBtnDia.setOnClickListener(view -> dialog.dismiss());

    }
}