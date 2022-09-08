package com.example.projectandroidmusicapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.adapter.artist.ArtistAdapter;
import com.example.projectandroidmusicapp.adapter.slide.SlideAdapter;
import com.example.projectandroidmusicapp.adapter.song.SongAdapter;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Song;
import com.example.projectandroidmusicapp.validator.SortMapByValue;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class SearchFragment extends Fragment {
    DatabaseReference mDatabaseRef;


    private EditText edKeySearch;
    private ImageButton ibSearch;
    private RecyclerView rvSearchSong;

    private SongAdapter songAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //begin
        rvSearchSong = rootView.findViewById(R.id.rvSearchSong);
        edKeySearch = rootView.findViewById(R.id.edKeySearch);
        //
        showSongByTitle("");

        //Button search
        ibSearch = rootView.findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get key title
                String keyTitle = edKeySearch.getText().toString().trim();
                //Get song in database
                showSongByTitle(keyTitle);

            }
        });
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvSearchSong.setLayoutManager(linearLayoutManager2);
        return rootView;
    }

    public void showSongByTitle(String keyTitle) {
        System.out.println(keyTitle);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_SONGS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Song> songs = new ArrayList<>();
                HashMap<String, Song> mapSongs = new HashMap<>();
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                            if (s.getValue(Song.class).getTitle().toLowerCase(Locale.ROOT).contains(keyTitle.toLowerCase(Locale.ROOT))) {
                                mapSongs.put(s.getKey(),s.getValue(Song.class));
                        }
                    }
                    LinkedHashMap<String,Song> sortedMap = SortMapByValue.sortHashMapByValues(mapSongs);
                    Set<String> str = sortedMap.keySet();
                    for (String s: str) {
                        songs.add(sortedMap.get(s));
                    }
                    songAdapter = new SongAdapter(getContext(), songs);
                    rvSearchSong.setAdapter(songAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

