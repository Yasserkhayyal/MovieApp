package com.example.mohamedyasser.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mohamedyasser.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Mohamed Yasser on 9/29/2015.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "DetailFragment";
    public static final String DETAIL_URI = "detailUri";

    private String baseURL;
    private String movieTitle;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private int id;
    private int duration;
    private ExpandableHeightListView trailersLv;
    private ExpandableHeightListView reviewsLv;
    private GetRunTimeTask getRunTime;
    private GetTrailers getTrailers;
    private GetReviewsTask getReviews;
    private VideosCursorAdaptor trailersAdapter;
    private ReviewsCursorAdapter reviewsAdapter;
    private TextView durationTV;
    private ToggleButton button;
    private Uri detail_uri;
    private Uri firstTrailerUri;
    private ShareActionProvider mShareActionProvider;
    private boolean isFavorite = false;
    private final static String[] DETAIL_PROJECTION = new String[]
                    {MovieContract.MovieDetailsEntry._ID,
                    MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID,
                    MovieContract.MovieDetailsEntry.COLUMN_POSTER_PATH,
                    MovieContract.MovieDetailsEntry.COLUMN_OVERVIEW,
                    MovieContract.MovieDetailsEntry.COLUMN_RELEASE_DATE,
                    MovieContract.MovieDetailsEntry.COLUMN_POPULARITY,
                    MovieContract.MovieDetailsEntry.COLUMN_TITLE,
                    MovieContract.MovieDetailsEntry.COLUMN_VOTE_AVERAGE,
                    MovieContract.MovieDetailsEntry.COLUMN_VOTE_COUNT,
                    MovieContract.MovieDetailsEntry.COLUMN_FAVOURITE};

    private final static String[] DURATION_PROJECTION = new String[]{
            MovieContract.MovieDurationEntry._ID,
            MovieContract.MovieDurationEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieDurationEntry.COLUMN_MOVIE_DURATION
    };

    private final static int MOVIE_ID_INDEX = 1;
    private final static int MOVIE_POSTER_PATH_INDEX = 2;
    private final static int MOVIE_OVERVIEW_INDEX = 3;
    private final static int MOVIE_RELEASE_DATE_INDEX = 4;
    private final static int MOVIE_POPULARITY_INDEX = 5;
    private final static int MOVIE_TITLE_INDEX = 6;
    private final static int MOVIE_VOTE_AVERAGE_INDEX = 7;
    private final static int MOVIE_VOTE_COUNT_INDEX = 8;
    private final static int MOVIE_FAVOURITE_INDEX = 9;

    private final static int MOVIE_DURATION_INDEX = 2;

    // interface for toggling favorite mark of a movie
    public interface onFavouriteStateChanged{
        public void onFavouriteStateChanged();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_fragment_menu, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieVideosEntry.CONTENT_URI,
                new String[]{MovieContract.MovieVideosEntry.COLUMN_VIDEO_KEY},
                MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{Integer.toString(id)},null);

        if(cursor.moveToFirst()){
            String key = cursor.getString(0);
            firstTrailerUri = Uri.parse("https://www.youtube.com/watch")
                    .buildUpon()
                    .appendQueryParameter("v", key).build();

        }

        if(firstTrailerUri!=null) {
            mShareActionProvider.setShareIntent(createShareURLIntent());
        }
    }

    private Intent createShareURLIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,firstTrailerUri.toString());
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args!=null) {
            detail_uri = args.getParcelable(DETAIL_URI);
            id = Integer.parseInt(detail_uri.getLastPathSegment());
        }
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        //Expandable height list view to force showing all the list view not the first item
        //only
        trailersLv = (ExpandableHeightListView) rootView.findViewById(R.id.trailers_list_view);
        trailersLv.setExpanded(true);
        trailersAdapter = new VideosCursorAdaptor(getActivity(),null,0);
        trailersLv.setAdapter(trailersAdapter);
        // handles clicking on a trailer list item event
        trailersLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                int keyColumnIndex = cursor.getColumnIndex(MovieContract
                        .MovieVideosEntry.COLUMN_VIDEO_KEY);
                String key = cursor.getString(keyColumnIndex);

                Uri youtubeUri = Uri.parse("https://www.youtube.com/watch")
                                 .buildUpon()
                                 .appendQueryParameter("v",key).build();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW).setData(youtubeUri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        reviewsLv = (ExpandableHeightListView) rootView.findViewById(R.id.reviews_list_view);
        reviewsLv.setExpanded(true);
        reviewsAdapter = new ReviewsCursorAdapter(getActivity(),null,0);
        reviewsLv.setAdapter(reviewsAdapter);


        Cursor retCursor = getActivity()
                .getContentResolver().query(detail_uri,DETAIL_PROJECTION,null,null,null);
        if(retCursor.moveToNext()!=false){
            String poster_path = retCursor.getString(MOVIE_POSTER_PATH_INDEX);
            baseURL = "http://image.tmdb.org/t/p/w185" + poster_path;
            overview = retCursor.getString(MOVIE_OVERVIEW_INDEX);
            releaseDate = retCursor.getString(MOVIE_RELEASE_DATE_INDEX);
            movieTitle = retCursor.getString(MOVIE_TITLE_INDEX);
            voteAverage = retCursor.getDouble(MOVIE_VOTE_AVERAGE_INDEX);
            int favorite_indicator = retCursor.getInt(MOVIE_FAVOURITE_INDEX);
            if(favorite_indicator ==1){
                isFavorite = true;
            }
        }
        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView releaseDateTv = (TextView) rootView.findViewById(R.id.release_date);
        durationTV = (TextView) rootView.findViewById(R.id.duration_text_view);
        TextView voteAverageTv = (TextView) rootView.findViewById(R.id.vote_average);
        TextView overviewTv = (TextView) rootView.findViewById(R.id.overview);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_image);
        button = (ToggleButton) rootView.findViewById(R.id.button);
        if(isFavorite == true){
            button.setChecked(true);
        }
        button.setOnClickListener(this);
        if (baseURL != null && !baseURL.isEmpty()) {
            if (baseURL.contains("null")) {
                Picasso.with(getActivity()).load(R.drawable.poster_not_available).into(imageView);
            } else {
                Picasso.with(getActivity()).load(baseURL).into(imageView);
            }
        }

        if(movieTitle.contentEquals("null")){
            title.setText("No title available.");
        }else {
            if (movieTitle != null && !movieTitle.isEmpty()) {
                title.setText(movieTitle);
            } else {
                title.setText("No title available.");
            }
        }

        if(releaseDate.contentEquals("null")) {
            releaseDateTv.setText("No release date available.");
            releaseDateTv.setTextSize(14);
        }else {
            if (releaseDate != null && !releaseDate.isEmpty()) {
                releaseDate = releaseDate.substring(0, 4);
                releaseDateTv.setText(releaseDate);
            } else {
                releaseDateTv.setText("No release date available.");
                releaseDateTv.setTextSize(14);
            }
        }

        voteAverageTv.setText(Double.toString(voteAverage));
        voteAverageTv.append("/10");

        if(overview.contentEquals("null")){
            overviewTv.setText("No overview available.");
        }else {

            if (overview != null && !overview.isEmpty()) {
                overviewTv.setText(overview);
            } else {
                overviewTv.setText("No overview available.");
            }
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(Utility.isNetworkAvailable(getActivity())) {
            getRunTime = new GetRunTimeTask();
            getTrailers = new GetTrailers();
            getReviews = new GetReviewsTask();
            getRunTime.execute(id);
            getTrailers.execute(id);
            getReviews.execute(id);
        }else {
            Cursor cursor = getMovieDurationFromDataBase();
            if(cursor.moveToFirst()) {
                int movieDuration = cursor.getInt(MOVIE_DURATION_INDEX);

                if (movieDuration == 0) {
                    durationTV.setText("No run time available.");
                    durationTV.setTextSize(14);
                } else {
                    durationTV.setText(Integer.toString(movieDuration) + " min");

                }
            }
            cursor.close();

            Cursor cursor1 = getMoviesTrailersFromDB();
            if(cursor1.moveToNext()){
                FrameLayout trailersTopDivider = (FrameLayout) getView().findViewById(R.id.trailers_top_divider);
                trailersTopDivider.setVisibility(View.VISIBLE);

            }
            trailersAdapter.swapCursor(cursor1);

            Cursor cursor2 = getMovieReviewsFromDB();
            if(cursor2.moveToNext()){
                FrameLayout reviewsTopDivider = (FrameLayout) getView().findViewById(R.id.reviews_top_divider);
                reviewsTopDivider.setVisibility(View.VISIBLE);

            }
            reviewsAdapter.swapCursor(cursor2);


        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // get movie durtion from database whether online or offline
    private Cursor getMovieDurationFromDataBase(){
        Cursor retCursor = getActivity().getContentResolver().query(
                MovieContract.MovieDurationEntry.CONTENT_URI, DURATION_PROJECTION,
                MovieContract.MovieDurationEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{Integer.toString(id)}, null);

        if(retCursor.moveToFirst()== false) {
            getNetworkErrorToast();
        }

        return retCursor;
    }


    //get movie trailers from database whether online or offline
    private Cursor getMoviesTrailersFromDB(){
        Cursor cursor = getActivity().getContentResolver()
                .query(MovieContract.MovieVideosEntry.CONTENT_URI, null,
                        MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID + " =?"
                        , new String[]{Integer.toString(id)}, null);


        return cursor;
    }

    //get reviews from database whether online or offline
    private Cursor getMovieReviewsFromDB(){
        Cursor retCursor = getActivity().getContentResolver()
                .query(MovieContract.MovieReviewsEntry.CONTENT_URI, null,
                        MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID + " =?"
                        , new String[]{Integer.toString(id)}, null);

        return retCursor;
    }

    private void getNetworkErrorToast(){
        Toast.makeText(getActivity(), "Please check your internet connection and try again!!'"
                , Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //listener's onClick method of click event on the favorite button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String text = button.getText().toString();
                ContentValues values = new ContentValues();
                if(text.equals(button.getTextOn())) {
                    values.put(MovieContract.MovieDetailsEntry.COLUMN_FAVOURITE, 1);
                    Toast.makeText(getActivity(),"Marked as favorite!!",Toast.LENGTH_LONG).show();

                }else if(text.equals(button.getTextOff())){
                    values.put(MovieContract.MovieDetailsEntry.COLUMN_FAVOURITE, 0);
                }

                getActivity().getContentResolver().update(MovieContract
                                .MovieDetailsEntry.CONTENT_URI, values,
                        MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{Integer.toString(id)});
                if(DetailFragment.this.getActivity().getClass().getSimpleName().equals("MainActivity")) {
                    ((onFavouriteStateChanged) getActivity()).onFavouriteStateChanged();
                }

                break;
        }
    }

    // implement network call part of each asynctask
    private String getJSONData(Uri uri){
        String resultString = null;
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

    // get movie duration through api request
    private class GetRunTimeTask extends AsyncTask<Integer, Void, String> {
        private final String API_KEY = BuildConfig.MOVIES_API_KEY;

        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0];
            String resultString = null;
            Uri uri = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            resultString = getJSONData(uri);

            return resultString;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.v(LOG_TAG,s);
                duration = jsonObject.getInt("runtime");
                Cursor retCursor = getActivity().getContentResolver().query(
                        MovieContract.MovieDurationEntry.CONTENT_URI, DURATION_PROJECTION,
                        MovieContract.MovieDurationEntry.COLUMN_MOVIE_ID + " =?",
                        new String[]{Integer.toString(id)}, null);

                if(retCursor.moveToFirst()==false){

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieDurationEntry.COLUMN_MOVIE_ID,id);
                    Log.v(LOG_TAG, id + "");
                    contentValues.put(MovieContract.MovieDurationEntry.COLUMN_MOVIE_DURATION
                            , duration);
                    Log.v(LOG_TAG,duration+ "");
                    Uri uri = getActivity().getContentResolver().
                            insert(MovieContract.MovieDurationEntry.CONTENT_URI, contentValues);

                    Log.v(LOG_TAG,uri.getLastPathSegment()+" row inserted successfully!!");


                }
                retCursor.close();
                Cursor cursor = getMovieDurationFromDataBase();
                if(cursor.moveToFirst()) {
                    int movieDuration = cursor.getInt(MOVIE_DURATION_INDEX);
                    if (movieDuration == 0) {
                        durationTV.setText("No run time available.");
                        durationTV.setTextSize(14);
                    } else {
                        durationTV.setText(Integer.toString(movieDuration) + " min");
                    }
                }

                cursor.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // get movie trailers through api request
    private class GetTrailers extends AsyncTask<Integer, Void, String> {
        private final String API_KEY = BuildConfig.MOVIES_API_KEY;
        private final String API_KEY_PARAM = "api_key";
        String resultString;

        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0];
            Uri uri = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon().appendPath(Integer.toString(id))
                    .appendPath("videos")
                    .appendQueryParameter(API_KEY_PARAM, API_KEY).build();

            resultString = getJSONData(uri);
            return resultString;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final String JSON_ARRAY_NAME = "results";
            final String NAME = "name";
            final String KEY = "key";
            if (s != null && !s.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());
                    ArrayList<String> trailersList = new ArrayList<>();
                    ArrayList<String> trailersKeysList = new ArrayList<>();
                    Cursor retCursor = getActivity().getContentResolver()
                            .query(MovieContract.MovieVideosEntry.CONTENT_URI, null,
                                    MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID + " =?"
                                    , new String[]{Integer.toString(id)}, null);

                    if(retCursor.moveToNext()==false){
                        for (int index = 0; index < jsonArray.length(); index++) {

                            JSONObject jsonArrayElement = jsonArray.getJSONObject(index);
                            String videoName = jsonArrayElement.getString(NAME);
                            String key = jsonArrayElement.getString(KEY);
                            trailersList.add(videoName);
                            trailersKeysList.add(key);

                            if(key!=null && !key.isEmpty()) {
                                ContentValues moviesValues = new ContentValues();

                                moviesValues.put(MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID, id);
                                moviesValues.put(MovieContract.MovieVideosEntry.COLUMN_VIDEO_NAME, videoName);
                                moviesValues.put(MovieContract.MovieVideosEntry.COLUMN_VIDEO_KEY, key);

                                cVVector.add(moviesValues);
                            }

                        }
                        // add to database
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            getActivity().getContentResolver()
                                    .bulkInsert(MovieContract.MovieVideosEntry.CONTENT_URI, cvArray);
                        }

                    }
                    retCursor.close();
                    Cursor cursor = getMoviesTrailersFromDB();
                    if(cursor.moveToNext()){
                        FrameLayout trailersTopDivider = (FrameLayout) getView().findViewById(R.id.trailers_top_divider);
                        trailersTopDivider.setVisibility(View.VISIBLE);

                    }
                    trailersAdapter.swapCursor(cursor);
                    //force recreation of options menu to setshareIntent with valid uri as the
                    //the movie trailers table is now populated with trailers if any
                    DetailFragment.this.getActivity().invalidateOptionsMenu();




                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }

            }
        }
    }

    // get moview reviews through api request
    private class GetReviewsTask extends AsyncTask<Integer, Void, String> {
        final String JSON_ARRAY_NAME = "results";
        private final String AUTHOR = "author";
        private final String CONTENT = "content";
        ArrayList<String> authors = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();
        private final String API_KEY = BuildConfig.MOVIES_API_KEY;

        @Override
        protected String doInBackground(Integer... params) {
            int id = params[0];
            String resultString = null;
            Uri uri = Uri.parse("http://api.themoviedb.org/3/movie")
                    .buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            resultString = getJSONData(uri);
            return resultString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
                Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());
                Cursor retCursor = getActivity().getContentResolver()
                        .query(MovieContract.MovieReviewsEntry.CONTENT_URI, null,
                                MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID + " =?"
                                , new String[]{Integer.toString(id)}, null);
                if(retCursor.moveToNext()==false) {
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject arrayElement = jsonArray.getJSONObject(index);
                        String authorName = arrayElement.getString(AUTHOR);
                        String content = arrayElement.getString(CONTENT);

                        if(authorName!=null && !authorName.isEmpty()) {
                            ContentValues moviesValues = new ContentValues();

                            moviesValues.put(MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID, id);
                            moviesValues.put(MovieContract.MovieReviewsEntry.COLUMN_AUTHOR_NAME, authorName);
                            moviesValues.put(MovieContract.MovieReviewsEntry.COLUMN_CONTENT_NAME, content);

                            cVVector.add(moviesValues);
                        }

                    }
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        getActivity().getContentResolver()
                                .bulkInsert(MovieContract.MovieReviewsEntry.CONTENT_URI, cvArray);
                    }
                }
                retCursor.close();
                retCursor = getMovieReviewsFromDB();
                if(retCursor.moveToNext()){
                    FrameLayout reviewsTopDivider = (FrameLayout) getView().findViewById(R.id.reviews_top_divider);
                    reviewsTopDivider.setVisibility(View.VISIBLE);

                }
                reviewsAdapter.swapCursor(retCursor);





            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }



}
