package com.example.mohamedyasser.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Mohamed Yasser on 9/29/2015.
 */
public class DetailFragment extends Fragment implements View.OnClickListener,ListView.OnTouchListener {
    private static final String LOG_TAG = "DetailFragment";

    private String baseURL;
    private String movieTitle;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private int id;
    private int duration;
    private ListView trailersLv;
    private ListView reviewsLv;
    private GetRunTimeTask getRunTime;
    private GetTrailers getTrailers;
    private GetReviewsTask getReviews;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args!=null) {
            String[] argsArray = args.getStringArray(MainActivity.MOVIE_ARRAY);
            baseURL = argsArray[0];
            movieTitle = argsArray[1];
            overview = argsArray[2];
            releaseDate = argsArray[3];
            voteAverage = args.getDouble(MainActivity.MOVIE_VOTE_AVERAGE);
            id = args.getInt(MainActivity.MOVIE_ID);
        }
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        trailersLv = (ListView) rootView.findViewById(R.id.trailers_list_view);
        trailersLv.setOnTouchListener(this);
//        setListViewHeightBasedOnChildren(trailersLv);

        reviewsLv = (ListView) rootView.findViewById(R.id.reviews_list_view);
        reviewsLv.setOnTouchListener(this);
//        setListViewHeightBasedOnChildren(reviewsLv);


        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView releaseDateTv = (TextView) rootView.findViewById(R.id.release_date);
        TextView voteAverageTv = (TextView) rootView.findViewById(R.id.vote_average);
        TextView overviewTv = (TextView) rootView.findViewById(R.id.overview);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_image);
        Button button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(this);
        if (baseURL != null && !baseURL.isEmpty()) {
            if (baseURL.contains("null")) {
                imageView.setImageResource(R.drawable.poster_not_available);
            } else {
                Picasso.with(getActivity()).load(baseURL).into(imageView);
            }
        }
        if (movieTitle != null && !movieTitle.isEmpty()) {
            title.setText(movieTitle);
        }
        if (releaseDate != null && !releaseDate.isEmpty()) {
            releaseDate = releaseDate.substring(0, 4);
            releaseDateTv.setText(releaseDate);
        }

        voteAverageTv.setText(Double.toString(voteAverage));
        voteAverageTv.append("/10");

        if (overview != null && !overview.isEmpty()) {
            overviewTv.setText(overview);
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getRunTime = new GetRunTimeTask();
        getTrailers = new GetTrailers();
        getReviews = new GetReviewsTask();
        getRunTime.execute(id);
        getTrailers.execute(id);
        getReviews.execute(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Toast.makeText(getActivity(), "button clicked!!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
    }
    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private class GetRunTimeTask extends AsyncTask<Integer, Void, String> {
        private final String API_KEY = "32f7436a70e7635bfbb6ad24f099334b";

        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0];
            String resultString = null;
            Uri uri = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
            try {
                URL url = null;
                url = new URL(uri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    resultString = null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    resultString = null;
                }
                resultString = buffer.toString();

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }


            return resultString;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                duration = jsonObject.getInt("runtime");
                TextView durationTV = (TextView)getView().findViewById(R.id.duration_text_view);
                durationTV.setText(Integer.toString(duration));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetTrailers extends AsyncTask<Integer, Void, String> {
        private final String API_KEY = "32f7436a70e7635bfbb6ad24f099334b";
        private final String API_KEY_PARAM = "api_key";
        String resultString;

        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0];
            Uri uri = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon().appendPath(Integer.toString(id))
                    .appendPath("videos")
                    .appendQueryParameter(API_KEY_PARAM, API_KEY).build();
            try {
                URL url = new URL(uri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                resultString = buffer.toString();


            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }


            return resultString;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final String JSON_ARRAY_NAME = "results";
            final String TYPE = "type";
            final String NAME = "name";
            final String KEY = "key";
            if (s != null && !s.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
                    ArrayList<String> trailersList = new ArrayList<>();
                    ArrayList<String> teaserKeysList = new ArrayList<>();
                    for (int index = 0; index < jsonArray.length(); index++) {

                        JSONObject jsonArrayElement = jsonArray.getJSONObject(index);
                        String videoTypeName = jsonArrayElement.getString(NAME);
                        String key = jsonArrayElement.getString(KEY);
                        trailersList.add(videoTypeName);
                        teaserKeysList.add(key);


                        trailersAdapter = new TrailersAdapter(getActivity(), trailersList);

                        trailersLv.setAdapter(trailersAdapter);
//                        setListViewHeightBasedOnChildren(trailersLv);



                    }


                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }

            }
        }
    }

    private class GetReviewsTask extends AsyncTask<Integer, Void, String> {
        final String JSON_ARRAY_NAME = "results";
        private final String AUHTOR = "author";
        private final String CONTENT = "content";
        ArrayList<String> authors = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        private final String API_KEY = "32f7436a70e7635bfbb6ad24f099334b";

        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0];
            String resultString = null;
            Uri uri = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
            try {
                URL url = null;
                url = new URL(uri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    resultString = null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    resultString = null;
                }
                resultString = buffer.toString();

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }


            return resultString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject arrayElement = jsonArray.getJSONObject(index);
                    String authorName = arrayElement.getString(AUHTOR);
                    String content = arrayElement.getString(CONTENT);
                    if (authorName != null && content != null) {
                        authors.add(authorName);
                        contents.add(content);
                    }
                }

                reviewsAdapter = new ReviewsAdapter(getActivity(), authors, contents);
                reviewsLv.setAdapter(reviewsAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
