package com.example.melodymusikserverside.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.melodymusikserverside.Models.Song;
import com.example.melodymusikserverside.R;

import java.util.HashMap;
import java.util.List;

public class SongAdapterAddAlbum extends RecyclerView.Adapter<SongAdapterAddAlbum.SongViewHolder> {
    private Context mContext;
    private List<Song> songList;

    public SongAdapterAddAlbum(Context mContext, List<Song> songList) {
        this.mContext = mContext;
        this.songList = songList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongAdapterAddAlbum.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapterAddAlbum.SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvSongTitleItem.setText(song.getTitle());
        Glide.with(mContext).load(song.getUrlImage()).into(holder.ivSongImageItem);
    }

    @Override
    public int getItemCount() {
        if (songList != null) {
            return songList.size();
        }
        return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivSongImageItem;
        private TextView tvSongTitleItem;
        private ImageButton btn_delete_song;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongImageItem = (ImageView) itemView.findViewById(R.id.ivSongImageItem);
            tvSongTitleItem = (TextView) itemView.findViewById(R.id.tvSongTitleItem);
            btn_delete_song = (ImageButton) itemView.findViewById(R.id.btn_delete_song);

            btn_delete_song.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext.getApplicationContext(), "Delete successfull", Toast.LENGTH_SHORT).show();
                    // songList.remove(getBindingAdapterPosition());
                    removeAt(getPosition());

                }
            });
        }

        public void removeAt(int position) {
            songList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, songList.size());
        }

    }

}