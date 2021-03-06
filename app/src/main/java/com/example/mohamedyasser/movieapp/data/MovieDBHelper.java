package com.example.mohamedyasser.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mohamed Yasser on 10/2/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private final static String LOG_TAG = MovieDBHelper.class.getSimpleName();

    static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.v(LOG_TAG,"inside onCreate");
        final String SQL_CREATE_MOVIE_DETAILS_TABLE = "CREATE TABLE "
                + MovieContract.MovieDetailsEntry.TABLE_NAME + " (" +

                MovieContract.MovieDetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MovieContract.MovieDetailsEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieDetailsEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieDetailsEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieDetailsEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieContract.MovieDetailsEntry.COLUMN_TITLE + " TEXT NOT NULL, "+

                MovieContract.MovieDetailsEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.MovieDetailsEntry.COLUMN_VOTE_COUNT + " REAL NOT NULL, " +
                MovieContract.MovieDetailsEntry.COLUMN_FAVOURITE + " INTEGER DEFAULT 0" + " );";


        final String SQL_CREATE_MOVIE_VIDEOS_TABLE = "CREATE TABLE " +
                MovieContract.MovieVideosEntry.TABLE_NAME + " (" +
                MovieContract.MovieVideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.MovieVideosEntry.COLUMN_VIDEO_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieVideosEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                // Set up the movie id column as a foreign key to movie_details table.
                 " FOREIGN KEY (" + MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieContract.MovieDetailsEntry.TABLE_NAME +
                " (" + MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID + "));";


        final String SQL_CREATE_MOVIE_REVIEWS_TABLE = "CREATE TABLE " +
                MovieContract.MovieReviewsEntry.TABLE_NAME + " (" +
                MovieContract.MovieReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.MovieReviewsEntry.COLUMN_AUTHOR_NAME + " TEXT, " +
                MovieContract.MovieReviewsEntry.COLUMN_CONTENT_NAME + " TEXT, " +
                // Set up the movie id column as a foreign key to movie_details table.
                " FOREIGN KEY (" + MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieContract.MovieDetailsEntry.TABLE_NAME +
                " (" + MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID + "));";

        final String SQL_CREATE_MOVIE_DURATION_TABLE = "CREATE TABLE " +
                MovieContract.MovieDurationEntry.TABLE_NAME + " (" +
                MovieContract.MovieDurationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieDurationEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieDurationEntry.COLUMN_MOVIE_DURATION + " INTEGER NOT NULL, " +
                // Set up the movie id column as a foreign key to movie_details table.
                " FOREIGN KEY (" + MovieContract.MovieDurationEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieContract.MovieDetailsEntry.TABLE_NAME +
                " (" + MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_DETAILS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_DURATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieDetailsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieVideosEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieDurationEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
