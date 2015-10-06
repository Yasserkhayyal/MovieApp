package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mohamed Yasser on 8/23/2015.
 */
public class ImageAdaptor extends BaseAdapter {
    private final String LOG_TAG = ImageAdaptor.class.getSimpleName();
    Context mContext;
    ArrayList<String> data;
    LayoutInflater inflater;

    public ImageAdaptor(Context mContext,ArrayList<String> data){
        this.mContext = mContext;
        this.data = data;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(LOG_TAG,"inside getView method");
        if(convertView==null){
            convertView = inflater.inflate(R.layout.grid_item,null);
        }
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
        imageView.setImageDrawable(null);

        String url = data.get(position);
        if(url!=null && !url.isEmpty()) {
            if(url.contains("null")){
                Picasso.with(mContext).load(R.drawable.poster_not_available).into(imageView);

            }else {
                Picasso.with(mContext).load(url).into(imageView);
            }
        }
        return convertView;
    }
}
