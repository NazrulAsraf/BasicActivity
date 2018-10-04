package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.nazrulasraf.basicactivity.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent noUser = new Intent(this, LoginActivity.class);
            startActivity(noUser);
            finish();
        }
        else {
            Intent loggedUser = new Intent(this, MainActivity.class);
            startActivity(loggedUser);
            finish();
        }

    }
}
