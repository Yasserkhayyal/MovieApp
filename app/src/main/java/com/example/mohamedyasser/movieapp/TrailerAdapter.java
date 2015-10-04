package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mohamed Yasser on 10/3/2015.
 */
public class TrailerAdapter extends BaseAdapter {
    ArrayList<String> mData;
    Context mContext;

    public TrailerAdapter(Context context,ArrayList<String> data){
        mContext = context;
        mData = data;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item,parent,false);
        }
        ImageView playButton = (ImageView) convertView.findViewById(R.id.button_image);
        playButton.setImageResource(R.drawable.playback_play);
        TextView trailerName = (TextView) convertView.findViewById(R.id.trailer_text_view);
        trailerName.setText(mData.get(position));

        return convertView;

    }
}
