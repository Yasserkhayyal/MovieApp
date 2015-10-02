
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public void onItemSelected(Poster poster) {
        if(mTwoPane==true) {

            DetailFragment df = new DetailFragment();
            if(getSupportFragmentManager().findFragmentByTag(DETAiL_TAG)!=null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container
                                , df, DETAiL_TAG).commit();
            }else{
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null).add(R.id.detail_fragment_container
                                                ,df,DETAiL_TAG).commit();
            }


            if(df !=null){
                df.setPoster(poster);
            }

        }else{
            DetailsActivity.setPoster(poster);
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
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
