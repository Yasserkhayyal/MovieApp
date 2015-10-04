package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mohamed Yasser on 10/3/2015.
 */
public class DetailsFragmentAdaptor extends BaseAdapter {
    Context mContext;
    LayoutInflater layoutInflater;

    public DetailsFragmentAdaptor(Context context){
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

//    public static class MovieViewHolder{
//        TextView titleTV;
//        ImageView posterImage;
//        TextView releaseDateTV;
//        TextView voteAverageTV;
//        TextView overViewTV;
//        public MovieViewHolder(View view){
//            titleTV = (TextView) view.findViewById(R.id.title);
//            posterImage = (ImageView) view.findViewById(R.id.poster_image);
//            releaseDateTV = (TextView) view.findViewById(R.id.release_date);
//            voteAverageTV = (TextView) view.findViewById(R.id.vote_average);
//            overViewTV = (TextView) view.findViewById(R.id.overview);
//
//        }
//
//    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        switch (position){
            case 0:
                if(view==null){
                    view = layoutInflater.inflate(R.layout.movie_basic_details,parent,false);
//                    MovieViewHolder movieViewHolder = new MovieViewHolder(view);
//                    view.setTag(movieViewHolder);
                }
            break;

            case 1:
                if(view==null){
                    view = layoutInflater.inflate(R.layout.movie_videos,parent,false);
                }
                break;

            case 2:
                if(view==null){
                    view = layoutInflater.inflate(R.layout.movie_reviews,parent,false);
                }
                break;
        }
        return view;
    }
}
