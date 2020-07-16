package com.example.gymbuddies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
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
    public static final int HEIGHT = 400;
    public static final int NUMBER_OF_GALLERY_PHOTOS = 6;

    private List<String> images = new ArrayList<String>();

    public GalleryGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_GALLERY_PHOTOS;
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
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_gallery_photo,null);
            Log.i(TAG, "view is null");
        }
        view.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, HEIGHT));
        ImageView icon = view.findViewById(R.id.ivRemovePhoto);
        icon.setImageResource(R.drawable.ic_baseline_remove_circle_24);
        imageView = view.findViewById(R.id.ivGalleryImage);
        //imageView.setImageResource(R.drawable.ic_baseline_image_24);

        //imageView.setImageResource(R.drawable.ic_baseline_image_24);
        return view;
    }
}
