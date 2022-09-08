package com.example.projectandroidmusicapp.adapter.song;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.LoginFragment;
import com.example.projectandroidmusicapp.activities.PlayerFragment;
import com.example.projectandroidmusicapp.adapter.playlist.PlaylistToAddAdapter;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Context mContext;
    private List<Song> songs;


    public SongAdapter(Context mContext, List<Song> songs) {
        this.mContext = mContext;
        this.songs = songs;

    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongAdapter.SongViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        if (song == null) {
            return;
        }

        //Image
        Glide
                .with(mContext)
                .load(song.getUrlImage())
                .apply(new RequestOptions().override(60, 60))
                .centerCrop()
                .into(holder.ivSong);
        holder.tvTitleSong.setText(song.getTitle());

        //Artist name
        String textArtist = "";
        HashMap<String, Artist> mapArtists = song.getArtists();
        Set<String> artistIds = mapArtists.keySet();
        for (String str : artistIds) {
            textArtist = textArtist + (textArtist.trim().isEmpty() ? "" : ", ") + mapArtists.get(str).getName();
        }

        holder.tvArtistName.setText(textArtist);

        //Click song -> play song
        holder.layoutSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerFragment fragment = new PlayerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("song", song);
                bundle.putSerializable("songs", (Serializable) songs);
                if(fragment.getmMediaPlayer() != null){
                    fragment.getmMediaPlayer().pause();
                }
                fragment.setArguments(bundle);
                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentMain,fragment)
                        .commit();
            }

        });

        //Click imagebutton -> add to playlist
        holder.ibSongAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser() == null){
                    //Go to Login
                    LoginFragment fragment = new LoginFragment();
                    ((FragmentActivity) mContext).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null)
                            .commit();
                }
                else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            mContext, R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(mContext)
                            .inflate(R.layout.playlist_bottom_sheet_add_play_list_layout,
                                    holder.bottomSheetContainer);

                    rvListPlaylistToAdd = bottomSheetView.findViewById(R.id.rvListPlaylistToAdd);

                    //Set data to display
                    showPlaylists(bottomSheetDialog, song);
                    //Display HORIZONTAL
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext.getApplicationContext(), RecyclerView.VERTICAL, false);
                    rvListPlaylistToAdd.setLayoutManager(linearLayoutManager);


                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (songs != null) {
            return songs.size();
        }
        return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSong;
        private TextView tvTitleSong;
        private TextView tvArtistName;
        private RelativeLayout layoutSong;
        private ImageButton ibSongAddToPlaylist;
        private LinearLayout bottomSheetContainer;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleSong = itemView.findViewById(R.id.tvTitleSong);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivSong = itemView.findViewById(R.id.ivSong);
            layoutSong = itemView.findViewById(R.id.layoutSong);
            ibSongAddToPlaylist = itemView.findViewById(R.id.ibSongAddToPlaylist);
            bottomSheetContainer = itemView.findViewById(R.id.bottomSheetContainer);

        }

    }
    DatabaseReference mDatabaseRef;
    RecyclerView rvListPlaylistToAdd;
    PlaylistToAddAdapter playlistToAddAdapter;
    FirebaseAuth mAuth;

    private void showPlaylists(BottomSheetDialog bottomSheetDialog, Song song) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Playlist> playlists = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Playlist playlist = s.getValue(Playlist.class);
                        if (playlist.getUsers().get(mAuth.getCurrentUser().getUid()) != null) {
                            playlists.add(s.getValue(Playlist.class));
                        }
                    }
                    playlistToAddAdapter = new PlaylistToAddAdapter(mContext, playlists, song, bottomSheetDialog);
                    rvListPlaylistToAdd.setAdapter(playlistToAddAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
