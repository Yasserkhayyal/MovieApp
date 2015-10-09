package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamedyasser.movieapp.data.MovieContract;

/**
 * Created by Mohamed Yasser on 10/8/2015.
 */
public class VideosCursorAdaptor extends CursorAdapter {

    Context mContext;
    LayoutInflater inflater;

    public VideosCursorAdaptor(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        inflater = LayoutInflater.from(context);

    }

    public static class ViewHolder{
        TextView  trailerTextView;

        public ViewHolder(View view){
            trailerTextView = (TextView) view.findViewById(R.id.trailer_text_view);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int position = cursor.getPosition();
        View view;
        if(position==0) {
            view = inflater.inflate(R.layout.trailers_first_item, parent, false);
        }else{
            view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int trailerNameColumnIndex = cursor.getColumnIndex(MovieContract
                                        .MovieVideosEntry.COLUMN_VIDEO_NAME);
        String trailerName = cursor.getString(trailerNameColumnIndex);
        viewHolder.trailerTextView.setText(trailerName);
    }
}
