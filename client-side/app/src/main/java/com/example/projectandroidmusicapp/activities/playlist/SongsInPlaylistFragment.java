package com.example.projectandroidmusicapp.activities.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.LoginFragment;
import com.example.projectandroidmusicapp.adapter.song.SongAddAdapter;
import com.example.projectandroidmusicapp.adapter.song.SongInPlaylistAdapter;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class SongsInPlaylistFragment extends Fragment {
    DatabaseReference mDatabaseRef;

    private FirebaseAuth mAuth;
    private RecyclerView rvListSongInPlaylist;
    private SongAddAdapter songAddAdapter;
    private SongInPlaylistAdapter songInPlaylistAdapter;
    private TextView tvPlaylistName;
    private Button btnAddSongToPlaylist;
    private ImageView ivPlaylistCover;
    private String keyPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs_in_playlist, container, false);
        mAuth = FirebaseAuth.getInstance();
        //if login
        if (mAuth.getCurrentUser() == null) {
            LoginFragment fragment = new LoginFragment();
            ((FragmentActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment).addToBackStack(null)
                    .commit();
        }
        //if didn't login
        else {
            //Begin
            rvListSongInPlaylist = rootView.findViewById(R.id.rvListSongInPlaylist);
            tvPlaylistName = rootView.findViewById(R.id.tvPlaylistName);
            ivPlaylistCover = rootView.findViewById(R.id.ivPlaylistCover);

            //Get key playlist
            if ((String) getArguments().getSerializable("playlistId") != null) {
                keyPlaylist = (String) getArguments().getSerializable("playlistId");
            }

            //Display songs of playlist
            getSongFromPlaylist(keyPlaylist, rootView);
            //Display HORIZONTAL
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            rvListSongInPlaylist.setLayoutManager(linearLayoutManager);

            btnAddSongToPlaylist = rootView.findViewById(R.id.btnAddSongToPlaylist);
            btnAddSongToPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(rootView);
                }
            });

        }
        return rootView;

    }
    private void getSongFromPlaylist(String keyPlaylist, View rootView) {
        List<Song> songsInPlaylist = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS).child(keyPlaylist).child("songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot s : snapshot.getChildren()) {
                        songsInPlaylist.add(s.getValue(Song.class));
                    }
                    songInPlaylistAdapter = new SongInPlaylistAdapter(getContext(), songsInPlaylist, keyPlaylist);

                    rvListSongInPlaylist.setAdapter(songInPlaylistAdapter);
                    if(songsInPlaylist.size() != 0){
                        Random r = new Random();
                        Glide.with(rootView.getContext()).load(songsInPlaylist.get(r.nextInt(songsInPlaylist.size())).getUrlImage()).into(ivPlaylistCover);
                    }
                    else{
                        Glide.with(rootView.getContext()).load(R.drawable.playlistdefault).into(ivPlaylistCover);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    RecyclerView rvSongBottomSheet;

    private void showBottomSheetDialog(View rootView) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_choose_song,
                        (LinearLayout) rootView.findViewById(R.id.bottomSheetSongContainer));

        EditText mSearchField;
        ImageButton mSearchBtn;

        mSearchField = bottomSheetView.findViewById(R.id.search_field);
        mSearchBtn = bottomSheetView.findViewById(R.id.search_btn);

        rvSongBottomSheet = bottomSheetView.findViewById(R.id.rvSongBottomSheet);
        rvSongBottomSheet.setLayoutManager(new LinearLayoutManager(getContext()));

        //set data to recycle view
        showSongByTitle("", bottomSheetDialog);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvSongBottomSheet.setLayoutManager(linearLayoutManager);
        rvSongBottomSheet.addItemDecoration(new DividerItemDecoration(rvSongBottomSheet.getContext(), DividerItemDecoration.VERTICAL));
        ViewGroup.LayoutParams params = rvSongBottomSheet.getLayoutParams();
        params.height = 2000;
        rvSongBottomSheet.setLayoutParams(params);


        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyTittle = mSearchField.getText().toString().trim();
                showSongByTitle(keyTittle, bottomSheetDialog);
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    public void showSongByTitle(String keyTitle, BottomSheetDialog bottomSheetDialog) {
        //Get playlist
        List<String> songsInPlaylist = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS);
        Query query = mDatabaseRef.orderByKey().equalTo(keyPlaylist);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        tvPlaylistName.setText(s.getValue(Playlist.class).getName());
                        HashMap<String, Song> mapSongs = s.getValue(Playlist.class).getSongs();
                        if (mapSongs != null) {
                            Set<String> strSongIds = mapSongs.keySet();
                            for (String str : strSongIds) {
                                songsInPlaylist.add(str);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //get all song
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_SONGS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Song> songs = new ArrayList<>();
                HashMap<String, Song> mapSongs = new HashMap<>();
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        if (!songsInPlaylist.contains(s.getKey())) {
                            if (s.getValue(Song.class).getTitle().toLowerCase(Locale.ROOT).contains(keyTitle.toLowerCase(Locale.ROOT))) {
                                songs.add(s.getValue(Song.class));
                                mapSongs.put(s.getKey(),s.getValue(Song.class));
                            }
                        }
                    }
                    songAddAdapter = new SongAddAdapter(getContext(), songs, keyPlaylist, bottomSheetDialog);
                    rvSongBottomSheet.setAdapter(songAddAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}