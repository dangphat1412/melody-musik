package com.example.projectandroidmusicapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.adapter.album.AlbumAdapter;
import com.example.projectandroidmusicapp.adapter.artist.ArtistAdapter;
import com.example.projectandroidmusicapp.entity.Album;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AlbumFragment extends Fragment {
    DatabaseReference mDatabaseRef;

    private List<Album> albums;
    private AlbumAdapter albumAdapter;
    private RecyclerView rvListAlbum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_album, container, false);

        //recycle view album
        rvListAlbum = rootView.findViewById(R.id.rvListAlbum);

        showAlbums();

        //Display HORIZONTAL
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        rvListAlbum.setLayoutManager(mLayoutManager);

        return rootView;
    }

    private void showAlbums() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_ALBUMS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    albums = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Album album = s.getValue(Album.class);
                        albums.add(album);
                    }
                    albumAdapter = new AlbumAdapter(getContext(), albums);
                    rvListAlbum.setAdapter(albumAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
