package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nazrulasraf.basicactivity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity {

    private String clubJoined;
    private EditText etPostTitle, etPostContent;
    private Button btnPost;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference dRef, mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etPostTitle = findViewById(R.id.etPostTitle);
        etPostContent = findViewById(R.id.etPostContent);
        btnPost = findViewById(R.id.btnPost);

        database = FirebaseDatabase.getInstance();
        dRef = database.getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String postTitle = etPostTitle.getText().toString().trim();
                final String postContent = etPostContent.getText().toString().trim();

                if (!TextUtils.isEmpty(postTitle) && !TextUtils.isEmpty(postTitle)) {
                    Toast.makeText(PostActivity.this, "POSTING...", Toast.LENGTH_LONG).show();
                    //Get club joined by the user
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            clubJoined = dataSnapshot.child("clubJoined").getValue().toString();

                            //To post
                            final DatabaseReference newPost = dRef.child(clubJoined).push();
                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    newPost.child("title").setValue(postTitle);
                                    newPost.child("content").setValue(postContent);
                                    newPost.child("uid").setValue(mCurrentUser.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("username").getValue()).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("PostActivity", "writePost:onCancelled()", databaseError.toException());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(PostActivity.this, "Please fill the fields above", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
