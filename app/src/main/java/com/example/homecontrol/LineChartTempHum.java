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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;

public class LineChartTempHum extends AppCompatActivity {

    LineChart lineChart;
    String path = "HomeControl/ESP8266/ChartData/";
    DatabaseReference refChartTemp, refChartHum;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_temp_hum);

        lineChart = findViewById(R.id.chart_tem_hum);

        database = FirebaseDatabase.getInstance();
        refChartTemp = database.getReference(path + "Temperature");
        refChartHum = database.getReference(path + "Humanity");


        retrieveData();
    }

    private void retrieveData() {

        refChartTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> tempArrayList = new ArrayList<Entry>();
                if(snapshot.hasChildren()){
                    for(DataSnapshot mySP : snapshot.getChildren()){
                        int x = mySP.child("x").getValue(Integer.class);
                        float y = mySP.child("y").getValue(Float.class);
                        tempArrayList.add(new Entry(x,y));
                    }
                    showChart(tempArrayList);
                }else{
                    lineChart.clear();
                    lineChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showChart(ArrayList<Entry> tempArrayList) {

        LineDataSet lineDataSet = new LineDataSet(tempArrayList,"Nhiệt Dộ");
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(10f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.parseColor("#FF9800"));
        lineChart.getDescription().setText("Biểu Đồ Nhiệt Độ");
        lineChart.getDescription().setTextSize(15);
        lineChart.getDescription().setTextColor(Color.parseColor("#FF9800"));
        lineChart.animateY(2000);

    }
}