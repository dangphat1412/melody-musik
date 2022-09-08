package com.example.projectandroidmusicapp.activities.playlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.LoginFragment;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddPlaylistFragment extends Fragment {
    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<User> users;

    private EditText edPlaylistName;
    private Button btnSaveAddPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        View rootView = null;

        if (mAuth.getCurrentUser() == null) {
            LoginFragment fragment = new LoginFragment();
            ((FragmentActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment).addToBackStack(null)
                    .commit();
        } else {
            rootView = inflater.inflate(R.layout.fragment_add_playlist, container, false);

            edPlaylistName = rootView.findViewById(R.id.edPlaylistName);

            //Get user to add playlist
            getCurentUser();

            //Button Save to Database
            btnSaveAddPlaylist = rootView.findViewById(R.id.btnSaveAddPlaylist);
            btnSaveAddPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String playlistName = edPlaylistName.getText().toString().trim();
                    if (playlistName.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a playlist name!", Toast.LENGTH_LONG).show();
                    } else {
                        addNewPlaylist();
                    }
                }
            });
        }
        return rootView;
    }


    private void addNewPlaylist() {
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_PLAYLISTS);
        String id = mDatabase.push().getKey();
        HashMap<String, User> mapUsers = new HashMap<>();
        mapUsers.put(mAuth.getCurrentUser().getUid(), users.get(0));
        Playlist newPlaylist = new Playlist(id, edPlaylistName.getText().toString().trim(), null, mapUsers);
        mDatabase.child(id).setValue(newPlaylist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Add successfully!", Toast.LENGTH_LONG).show();
                    ((FragmentActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new PlaylistFragment()).addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Add failed!!!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void getCurentUser() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_USERS);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        if (s.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            users.add(s.getValue((User.class)));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}