package com.example.homecontrol;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    TextView temp,hum;
    DatabaseReference refTemp,refHum;
    FirebaseDatabase database;

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

        database = FirebaseDatabase.getInstance();
        refTemp = database.getReference("DHT11/temp");
        refHum = database.getReference("DHT11/hum");

        refTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Float getTemp = dataSnapshot.getValue(Float.class);
                if(getTemp == null){
                    temp.setText("Null");
                }else{
                    temp.setText(getTemp.toString() + "\u2103");
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
                if(getHum == null){
                    hum.setText("Null");
                }else{
                    hum.setText(getHum.toString() + "%");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        return view;
    }
}