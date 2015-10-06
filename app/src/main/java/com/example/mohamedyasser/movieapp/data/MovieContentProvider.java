package com.example.mohamedyasser.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
//    final static int SPECIFIC_MOVIE_VIDEOS = 200;
//    final static int SPECIFIC_MOVIE_REVIEWS = 300;

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher  uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.
        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_DETAILS,MOVIE_DETAILS);
//        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_VIDEOS,SPECIFIC_MOVIE_VIDEOS);
//        uriMatcher.addURI(authority,MovieContract.PATH_MOVIE_REVIEWS,SPECIFIC_MOVIE_REVIEWS);
//        // 3) Return the new matcher!
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
//
//            case SPECIFIC_MOVIE_VIDEOS:
//                retCursor = mOpenHelper.getReadableDatabase()
//                        .query(MovieContract.MovieVideosEntry.TABLE_NAME,projection,selection
//                        ,selectionArgs,null,null,sortOrder);
//                break;
//
//            case SPECIFIC_MOVIE_REVIEWS:
//                retCursor = mOpenHelper.getReadableDatabase()
//                        .query(MovieContract.MovieReviewsEntry.TABLE_NAME,projection,selection
//                        ,selectionArgs,null,null,sortOrder);
//                break;

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
//            case SPECIFIC_MOVIE_VIDEOS:
//                return MovieContract.MovieVideosEntry.CONTENT_TYPE;
//            case SPECIFIC_MOVIE_REVIEWS:
//                return MovieContract.MovieReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }    }

//    @Override
//    public int bulkInsert(Uri uri, ContentValues[] values) {
//            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//            final int match = sUriMatcher.match(uri);
//            int returnCount = 0;
//
//        switch (match) {
//                case MOVIE_DETAILS:
//                    db.beginTransaction();
//                    try {
//                        for (ContentValues value : values) {
//                            long _id = db.insert(MovieContract.MovieDetailsEntry.TABLE_NAME, null, value);
//                            if (_id != -1) {
//                                returnCount++;
//                            }
//                        }
//                        db.setTransactionSuccessful();
//                    } finally {
//                        db.endTransaction();
//                    }
//                    getContext().getContentResolver().notifyChange(uri, null);
//                    return returnCount;
//
//                case SPECIFIC_MOVIE_VIDEOS:
//                    db.beginTransaction();
//                    try {
//                        for (ContentValues value : values) {
//                            long _id = db.insert(MovieContract.MovieVideosEntry.TABLE_NAME, null, value);
//                            if (_id != -1) {
//                                returnCount++;
//                            }
//                        }
//                        db.setTransactionSuccessful();
//                    } finally {
//                        db.endTransaction();
//                    }
//                    getContext().getContentResolver().notifyChange(uri, null);
//                    return returnCount;
//
//                case SPECIFIC_MOVIE_REVIEWS:
//                    db.beginTransaction();
//                    try {
//                        for (ContentValues value : values) {
//                            long _id = db.insert(MovieContract.MovieReviewsEntry.TABLE_NAME, null, value);
//                            if (_id != -1) {
//                                returnCount++;
//                            }
//                        }
//                        db.setTransactionSuccessful();
//                    } finally {
//                        db.endTransaction();
//                    }
//                    getContext().getContentResolver().notifyChange(uri, null);
//                    return returnCount;
//
//                default:
//                    return super.bulkInsert(uri, values);
//            }
//        }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_DETAILS: {
                long _id = db.insert(MovieContract.MovieDetailsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieDetailsEntry.buildMovieDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
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
