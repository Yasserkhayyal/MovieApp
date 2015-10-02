package com.example.mohamedyasser.movieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
public class DetailFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "DetailFragment";

    private String baseURL;
    private String movieTitle;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setPoster(Poster poster) {
        baseURL = poster.baseURL;
        movieTitle = poster.movieTitle;
        overview = poster.overview;
        voteAverage = poster.voteAverage;
        releaseDate = poster.releaseDate;
        id = (poster.id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        return rootView;
    }

    public void setContent(View view) {
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView releaseDateTv = (TextView) view.findViewById(R.id.release_date);
        TextView voteAverageTv = (TextView) view.findViewById(R.id.vote_average);
        TextView overviewTv = (TextView) view.findViewById(R.id.overview);
        ImageView imageview = (ImageView) view.findViewById(R.id.imageView2);
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(this);
        if (baseURL != null && !baseURL.isEmpty()) {
            if (baseURL.contains("null")) {
                imageview.setImageResource(R.drawable.poster_not_available);
            } else {
                Picasso.with(getActivity()).load(baseURL).into(imageview);
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContent(getView());
        GetTrailers getTrailers = new GetTrailers();
        getTrailers.execute(id);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Toast.makeText(getActivity(), "button clicked!!", Toast.LENGTH_LONG).show();
                break;
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
            if(s!=null && !s.isEmpty()){
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_NAME);
                    ArrayList<String> trailersList = new ArrayList<>();
                    ArrayList<String> teasersList = new ArrayList<>();
                    ArrayList<String> trailerKeysList = new ArrayList<>();
                    ArrayList<String> teaserKeysList = new ArrayList<>();
                    for(int index=0;index<jsonArray.length();index++) {

                        JSONObject jsonArrayElement = jsonArray.getJSONObject(index);
                        String type = jsonArrayElement.getString(TYPE);
                        String videoTypeName = jsonArrayElement.getString(NAME);
                        String key = jsonArrayElement.getString(KEY);

                        if(type.equalsIgnoreCase("trailer")){

                            trailersList.add(videoTypeName);
                            teaserKeysList.add(key);

                        }else{

                            teasersList.add(videoTypeName);
                            teaserKeysList.add(key);

                        }

                        ArrayAdapter<String> trailerAdapter = new ArrayAdapter<>(getActivity()
                                                                ,R.layout.trailer_list_item
                                                                ,R.id.trailer_text_view
                                                                ,new String[trailersList.size()]);
                        ListView trailerLv = (ListView) getView()
                                                        .findViewById(R.id.trailer_list_view);
                        trailerLv.setAdapter(trailerAdapter);


                    }



                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(),e);
                }

            }
        }
    }
}
