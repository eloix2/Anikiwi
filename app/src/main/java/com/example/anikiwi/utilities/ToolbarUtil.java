package com.example.anikiwi.utilities;

import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.anikiwi.R;

public class ToolbarUtil {
    /**
     * Sets the custom toolbar for the activity
     * @param activity the activity
     * @param root the root view of the activity
     * @param title the title of the toolbar
     */
    public static void setCustomToolbar(AppCompatActivity activity, View root, String title) {
        // Find the Toolbar in the layout
        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
        toolbar.setTitle(title);
        // Set the Toolbar as the ActionBar
        activity.setSupportActionBar(toolbar);
    }

    /**
     * Adds the back button to the custom toolbar
     * @param activity the activity
     */
    public static void addBackButtonToCustomToolbar(AppCompatActivity activity) {
        // Add back button
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
