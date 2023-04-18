package com.example.homecontrol;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

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
    AppCompatButton setColor;
    FirebaseDatabase database;
    DatabaseReference refRed, refGreen, refBlue;

    int red = 0, green = 0, blue = 0;

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

        database = FirebaseDatabase.getInstance();
        refRed = database.getReference( path + "RGB/red");
        refGreen = database.getReference(path + "RGB/green");
        refBlue = database.getReference(path + "RGB/blue");

        preColor = view.findViewById(R.id.pre_color);
        code = view.findViewById(R.id.rgbCode);
        Ired = view.findViewById(R.id.red);
        Igreen = view.findViewById(R.id.green);
        Iblue = view.findViewById(R.id.blue);
        sRed = view.findViewById(R.id.seekbarRed);
        sGreen = view.findViewById(R.id.seekbarGreen);
        sBlue = view.findViewById(R.id.seekbarBlue);
        setColor = view.findViewById(R.id.setColor_button);

        Ired.setFilters( new InputFilter[] { new MinMaxValueFilter("0", "255")});
        Igreen.setFilters( new InputFilter[] { new MinMaxValueFilter("0", "255")});
        Iblue.setFilters( new InputFilter[] { new MinMaxValueFilter("0", "255")});

        setColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                red = Integer.parseInt(Ired.getText().toString());
                green = Integer.parseInt(Igreen.getText().toString());
                blue = Integer.parseInt(Iblue.getText().toString());

                refRed.setValue(red);
                refGreen.setValue(green);
                refBlue.setValue(blue);

                preColor.setBackgroundColor(Color.rgb(red,green,blue));
                code.setText(String.format("(%d,%d,%d)",red,green,blue));

                sRed.setProgress(red);
                sGreen.setProgress(green);
                sBlue.setProgress(blue);

            }
        });

        sRed.setOnSeekBarChangeListener(this);
        sGreen.setOnSeekBarChangeListener(this);
        sBlue.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        switch (seekBar.getId()){

            case R.id.seekbarRed:
                red = i;
                break;
            case R.id.seekbarGreen:
                green = i;
                break;
            case R.id.seekbarBlue:
                blue = i;
                break;
        }
        refRed.setValue(red);
        refGreen.setValue(green);
        refBlue.setValue(blue);
        preColor.setBackgroundColor(Color.rgb(red,green,blue));
        code.setText(String.format("(%d,%d,%d)",red,green,blue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}