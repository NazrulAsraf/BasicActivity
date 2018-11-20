package com.example.nazrulasraf.basicactivity.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nazrulasraf.basicactivity.R;
import com.example.nazrulasraf.basicactivity.fragment.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView imageProf;
    private Uri uri = null;
    private String userID, imageUri;
    private boolean isChanged = false;
    private MaterialButton btnEditProf;
    private Bitmap compressedImageFile;
    private TextInputEditText editTextUsername, editTextPassword, editTextConfPassword;

    private StorageReference storage, pathRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFireStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        firebaseFireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        pathRef = storage.child("profile_images").child(userID + ".jpg");

        imageProf = findViewById(R.id.editProfImage);
        btnEditProf = findViewById(R.id.btnEditSave);
        editTextUsername = findViewById(R.id.editTextProfUsername);
        editTextPassword = findViewById(R.id.editTextProfPassword);
        editTextConfPassword = findViewById(R.id.editTextProfConfPassword);


        firebaseFireStore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        String image = task.getResult().getString("image");
                        if (uri != null) {
                            uri = Uri.parse(image);
                        }

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.baseline_account_circle_black_48);

                        Glide.with(EditProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(imageProf);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(EditProfileActivity.this, "(FIRESTORE Retrieve Errror) : " + error, Toast.LENGTH_LONG).show();
                }
            }
        });

        btnEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    if (isChanged) {
                        userID = mAuth.getCurrentUser().getUid();

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

                                        storeFirestore(userID, imageUri);
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(EditProfileActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        storeFirestore(imageUri, userID);
                    }
                }
            }
        });

        imageProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(EditProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(EditProfileActivity.this, "Permission is denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });
    }

    private void storeFirestore(String uid, String uri){

        Map<String, String> userMap = new HashMap<>();
        userMap.put("user_id", uid);
        userMap.put("image", uri);

        firebaseFireStore.collection("Users").document(uid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "The Profile is updated.", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
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
}