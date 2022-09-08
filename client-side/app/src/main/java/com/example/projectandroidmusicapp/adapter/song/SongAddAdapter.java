package com.example.projectandroidmusicapp.adapter.song;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.PlayerFragment;
import com.example.projectandroidmusicapp.activities.playlist.SongsInPlaylistFragment;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SongAddAdapter extends RecyclerView.Adapter<SongAddAdapter.SongViewHolder> {
    private Context mContext;
    private List<Song> songs;
    private String keyPlaylist;
    private BottomSheetDialog bottomSheetDialog;


    public SongAddAdapter(Context mContext, List<Song> songs, String keyPlaylist, BottomSheetDialog bottomSheetDialog) {
        this.mContext = mContext;
        this.songs = songs;
        this.keyPlaylist = keyPlaylist;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public SongAddAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongAddAdapter.SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        String keySongId = song.getId();
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

       //Title
        holder.tvTitleSong.setText(song.getTitle());

        //Artist
        String textArtist = "";
        HashMap<String, Artist> mapArtists = song.getArtists();
        Set<String> artistIds = mapArtists.keySet();
        for (String str : artistIds) {
            textArtist = textArtist + (textArtist.trim().isEmpty() ? "" : ", ") + mapArtists.get(str).getName();
        }

        holder.tvArtistName.setText(textArtist);

        holder.layoutSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlaylist(keySongId, song);
                bottomSheetDialog.dismiss();
                //Go to this playlist
                SongsInPlaylistFragment fragment = new SongsInPlaylistFragment();
                Bundle bundle = new Bundle();
                bundle.putString("playlistId", keyPlaylist);
                fragment.setArguments(bundle);
                ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
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

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleSong = itemView.findViewById(R.id.tvTitleSong);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivSong = itemView.findViewById(R.id.ivSong);
            layoutSong = itemView.findViewById(R.id.layoutSong);
        }

    }

    private void updatePlaylist(String songId, Song newSong) {
        //updating artist
        FirebaseDatabase.getInstance().getReference().child("playlists").child(keyPlaylist)
                .child("songs").child(songId)
                .setValue(newSong).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Playlist Updated!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(mContext, "Update failed!", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}
