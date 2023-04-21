package com.example.homecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
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

public class LineChartTemp extends AppCompatActivity {

    LineChart lineChart;
    String path = "HomeControl/ESP8266/ChartData/";
    DatabaseReference refChartTemp, refError;
    FirebaseDatabase database;
    TextView errorDHT;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_temp);

        lineChart = findViewById(R.id.chart_tem);
        errorDHT = findViewById(R.id.textDHTError);
        backBtn = findViewById(R.id.back_btn);

        database = FirebaseDatabase.getInstance();
        refChartTemp = database.getReference(path + "Temperature");
        refError = database.getReference("HomeControl/ESP8266/DATA/DHT11/error");


        retrieveData();

        backBtn();
    }

    @Override
    protected void onStart() {
        retrieveData();
        super.onStart();
    }

    private void backBtn() {

        backBtn.setOnClickListener(view -> LineChartTemp.super.onBackPressed());

    }

    private void retrieveData() {

        refError.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String getError = snapshot.getValue(String.class);
                assert getError != null;
                if(getError.equals("Read/Connect")){
                    errorDHT.setVisibility(View.VISIBLE);
                }else{
                    errorDHT.setVisibility(View.INVISIBLE);
                    refChartTemp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                ArrayList<Entry> tempArrayList = new ArrayList<>();
                                for(DataSnapshot mySP : snapshot.getChildren()){
                                    Integer pos = mySP.child("pos").getValue(Integer.class);
                                    Integer data = mySP.child("data").getValue(Integer.class);
                                    assert pos != null;
                                    assert data != null;
                                    int x = pos;
                                    float y = data;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showChart(ArrayList<Entry> tempArrayList) {

        LineDataSet lineDataSetTemp = new LineDataSet(tempArrayList,"Nhiệt Dộ");
        lineDataSetTemp.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSetTemp.setValueTextColor(Color.BLACK);
        lineDataSetTemp.setValueTextSize(10f);

        LineData lineData = new LineData(lineDataSetTemp);
        lineChart.setData(lineData);

        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.parseColor("#FF9800"));
        lineChart.getDescription().setText("Biểu Đồ Nhiệt Độ");
        lineChart.getDescription().setTextSize(10);
        lineChart.getDescription().setTextColor(Color.parseColor("#FF9800"));

    }
}