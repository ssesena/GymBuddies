package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gymbuddies.databinding.ActivityLoginBinding;
import com.example.gymbuddies.databinding.ActivitySignUpBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


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
                goLoginActivity(firstName);
                Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_LONG).show();
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