package com.example.nazrulasraf.basicactivity.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nazrulasraf.basicactivity.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogConfirmPassword extends DialogFragment {

    //Interface Listener

    public DialogConfirmPassword() {
        //Empty constructors
    }

    //Listener reference
    OnDialogConfirmPassword monDialogConfirmPassword;

    TextInputEditText etPassword;
    MaterialButton btConfirm, btCancel;

    public static DialogConfirmPassword newInstance(String title) {
        DialogConfirmPassword frag = new DialogConfirmPassword();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public interface OnDialogConfirmPassword {
        void onDialogConfirmPassword(String password);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_conf_password, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPassword = view.findViewById(R.id.editTextDialogPass);
        btConfirm = view.findViewById(R.id.btConfirm);
        btCancel = view.findViewById(R.id.btCancel);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();

                if (!password.equals("")) {
                    monDialogConfirmPassword.onDialogConfirmPassword(password);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String title = getArguments().getString("title", "Enter Password");
        getDialog().setTitle("Enter Password");

        etPassword.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            monDialogConfirmPassword = (OnDialogConfirmPassword) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement EditNameDialogListener");
        }
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        window.setLayout((int) (size.x*0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER
        );
        super.onResume();
    }
}
