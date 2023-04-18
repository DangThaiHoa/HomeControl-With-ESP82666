package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LineChartTempHum extends AppCompatActivity {

    LineChart lineChart;
    String path = "HomeControl/ESP8266/DATA/";
    DatabaseReference refTemp, refHum;
    FirebaseDatabase database;
    ArrayList<Entry> entryArrayList;
    int aTemp[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_temp_hum);

        lineChart = findViewById(R.id.chart_tem_hum);

        database = FirebaseDatabase.getInstance();
        refTemp = database.getReference(path + "DHT11/temp");
        refHum = database.getReference(path + "DHT11/hum");

        refTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Float getTemp = snapshot.getValue(Float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lineChar();
    }

    private void setData(int pos, int value) {
        entryArrayList = new ArrayList<>();
        entryArrayList.add(new BarEntry(pos, value));

    }

    private void lineChar() {

        LineDataSet lineDataSet = new LineDataSet(entryArrayList,"Nhiệt Độ");
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(15f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.parseColor("#FF9800"));
        lineChart.getDescription().setText("Biểu Đồ Nhiệt Độ/Độ Ẩm");
        lineChart.getDescription().setTextSize(15);
        lineChart.getDescription().setTextColor(Color.parseColor("#FF9800"));
        lineChart.animateY(2000);
    }
}