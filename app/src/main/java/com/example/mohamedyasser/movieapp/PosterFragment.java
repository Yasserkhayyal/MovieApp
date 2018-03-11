package com.example.mohamedyasser.movieapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mohamedyasser.movieapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Mohamed Yasser on 8/23/2015.
 */

public class PosterFragment extends Fragment {
    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    GridView mGridView;
    PostersCursorAdapter mAdaptor;
    String mSelection;
    int mSelectedItemId;
    Callback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (Callback) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //interface fired when the user selects a poster thumbnail
    public interface Callback{
        public void onItemSelected(Uri itemUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSelection = Utility.getSortSelection(getActivity());
        ///get the rootView by inflating the fragment from main_fragment.xml
        View rootView = inflater.inflate(R.layout.main_fragment,container,false);
        //retrieve mGridView from the inflated view by calling findViewById() and passing
        // the id of the specified grid in main_fragment.xml
        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mAdaptor = new PostersCursorAdapter(getActivity(),null,0);
        mGridView.setAdapter(mAdaptor);
        if(Utility.isNetworkAvailable(getActivity()) && !mSelection.equals("favorite")){
            Log.v(LOG_TAG,"inside onCreateView Network enabled");
            fireAsyncTask();
        }else{
            Log.v(LOG_TAG,"inside onCreateView Network disabled");
            Cursor cursor = getDataFromDataBase();
            mAdaptor.swapCursor(cursor);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mSelectedItemId = position;
                Cursor retCursor = (Cursor) adapterView.getItemAtPosition(position);
                if (retCursor != null) {
                    int movieIdColumnIndex = retCursor.getColumnIndex(MovieContract.
                            MovieDetailsEntry.COLUMN_MOVIE_ID);
                    int movieId = retCursor.getInt(movieIdColumnIndex);
                    // append movie id as a path to the uri of the selected movie to be sent to the
                    // detail fragment later to extract this id and makes api calls with it
                    Uri uri = MovieContract.MovieDetailsEntry.buildUriWithMovieId(movieId);
                    callback.onItemSelected(uri);
                }


            }
        });
        return rootView;
    }

    // update cursor due to favorite button click event in the detail fragment in the tablet case
    public void updateCursor(){
        Cursor cursor = getDataFromDataBase();
        if(mSelection.equals("favorite") && cursor.moveToFirst()==false){
            Toast.makeText(getActivity(),"No favorite movies selected!!"
                    ,Toast.LENGTH_LONG).show();
        }
        mAdaptor.swapCursor(cursor);

    }

    @Override
    public void onStart() {
        super.onStart();
        // handles back from settings activity
        String selection = Utility.getSortSelection(getActivity());
        if(selection!=null && !mSelection.equals(selection)) {
            mSelection = selection;
            if(mAdaptor!=null) {
                mAdaptor.swapCursor(null);
            }
            if(Utility.isNetworkAvailable(getActivity()) && !mSelection.equals("favorite")){
                fireAsyncTask();
            }else{
                Cursor cursor = getDataFromDataBase();
                if(mSelection.equals("favorite") && cursor.moveToFirst()==false){
                    Toast.makeText(getActivity(),"No favorite movies selected!!"
                            ,Toast.LENGTH_LONG).show();
                }
                mAdaptor.swapCursor(cursor);
            }
        }else if(selection.equals("favorite") && mSelection.equals("favorite")){
            Cursor cursor = getDataFromDataBase();
            if(mSelection.equals("favorite") && cursor.moveToFirst()==false){
                Toast.makeText(getActivity(),"No fav" +
                        "orite movies selected!!"
                        ,Toast.LENGTH_LONG).show();
            }
            mAdaptor.swapCursor(cursor);
        }

        // scroll to the current selection in the grid view in phone case
        if(getActivity().findViewById(R.id.detail_fragment_container)==null){
            mGridView.smoothScrollToPosition(mSelectedItemId);
        }

    }

    public void fireAsyncTask(){
        GetDataTask get = new GetDataTask();
        get.execute(mSelection);
    }


    //gets data from data base whether in online or offline modes
    public Cursor getDataFromDataBase(){
        Log.v(LOG_TAG,"inside getDataFromDataBase");
        String sortSelection = Utility.getSortSelection(getActivity());
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        if(sortSelection.equals("vote_average.desc")){
            sortOrder = MovieContract.MovieDetailsEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }else if(sortSelection.equals("popularity.desc")){
            sortOrder = MovieContract.MovieDetailsEntry.COLUMN_POPULARITY + " DESC";
        }else{
            selection = MovieContract.MovieDetailsEntry.COLUMN_FAVOURITE + " =?";
            selectionArgs = new String[] {Integer.toString(1)};
        }

        Cursor retCursor = getActivity().getContentResolver().query(
                MovieContract.MovieDetailsEntry.CONTENT_URI, null, selection, selectionArgs, sortOrder);

        if( retCursor.moveToFirst()==false && !sortSelection.equals("favorite")){
                Toast.makeText(getActivity(), "Please check your internet connection and try again!!"
                        , Toast.LENGTH_LONG).show();
                getActivity().finish();

        }
        return retCursor;

    }


    // get movie details through api request
    public class GetDataTask extends AsyncTask<String,Void,String> {
        private final String MOST_POPULAR = "popularity.desc";
        private final String HIGHEST_RATED = "vote_average.desc";
        private final String API_KEY = BuildConfig.MOVIES_API_KEY;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String selection = params[0];
            Uri builtUri = null;
            String resultString = null;

            if (selection.equals(MOST_POPULAR)) {
                Log.v(LOG_TAG, "inside selection first if else statement");
                builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?")
                        .buildUpon()
                        .appendQueryParameter("sort_by", MOST_POPULAR)
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
            } else if (selection.equals(HIGHEST_RATED)) {
                Log.v(LOG_TAG, "inside selection second if else statement");
                builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?")
                        .buildUpon()
                        .appendQueryParameter("sort_by", HIGHEST_RATED)
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
            }
            try {
                URL url = null;
                url = new URL(builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    resultString = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

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
            Log.v(LOG_TAG, "JSON Data: " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");
                int resultsLength = results.length();
                Log.v("array length", "" + resultsLength);
                Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsLength);
                for(int index=0;index<resultsLength;index++){
                    JSONObject arrayElement = results.getJSONObject(index);
                    int id = arrayElement.getInt("id");
                    String relativePath = arrayElement.getString("poster_path");
                    String overview = arrayElement.getString("overview");
                    String releaseDate = arrayElement.getString("release_date");
                    String movieTitle = arrayElement.getString("title");
                    double popularity = arrayElement.getDouble("popularity");
                    double voteAverage = arrayElement.getDouble("vote_average");
                    double voteCount = arrayElement.getInt("vote_count");

                    Cursor retCursor = getActivity().getContentResolver()
                            .query(MovieContract.MovieDetailsEntry.CONTENT_URI, null,
                                    MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " =?"
                                    , new String[] {Integer.toString(id)},null);

                    if(retCursor.moveToNext()==true){
                        continue;
                    }

                    ContentValues moviesValues = new ContentValues();

                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID, id);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_POSTER_PATH,relativePath );
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_OVERVIEW, overview);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_RELEASE_DATE, releaseDate);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_POPULARITY, popularity);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_TITLE, movieTitle);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_VOTE_COUNT, voteCount);
                    moviesValues.put(MovieContract.MovieDetailsEntry.COLUMN_FAVOURITE, 0);

                    cVVector.add(moviesValues);


                }

                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getActivity().getContentResolver()
                            .bulkInsert(MovieContract.MovieDetailsEntry.CONTENT_URI, cvArray);
                }


                    Cursor cursor = getDataFromDataBase();
                    mAdaptor.swapCursor(cursor);


            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }


        }
    }



}
