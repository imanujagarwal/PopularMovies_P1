package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {


    private int page = 1;
    MovieAdapter movieAdapter;
    List<GridItem> gridItemList;
    GridView gridView;

    private String KEY = "api_key"; //Do not change this value, key must be defined on strings.xml
    private String RESULTS = "results";
    private String MOVIE_NAME = "title";
    private String MOVIE_IMAGE = "poster_path";
    private String MOVIE_SYNOPSIS = "overview";
    private String MOVIE_RATING = "vote_average";
    private String MOVIE_RELEASE_DATE = "release_date";
    private String PAGE = "page";
    private String pageNumber = "";

    public static final String TAG = "MyActivity";
    public FetchMovieInfo task = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        gridItemList = new ArrayList<>();


        movieAdapter = new MovieAdapter(this, R.layout.grid_view_item, gridItemList);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
                intent.putExtra(MOVIE_NAME,gridItemList.get(i).getmTitle());
                intent.putExtra(MOVIE_IMAGE,gridItemList.get(i).getmImageUrlSuffix());
                intent.putExtra(MOVIE_SYNOPSIS,gridItemList.get(i).getmOverview());
                intent.putExtra(MOVIE_RATING,gridItemList.get(i).getmRating());
                intent.putExtra(MOVIE_RELEASE_DATE,gridItemList.get(i).getmReleaseDate());
                Log.i(TAG, "onItemClick: VALUES "+gridItemList.get(i).getmTitle());
                startActivity(intent);
            }
        });
    }


    public class FetchMovieInfo extends AsyncTask<String, Void, String[]> {

        private String LOG_TAG = FetchMovieInfo.class.getSimpleName();
        private String BASE_URL = "https://api.themoviedb.org/3/movie";


        private String[] getMovieInfoFromJson(String movieJsonString) throws JSONException {

            JSONObject moviesJson = new JSONObject(movieJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            String[] resultsStr = new String[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                String name;
                String posterPath;
                String synopsis;
                double rating;
                String released;

                JSONObject movie = moviesArray.getJSONObject(i);
                name = movie.getString(MOVIE_NAME);
                posterPath = movie.getString(MOVIE_IMAGE);
                synopsis = movie.getString(MOVIE_SYNOPSIS);
                rating = movie.getDouble(MOVIE_RATING);
                released = movie.getString(MOVIE_RELEASE_DATE);

                gridItemList.add(new GridItem(posterPath, name, synopsis, rating, released));

            }

            return resultsStr;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonString = null;

            try {
                Uri buildUri = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(KEY, getResources().getString(R.string.api_key))
                        .appendQueryParameter(PAGE, pageNumber)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.v(LOG_TAG, "Build result: " + buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    movieJsonString = null;
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    movieJsonString = null;
                    return null;
                }
                movieJsonString = stringBuffer.toString();
                Log.v(LOG_TAG, "Movie string: " + movieJsonString);

            } catch (Exception e) {
                movieJsonString = null;
                return null;
            } finally {
                if (urlConnection != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        return null;
                    } catch (final Exception e) {
                        return null;
                    }
                }
            }

            try {
                getMovieInfoFromJson(movieJsonString);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            View pb = findViewById(R.id.loading_indicator);
            pb.setVisibility(View.GONE);
            if(movieAdapter!=null){
                gridView.setAdapter(movieAdapter);
            }
            task = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.i(TAG, "onCreateOptionsMenu: ");

        MenuItem item = menu.findItem(R.id.menu_spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movieTypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spinner.getSelectedItem().toString().toLowerCase();

                switch (i){
                    case 0:
                        if(task == null){
                            movieAdapter.clear();
                            task = new FetchMovieInfo();
                            task.execute(selectedItem);
                            break;

                        }

                    case 1:
                        if(task == null){
                            movieAdapter.clear();
                            new FetchMovieInfo().execute("top_rated");
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

}
