package com.eloix.anikiwi.utilities;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class DateConverter {

    public static String convertDateMongoToJava(String dateString) {
        if (dateString == null) {
            return null;
        }

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = inputFormat.parse(dateString);

            if (date != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                return outputFormat.format(date);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle the error gracefully
        }
    }

    public static String convertDateJavaToMongo(String dateString) {
        if (dateString == null) {
            return null;
        }

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inputFormat.parse(dateString);

            if (date != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                return outputFormat.format(date);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle the error gracefully
        }
    }


}
