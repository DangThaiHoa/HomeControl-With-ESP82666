package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homecontrol.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    String path = "HomeControl/ESP8266/Users/";
    FirebaseDatabase database;
    DatabaseReference refUidESP, refShare1, refShare2, refUid1, refUid2;
    String uid, email;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new UserFragment());

        database = FirebaseDatabase.getInstance();

        refUidESP = database.getReference( path + "UID-01/uid");
        refUid1 = database.getReference( path + "UID-02/uid");
        refUid2 = database.getReference( path + "UID-03/uid");
        refShare1 = database.getReference( path + "UID-02/email");
        refShare2 = database.getReference( path + "UID-03/email");
        FirebaseUser userUID = FirebaseAuth.getInstance().getCurrentUser();
        uid = userUID.getUid();
        email = userUID.getEmail();

        if (CheckInternet.isNetworkAvailable(this)) {

            refUidESP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String uidESP = dataSnapshot.getValue(String.class);
                    if(uidESP.equals(uid)){
                        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                switch (item.getItemId()) {

                                    case R.id.weather:
                                        replaceFragment(new WeatherInformationFragment());
                                        break;
                                    case R.id.control:
                                        replaceFragment(new ControlFragment());
                                        break;
                                    case R.id.led:
                                        replaceFragment(new ColorPickerLed());
                                        break;
                                    case R.id.user:
                                        replaceFragment(new UserFragment());
                                        break;

                                }
                                return true;
                            }
                        });

                    }else{
                        refShare1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                String gEmail1 = dataSnapshot1.getValue(String.class);
                                if(gEmail1.equals(email)){
                                    refUid1.setValue(uid);
                                    binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                        @Override
                                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                            switch (item.getItemId()) {

                                                case R.id.weather:
                                                    replaceFragment(new WeatherInformationFragment());
                                                    break;
                                                case R.id.control:
                                                    replaceFragment(new ControlFragment());
                                                    break;
                                                case R.id.led:
                                                    replaceFragment(new ColorPickerLed());
                                                    break;
                                                case R.id.user:
                                                    replaceFragment(new UserFragment());
                                                    break;

                                            }
                                            return true;
                                        }
                                    });
                                }else{
                                    refShare2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            String gEmail2 = dataSnapshot2.getValue(String.class);
                                            if(gEmail2.equals(email)){
                                                refUid2.setValue(uid);
                                                binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                                    @Override
                                                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                                        switch (item.getItemId()) {

                                                            case R.id.weather:
                                                                replaceFragment(new WeatherInformationFragment());
                                                                break;
                                                            case R.id.control:
                                                                replaceFragment(new ControlFragment());
                                                                break;
                                                            case R.id.led:
                                                                replaceFragment(new ColorPickerLed());
                                                                break;
                                                            case R.id.user:
                                                                replaceFragment(new UserFragment());
                                                                break;

                                                        }
                                                        return true;
                                                    }
                                                });
                                            }else{

                                                binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                                    @Override
                                                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                                        switch (item.getItemId()) {

                                                            case R.id.weather:
                                                            case R.id.control:
                                                            case R.id.led:
                                                                replaceFragment(new UserFragment());
                                                                Toast.makeText(MainActivity.this, "Vui lòng thiết lập mạch để sử dụng ứng dụng", Toast.LENGTH_SHORT).show();
                                                                break;
                                                            case R.id.user:
                                                                replaceFragment(new UserFragment());
                                                                break;

                                                        }
                                                        return true;
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            ShowDiaLog();
        }

    }

    private void ShowDiaLog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        View view = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog, findViewById(R.id.no_internet_layout));
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.retryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckInternet.isNetworkAvailable(MainActivity.this)){
                    alertDialog.dismiss();
                }else{
                    ShowDiaLog();
                }
            }
        });
        alertDialog.show();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}