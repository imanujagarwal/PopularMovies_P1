package com.example.android.popularmovies;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by anuj on 10/3/17.
 */

public class DetailActivity extends AppCompatActivity {


    private String MOVIE_NAME = "title";
    private String MOVIE_IMAGE = "poster_path";
    private String MOVIE_SYNOPSIS = "overview";
    private String MOVIE_RATING = "vote_average";
    private String MOVIE_RELEASE_DATE = "release_date";
    String BASE_URL = "http://image.tmdb.org/t/p/w500//";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);


        // Enabling Up / Back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String MovieName = i.getStringExtra(MOVIE_NAME);
        String MovieSynopsis = i.getStringExtra(MOVIE_SYNOPSIS);
        double MovieRating = i.getDoubleExtra(MOVIE_RATING,0);
        String MovieReleaseDate = i.getStringExtra(MOVIE_RELEASE_DATE);
        String ImageSuffix = i.getStringExtra(MOVIE_IMAGE);
        String ImageURL = BASE_URL+ImageSuffix;

        ImageView  imageView_artwork = (ImageView) findViewById(R.id.imageView_movieArtwork_detail);

        TextView tvMovieName = (TextView) findViewById(R.id.textView_moviename);
        tvMovieName.setText(MovieName);

        TextView tvRating = (TextView) findViewById(R.id.textview_rating);
        tvRating.setText(MovieRating+"");

        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        tvReleaseDate.setText(MovieReleaseDate);

        Picasso.with(this).load(ImageURL).into(imageView_artwork);
        TextView textView_summary = (TextView)findViewById(R.id.textview_description);
        textView_summary.setText(MovieSynopsis);

    }
}
