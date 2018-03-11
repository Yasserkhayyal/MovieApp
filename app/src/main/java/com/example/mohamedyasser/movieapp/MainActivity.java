
package com.example.mohamedyasser.movieapp;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.mohamedyasser.movieapp.data.MovieContract;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements PosterFragment.Callback,
DetailFragment.onFavouriteStateChanged{
    private static final String DETAiL_TAG = "dftag";
    boolean mTwoPane;
    String[] posterStringExtras;
    public final static String MOVIE_ARRAY = "mov_array";
    public final static String MOVIE_VOTE_AVERAGE = "mov_vote_avg";
    public final static String MOVIE_ID = "mov_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check current configuration - phone or tablet
        if(findViewById(R.id.detail_fragment_container)!=null){
            mTwoPane = true;
        }else{
            mTwoPane = false;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI,uri);

        if(mTwoPane==true) {

            DetailFragment df = new DetailFragment();
            df.setArguments(args);
            if(getSupportFragmentManager().findFragmentByTag(DETAiL_TAG)!=null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container
                        , df, DETAiL_TAG).commit();

            }else{
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null).add(R.id.detail_fragment_container
                        , df, DETAiL_TAG).commit();

            }

        }else{
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.setData(uri);
            startActivity(intent);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailFragment df = (DetailFragment) getSupportFragmentManager().
                findFragmentByTag(DETAiL_TAG);
        if(df != null){
            getSupportFragmentManager().beginTransaction().remove(df).commit();
        }
    }

    @Override
    public void onFavouriteStateChanged() {
           PosterFragment posterFragment = (PosterFragment) getSupportFragmentManager()
                                                 .findFragmentById(R.id.poster_fragment);

            if(posterFragment!=null){
                posterFragment.updateCursor();
            }

    }


}
