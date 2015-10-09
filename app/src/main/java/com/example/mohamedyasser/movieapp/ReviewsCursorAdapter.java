package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohamedyasser.movieapp.data.MovieContract;

/**
 * Created by Mohamed Yasser on 10/8/2015.
 */
public class ReviewsCursorAdapter extends CursorAdapter {

    Context mContext;
    LayoutInflater inflater;

    public ReviewsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
    }

    public static class ViewHolder{
        TextView authorTV;
        TextView contentTV;
        public ViewHolder(View view){
            authorTV = (TextView) view.findViewById(R.id.author_name_text_view);
            contentTV = (TextView) view.findViewById(R.id.content_text_view);

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int position = cursor.getPosition();
        View view;
        if(position==0){
            view = inflater.inflate(R.layout.reviews_first_item,parent,false);
        }else{
            view = inflater.inflate(R.layout.reviews_list_item,parent,false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int authorColumnIndex = cursor
                                .getColumnIndex(MovieContract.MovieReviewsEntry.COLUMN_AUTHOR_NAME);
        String authorName = cursor.getString(authorColumnIndex);

        int contentColumnIndex = cursor
                                .getColumnIndex(MovieContract.MovieReviewsEntry.COLUMN_CONTENT_NAME);
        String content = cursor.getString(contentColumnIndex);

        viewHolder.authorTV.setText(authorName);
        viewHolder.contentTV.setText(content);

    }
}
