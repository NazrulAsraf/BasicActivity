package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.nazrulasraf.basicactivity.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

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
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}
