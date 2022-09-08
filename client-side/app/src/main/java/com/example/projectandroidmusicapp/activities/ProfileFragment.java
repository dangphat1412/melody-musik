package com.example.projectandroidmusicapp.activities;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.playlist.PlaylistFragment;
import com.example.projectandroidmusicapp.adapter.category.CategoryAdapter;
import com.example.projectandroidmusicapp.entity.Category;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private Button btnMyPlaylist;
    private ImageButton ibSignOut;
    private TextView tvUsername;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;

        mAuth = FirebaseAuth.getInstance();

        //if didn't login
        if (mAuth.getCurrentUser() == null) {
            //Go to Login
            LoginFragment fragment = new LoginFragment();
            ((FragmentActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment).addToBackStack(null)
                    .commit();
        }
        //Have already login
        else {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            tvUsername = rootView.findViewById(R.id.tvUsername);
            getUser();
            //View playlist
            btnMyPlaylist = rootView.findViewById(R.id.btnMyPlaylist);
            btnMyPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    PlaylistFragment fragment = new PlaylistFragment();
                    fragment.setArguments(bundle);
                    ((FragmentActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            });

            //Sign out
            ibSignOut = rootView.findViewById(R.id.ibSignOut);
            ibSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    //Go to Login
                    LoginFragment fragment = new LoginFragment();
                    ((FragmentActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null)
                            .commit();

                }
            });
        }
        return rootView;
    }

    DatabaseReference mDatabaseRef;
    User user;

    private void getUser() {
        user = new User();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_USERS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        if (s.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            user = s.getValue(User.class);
                        }
                    }
                    tvUsername.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
