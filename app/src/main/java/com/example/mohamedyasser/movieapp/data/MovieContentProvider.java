package com.example.mohamedyasser.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Mohamed Yasser on 10/2/2015.
 */
public class MovieContentProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    final static int MOVIE_DETAILS = 100;
    final static int SPECIFIC_MOVIE_VIDEOS = 200;
    final static int SPECIFIC_MOVIE_REVIEWS = 300;

    private static SQLiteQueryBuilder sMovieAddsQueryBuilder = new SQLiteQueryBuilder();



    private final String MOVIE_VIDEOS_INNER_JOIN_MOVIE_DETAILS =
            MovieContract.MovieVideosEntry.TABLE_NAME + " INNER JOIN " +
            MovieContract.MovieDetailsEntry.TABLE_NAME +
            " ON " + MovieContract.MovieVideosEntry.TABLE_NAME +
            "." + MovieContract.MovieVideosEntry.COLUMN_MOVIE_ID +
            " = " + MovieContract.MovieDetailsEntry.TABLE_NAME +
            "." + MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID;

    private final String MOVIE_REVIEWS_INNER_JOIN_MOVIE_DETAILS =
            MovieContract.MovieReviewsEntry.TABLE_NAME + " INNER JOIN " +
            MovieContract.MovieDetailsEntry.TABLE_NAME +
            " ON " + MovieContract.MovieReviewsEntry.TABLE_NAME +
            "." + MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID +
            " = " + MovieContract.MovieDetailsEntry.TABLE_NAME +
            "." + MovieContract.MovieDetailsEntry.COLUMN_MOVIE_ID;

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher  uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_DETAILS,MOVIE_DETAILS);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_VIDEOS,SPECIFIC_MOVIE_VIDEOS);
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_REVIEWS,SPECIFIC_MOVIE_REVIEWS);
        // 3) Return the new matcher!
        return uriMatcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch(sUriMatcher.match(uri)){
            case MOVIE_DETAILS:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieDetailsEntry.TABLE_NAME,projection,selection
                        ,selectionArgs,null,null,sortOrder);
                break;

            case SPECIFIC_MOVIE_VIDEOS:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieVideosEntry.TABLE_NAME,projection,selection
                        ,selectionArgs,null,null,sortOrder);
                break;

            case SPECIFIC_MOVIE_REVIEWS:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieReviewsEntry.TABLE_NAME,projection,selection
                        ,selectionArgs,null,null,sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_DETAILS:
                return MovieContract.MovieDetailsEntry.CONTENT_TYPE;
            case SPECIFIC_MOVIE_VIDEOS:
                return MovieContract.MovieVideosEntry.CONTENT_TYPE;
            case SPECIFIC_MOVIE_REVIEWS:
                return MovieContract.MovieReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
