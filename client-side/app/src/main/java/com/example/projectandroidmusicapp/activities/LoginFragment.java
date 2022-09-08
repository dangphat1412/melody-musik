package com.example.projectandroidmusicapp.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.User;
import com.example.projectandroidmusicapp.validator.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginFragment extends Fragment {


    private TextView tvSignUp;
    private EditText edLoginEmail, edLoginPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_USERS);
        mAuth = FirebaseAuth.getInstance();

        //Email
        edLoginEmail = rootView.findViewById(R.id.edLoginEmail);

        edLoginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!Validator.isEmail(edLoginEmail.getText().toString())) {
                        edLoginEmail.setError("Not empty! Enter an email address!");
                    }
                }
            }
        });

        //Password
        edLoginPassword = rootView.findViewById(R.id.edLoginPassword);

        edLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (edLoginPassword.getText().toString().trim().isEmpty()) {
                        edLoginPassword.setError("Not empty!");
                    }

                }
            }
        });

        //Button login
        btnLogin = rootView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (!Validator.isEmail(edLoginEmail.getText().toString())) {
                    flag = false;
                    edLoginEmail.setError("Not empty! Enter an email address!");
                }
                if (edLoginPassword.getText().toString().trim().isEmpty()) {
                    flag = false;
                    edLoginPassword.setError("Not empty!");
                }
                if (flag) {
                    login();
                }
            }
        });

        //TextView if have not account -> Register
        tvSignUp = rootView.findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment fragment = new RegisterFragment();
                ((FragmentActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment).addToBackStack(null)
                        .commit();
            }
        });
        return rootView;
    }

    private void login() {
        String email = edLoginEmail.getText().toString().trim();
        String password = edLoginPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println(mAuth.getCurrentUser().getUid());
                    Toast.makeText(getContext(), "Login successfully!", Toast.LENGTH_SHORT).show();
                    //Go to home fragment
                    HomeFragment fragment = new HomeFragment();
                    ((FragmentActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null)
                            .commit();

                } else {
                    Toast.makeText(getContext(), "Invalid Username or Password!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}