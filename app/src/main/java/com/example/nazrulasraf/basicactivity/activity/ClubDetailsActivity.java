package com.example.nazrulasraf.basicactivity.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nazrulasraf.basicactivity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ClubDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvClubName, tvClubFaculty, tvClubDetails, tvClubAdminUserName;
    String userID, clubJoined;
    DatabaseReference clubRef, userRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_details);

        toolbar = findViewById(R.id.toolbarClubDetails);
        setSupportActionBar(toolbar);

        tvClubName = findViewById(R.id.tvClubName);
        tvClubFaculty = findViewById(R.id.tvClubFaculty);
        tvClubDetails = findViewById(R.id.tvClubDetails);
        tvClubAdminUserName = findViewById(R.id.tvClubAdminUserName);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        clubRef = FirebaseDatabase.getInstance().getReference().child("Club");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //Retrieve club joined by the user.
        userRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clubJoined = dataSnapshot.child("clubJoined").getValue(String.class);
//                Toast.makeText(ClubDetailsActivity.this, "clubJoined : " + clubJoined, Toast.LENGTH_SHORT).show();

                Query query = clubRef.orderByChild("clubName").equalTo(clubJoined);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String clubName = ds.child("clubName").getValue(String.class);
                            String clubFaculty = ds.child("clubFaculty").getValue(String.class);
                            String clubDetails = ds.child("clubDetails").getValue(String.class);
                            String clubAdmin = ds.child("clubAdminName").getValue(String.class);

                            tvClubName.setText(clubName);
                            tvClubFaculty.setText(clubFaculty);
                            tvClubDetails.setText(clubDetails);
                            tvClubAdminUserName.setText(clubAdmin);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        Toast.makeText(ClubDetailsActivity.this, "clubJoined : " + clubJoined, Toast.LENGTH_SHORT).show();
    }
}
