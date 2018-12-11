package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nazrulasraf.basicactivity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextUsername, editTextConfPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        MaterialButton btnToLogIn = findViewById(R.id.btnToLogIn);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextConfPassword = findViewById(R.id.editTextConfPassword);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users");

        btnToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent toLogin = new Intent(SignActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confPassword = editTextConfPassword.getText().toString().trim();

        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
        }

        if(!(password.equals(confPassword))){
            editTextConfPassword.setError("Password do not match");
            editTextConfPassword.requestFocus();
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = dRef.child(user_id);
                    current_user_db.child("username").setValue(username);
                    current_user_db.child("fullname").setValue("User Full Name");
                    current_user_db.child("class").setValue("User Class");
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(SignActivity.this, MainActivity.class));
                }
                else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
