package com.example.gymbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gymbuddies.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding. etLoginUsername.getText().toString();
                String password = binding.etLoginPassword.getText().toString();
                loginUser(username, password);
            }
        });
        binding.tvStartSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUpActivity();
            }
        });
    }

    private void goSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);

    }

    private void loginUser(String username, String password){

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with Login");
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}