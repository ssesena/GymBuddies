package com.example.gymbuddies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.gymbuddies.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryGridAdapter extends BaseAdapter {
    public static final String TAG = "GalleryGridAdapter";
    private Context context;
    private List<String> images = new ArrayList<String>();

    public GalleryGridAdapter(Context context) {
        this.context = context;

    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        Log.i(TAG, "method is called");
        if(view == null){
            Log.i(TAG, "view is null");
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(100,100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else{
            imageView = (ImageView) view;
        }

        imageView.setImageResource(R.drawable.ic_baseline_image_24);
        return imageView;
    }
}
