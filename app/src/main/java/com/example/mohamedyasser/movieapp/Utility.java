package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    // check if there is a network connected
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
