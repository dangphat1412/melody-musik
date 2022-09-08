package com.example.projectandroidmusicapp.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.adapter.song.SongAdapter;
import com.example.projectandroidmusicapp.entity.Album;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Category;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Song;
import com.example.projectandroidmusicapp.validator.SortMapByValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class SongsFragment extends Fragment {
    DatabaseReference mDatabaseRef;


    private RecyclerView rvListSong;
    private SongAdapter songAdapter;

    private ImageView ivListSongCover;
    private TextView tvTitleSongs;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        //Begin
        rvListSong = (RecyclerView) rootView.findViewById(R.id.rvListSong);
        ivListSongCover = rootView.findViewById(R.id.ivListSongCover);
        tvTitleSongs = rootView.findViewById(R.id.tvPlaylistName);

        String coverImage = "";
        String title = "";
        HashMap<String,Song> mapSongs;

        //get song from artist
        if ((Artist) getArguments().getSerializable("artist") != null ) {
            Artist artist = (Artist) getArguments().getSerializable("artist");

            getSongFromArtist(artist.getId());

            coverImage = artist.getUrlImage();
            title = artist.getName();

        }
        //get song from album
        if((Album) getArguments().getSerializable("album") != null){
            Album album = (Album) getArguments().getSerializable("album");
            mapSongs = album.getSongs();

            //Sort by time
            LinkedHashMap<String,Song> sortedMap = SortMapByValue.sortHashMapByValues(mapSongs);
            List<Song> songs = new ArrayList<>();
            Set<String> str = sortedMap.keySet();
            for (String s: str) {
                songs.add(sortedMap.get(s));
            }
            songAdapter = new SongAdapter(getContext() ,songs);
            rvListSong.setAdapter(songAdapter);

            coverImage = album.getUrlImage();
            title = album.getAlbumName();

        }

        //get Song from category
        if((List<Song>) getArguments().getSerializable("songs") != null
                && (Category) getArguments().getSerializable("category") != null  ) {
            List<Song> songs = (List<Song>) getArguments().getSerializable("songs");
            songAdapter = new SongAdapter(getContext(), songs);
            rvListSong.setAdapter(songAdapter);

            Category category = (Category) getArguments().getSerializable("category");
            coverImage = category.getUrlImage();
            title = category.getName();
        }

        //Display HORIZONTAL
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvListSong.setLayoutManager(linearLayoutManager);

        //Image Cover
        Artist artist = (Artist) getArguments().getSerializable("artist");
        Glide
                .with(getContext())
                .load(coverImage)
                .apply(new RequestOptions().override(400, 150))
                .centerCrop()
                .into(ivListSongCover);
        //Title
        tvTitleSongs.setText(title);

        return rootView;
    }

    private void getSongFromArtist(String keyArtist){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_SONGS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    HashMap<String, Song> mapSongs = new HashMap<>();
                    HashMap<String, Artist> mapArtists = new HashMap<>();
                    List<Song> songs = new ArrayList<>();

                    for (DataSnapshot s : snapshot.getChildren()) {
                        mapArtists = s.getValue(Song.class).getArtists();
                        Set<String> keyArtists = mapArtists.keySet();
                        for (String str : keyArtists){
                            if(keyArtist.equals(str)){
                                mapSongs.put(s.getKey(),s.getValue(Song.class));
                            }
                        }
                    }

                    //Sort song  by time
                    LinkedHashMap<String,Song> sortedMap = SortMapByValue.sortHashMapByValues(mapSongs);
                    Set<String> str = sortedMap.keySet();
                    for (String s: str) {
                        songs.add(sortedMap.get(s));
                    }
                    songAdapter = new SongAdapter(getContext(), songs);
                    rvListSong.setAdapter(songAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}