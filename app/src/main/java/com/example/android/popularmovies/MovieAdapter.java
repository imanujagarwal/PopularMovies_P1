package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by anuj on 4/3/17.
 */

public class MovieAdapter extends ArrayAdapter<GridItem>{

    private List<GridItem> mGridItemList;
    private final String BASE_URL = "http://image.tmdb.org/t/p/w500//";

    public MovieAdapter(Context context, int resource,List<GridItem> objects) {
        super(context, resource,0,objects);
        this.mGridItemList = objects;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View cv = convertView;
        GridItem gridItem = mGridItemList.get(position);

        if(cv == null ){
            cv = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item,null);
        }

        String Url = BuildImageUrl(gridItem.getmImageUrlSuffix());

        ImageView imageView = (ImageView) cv.findViewById(R.id.imageView_movieArtwork);

        Picasso.with(getContext()).load(Url).into(imageView);

        return cv;


    }

    @Override
    public int getCount() {
        return mGridItemList.size();
    }

    public String BuildImageUrl(String ImageSuffix){
        return BASE_URL+ImageSuffix;

    }
}
