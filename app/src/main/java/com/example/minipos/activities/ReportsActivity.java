package com.example.minipos.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minipos.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        chart = findViewById(R.id.chart1);

        int[] numArr = {1, 2, 3, 4, 5, 6, 7};
        int[] xValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        List<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i < numArr.length; i++) {
            entries.add(new BarEntry(xValues[i], numArr[i]));
        }

//        for (int num : numArr) {
//            entries.add(new BarEntry(num, num));
//        }
        BarDataSet dataSet = new BarDataSet(entries, "Totals");
        BarData data = new BarData(dataSet);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        chart.setData(data);
        chart.invalidate();


        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.animateXY(3000, 3000);
        chart.setHorizontalScrollBarEnabled(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setHighlightFullBarEnabled(true);
        chart.getDescription().setText("");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

    }


    public void openSummaryActivity(View view) {
        Intent intent = new Intent(this, SalesSummaryActivity.class);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition

            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="robot"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, findViewById(R.id.layoutTrans), "summary");
            // start the new activity
            startActivity(intent, options.toBundle());
        } else {
            // Swap without transition
            startActivity(intent);
        }
    }

    public void openDailySummary(View view) {
        Intent intent = new Intent(this, DailySaleReportActivity.class);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition

            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="robot"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, findViewById(R.id.layoutDaily), "dailySummary");
            // start the new activity
            startActivity(intent, options.toBundle());
        } else {
            // Swap without transition
            startActivity(intent);
        }
    }

    public class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            String month = "invalid";
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();

            /* String[] arr=String.valueOf(value).split("\\.");
            int[] intArr=new int[2];
            intArr[0]=Integer.parseInt(arr[0]); // 1
            intArr[1]=Integer.parseInt(arr[1]); // 9

             */
            if (value >= 0 && value <= 11) {
                month = months[(int) value];
                return month;
            } else {
                return "" + value;
            }

        }
    }
}