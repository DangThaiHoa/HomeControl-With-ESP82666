package com.example.homecontrol;

import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance(String param1, String param2) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    String path = "HomeControl/ESP8266/DATA/";
    Button OpenCloseRoof, led1, led2, btnMode;
    TextView roof,textLed1,textLed2;
    FirebaseDatabase database;
    DatabaseReference refAngleServo, refModeServo, refWaterSensor, refDate, refTime, refErrorDS1302, refLed1, refLed2, refRole1, refRole2, refEmail01, refEmail02;
    CardView cardServo;
    int AngleCur, sLed1, sLed2;
    ImageView weaControl, imageLed1, imageLed2, IconMode, IconRoof;
    ProgressLoading progressLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        progressLoading = new ProgressLoading(getActivity());

        OpenCloseRoof = view.findViewById(R.id.btnOpenCloseRoof);
        roof = view.findViewById(R.id.textRoof);
        weaControl = view.findViewById(R.id.weatherControl);
        btnMode = view.findViewById(R.id.btnMode);
        IconMode = view.findViewById(R.id.iconMode);
        IconRoof = view.findViewById(R.id.iconRoof);

        textLed1 = view.findViewById(R.id.textLed1);
        textLed2 = view.findViewById(R.id.textLed2);

        imageLed1 = view.findViewById(R.id.imageLed1);
        imageLed2 = view.findViewById(R.id.imageLed2);

        led1 = view.findViewById(R.id.btnLed1);
        led2 = view.findViewById(R.id.btnLed2);

        cardServo = view.findViewById(R.id.cardServo);

        database = FirebaseDatabase.getInstance();
        refAngleServo = database.getReference( path + "Servo/roof");
        refModeServo = database.getReference(path + "Servo/mode");

        refWaterSensor = database.getReference(path + "WATERSENSOR/waterdata");

        refDate = database.getReference(path + "DS1302/date");
        refTime = database.getReference(path + "DS1302/time");
        refErrorDS1302 = database.getReference(path + "DS1302/error");

        refLed1 = database.getReference(path + "LED/led1");
        refLed2 = database.getReference(path + "LED/led2");

        refRole1 = database.getReference("HomeControl/ESP8266/Users/UID-02/role");
        refRole2 = database.getReference("HomeControl/ESP8266/Users/UID-03/role");
        refEmail01 = database.getReference("HomeControl/ESP8266/Users/UID-02/email");
        refEmail02 = database.getReference("HomeControl/ESP8266/Users/UID-03/email");


        refAngleServo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int Angle = dataSnapshot.getValue(Integer.class);
                if(Angle == 180){
                    roof.setText("Mở");
                    OpenCloseRoof.setText("Đóng");
                    IconRoof.setImageResource(R.drawable.openroof);
                    OpenCloseRoof.setBackgroundColor(Color.parseColor("red"));
                    cardServo.setCardBackgroundColor(Color.parseColor("green"));
                    AngleCur = 0;
                }else{
                    roof.setText("Đóng");
                    OpenCloseRoof.setText("Mở");
                    IconRoof.setImageResource(R.drawable.closeroof);
                    OpenCloseRoof.setBackgroundColor(Color.parseColor("green"));
                    cardServo.setCardBackgroundColor(Color.parseColor("red"));
                    AngleCur = 180;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
                refWaterSensor.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int status = dataSnapshot.getValue(Integer.class);
                        OpenCloseRoof.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (status > 100 && btnMode.getText().toString().equals("Tự Động")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(false);
                                    View viewDialog = LayoutInflater.from(getActivity()).inflate(R.layout.alert_override_servo, view.findViewById(R.id.alert_servo));
                                    builder.setView(viewDialog);
                                    AlertDialog alertDialog = builder.create();
                                    TextView textView = viewDialog.findViewById(R.id.textDialogServo);
                                    textView.setText("Bạn có chắc chắn muốn mở Cửa, Đang có mưa!!?\n Nếu bạn muốn cửa tự động đóng khi có mưa, hãy chuyển chế độ bằng cách nhấn vào chữ thủ công");
                                    viewDialog.findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            refAngleServo.setValue(AngleCur);
//                                            refTrigServo.setValue("Trig");
                                            refModeServo.setValue("Manual");
                                            alertDialog.dismiss();
                                            progressLoading.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressLoading.dismiss();
                                                }
                                            }, 3000);
                                        }
                                    });

                                    viewDialog.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                } else {
                                    refAngleServo.setValue(AngleCur);
                                    refModeServo.setValue("Manual");
                                    progressLoading.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressLoading.dismiss();
                                        }
                                    }, 3000);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnMode.getText().equals("Tự Động")){
                    refModeServo.setValue("Manual");
                }else{
                    refModeServo.setValue("Auto");
                }
            }
        });

        ReadDateTime();

        ReadWeather("6");

        ControlLed();

        AutoRoof();

        Role();

        return view;
    }

    private void Role() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        refEmail01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                if(email.equals(gEmail)){
                    refRole1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String gRole = snapshot.getValue(String.class);
                            if(gRole.equals("Read")){
                                OpenCloseRoof.setClickable(false);
                                led1.setClickable(false);
                                led2.setClickable(false);
                                btnMode.setClickable(false);
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

        refEmail02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                if(email.equals(gEmail)){
                    refRole2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String gRole = snapshot.getValue(String.class);
                            if(gRole.equals("Read")){
                                OpenCloseRoof.setClickable(false);
                                led1.setClickable(false);
                                led2.setClickable(false);
                                btnMode.setClickable(false);
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

    private void AutoRoof() {

        refWaterSensor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int getWaterData = dataSnapshot.getValue(Integer.class);
                refModeServo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotMode) {
                        String mode = dataSnapshotMode.getValue(String.class);
                        if (getWaterData > 100 && mode.equals("Auto")) {
                            refAngleServo.setValue(0);
                        }else if(getWaterData < 100 && mode.equals("Auto")){
                            refAngleServo.setValue(180);
                        }
                        if(mode.equals("Auto")){
                            btnMode.setText("Tự Động");
                            btnMode.setBackgroundColor(Color.parseColor("green"));
                            IconMode.setImageResource(R.drawable.auto);
                        }else{
                            btnMode.setText("Thủ Công");
                            btnMode.setBackgroundColor(Color.parseColor("red"));
                            IconMode.setImageResource(R.drawable.manual);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ControlLed() {

        refLed1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int statusLed1 = dataSnapshot.getValue(Integer.class);
                if (statusLed1 == 1) {
                    textLed1.setText("Bật");
                    led1.setText("Tắt");
                    led1.setBackgroundColor(Color.parseColor("red"));
                    imageLed1.setImageResource(R.drawable.lamp_on);
                    sLed1 = 0;
                } else {
                    textLed1.setText("Tắt");
                    led1.setText("Bật");
                    led1.setBackgroundColor(Color.parseColor("green"));
                    imageLed1.setImageResource(R.drawable.lamp_off);
                    sLed1 = 1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refLed2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int statusLed2 = dataSnapshot.getValue(Integer.class);
                if (statusLed2 == 1) {
                    textLed2.setText("Bật");
                    led2.setText("Tắt");
                    led2.setBackgroundColor(Color.parseColor("red"));
                    imageLed2.setImageResource(R.drawable.lamp_on);
                    sLed2 = 0;
                } else {
                    textLed2.setText("Tắt");
                    led2.setText("Bật");
                    led2.setBackgroundColor(Color.parseColor("green"));
                    imageLed2.setImageResource(R.drawable.lamp_off);
                    sLed2 = 1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        led1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refLed1.setValue(sLed1);
                progressLoading.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressLoading.dismiss();
                    }
                }, 1000);
            }
        });

        led2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refLed2.setValue(sLed2);
                progressLoading.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressLoading.dismiss();
                    }
                }, 1000);
            }
        });
    }

    private void ReadDateTime(){

        refErrorDS1302.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getError = dataSnapshot.getValue(String.class);
                if(getError.equals("Read/Connect/Battery")){
                }else{
                    refTime.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String getTime = dataSnapshot.getValue(String.class);
                            ReadWeather(getTime);

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

    private void ReadWeather(String time){
        refWaterSensor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int getWaterData = dataSnapshot.getValue(Integer.class);
                String string = time;
                String[] parts = string.split(":");
                String hour = parts[0];
                if(Integer.parseInt(hour) >= 6 && Integer.parseInt(hour) < 18){
                    if(getWaterData <= 100){
                        weaControl.setImageResource(R.drawable.sun);
                    }else if (getWaterData >= 100 && getWaterData <= 500){
                        weaControl.setImageResource(R.drawable.raining);
                    }else{
                        weaControl.setImageResource(R.drawable.storm);
                    }
                }else{
                    if(getWaterData <= 100){
                        weaControl.setImageResource(R.drawable.moon);
                    }else if (getWaterData >= 100    && getWaterData <= 500){
                        weaControl.setImageResource(R.drawable.raining);
                    }else{
                        weaControl.setImageResource(R.drawable.storm);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}