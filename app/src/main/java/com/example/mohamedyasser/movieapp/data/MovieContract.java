package com.example.mohamedyasser.movieapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mohamed Yasser on 10/2/2015.
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.mohamedyasser.movieapp";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.mohamedyasser.movieapp/moviemoviedetails/ is a valid path for
    // looking at moviedetails data.
    public static final String PATH_MOVIE_DETAILS = "movie_details";
    public static final String PATH_MOVIE_DURATION = "movie_duration";
    public static final String PATH_MOVIE_VIDEOS = "movie_videos";
    public static final String PATH_MOVIE_REVIEWS = "movie_reviews";

    public static final class MovieDetailsEntry implements BaseColumns {
        
        //movie details table name
        public static final String TABLE_NAME = "movie_details";
        
        // movie id column name
        public static final String COLUMN_MOVIE_ID = "id";
        // movie poster path column name
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // movie overview column name
        public static final String COLUMN_OVERVIEW = "overview";
        // movie release date column name
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //movie popularity column name
        public static final String COLUMN_POPULARITY = "popularity";

        //movie title column name
        public static final String COLUMN_TITLE = "title";

        //movie vote average column name
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //movie vote_count column name
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        //movie favourite indicator column name
        public static final String COLUMN_FAVOURITE = "favourite";

        // movie details table uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAILS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;

        public static Uri buildMovieDetailUri(long rowId) {
            return ContentUris.withAppendedId(CONTENT_URI, rowId);
        }

        public static Uri buildUriWithMovieId(int movieId){
            return MovieDetailsEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId))
                    .build();
        }


    }

    public static final class MovieDurationEntry implements BaseColumns{
        public static final String TABLE_NAME = "movie_duration";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_DURATION = "runtime";

        // movie details table uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DURATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DURATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DURATION;

        public static Uri buildMovieDurationUri(long rowId){
            return ContentUris.withAppendedId(CONTENT_URI,rowId);
        }
    }


    public static final class MovieVideosEntry implements BaseColumns {

        //movie videos table name
        public static final String TABLE_NAME = "movie_videos";
        // movie id column name
        public static final String COLUMN_MOVIE_ID = "id";

        // movie video name column name
        public static final String COLUMN_VIDEO_NAME = "name";

        // movie video key column name
        public static final String COLUMN_VIDEO_KEY = "key";


        //movie details uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_VIDEOS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_VIDEOS;

        public static Uri buildMovieVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class MovieReviewsEntry implements BaseColumns {

        //movie reviews table name
        public static final String TABLE_NAME = "movie_reviews";

        // movie id column name
        public static final String COLUMN_MOVIE_ID = "id";

        // review author column name
        public static final String COLUMN_AUTHOR_NAME = "author";

        // review content column name
        public static final String COLUMN_CONTENT_NAME = "content";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS;

        public static Uri buildMovieReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
