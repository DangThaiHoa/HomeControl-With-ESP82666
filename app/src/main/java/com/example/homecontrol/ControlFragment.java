package com.example.homecontrol;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
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

    Button OpenCloseRoof;
    TextView roof;
    FirebaseDatabase database;
    DatabaseReference refAngleServo, refWaterSensor, refDate, refTime, refErrorDS1302;
    CardView cardServo;
    int AngleCur;
    ImageView weaControl;

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
        OpenCloseRoof = view.findViewById(R.id.btnOpenCloseRoof);
        roof = view.findViewById(R.id.textRoof);
        weaControl = view.findViewById(R.id.weatherControl);

        cardServo = view.findViewById(R.id.cardServo);

        database = FirebaseDatabase.getInstance();
        refAngleServo = database.getReference("Servo/roof");
        refWaterSensor = database.getReference("WATERSENSOR/waterdata");

        refDate = database.getReference("DS1302/date");
        refTime = database.getReference("DS1302/time");
        refErrorDS1302 = database.getReference("DS1302/error");


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

        OpenCloseRoof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refAngleServo.setValue(AngleCur);
            }
        });

        ReadDateTime();

        ReadWeather("6");

        return view;
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