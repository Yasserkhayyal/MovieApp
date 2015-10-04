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
 * Created by Mohamed Yasser on 10/4/2015.
 */
public class TrailersAdapter extends BaseAdapter{
    Context context;
    ArrayList<String> trailersTitles;
    LayoutInflater layoutInflater;


    public TrailersAdapter(Context context, ArrayList<String> trailersTitles){
        this.context = context;
        this.trailersTitles = trailersTitles;
        layoutInflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return trailersTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder{
        ImageView playBackImage;
        TextView TrailerNameTV;

        public ViewHolder(View view){
            playBackImage = (ImageView) view.findViewById(R.id.play_back_image);
            TrailerNameTV = (TextView) view.findViewById(R.id.trailer_text_view);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){
            view = layoutInflater.inflate(R.layout.trailer_list_item,parent,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.playBackImage.setImageDrawable(null);
        viewHolder.playBackImage.setImageResource(R.drawable.playback_play);
        viewHolder.TrailerNameTV.setText(trailersTitles.get(position));
        return view;

    }
}
