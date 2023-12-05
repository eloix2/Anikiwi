package com.example.anikiwi.utilities;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.core.content.ContextCompat;

import com.example.anikiwi.R;
import com.example.anikiwi.networking.AnimeScoreCount;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class BarChartCustomizer {

    private final Context context;

    public BarChartCustomizer(Context context) {
        this.context = context;
    }

    public void customizeBarChart(BarChart barChart, List<BarEntry> barEntries) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.ViewStyle);

        int colorPrimary = ta.getColor(R.styleable.ViewStyle_colorPrimary, ContextCompat.getColor(context, R.color.md_theme_dark_primary));
        int colorOnPrimary = ta.getColor(R.styleable.ViewStyle_colorOnPrimary, ContextCompat.getColor(context, R.color.md_theme_dark_onPrimary));
        int colorPrimaryContainer = ta.getColor(R.styleable.ViewStyle_colorPrimaryContainer, ContextCompat.getColor(context, R.color.md_theme_dark_primaryContainer));
        int colorOnPrimaryContainer = ta.getColor(R.styleable.ViewStyle_colorOnPrimaryContainer, ContextCompat.getColor(context, R.color.md_theme_dark_onPrimaryContainer));
        int colorSurface = ta.getColor(R.styleable.ViewStyle_colorSurface, ContextCompat.getColor(context, R.color.md_theme_dark_surface));
        int colorOnSurface = ta.getColor(R.styleable.ViewStyle_colorOnSurface, ContextCompat.getColor(context, R.color.md_theme_dark_onSurface));
        int colorSurfaceVariant = ta.getColor(R.styleable.ViewStyle_colorSurfaceVariant, ContextCompat.getColor(context, R.color.md_theme_dark_surfaceVariant));
        int colorOnSurfaceVariant = ta.getColor(R.styleable.ViewStyle_colorOnSurfaceVariant, ContextCompat.getColor(context, R.color.md_theme_dark_onSurfaceVariant));

        ta.recycle();

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(colorOnSurface);

        xAxis.setValueFormatter(new XAxisValueFormatter());

        xAxis.setGranularity(1f);
        xAxis.setLabelCount(11);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setTextColor(colorOnSurface);
        yAxisLeft.setDrawLabels(true);
        yAxisLeft.setDrawTopYLabelEntry(true);

        // Set a custom value formatter for the Y-axis to accept only integers
        yAxisLeft.setValueFormatter(new IntegerYAxisValueFormatter());

        // Enable granularity and set it to 1 to force displaying all labels
        yAxisLeft.setGranularity(1f);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        BarDataSet dataSet = new BarDataSet(barEntries, "Label");
        dataSet.setColors(colorPrimary);
        dataSet.setValueTextColor(colorOnSurface);
        dataSet.setValueTextSize(12f);  // Set the text size for the values
        // Set a custom value formatter for the bar values to display only integers
        dataSet.setValueFormatter(new IntegerValueFormatter());
        dataSet.setDrawValues(true);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.setBackgroundColor(colorSurface);
        barChart.animateY(1000);
        barChart.setDrawValueAboveBar(true);
        barChart.invalidate();
    }

    private class IntegerValueFormatter extends ValueFormatter {
        @Override
        public String getBarLabel(BarEntry barEntry) {
            return String.valueOf((int) barEntry.getY());
        }
    }

    private class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.valueOf((int) value);
        }
    }

    private class IntegerYAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.valueOf((int) value);
        }
    }

    public static List<BarEntry> convertAnimeScoreCountToBarEntries(List<AnimeScoreCount> animeScoreCounts) {
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < animeScoreCounts.size(); i++) {
            AnimeScoreCount animeScoreCount = animeScoreCounts.get(i);
            barEntries.add(new BarEntry(i, animeScoreCount.getCount()));
        }

        return barEntries;
    }
}
