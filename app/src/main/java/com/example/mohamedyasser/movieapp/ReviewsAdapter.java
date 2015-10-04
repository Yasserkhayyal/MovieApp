package com.example.mohamedyasser.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Mohamed Yasser on 10/4/2015.
 */
public class ReviewsAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> authors;
    ArrayList<String> reviews;
    LayoutInflater layoutInflater;

    public ReviewsAdapter(Context context,ArrayList<String> authors,ArrayList<String> reviews){
        this.context = context;
        this.reviews = reviews;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reviews.size();
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
        TextView authorTV;
        TextView contentTV;

        public ViewHolder(View view){
            authorTV = (TextView) view.findViewById(R.id.author_name_text_view);
            contentTV = (TextView) view.findViewById(R.id.content_text_view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){
            view = layoutInflater.inflate(R.layout.reviews_list_item,parent,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.authorTV.setText(authors.get(position));
        viewHolder.contentTV.setText(reviews.get(position));
        return view;
    }
}
