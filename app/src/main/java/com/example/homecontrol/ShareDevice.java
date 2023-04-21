package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ShareDevice extends AppCompatActivity {

    TextView count, email1, email2;
    TextInputEditText input_email;
    Button shareBtn;
    ImageView backBtn, deleteEmail1, deleteEmail2;
    String path = "HomeControl/ESP8266/Users/";
    FirebaseDatabase database;
    DatabaseReference refUid02, refUid03, refEmail02, refEmail03, refRole02, refRole03;
    int cou1 = 0, cou2 = 0, delete1 = 0, delete2 = 0;
    String[] roleItem = {"Đọc", "Đọc/Ghi"};
    AutoCompleteTextView role;
    ArrayAdapter<String> adapterItemRole;
    String gRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_device);

        count = findViewById(R.id.count_share);
        email1 = findViewById(R.id.share1_email);
        email2 = findViewById(R.id.share2_email);
        input_email = findViewById(R.id.Email_share);
        shareBtn = findViewById(R.id.share_button);
        backBtn = findViewById(R.id.back_btn);
        deleteEmail1 = findViewById(R.id.delete_email1);
        deleteEmail2 = findViewById(R.id.delete_email2);
        role = findViewById(R.id.Role_share);
        adapterItemRole = new ArrayAdapter<>(this, R.layout.list_item_dropmenu, roleItem);
        role.setAdapter(adapterItemRole);

        database = FirebaseDatabase.getInstance();
        refEmail02 = database.getReference(path + "UID-02/email");
        refEmail03 = database.getReference(path + "UID-03/email");
        refUid02 = database.getReference(path + "UID-02/uid");
        refUid03 = database.getReference(path + "UID-03/uid");
        refRole02 = database.getReference(path + "UID-02/role");
        refRole03 = database.getReference(path + "UID-03/role");

        refEmail02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                assert gEmail != null;
                if (gEmail.equals("null")) {
                    email1.setText(R.string.default_label_share_email_1);
                    cou1 = 0;
                } else {
                    email1.setText(gEmail);
                    cou1 = 1;
                }
                Count();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refEmail03.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                assert gEmail != null;
                if (gEmail.equals("null")) {
                    email2.setText(R.string.default_label_share_email_1);
                    cou2 = 0;
                } else {
                    email2.setText(gEmail);
                    cou2 = 1;
                }
                Count();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        shareBtn.setOnClickListener(view -> {
            gRole = role.getText().toString();
            delete1 = 0;
            delete2 = 0;
            if (cou1 == 0) {
                refEmail02.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String gEmail = dataSnapshot.getValue(String.class);
                        assert gEmail != null;
                        if (gRole.isEmpty() || Objects.requireNonNull(input_email.getText()).toString().trim().isEmpty()) {
                            Toast.makeText(ShareDevice.this, "Vui lòng chọn quyền hoặc Email", Toast.LENGTH_SHORT).show();
                        } else if (gEmail.equals("null") && delete1 == 0) {
                            refEmail02.setValue(input_email.getText().toString().trim());
                            if (gRole.equals("Đọc")) {
                                refRole02.setValue("Read");
                            } else {
                                refRole02.setValue("Read/Write");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                refEmail03.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String gEmail = dataSnapshot.getValue(String.class);
                        assert gEmail != null;
                        if (gRole.isEmpty()) {
                            Toast.makeText(ShareDevice.this, "Vui lòng chọn quyền", Toast.LENGTH_SHORT).show();
                        } else if (gEmail.equals("null") && delete2 == 0) {
                            refEmail03.setValue(Objects.requireNonNull(input_email.getText()).toString().trim());
                            if (gRole.equals("Đọc")) {
                                refRole02.setValue("Read");
                            } else {
                                refRole02.setValue("Read/Write");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        deleteEmail1.setOnClickListener(view -> {
            refEmail02.setValue("null");
            refUid02.setValue("null");
            refRole02.setValue("null");
            cou1 = 0;
            delete1 = 1;
            input_email.setText("");
        });

        deleteEmail2.setOnClickListener(view -> {
            refEmail03.setValue("null");
            refUid03.setValue("null");
            refRole03.setValue("null");
            cou2 = 0;
            delete2 = 1;
            input_email.setText("");
        });

        backBtn();
    }

    private void Count() {
        if(cou1 + cou2 == 0){
            count.setText(R.string.share_email_0);
        }else if(cou1 + cou2 == 1){
            count.setText(R.string.share_email_1);
        }else{
            count.setText(R.string.share_email_2);
        }
    }

    private void backBtn() {
        backBtn.setOnClickListener(view -> ShareDevice.super.onBackPressed());
    }
}