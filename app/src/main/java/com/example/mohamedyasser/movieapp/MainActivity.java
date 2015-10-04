
package com.example.mohamedyasser.movieapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


public class MainActivity extends ActionBarActivity implements PosterFragment.Callback {
    private static final String DETAiL_TAG = "dftag";
    public static String twoPane;
    boolean mTwoPane;
    String[] posterStringExtras;
    public final static String MOVIE_ARRAY = "mov_array";
    public final static String MOVIE_VOTE_AVERAGE = "mov_vote_avg";
    public final static String MOVIE_ID = "mov_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.detail_fragment_container)!=null){

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container
                            , new DetailFragment(), DETAiL_TAG).commit();
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
    public void onItemSelected(Poster poster) {
        Bundle args = new Bundle();
        posterStringExtras = new String[]{poster.baseURL,poster.movieTitle,poster.overview,
                poster.releaseDate};
        args.putDouble(MOVIE_VOTE_AVERAGE,poster.voteAverage);
        args.putInt(MOVIE_ID,poster.id);
        args.putStringArray(MOVIE_ARRAY,posterStringExtras);

        if(mTwoPane==true) {

            DetailFragment df = new DetailFragment();
            df.setArguments(args);
            if(getSupportFragmentManager().findFragmentByTag(DETAiL_TAG)!=null) {
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null).replace(R.id.detail_fragment_container
                        , df, DETAiL_TAG).commit();

            }

        }else{
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtras(args);
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
}
