package com.example.nazrulasraf.basicactivity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.nazrulasraf.basicactivity.R;
import com.example.nazrulasraf.basicactivity.fragment.DialogConfirmPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditAccountActivity extends AppCompatActivity implements DialogConfirmPassword.OnDialogConfirmPassword {

    Toolbar toolbar;
    TextInputEditText editTextEmail, editTextPassword, editTextConfPassword;
    MaterialButton btnSaveAcc;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        toolbar = findViewById(R.id.toolbarEditAcc);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        editTextEmail = findViewById(R.id.editTextAccEmail);
        editTextPassword = findViewById(R.id.editTextAccPassword);
        editTextConfPassword = findViewById(R.id.editTextAccConfPassword);
        btnSaveAcc = findViewById(R.id.btnAccSave);

        btnSaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmpty();

                DialogConfirmPassword dialogConfirmPassword = DialogConfirmPassword.newInstance("Enter Password");
                dialogConfirmPassword.show(getSupportFragmentManager(), "dialog_conf_password");
                dialogConfirmPassword.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            }
        });
    }

    public void checkEmpty() {

        String email = editTextEmail.getText().toString();
        final String newPassword = editTextPassword.getText().toString();
        String confPassword = editTextConfPassword.getText().toString();

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

        if (newPassword.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
        }

        if (!newPassword.equals(confPassword)) {
            editTextConfPassword.setError("Password do not match");
            editTextConfPassword.requestFocus();
        }
    }

    public void onDialogConfirmPassword(final String password) {

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        final String email = editTextEmail.getText().toString();
        final String newPassword = editTextPassword.getText().toString();

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("EditAccountActivity", "Email Updated");
                            }
                        }
                    });

                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("EditAccountActivity", "Password Updated");

                                FirebaseAuth.getInstance().signOut();

                                Toast.makeText(EditAccountActivity.this,
                                        "User Details Updated! Please Login Again.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(EditAccountActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                finish();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(EditAccountActivity.this,
                                        "Error " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditAccountActivity.this,
                            "Password is Wrong! Please Re-enter your password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
