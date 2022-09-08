package com.example.projectandroidmusicapp.adapter.playlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.playlist.SongsInPlaylistFragment;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Playlist;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context mContext;
    private List<Playlist> playlists;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public PlaylistAdapter(Context mContext, List<Playlist> playlists) {
        this.mContext = mContext;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        if (playlist == null) {
            return;
        }

        Bundle bundle = new Bundle();

        //Set image of playlist: random
        if (playlist.getSongs() != null) {
            HashMap<String, Song> mapSongs = playlist.getSongs();
            List<String> keysAsArray = new ArrayList<String>(mapSongs.keySet());
            Random r = new Random();
            mapSongs.get(keysAsArray.get(r.nextInt(keysAsArray.size())));
            Glide.with(mContext)
                    .load( mapSongs.get(keysAsArray.get(r.nextInt(keysAsArray.size())))
                            .getUrlImage()).into(holder.ivPlaylist);
            bundle.putString("imageCover"
                    , mapSongs.get(keysAsArray.get(r.nextInt(keysAsArray.size()))).getUrlImage());
        } else {
            Glide.with(mContext).load(R.drawable.playlistdefault).into(holder.ivPlaylist);
        }

        //Title
        holder.tvTitlePlaylist.setText(playlist.getName());

        //Click playlist -> list songs of playlist
        holder.playlistItemLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("playlistId", playlist.getId());
                SongsInPlaylistFragment fragment = new SongsInPlaylistFragment();
                fragment.setArguments(bundle);
                ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment).addToBackStack(null)
                        .commit();
            }
        });

        //Swipe to delete
        viewBinderHelper.bind(holder.playlistItem, playlist.getName());
        holder.deleteArtistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlists.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getPosition());
                //Delete in Database
                FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS)
                        .child(playlist.getId())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Delete successfully!", Toast.LENGTH_SHORT).show();
                        }
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
        private TextView tvTitlePlaylist;
        private ImageView ivPlaylist;
        private SwipeRevealLayout playlistItem;
        private LinearLayout deleteArtistLayout;
        private LinearLayout playlistItemLeft;


        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePlaylist = itemView.findViewById(R.id.tvTitlePlaylist);
            ivPlaylist = itemView.findViewById(R.id.ivPlaylist);
            playlistItemLeft = itemView.findViewById(R.id.playlistItemLeft);
            deleteArtistLayout = itemView.findViewById(R.id.deleteArtistLayout);
            playlistItem = itemView.findViewById(R.id.playlistItem);


        }
    }
}

