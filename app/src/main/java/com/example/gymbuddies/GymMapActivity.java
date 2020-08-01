package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.gymbuddies.databinding.ActivityGymMapBinding;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class GymMapActivity extends AppCompatActivity {

    ActivityGymMapBinding binding;

    public static final String TAG = "GymMapActivity";

    private SupportMapFragment mapFragment;

    public static final String PLACES_API_SEARCH_URL = "https://api.foursquare.com/v2/venues/search";
    public String CLIENT_ID;
    public String CLIENT_SECRET;
    public static final String GYM_CATEGORY_ID = "4bf58dd8d48988d175941735";
    public static final int MINIMUM_SEARCH_RADIUS = 1500;
    public static final int MAXIMUM_SEARCH_RADIUS = 3000;
    public static final String VERSION = "20200731";
    public static final int GYMS_TO_SHOW = 20;

    JSONArray venues;

    ParseUser user = ParseUser.getCurrentUser();
    ParseUser match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGymMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CLIENT_ID = this.getResources().getString(R.string.foursquare_client_id);
        CLIENT_SECRET = this.getResources().getString(R.string.foursquare_client_secret);

        //Getting info from intent
        Intent intent = getIntent();

        match = Parcels.unwrap(intent.getParcelableExtra(ViewProfileActivity.class.getSimpleName()));

        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        try {
                            loadMap(map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private static double findRadius(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // meters
        double φ1 = lat1 * Math.PI/180; // φ, λ in radians
        double φ2 = lat2 * Math.PI/180;
        double Δφ = (lat2-lat1) * Math.PI/180;
        double Δλ = (lon2-lon1) * Math.PI/180;

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c; // in meters
        if(d < MINIMUM_SEARCH_RADIUS){
            d = MINIMUM_SEARCH_RADIUS;
        }
        else if(d > MAXIMUM_SEARCH_RADIUS){
            d = MAXIMUM_SEARCH_RADIUS;
        }
        return d;
    }

    private void findGyms(double[] midpoint, String radius, final GoogleMap googleMap) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("ll", midpoint[0]+","+midpoint[1]);
        params.put("radius", radius);
        params.put("limit", GYMS_TO_SHOW+"");
        params.put("categoryId", GYM_CATEGORY_ID);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("v", VERSION);

        client.get(PLACES_API_SEARCH_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Access a JSON array response with `json.jsonArray`
                //Log.d("DEBUG ARRAY", json.jsonArray.toString());
                // Access a JSON object response with `json.jsonObject`
                Log.d(TAG, json.jsonObject.toString());
                try {
                    venues = json.jsonObject.getJSONObject("response").getJSONArray("venues");
                    BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    for(int i = 0; i < venues.length();i++){
                        JSONObject gym = venues.getJSONObject(i);
                        String gymName = gym.getString("name");
                        double gymLat = gym.getJSONObject("location").getDouble("lat");
                        double gymLon = gym.getJSONObject("location").getDouble("lng");
                        JSONArray addressInfo = gym.getJSONObject("location").getJSONArray("formattedAddress");
                        String finalAddress = addressInfo.getString(0)+"\n"+addressInfo.getString(1);
                        // listingPosition is a LatLng point
                        LatLng listingPosition = new LatLng(gymLat, gymLon);
                        // Create the marker on the fragment
                        Marker mapMarker = googleMap.addMarker(new MarkerOptions()
                                .position(listingPosition)
                                .title(gymName)
                                .snippet(finalAddress)
                                .icon(defaultMarker));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Failed to get a response" + response);
            }
        });
    }

    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(GoogleMap googleMap) throws JSONException {
        if (googleMap != null) {

            //Finding match's location
            String matchCoords[] = match.getString("location").split(" ");
            double matchLat = Double.parseDouble(matchCoords[0]);
            double matchLon = Double.parseDouble(matchCoords[1]);

            //Finding user's location
            String[] coords = user.getString("location").split(" ");
            double userLat = Double.parseDouble(coords[0]);
            double userLon = Double.parseDouble(coords[1]);

            //Finding the midpoint
            double[] midpoint = findMidPoint(userLat, userLon, matchLat, matchLon);

            //Calculating the search radius to look for gyms in both user's areas
            String radius = "" + findRadius(userLat,userLon,midpoint[0],midpoint[1]);

            GoogleMapOptions options = new GoogleMapOptions();

            LatLng latLng = new LatLng(userLat, userLon);

            float cameraBearing = 0;
            float cameraTilt = 0;
            float cameraZoom = 15;
            CameraPosition cameraPosition = new CameraPosition(latLng,cameraZoom,cameraTilt,cameraBearing);

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            findGyms(midpoint, radius, googleMap);

        }
    }

    public double[] findMidPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        lat3 = Math.toDegrees(lat3);
        lon3 = Math.toDegrees(lon3);

        //print out in degrees
        System.out.println(Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
        return new double[]{lat3,lon3};
    }

}