package com.example.homecontrol;

import android.graphics.Color;
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

    Button OpenCloseRoof, led1, led2, led3;
    TextView roof,textLed1,textLed2,textLed3, textModeServo;
    FirebaseDatabase database;
    DatabaseReference refAngleServo, refTrigServo, refModeServo, refWaterSensor, refDate, refTime, refErrorDS1302, refLed1, refLed2, refLed3;
    CardView cardServo;
    int AngleCur, sLed1, sLed2, sLed3;
    ImageView weaControl, imageLed1, imageLed2, imageLed3;

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
        textModeServo = view.findViewById(R.id.textMode);

        textLed1 = view.findViewById(R.id.textLed1);
        textLed2 = view.findViewById(R.id.textLed2);
        textLed3 = view.findViewById(R.id.textLed3);

        imageLed1 = view.findViewById(R.id.imageLed1);
        imageLed2 = view.findViewById(R.id.imageLed2);
        imageLed3 = view.findViewById(R.id.imageLed3);

        led1 = view.findViewById(R.id.btnLed1);
        led2 = view.findViewById(R.id.btnLed2);
        led3 = view.findViewById(R.id.btnLed3);

        cardServo = view.findViewById(R.id.cardServo);

        database = FirebaseDatabase.getInstance();
        refAngleServo = database.getReference("ESP8266/Servo/roof");
        refTrigServo = database.getReference("ESP8266/Servo/trigger");
        refModeServo = database.getReference("ESP8266/Servo/mode");

        refWaterSensor = database.getReference("ESP8266/WATERSENSOR/waterdata");

        refDate = database.getReference("ESP8266/DS1302/date");
        refTime = database.getReference("ESP8266/DS1302/time");
        refErrorDS1302 = database.getReference("ESP8266/DS1302/error");

        refLed1 = database.getReference("ESP8266/LED/led1");
        refLed2 = database.getReference("ESP8266/LED/led2");
        refLed3 = database.getReference("ESP8266/LED/led3");


        refAngleServo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int Angle = dataSnapshot.getValue(Integer.class);
                if(Angle == 180){
                    roof.setText("Mở");
                    OpenCloseRoof.setText("Đóng");
                    OpenCloseRoof.setBackgroundColor(Color.parseColor("red"));
                    cardServo.setCardBackgroundColor(Color.parseColor("green"));
                    AngleCur = 0;
                }else{
                    roof.setText("Đóng");
                    OpenCloseRoof.setText("Mở");
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
                                if (status > 50 && textModeServo.getText().toString().equals("Tự Động")) {
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
                                            refTrigServo.setValue("Trig");
                                            refModeServo.setValue("Manual");
                                            alertDialog.dismiss();
                                            progressLoading.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressLoading.dismiss();
                                                    refTrigServo.setValue("null");
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
                                    refTrigServo.setValue("Trig");
                                    refModeServo.setValue("Manual");
                                    progressLoading.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressLoading.dismiss();
                                            refTrigServo.setValue("null");
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

        textModeServo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textModeServo.getText().equals("Tự Động")){
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

        return view;
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
                        if (getWaterData > 50 && mode.equals("Auto")) {
                            refTrigServo.setValue("Trig");
                            refAngleServo.setValue(0);
                            refTrigServo.setValue("null");
                        }else if(getWaterData < 50 && mode.equals("Auto")){
                            refTrigServo.setValue("Trig");
                            refAngleServo.setValue(180);
                            refTrigServo.setValue("null");
                        }
                        if(mode.equals("Auto")){
                            textModeServo.setText("Tự Động");
                        }else{
                            textModeServo.setText("Thủ Công");
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
                if(statusLed1 == 1){
                    textLed1.setText("Bật");
                    led1.setText("Tắt");
                    led1.setBackgroundColor(Color.parseColor("red"));
                    imageLed1.setImageResource(R.drawable.lamp_on);
                    sLed1 = 0;
                }else{
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
                if(statusLed2 == 1){
                    textLed2.setText("Bật");
                    led2.setText("Tắt");
                    led2.setBackgroundColor(Color.parseColor("red"));
                    imageLed2.setImageResource(R.drawable.lamp_on);
                    sLed2 = 0;
                }else{
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

        refLed3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int statusLed3 = dataSnapshot.getValue(Integer.class);
                if(statusLed3 == 1){
                    textLed3.setText("Bật");
                    led3.setText("Tắt");
                    led3.setBackgroundColor(Color.parseColor("red"));
                    imageLed3.setImageResource(R.drawable.lamp_on);
                    sLed3 = 0;
                }else{
                    textLed3.setText("Tắt");
                    led3.setText("Bật");
                    led3.setBackgroundColor(Color.parseColor("green"));
                    imageLed3.setImageResource(R.drawable.lamp_off);
                    sLed3 = 1;
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
                Handler handler =  new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressLoading.dismiss();
                    }
                },5000);
            }
        });

        led2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refLed2.setValue(sLed2);
                progressLoading.show();
                Handler handler =  new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressLoading.dismiss();
                    }
                },5000);
            }
        });

        led3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refLed3.setValue(sLed3);
                progressLoading.show();
                Handler handler =  new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressLoading.dismiss();
                    }
                },5000);
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
                    if(getWaterData <= 50){
                        weaControl.setImageResource(R.drawable.sun);
                    }else if (getWaterData >= 50 && getWaterData <= 350){
                        weaControl.setImageResource(R.drawable.raining);
                    }else{
                        weaControl.setImageResource(R.drawable.storm);
                    }
                }else{
                    if(getWaterData <= 50){
                        weaControl.setImageResource(R.drawable.moon);
                    }else if (getWaterData >= 50 && getWaterData <= 350){
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