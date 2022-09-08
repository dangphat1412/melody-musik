package com.example.projectandroidmusicapp.adapter.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PlaylistToAddAdapter extends RecyclerView.Adapter<PlaylistToAddAdapter.PlaylistViewHolder> {
    private Context mContext;
    private List<Playlist> playlists;
    private Song song;
    private BottomSheetDialog bottomSheetDialog;


    public PlaylistToAddAdapter(Context mContext, List<Playlist> playlists, Song song, BottomSheetDialog bottomSheetDialog){
        this.mContext = mContext;
        this.playlists = playlists;
        this.song = song;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_to_add_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        if (playlist == null) {
            return;
        }

        if (playlist.getSongs() != null) {
            HashMap<String, Song> mapSongs = playlist.getSongs();
            Set<String> songIds = mapSongs.keySet();
            for (String str : songIds) {
                Glide
                        .with(mContext)
                        .load(mapSongs.get(str).getUrlImage())
                        .apply(new RequestOptions().override(50, 50))
                        .centerCrop()
                        .into(holder.ivPlaylistToAdd);
                break;
            }
        } else {
            Glide
                    .with(mContext)
                    .load(R.drawable.playlistdefault)
                    .apply(new RequestOptions().override(50, 50))
                    .centerCrop()
                    .into(holder.ivPlaylistToAdd);
        }
        holder.tvTitlePlaylistToAdd.setText(playlist.getName());
        holder.playlistToAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistId = playlist.getId();
                FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_SONGS)
                        .orderByKey().equalTo(song.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                Song addedSong = s.getValue(Song.class);
                                FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.DATABASE_PATH_PLAYLISTS)
                                        .child(playlistId).child("songs").child(addedSong.getId()).setValue(addedSong);
                                Toast.makeText(mContext, "Add done!", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (playlists != null) {
            return playlists.size();
        }
        return 0;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout playlistToAddItem;
        private TextView tvTitlePlaylistToAdd;
        private ImageView ivPlaylistToAdd;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePlaylistToAdd = itemView.findViewById(R.id.tvTitlePlaylistToAdd);
            playlistToAddItem = itemView.findViewById(R.id.playlistToAddItem);
            ivPlaylistToAdd = itemView.findViewById(R.id.ivPlaylistToAdd);

        }
    }
}