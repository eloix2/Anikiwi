package com.eloix.anikiwi.utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;

import com.eloix.anikiwi.R;
import com.eloix.anikiwi.networking.Anime;

import java.util.Calendar;

public class FilterUtils {
    public static void setMaxEpisodes(Anime animeData, TextView textViewRateMaxEpisodes) {
        textViewRateMaxEpisodes.setText(animeData.getEpisodes());
    }

    public static void setEditTextFilters(EditText editText, String minValue, String maxValue) {
        editText.setFilters(new InputFilter[]{new InputFilterMinMax(minValue, maxValue)});
    }

    public static void setOnClickListeners(Context context, EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = createDatePickerDialog(context, editText, year, month, day);
            datePickerDialog.show();
        });
    }

    private static DatePickerDialog createDatePickerDialog(Context context, EditText editText, int year, int month, int day) {
        return new DatePickerDialog(context, R.style.MyDatePickerDialogStyle, (view, year1, monthOfYear, dayOfMonth) ->
                editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1), year, month, day);
    }
}
