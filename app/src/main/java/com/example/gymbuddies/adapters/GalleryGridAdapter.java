package com.example.gymbuddies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymbuddies.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryGridAdapter extends BaseAdapter {
    public static final String TAG = "GalleryGridAdapter";
    private Context context;
    public static final int HEIGHT = 400;
    public static final int NUMBER_OF_GALLERY_PHOTOS = 6;
    private final JSONArray gallery;


    public GalleryGridAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.gallery = jsonArray;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_GALLERY_PHOTOS;
    }

    @Override
    public Object getItem(int i) {
        try {
            return gallery.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.item_gallery_photo, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(new GridView.LayoutParams(params));

        if(i < gallery.length()) {
            try {
                final JSONObject image = gallery.getJSONObject(i);
                Log.i(TAG, "view is null");

                ImageView icon = (ImageView) view.findViewById(R.id.ivRemovePhoto);
                icon.setImageResource(R.drawable.ic_baseline_remove_circle_24);

                ImageView imageView = (ImageView) view.findViewById(R.id.ivGalleryImage);
                Glide.with(context).load(image.get("url")).into(imageView);

                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePhoto(i);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            ImageView imageView = (ImageView) view.findViewById(R.id.ivGalleryImage);
            imageView.setImageResource(R.drawable.ic_baseline_image_24);
        }

        return view;
    }

    public void deletePhoto(int index){
        ParseUser user = ParseUser.getCurrentUser();
        gallery.remove(index);
        user.put("gallery", gallery);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Error while saving",e);
                Toast.makeText(context, "Error while saving!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
