package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.mohamedyasser.movieapp.data.MovieContract;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Mohamed Yasser on 10/5/2015.
 */
public class PostersCursorAdapter extends CursorAdapter {

    Context mContext;

    public PostersCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int poster_path_index = cursor.getColumnIndex(MovieContract.MovieDetailsEntry.COLUMN_POSTER_PATH);
        String poster_path = cursor.getString(poster_path_index);
        String baseURL = "http://image.tmdb.org/t/p/w185" + poster_path;
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);


            if (baseURL.contains("null")) {
                Picasso.with(context).load(R.drawable.poster_not_available)
                        .resize(185, 200)
                        .into(imageView);

            } else {

                if(isNetworkAvailable()) {
                    Picasso.with(context).load(baseURL).into(imageView);
//                    Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView
                }else{
                    Picasso.with(context)
                            .load(baseURL)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.error_loading)
                            .into(imageView);

                }
            }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
