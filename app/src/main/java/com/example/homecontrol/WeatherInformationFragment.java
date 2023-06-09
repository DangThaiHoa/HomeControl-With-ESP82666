package com.example.homecontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherInformationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeatherInformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherInformationFragment newInstance(String param1, String param2) {
        WeatherInformationFragment fragment = new WeatherInformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    String path = "HomeControl/ESP8266/DATA/";
    TextView temp,hum,errorDHT,weather,date, time, errorWea;
    DatabaseReference refTemp,refHum,refErrorDHT, refWaterSensor, refDate, refTime, refErrorDS1302;
    FirebaseDatabase database;
    CardView tempCard, humCard, weaCard;
    ImageView weatherImage;
    ProgressBar tempPro, humPro;
    Button btnChartTemp, btnChartHum;

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
        View view = inflater.inflate(R.layout.fragment_weather_information, container, false);

        temp = view.findViewById(R.id.Temperature);
        hum = view.findViewById(R.id.Humanity);
        errorDHT = view.findViewById(R.id.textDHTError);
        weather = view.findViewById(R.id.Weather);
        date = view.findViewById(R.id.Date);
        time = view.findViewById(R.id.Time);
        errorWea = view.findViewById(R.id.textWeatherError);
        tempPro = view.findViewById(R.id.proTem);
        humPro = view.findViewById(R.id.proHum);
        btnChartTemp = view.findViewById(R.id.chartTempButton);
        btnChartHum = view.findViewById(R.id.chartHumButton);

        tempCard = view.findViewById(R.id.cardTemp);
        humCard = view.findViewById(R.id.cardHum);
        weaCard = view.findViewById(R.id.cardWeather);

        weatherImage = view.findViewById(R.id.WeatherIcon);

        database = FirebaseDatabase.getInstance();
        refTemp = database.getReference(path + "DHT11/temp");
        refHum = database.getReference(path + "DHT11/hum");
        refErrorDHT = database.getReference(path + "DHT11/error");

        refWaterSensor = database.getReference(path + "WATERSENSOR/waterdata");

        refDate = database.getReference(path + "DS1302/date");
        refTime = database.getReference(path + "DS1302/time");
        refErrorDS1302 = database.getReference(path + "DS1302/error");

        btnChartTemp.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), LineChartTemp.class);
            startActivity(intent);
        });

        btnChartHum.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), LineChartHum.class);
            startActivity(intent);
        });

        ReadDHT11();

        ReadDateTime();

        ReadWeather("6");

        return view;
    }

    private void ReadDHT11(){
        refErrorDHT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String getError = dataSnapshot.getValue(String.class);
                assert getError != null;
                if(getError.equals("Read/Connect")){
                    errorDHT.setVisibility(View.VISIBLE);
                    temp.setText(R.string.is_null);
                    hum.setText(R.string.is_null);
                    tempPro.setProgress(0);
                    humPro.setProgress(0);
                }else{
                    errorDHT.setVisibility(View.INVISIBLE);
                    refTemp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Float getTemp = dataSnapshot.getValue(Float.class);
                            assert getTemp != null;
                            String Temperature = getTemp + "\u2103";
                            temp.setText(Temperature);
                            tempPro.setProgress(Math.round(getTemp));
                            if(getTemp < 20){
                                tempCard.setCardBackgroundColor(Color.parseColor("blue"));
                            }else if(getTemp >= 20 && getTemp <= 35){
                                tempCard.setCardBackgroundColor(Color.parseColor("green"));
                            }else if(getTemp > 35){
                                tempCard.setCardBackgroundColor(Color.parseColor("red"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    refHum.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Float getHum = dataSnapshot.getValue(Float.class);
                            assert getHum != null;
                            String Humanity = getHum + "%";
                            hum.setText(Humanity);
                            humPro.setProgress(Math.round(getHum));
                            if(getHum < 40){
                                humCard.setCardBackgroundColor(Color.parseColor("blue"));
                            }else if(getHum >= 40 && getHum <= 70){
                                humCard.setCardBackgroundColor(Color.parseColor("green"));
                            }else if(getHum > 70){
                                humCard.setCardBackgroundColor(Color.parseColor("red"));
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

    private void ReadDateTime(){

        refErrorDS1302.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getError = dataSnapshot.getValue(String.class);
                assert getError != null;
                if(getError.equals("Read/Connect/Battery")){
                    errorWea.setVisibility(View.VISIBLE);
                    date.setText(R.string.is_null);
                    time.setText(R.string.is_null);
                }else{
                    errorWea.setVisibility(View.INVISIBLE);
                    refDate.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String getDate = dataSnapshot.getValue(String.class);
                            date.setText(getDate);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    refTime.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String getTime = dataSnapshot.getValue(String.class);
                            time.setText(getTime);
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

    private void ReadWeather(String time) {
        refWaterSensor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer getWaterData = dataSnapshot.getValue(Integer.class);
                assert getWaterData != null;
                String[] parts = time.split(":");
                String hour = parts[0];
                if (Integer.parseInt(hour) >= 6 && Integer.parseInt(hour) < 18) {
                    if (getWaterData <= 100) {
                        weather.setText(R.string.sun);
                        weatherImage.setImageResource(R.drawable.sun);
                        weaCard.setCardBackgroundColor(Color.parseColor("yellow"));
                    } else if (getWaterData <= 500) {
                        weather.setText(R.string.small_rain);
                        weatherImage.setImageResource(R.drawable.raining);
                        weaCard.setCardBackgroundColor(Color.parseColor("blue"));
                    } else {
                        weather.setText(R.string.heavy_rain);
                        weatherImage.setImageResource(R.drawable.storm);
                        weaCard.setCardBackgroundColor(Color.parseColor("black"));
                    }
                } else {
                    if (getWaterData <= 100) {
                        weather.setText(R.string.night);
                        weatherImage.setImageResource(R.drawable.moon);
                        weaCard.setCardBackgroundColor(Color.parseColor("black"));
                    } else if (getWaterData <= 500) {
                        weather.setText(R.string.small_rain);
                        weatherImage.setImageResource(R.drawable.raining);
                        weaCard.setCardBackgroundColor(Color.parseColor("blue"));
                    } else {
                        weather.setText(R.string.heavy_rain);
                        weatherImage.setImageResource(R.drawable.storm);
                        weaCard.setCardBackgroundColor(Color.parseColor("black"));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}