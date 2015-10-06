package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Mohamed Yasser on 10/5/2015.
 */
public class Utility {

    public static String getSortSelection(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selection = preferences.getString(context.getString(R.string.sort_by_title)
                                                    ,context.getString(R.string.sort_default_value));
        return selection;
    }
}
