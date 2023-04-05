package com.example.homecontrol;

import static android.net.wifi.WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectBoardConfig extends AppCompatActivity {

    TextView tu,dis;
    Button reset;
    ImageView backBtn, check, delete;
    LinearLayout esp_config;
    CardView Rcard;
    String uid;
    ProgressLoading progressLoading;
    FirebaseDatabase database;
    DatabaseReference refUidESP, refReset, refConnect, refEmail;
    String path = "HomeControl/ESP8266/Users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_board_config);

        progressLoading = new ProgressLoading(this);

        esp_config = findViewById(R.id.ESP8266_config);
        check = findViewById(R.id.check_esp);
        backBtn = findViewById(R.id.back_btn);
        reset = findViewById(R.id.btnFactoryReset);
        Rcard = findViewById(R.id.card_reset);
        tu = findViewById(R.id.tutorial);
        dis = findViewById(R.id.disconnect);
        delete = findViewById(R.id.delete_board);

        database = FirebaseDatabase.getInstance();
        refUidESP = database.getReference( path + "UID-01/uid");
        refEmail = database.getReference( path + "UID-01/email");
        refReset = database.getReference( "HomeControl/ESP8266/Reset");
        refConnect = database.getReference( "HomeControl/ESP8266/Connect");
        FirebaseUser userUID = FirebaseAuth.getInstance().getCurrentUser();
        uid = userUID.getUid();

        refUidESP.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uidESP = dataSnapshot.getValue(String.class);
                if(uid.equals(uidESP)){
                    check.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    Rcard.setVisibility(View.VISIBLE);
                    tu.setVisibility(View.INVISIBLE);
                    delete.setVisibility(View.VISIBLE);
                }else{
                    check.setVisibility(View.INVISIBLE);
                    reset.setVisibility(View.INVISIBLE);
                    Rcard.setVisibility(View.INVISIBLE);
                    tu.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        esp_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.4.1?UID=" + uid));
                startActivity(intent);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectBoardConfig.this);
                builder.setCancelable(false);
                View viewDialog = LayoutInflater.from(SelectBoardConfig.this).inflate(R.layout.alert_override_servo, findViewById(R.id.alert_servo));
                builder.setView(viewDialog);
                AlertDialog alertDialog = builder.create();
                TextView textView = viewDialog.findViewById(R.id.textDialogServo);
                textView.setText("Bạn có chắc chắn muốn đặt lại thiết bị!!?");
                viewDialog.findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        progressLoading.show();
                        refReset.setValue(1);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressLoading.dismiss();
                                refReset.setValue(0);
                                Intent intent = new Intent(SelectBoardConfig.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }, 10000);
                    }
                });

                viewDialog.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        backBtn();

        CheckBoardConnect();

        DeleteBoard();
    }

    private void DeleteBoard() {

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refUidESP.setValue("null");
                refEmail.setValue("null");
                refReset.setValue(1);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refReset.setValue(0);
                    }
                },2000);
            }
        });

    }

    private void CheckBoardConnect() {

        refConnect.setValue("null");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refConnect.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String gCon = dataSnapshot.getValue(String.class);
                        if(gCon.equals("null")){
                            dis.setVisibility(View.VISIBLE);
                            check.setImageResource(R.drawable.baseline_close_24);
                            Rcard.setVisibility(View.INVISIBLE);
                            delete.setVisibility(View.INVISIBLE);
                        }else{
                            dis.setVisibility(View.INVISIBLE);
                            check.setImageResource(R.drawable.baseline_check_24);
                            Rcard.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },5000);

    }

    private void backBtn() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectBoardConfig.super.onBackPressed();
            }
        });
    }
}