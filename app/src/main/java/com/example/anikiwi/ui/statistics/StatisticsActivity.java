package com.example.anikiwi.ui.statistics;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.anikiwi.R;
import com.example.anikiwi.databinding.ActivityStatisticsBinding;
import com.example.anikiwi.utilities.ToolbarUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private ActivityStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolbarUtil.setCustomToolbar(this, binding.getRoot(), "Statistics");
        ToolbarUtil.addBackButtonToCustomToolbar(this);

        userId = getIntent().getStringExtra("user_id");

        statisticsViewModel = new StatisticsViewModel();
        //statisticsViewModel.init(userId);

        // Initialize and set up your BarChart
        BarChart barChart = binding.barChart;
        setupBarChart(barChart);

        // Add dummy data (replace this with your actual data)
        //List<BarEntry> entries = getDummyBarEntries();
        //BarDataSet dataSet = new BarDataSet(entries, "Label");
        //BarData barData = new BarData(dataSet);
        //barChart.setData(barData);
        //barChart.invalidate(); // Refresh the chart
    }

    private void setupBarChart(BarChart barChart) {
        // Customize the appearance of your BarChart if needed
        // For example, set labels, colors, etc.
        // See the MPAndroidChart documentation for customization options.

        // Get colors from resources. Created a custom xml to retrieve colors in values folder attrs.xml
        TypedArray ta = this.getTheme().obtainStyledAttributes(R.styleable.ViewStyle);

        int colorPrimary = ta.getColor(R.styleable.ViewStyle_colorPrimary, ContextCompat.getColor(this, R.color.md_theme_dark_primary));
        int colorOnPrimary = ta.getColor(R.styleable.ViewStyle_colorOnPrimary, ContextCompat.getColor(this, R.color.md_theme_dark_onPrimary));
        int colorPrimaryContainer = ta.getColor(R.styleable.ViewStyle_colorPrimaryContainer, ContextCompat.getColor(this, R.color.md_theme_dark_primaryContainer));
        int colorOnPrimaryContainer = ta.getColor(R.styleable.ViewStyle_colorOnPrimaryContainer, ContextCompat.getColor(this, R.color.md_theme_dark_onPrimaryContainer));
        int colorSurface = ta.getColor(R.styleable.ViewStyle_colorSurface, ContextCompat.getColor(this, R.color.md_theme_dark_surface));
        int colorOnSurface = ta.getColor(R.styleable.ViewStyle_colorOnSurface, ContextCompat.getColor(this, R.color.md_theme_dark_onSurface));
        int colorSurfaceVariant = ta.getColor(R.styleable.ViewStyle_colorSurfaceVariant, ContextCompat.getColor(this, R.color.md_theme_dark_surfaceVariant));
        int colorOnSurfaceVariant = ta.getColor(R.styleable.ViewStyle_colorOnSurfaceVariant, ContextCompat.getColor(this, R.color.md_theme_dark_onSurfaceVariant));

        // Don't forget to recycle the TypedArray when you are done
        ta.recycle();

        // Customize the appearance of your BarChart
        barChart.getDescription().setEnabled(false); // Disable description
        barChart.setDrawGridBackground(false); // Disable grid background
        barChart.setDrawBorders(false); // Disable borders

        // Customize X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // Disable X-axis grid lines
        xAxis.setTextColor(colorOnSurface); // Set X-axis label text color

        // Set a custom value formatter for the X-axis
        xAxis.setValueFormatter(new XAxisValueFormatter());

        // Customize the appearance of the X-axis labels
        xAxis.setGranularity(1f); // Set the granularity to 1 to force displaying all labels
        xAxis.setLabelCount(11); // Set the label count to display all labels

        // Adjust other X-axis properties as needed
        //xAxis.setCenterAxisLabels(true); // Center the labels with the bars
        //xAxis.setAxisMinimum(0.5f); // Adjust the minimum axis value to center the first bar
        //xAxis.setAxisMaximum(10.5f);

        // Customize Y-axis
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false); // Disable Y-axis grid lines
        yAxisLeft.setTextColor(colorOnSurface); // Set Y-axis label text color

        // Show values on the left Y-axis
        yAxisLeft.setDrawLabels(true);
        yAxisLeft.setDrawTopYLabelEntry(true);

        // Customize the right Y-axis
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false); // Disable the right Y-axis

        // Customize legend
        Legend legend = barChart.getLegend();
        legend.setEnabled(false); // Disable legend

        // Customize the appearance of the bars (BarDataSet)
        BarDataSet dataSet = new BarDataSet(getDummyBarEntries(), "Label");
        dataSet.setColors(colorPrimary); // Set color for bars
        dataSet.setValueTextColor(colorOnSurface); // Set text color for values inside bars
        dataSet.setDrawValues(true); // Enable drawing values on top of the bars


        // Customize the appearance of the BarData
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f); // Adjust the width of bars

        // Set the BarData to the BarChart
        barChart.setData(barData);

        // Set background color of the chart
        barChart.setBackgroundColor(colorSurface);

        // Set the animation duration for the chart
        barChart.animateY(1000);
        // Display values next to the bars
        barChart.setDrawValueAboveBar(true);

        // Invalidate the chart to refresh and apply the changes
        barChart.invalidate();

    }

    // Add this class for the custom X-axis value formatter
    private class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.valueOf((int) value);
        }
    }

    private List<BarEntry> getDummyBarEntries() {
        List<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(0f, 2f));
        entries.add(new BarEntry(1f, 6f));
        entries.add(new BarEntry(2f, 4f));
        entries.add(new BarEntry(3f, 1f));
        entries.add(new BarEntry(4f, 13f));
        entries.add(new BarEntry(5f, 14f));
        entries.add(new BarEntry(6f, 4f));
        entries.add(new BarEntry(7f, 3f));
        entries.add(new BarEntry(8f, 55f));
        entries.add(new BarEntry(9f, 23f));
        entries.add(new BarEntry(10f, 5f));
        // Add more entries as needed
        return entries;
    }

    //add back button functionality replicating the behaviour of real android back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // This is the ID of the back button in the ActionBar/Toolbar
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
