package com.example.zeph1.stockprediction;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    Button btnSearchStock;
    public static TextView ptSearchStock;
    public static TextView tvDate;
    public static TextView tvOpening;
    public static TextView tvClosing;
    public static LineChartView lcv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ptSearchStock = findViewById(R.id.ptstockName);
        btnSearchStock = findViewById(R.id.btnSearchStock);
        tvDate = findViewById(R.id.tvDate);
        tvOpening = findViewById(R.id.tvOpening);
        tvClosing = findViewById(R.id.tvClosing);

        ptSearchStock.setHint("Key in Stock Symbol Here...");

        btnSearchStock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                fetchAlphaVantageData fetchStockData = new fetchAlphaVantageData();
                fetchStockData.execute();
            }
        });

        lcv = (LineChartView) findViewById(R.id.lcv);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        axisValues.add(new AxisValue(1));
        axisValues.add(new AxisValue(2));
        axisValues.add(new AxisValue(3));
        axisValues.add(new AxisValue(4));
        axisValues.add(new AxisValue(5));

        Axis axisX = new Axis(axisValues).setHasLines(true).setLineColor(Color.BLACK);
        Axis axisY = new Axis().setHasLines(true).setLineColor(Color.BLACK);

        axisX.setName("Time").setTextColor(Color.BLACK);
        axisY.setName("Price").setTextColor(Color.BLACK);


        List<PointValue> pv = new ArrayList<PointValue>();
        pv.add(new PointValue(0,0));
        pv.add(new PointValue(1,1));
        pv.add(new PointValue(2,3));
        pv.add(new PointValue(3,5));
        pv.add(new PointValue(4,8));
        pv.add(new PointValue(5,15));

        Line line = new Line(pv).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        lcv.setLineChartData(data);

    }
}
