package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gymbuddies.databinding.ActivityLoginBinding;
import com.example.gymbuddies.databinding.ActivitySignUpBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    public static final String TAG = "SignUpActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        binding.btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String username = binding.etSignUpUsername.getText().toString();
                String password = binding.etSignUpPassword.getText().toString();
                String firstName = binding.etFirstName.getText().toString();
                String lastName = binding.etLastName.getText().toString();
                if(username.length() != 0 && password.length() != 0 && firstName.length()!=0 && lastName.length()!=0) {
                    signUpUser(username, password, firstName);
                } else {
                    Toast.makeText(SignUpActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUpUser(String username, String password, final String firstName) {

        // Create the ParseUser
        final ParseUser user = new ParseUser();



        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.put("screenName",firstName);
        user.put("gallery", new JSONArray());
        user.put("biography", "");
        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG,"Issue with login" + e.toString());
                    Toast.makeText(SignUpActivity.this, "Account already exists for this username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ActivityCompat.checkSelfPermission( SignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.i(TAG, "Permission denied");
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            updateLocation(location);
                        }
                    }
                });
                goLoginActivity(firstName);
                Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateLocation(Location location) {
        final ParseUser user = ParseUser.getCurrentUser();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        user.put("location", latitude+" "+longitude);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Toast.makeText(SignUpActivity.this, "Issue updating location", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, e.toString());
                }
                Log.i(TAG, user.getString("location"));
            }
        });
    }

    private void goLoginActivity(String firstName) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private File saveToFile() {
        String name = "profileImage";
        Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_person_24, getTheme());
        Bitmap bm = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);



        File filesDir = SignUpActivity.this.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }
}