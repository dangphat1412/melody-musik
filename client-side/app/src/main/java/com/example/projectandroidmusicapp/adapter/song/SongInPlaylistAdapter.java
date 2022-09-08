package com.example.projectandroidmusicapp.adapter.song;

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
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.PlayerFragment;
import com.example.projectandroidmusicapp.activities.playlist.SongsInPlaylistFragment;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.SongInPlaylistViewHolder> {
    private Context mContext;
    private List<Song> songs;
    private String keyPlaylist;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public SongInPlaylistAdapter(Context mContext, List<Song> songs, String keyPlaylist) {
        this.mContext = mContext;
        this.songs = songs;
        this.keyPlaylist = keyPlaylist;
    }

    @NonNull
    @Override
    public SongInPlaylistAdapter.SongInPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_in_playlist_item, parent, false);
        return new SongInPlaylistAdapter.SongInPlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongInPlaylistViewHolder holder, int position) {
        Song song = songs.get(position);
        String keySongId = song.getId();
        if (song == null) {
            return;
        }
        Glide
                .with(mContext)
                .load(song.getUrlImage())
                .apply(new RequestOptions().override(60, 60))
                .centerCrop()
                .into(holder.ivSongInPlaylist);
        holder.tvTitleSongInPlaylist.setText(song.getTitle());

        //set text artist
        String textArtist = "";
        HashMap<String, Artist> mapArtists = song.getArtists();
        Set<String> artistIds = mapArtists.keySet();
        for (String str : artistIds) {
            textArtist = textArtist + (textArtist.trim().isEmpty() ? "" : ", ") + mapArtists.get(str).getName();
        }

        holder.tvArtistNameSongInPlaylist.setText(textArtist);

        holder.songInPlaylistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerFragment fragment = new PlayerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("song", song);
                bundle.putSerializable("songs", (Serializable) songs);
                //Click again -> change song
                if (fragment.getmMediaPlayer() != null) {
                    fragment.getmMediaPlayer().pause();
                }
                fragment.setArguments(bundle);
                ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentMain, fragment)
                        .commit();
            }

        });

        //Swipe to delete
        viewBinderHelper.bind(holder.swipeRevealLayout, song.getTitle());
        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songs.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getPosition());
                //Delete in Database
                FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_PLAYLISTS).child(keyPlaylist)
                        .child("songs").child(keySongId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Delete successfully!", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("playlistId", keyPlaylist);
                            SongsInPlaylistFragment fragment = new SongsInPlaylistFragment();
                            fragment.setArguments(bundle);
                            ((FragmentActivity) mContext).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, fragment).addToBackStack(null)
                                    .commit();
                        }
                    }
                });
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

    public class SongInPlaylistViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSongInPlaylist;
        private TextView tvTitleSongInPlaylist;
        private TextView tvArtistNameSongInPlaylist;
        private LinearLayout songInPlaylistItem;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutDelete;

        public SongInPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleSongInPlaylist = itemView.findViewById(R.id.tvTitleSongInPlaylist);
            tvArtistNameSongInPlaylist = itemView.findViewById(R.id.tvArtistNameSongInPlaylist);
            ivSongInPlaylist = itemView.findViewById(R.id.ivSongInPlaylist);
            songInPlaylistItem = itemView.findViewById(R.id.songInPlaylistItem);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
        }

    }
}
