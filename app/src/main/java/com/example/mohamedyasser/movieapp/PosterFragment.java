package com.example.mohamedyasser.movieapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mohamed Yasser on 8/23/2015.
 */

public class PosterFragment extends Fragment {
    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    ArrayList<Poster> mPosters=new ArrayList<>();
    ArrayList<String> mImageURLs=new ArrayList<>();
    GridView mGridView;
    SharedPreferences mPrefs;
    ImageAdaptor mAdaptor;
    String mSelection;
    int mSelectedItemId;
    boolean mTwoPane;
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

    public interface Callback{
        public void onItemSelected(Poster poster);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSelection = mPrefs.getString(getString(R.string.sort_by_title),
                getString(R.string.sort_default_value));
        ///get the rootView by inflating the fragment from main_fragment.xml
        View rootView = inflater.inflate(R.layout.main_fragment,container,false);
        //retrieve mGridView from the inflated view by calling findViewById() and passing
        // the id of the specified grid in main_fragment.xml
        mGridView = (GridView) rootView.findViewById(R.id.gridView);

        GetDataTask get = new GetDataTask();
        get.execute(mSelection);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String selection = mPrefs.getString(getString(R.string.sort_by_title),
                getString(R.string.sort_default_value));
        if(selection!=null && !mSelection.equals(selection)) {
            mAdaptor = null;
            GetDataTask get = new GetDataTask();
            get.execute(selection);
            mSelection = selection;
        }

        if(getActivity().findViewById(R.id.detail_fragment_container)==null){
            mGridView.smoothScrollToPosition(mSelectedItemId);
        }

    }


    public class GetDataTask extends AsyncTask<String,Void,String> {
        private final String NOT_SPECIFIED = "not_specified";
        private final String MOST_POPULAR = "popularity.desc";
        private final String HIGHEST_RATED = "vote_count.desc";
        private final String API_KEY = "32f7436a70e7635bfbb6ad24f099334b";

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String selection = params[0];
            Uri builtUri = null;
            String resultString = "";

            if (selection.equals(NOT_SPECIFIED)) {
                Log.v(LOG_TAG, "inside selection if statement");
                builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?")
                        .buildUpon()
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                Log.v(LOG_TAG,builtUri.toString());
            } else if (selection.equals(MOST_POPULAR)) {
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
                    resultString = "";
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
            mPosters = new ArrayList<>();
            mImageURLs = new ArrayList<>();
            Log.v(LOG_TAG, "JSON Data: " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");
                int resultsLength = results.length();
                Log.v("array length",""+resultsLength);
                for(int index=0;index<resultsLength;index++){
                    JSONObject arrayElement = results.getJSONObject(index);
                    String relativePath = arrayElement.getString("poster_path");
                    String baseURL = "http://image.tmdb.org/t/p/w185" + relativePath;
                    Log.v(LOG_TAG,baseURL);
                    mImageURLs.add(baseURL);
                    String movieTitle = arrayElement.getString("title");
                    String overview = arrayElement.getString("overview");
                    double voteAverage = arrayElement.getDouble("vote_average");
                    String releaseDate = arrayElement.getString("release_date");
                    int id = arrayElement.getInt("id");
                    Poster poster = new Poster(baseURL,movieTitle,overview,voteAverage,releaseDate,id);
                    mPosters.add(poster);

                }

                    mAdaptor = new ImageAdaptor(getActivity(), mImageURLs);
                    mGridView.setAdapter(mAdaptor);


                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mSelectedItemId = position;
                        if (mPosters != null) {
                            callback.onItemSelected(mPosters.get(position));
                        }


                }
            });
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }


        }
    }


}
