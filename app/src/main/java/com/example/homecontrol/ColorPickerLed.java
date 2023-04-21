package com.example.homecontrol;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ColorPickerLed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerLed extends Fragment implements SeekBar.OnSeekBarChangeListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ColorPickerLed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ColorPickerLed.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorPickerLed newInstance(String param1, String param2) {
        ColorPickerLed fragment = new ColorPickerLed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    String path = "HomeControl/ESP8266/DATA/";
    View preColor;
    TextView code;
    TextInputEditText Ired,Igreen,Iblue;
    SeekBar sRed, sGreen, sBlue;
    AppCompatButton setColor, onOffButton;
    FirebaseDatabase database;
    DatabaseReference refRed, refGreen, refBlue, refBtn, refRole1, refRole2, refEmail01, refEmail02;
    int red = 0, green = 0, blue = 0, status = 0;

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
        View view = inflater.inflate(R.layout.fragment_color_picker_led, container, false);

        progressLoading = new ProgressLoading(requireActivity());

        database = FirebaseDatabase.getInstance();
        refRed = database.getReference( path + "RGB/red");
        refGreen = database.getReference(path + "RGB/green");
        refBlue = database.getReference(path + "RGB/blue");
        refBtn = database.getReference(path + "RGB/status");

        refRole1 = database.getReference("HomeControl/ESP8266/Users/UID-02/role");
        refRole2 = database.getReference("HomeControl/ESP8266/Users/UID-03/role");
        refEmail01 = database.getReference("HomeControl/ESP8266/Users/UID-02/email");
        refEmail02 = database.getReference("HomeControl/ESP8266/Users/UID-03/email");

        preColor = view.findViewById(R.id.pre_color);
        code = view.findViewById(R.id.rgbCode);
        Ired = view.findViewById(R.id.red);
        Igreen = view.findViewById(R.id.green);
        Iblue = view.findViewById(R.id.blue);
        sRed = view.findViewById(R.id.seekbarRed);
        sGreen = view.findViewById(R.id.seekbarGreen);
        sBlue = view.findViewById(R.id.seekbarBlue);
        setColor = view.findViewById(R.id.setColor_button);
        onOffButton = view.findViewById(R.id.onOffButton);

        Ired.setFilters( new InputFilter[] { new MinMaxValueFilter("0", "255")});
        Igreen.setFilters( new InputFilter[] { new MinMaxValueFilter("0", "255")});
        Iblue.setFilters( new InputFilter[] { new MinMaxValueFilter("0", "255")});

        setColor.setOnClickListener(view12 -> {
            progressLoading.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (!Objects.requireNonNull(Ired.getText()).toString().isEmpty() && !Objects.requireNonNull(Igreen.getText()).toString().isEmpty() && !Objects.requireNonNull(Iblue.getText()).toString().isEmpty()) {
                    red = Integer.parseInt(Ired.getText().toString());
                    green = Integer.parseInt(Igreen.getText().toString());
                    blue = Integer.parseInt(Iblue.getText().toString());
                }
                refRed.setValue(red);
                refGreen.setValue(green);
                refBlue.setValue(blue);

                preColor.setBackgroundColor(Color.rgb(red,green,blue));
                code.setText(String.format(Locale.US,"(%d,%d,%d)",red,green,blue));

                sRed.setProgress(red);
                sGreen.setProgress(green);
                sBlue.setProgress(blue);
                progressLoading.dismiss();
            },1000);
        });

        refBtn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer getTrig = snapshot.getValue(Integer.class);
                assert getTrig != null;
                if (getTrig == 1) {
                    onOffButton.setBackgroundColor(Color.parseColor("red"));
                    onOffButton.setText("Tắt");
                    status = 0;
                } else {
                    onOffButton.setBackgroundColor(Color.parseColor("green"));
                    onOffButton.setText("Mở");
                    status = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        onOffButton.setOnClickListener(view1 -> {
            Handler handler = new Handler();
            progressLoading.show();
            handler.postDelayed(() -> {
                refBtn.setValue(status);
                progressLoading.dismiss();
            },1000);

        });

        sRed.setOnSeekBarChangeListener(this);
        sGreen.setOnSeekBarChangeListener(this);
        sBlue.setOnSeekBarChangeListener(this);

        SetColor();

        Role();

        return view;
    }

    private void Role() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        refEmail01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                assert email != null;
                if(email.equals(gEmail)){
                    refRole1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String gRole = snapshot.getValue(String.class);
                            assert gRole != null;
                            if(gRole.equals("Read")){
                                Ired.setClickable(false);
                                Igreen.setClickable(false);
                                Iblue.setClickable(false);
                                setColor.setClickable(false);
                                onOffButton.setClickable(false);
                                sRed.setClickable(false);
                                sGreen.setClickable(false);
                                sBlue.setClickable(false);
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
                assert email != null;
                if(email.equals(gEmail)){
                    refRole2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String gRole = snapshot.getValue(String.class);
                            assert gRole != null;
                            if(gRole.equals("Read")){
                                Ired.setClickable(false);
                                Igreen.setClickable(false);
                                Iblue.setClickable(false);
                                setColor.setClickable(false);
                                onOffButton.setClickable(false);
                                sRed.setClickable(false);
                                sGreen.setClickable(false);
                                sBlue.setClickable(false);
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

    private void SetColor() {
        refRed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gRed = snapshot.getValue(Integer.class);
                assert gRed != null;
                sRed.setProgress(gRed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refGreen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gGreen = snapshot.getValue(Integer.class);
                assert gGreen != null;
                sGreen.setProgress(gGreen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refBlue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gBlue = snapshot.getValue(Integer.class);
                assert gBlue != null;
                sBlue.setProgress(gBlue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        Ired.setText("");
        Igreen.setText("");
        Iblue.setText("");

        int id = seekBar.getId();
        if(id == R.id.seekbarRed){
            red = i;
        }
        if(id == R.id.seekbarGreen){
            green = i;
        }
        if(id == R.id.seekbarBlue){
            blue = i;
        }
        preColor.setBackgroundColor(Color.rgb(red,green,blue));
        code.setText(String.format(Locale.US,"(%d,%d,%d)",red,green,blue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}