package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nazrulasraf.basicactivity.R;
import com.example.nazrulasraf.basicactivity.fragment.DialogConfirmPassword;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView imageProf;
    private Uri uri = null;
    private String userID, imageUri;
    private Toolbar toolbar;
    private boolean isChanged = false;
    private MaterialButton btnEditProf;
    private Bitmap compressedImageFile;
    private TextInputEditText editTextUsername, editTextFullName, editTextClass;

    private StorageReference storage, pathRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFireStore;
    private DatabaseReference dRef;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = findViewById(R.id.toolbarEditProf);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseFireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        pathRef = storage.child("profile_images").child(userID + ".jpg");
        firebaseDatabase = FirebaseDatabase.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users");

        imageProf = findViewById(R.id.editProfImage);
        btnEditProf = findViewById(R.id.btnEditSave);
        editTextUsername = findViewById(R.id.editTextProfUsername);
        editTextFullName = findViewById(R.id.editTextProfFullName);
        editTextClass = findViewById(R.id.editTextProfClass);

        //Get current user profile image
        firebaseFireStore.collection("Users").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        String image = task.getResult().getString("image");

                        uri = Uri.parse(image);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.baseline_account_circle_black_24);

                        Glide.with(EditProfileActivity.this)
                                .setDefaultRequestOptions(placeholderRequest)
                                .load(image).into(imageProf);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(EditProfileActivity.this,
                            "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }
            }
        });

        //Select Image from gallery
        imageProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(EditProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(EditProfileActivity.this, "Permission is denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });

        btnEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    if (isChanged) {
                        userID = mAuth.getCurrentUser().getUid();
                        updateUserProfile();

                        File newImageFile = new File(uri.getPath());
                        try {

                            compressedImageFile = new id.zelory.compressor.Compressor(EditProfileActivity.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final UploadTask image_path = storage.child("profile_images").child(userID + ".jpg").putFile(uri);

                        Task<Uri> urlTask = image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return pathRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Toast.makeText(EditProfileActivity.this, "Successfully Uploaded!", Toast.LENGTH_LONG).show();
                                    if (downloadUri != null) {
                                        imageUri = downloadUri.toString();

                                        storeFireStore(userID, imageUri);
                                    }
                                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(EditProfileActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        userID = mAuth.getCurrentUser().getUid();
                        updateUserProfile();
                        startActivity(new Intent(EditProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }
            }
        });
    }

    private void storeFireStore(String uid, String uri) {

        Map<String, String> userMap = new HashMap<>();
        userMap.put("user_id", uid);
        userMap.put("image", uri);

        firebaseFireStore.collection("Users").document(uid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Edit Profile Activity", "Picture stored to FireStore");
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(EditProfileActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uri = result.getUri();
                imageProf.setImageURI(uri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    private void updateUserProfile() {
        String username = editTextUsername.getText().toString();
        String fullname = editTextFullName.getText().toString();
        String userclass = editTextClass.getText().toString();

        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (fullname.isEmpty()) {
            editTextFullName.setError("Full Name is required");
            editTextFullName.requestFocus();
            return;
        }

        if (userclass.isEmpty()) {
            editTextClass.setError("Class is required");
            editTextClass.requestFocus();
            return;
        }

        DatabaseReference current_user_db = dRef.child(userID);
        current_user_db.child("username").setValue(username);
        current_user_db.child("fullname").setValue(fullname);
        current_user_db.child("class").setValue(userclass);
    }
}
