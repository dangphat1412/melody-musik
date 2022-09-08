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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class RegisterFragment extends Fragment {
    private EditText edRegisterUsername, edRegisterEmail, edRegisterPassword, edRePassword;
    private TextView tvAlreadyHaveAccount;
    private Button btnRegister;
    private List<User> users;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        //begin

        //Get users
        users = getUserlList();

        //Username
        edRegisterUsername = rootView.findViewById(R.id.edRegisterUsername);
        edRegisterUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !Validator.isUsername(edRegisterUsername.getText().toString())) {
                        edRegisterUsername.setError("Not empty and only text! Enter again!");
                    }
                }
        });

        //Email
        edRegisterEmail = rootView.findViewById(R.id.edRegisterEmail);
        edRegisterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!Validator.isEmail(edRegisterEmail.getText().toString())) {
                        edRegisterEmail.setError("Wrong format! Enter again!");
                    } else {
                        if (!Validator.isExistedEmail(edRegisterEmail.getText().toString(), users)) {
                            edRegisterEmail.setError("Email has already registed!");
                        }
                    }
                }
            }
        });

        //Password
        edRegisterPassword = rootView.findViewById(R.id.edRegisterPassword);
        edRegisterPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !Validator.isPassword(edRegisterPassword.getText().toString())) {
                        edRegisterPassword.setError("Not empty and at least 8 characters! Enter again!");
                    }
                }
        });

        //Repassword
        edRePassword = rootView.findViewById(R.id.edRePassword);
        edRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (edRePassword.getText().toString().trim().isEmpty()) {
                        edRePassword.setError("Not Empty!");
                    } else {
                        if (!edRegisterPassword.getText().toString().equals(edRePassword.getText().toString())) {
                            edRePassword.setError("Not the same to Password! Enter again!");
                        }
                    }
                }
            }
        });

        //Button Register
        btnRegister = rootView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (!Validator.isUsername(edRegisterUsername.getText().toString())) {
                    flag = false;
                    edRegisterUsername.setError("Not empty and only text! Enter again!");
                }
                if (!Validator.isEmail(edRegisterEmail.getText().toString())) {
                    flag = false;
                    edRegisterEmail.setError("Wrong format! Enter again!");
                } else {
                    if (!Validator.isExistedEmail(edRegisterEmail.getText().toString(), users)) {
                        flag = false;
                        edRegisterEmail.setError("Email has already registed!");
                    }
                }
                if (!Validator.isPassword(edRegisterPassword.getText().toString())) {
                    flag = false;
                    edRegisterPassword.setError("Not empty and at least 8 characters! Enter again!");
                }
                if (edRePassword.getText().toString().trim().isEmpty()) {
                    flag = false;
                    edRePassword.setError("Not Empty!");
                } else {
                    if (!edRegisterPassword.getText().toString().equals(edRePassword.getText().toString())) {
                        flag = false;
                        edRePassword.setError("Not the same to Password! Enter again!");
                    }
                }
                if (flag) {
                    register();
                }

            }
        });

        //If have already had account -> Login
        tvAlreadyHaveAccount = rootView.findViewById(R.id.tvAlreadyHaveAccount);
        tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment fragment = new LoginFragment();
                ((FragmentActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment).addToBackStack(null)
                        .commit();
            }
        });
        return rootView;
    }

    //get all users
    private List<User> getUserlList() {
        List<User> list = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_USERS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        User user = s.getValue(User.class);
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }

    DatabaseReference mDatabase;
    StorageReference storageReference;

    private void register() {
        String username = edRegisterUsername.getText().toString().trim();
        String email = edRegisterEmail.getText().toString().trim();
        String password = edRePassword.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User user = new User(uid, username, email, password, "");

                    FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_USERS)
                            .child(uid)
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Register successfully!", Toast.LENGTH_SHORT).show();
                                HomeFragment fragment = new HomeFragment();
                                ((FragmentActivity) getContext()).getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, fragment).addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(getActivity(), "Register failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}