package com.example.melodymusikserverside.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.melodymusikserverside.Models.Artist;
import com.example.melodymusikserverside.Models.Constants;
import com.example.melodymusikserverside.Models.Song;
import com.example.melodymusikserverside.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Map;

public class MySongAdapter extends FirebaseRecyclerAdapter<Song, MySongAdapter.MyViewHolder> {

    public MySongAdapter(@NonNull FirebaseRecyclerOptions<Song> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Song model) {
        holder.tvSongTitleItem.setText(model.getTitle());

        HashMap<String, Artist> artistList = model.getArtists();
        System.out.println("----------");

        String artistName = holder.tvAritstNameItem.getText().toString();
        for(Map.Entry a : artistList.entrySet()) {

            Artist value = (Artist) a.getValue();
            System.out.println("----------");

            artistName += (artistName.isEmpty() ? "" : ", ") + value.getName();
        }
        holder.tvAritstNameItem.setText(artistName);
        Glide
                .with(holder.ivSongImageItem.getContext())
                .load(model.getUrlImage())
                .apply(new RequestOptions().override(60, 60))
                .centerCrop()
                .into(holder.ivSongImageItem);
        System.out.println(model.getTitle());
        System.out.println(artistName);
        System.out.println("----------");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_bottom_sheet_item, parent, false);
        return new MySongAdapter.MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSongImageItem;
        TextView tvSongTitleItem;
        TextView tvAritstNameItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongImageItem = (ImageView) itemView.findViewById(R.id.ivSongImageItem);
            tvSongTitleItem = (TextView) itemView.findViewById(R.id.tvSongTitleItem);
            tvAritstNameItem = (TextView) itemView.findViewById(R.id.tvAritstNameItem);
        }
    }

    @NonNull
    @Override
    public Song getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public DatabaseReference getRef(int position) {
        return super.getRef(position);
    }
}