package com.example.nazrulasraf.basicactivity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.nazrulasraf.basicactivity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddClubActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    TextInputEditText editTextClubName, editTextClubDetails, editTextClubFaculty;
    MaterialButton btnSave;

    FirebaseDatabase database;
    DatabaseReference dRef, mDatabaseUser;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        toolbar = findViewById(R.id.toolbarCreateClub);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        dRef = database.getReference().child("Club");

        editTextClubName = findViewById(R.id.editTextClubName);
        editTextClubDetails = findViewById(R.id.editTextClubDetails);
        editTextClubFaculty = findViewById(R.id.editTextClubFaculty);

        btnSave = findViewById(R.id.btnSaveClub);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String clubName = editTextClubName.getText().toString();
                final String clubDetails = editTextClubDetails.getText().toString();
                final String clubFaculty = editTextClubFaculty.getText().toString();

                checkFields(clubName, clubDetails, clubFaculty);

                final DatabaseReference newClub = dRef.push();
                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Toast.makeText(AddClubActivity.this, "Saving...", Toast.LENGTH_LONG).show();
                        newClub.child("clubName").setValue(clubName);
                        newClub.child("clubDetails").setValue(clubDetails);
                        newClub.child("clubFaculty").setValue(clubFaculty);
                        newClub.child("clubAdminUid").setValue(mCurrentUser.getUid());
                        newClub.child("clubAdminName").setValue(dataSnapshot.child("username").getValue()).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            startActivity(new Intent(AddClubActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void checkFields(String clubName, String clubDetails, String clubFaculty) {
        if (clubName.isEmpty()) {
            editTextClubName.setError("Club Name is required");
            editTextClubName.requestFocus();
            return;
        }
        if (clubFaculty.isEmpty()) {
            editTextClubFaculty.setError("Club Faculty is required");
            editTextClubFaculty.requestFocus();
            return;
        }
        if (clubDetails.isEmpty()) {
            editTextClubDetails.setError("Club Details is required");
            editTextClubDetails.requestFocus();
        }
    }
}
