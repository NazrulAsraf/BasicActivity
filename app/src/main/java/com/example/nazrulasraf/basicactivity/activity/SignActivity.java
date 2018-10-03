package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nazrulasraf.basicactivity.R;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnToLogIn = findViewById(R.id.btnToLogIn);

        btnToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLogin = new Intent(SignActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });
    }
}
