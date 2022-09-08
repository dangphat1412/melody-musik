package com.example.projectandroidmusicapp.activities.playlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.LoginFragment;
import com.example.projectandroidmusicapp.adapter.playlist.PlaylistAdapter;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PlaylistFragment extends Fragment {

    private Button btnAddPlaylist;
    private RecyclerView rvListPlaylist;
    private PlaylistAdapter playlistAdapter;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        mAuth = FirebaseAuth.getInstance();

        //If didn't login
        if (mAuth.getCurrentUser() == null) {
            LoginFragment fragment = new LoginFragment();
            ((FragmentActivity)getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment).addToBackStack(null)
                    .commit();        }
        //Have already login
        else {
            rootView = inflater.inflate(R.layout.fragment_playlist, container, false);

            rvListPlaylist = rootView.findViewById(R.id.rvListPlaylist);

            //Set data to display
            showPlaylists();

            //Display HORIZONTAL
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            rvListPlaylist.setLayoutManager(linearLayoutManager);

            //btn Add
            btnAddPlaylist = rootView.findViewById(R.id.btnAddPlaylist);
            btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddPlaylistFragment fragment = new AddPlaylistFragment();
                    ((FragmentActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            });
        }
        return rootView;
    }

    DatabaseReference mDatabaseRef;
    private void showPlaylists() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Playlist> playlists = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Playlist playlist = s.getValue(Playlist.class);
                        if(playlist.getUsers().get(mAuth.getCurrentUser().getUid()) != null) {
                            playlists.add(s.getValue(Playlist.class));
                        }
                    }
                    playlistAdapter = new PlaylistAdapter(getContext(), playlists);
                    rvListPlaylist.setAdapter(playlistAdapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}