package com.example.mohamedyasser.movieapp;

/**
 * Created by Mohamed Yasser on 8/26/2015.
 */
public class Poster {
    String baseURL;
    String movieTitle;
    String overview;
    double voteAverage;
    String releaseDate;
    int id;


    public Poster(String baseURL,String movieTitle,String overview,double voteAverage
            ,String releaseDate,int id){
        this.baseURL=baseURL;
        this.movieTitle=movieTitle;
        this.overview=overview;
        this.voteAverage=voteAverage;
        this.releaseDate=releaseDate;
        this.id = id;
    }

}
