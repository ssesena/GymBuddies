package com.example.gymbuddies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.gymbuddies.R;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class FlipperAdapter extends BaseAdapter {

    public static final String TAG = "FlipperAdapter";

    Context context;
    JSONArray photos;

    public FlipperAdapter(Context context, JSONArray photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Finding the image in the appropriate position
        String imageUrl = null;
        try {
            JSONObject photo = photos.getJSONObject(i);
            imageUrl = photo.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View view2 = inflater.inflate(R.layout.flipper_pictures, null);
        PhotoView imageView = (PhotoView) view2.findViewById(R.id.ivViewProfileImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Image clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(context).load(imageUrl).into(imageView);
        return view2;
    }
}
