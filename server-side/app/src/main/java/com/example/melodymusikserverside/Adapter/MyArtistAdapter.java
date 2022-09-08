package com.example.melodymusikserverside.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.melodymusikserverside.Models.Artist;
import com.example.melodymusikserverside.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyArtistAdapter extends FirebaseRecyclerAdapter<Artist, MyArtistAdapter.MyViewHolder> {
    public MyArtistAdapter(@NonNull FirebaseRecyclerOptions<Artist> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Artist model) {
        holder.tvAritstNameItem.setText(model.getName());
        Glide.with(holder.ivArtistImageItem.getContext()).load(model.getUrlImage()).into(holder.ivArtistImageItem);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_bottom_sheet_item, parent, false);
        return new MyViewHolder(view);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivArtistImageItem;
        TextView tvAritstNameItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArtistImageItem = (CircleImageView) itemView.findViewById(R.id.ivArtistImageItem);
            tvAritstNameItem = (TextView) itemView.findViewById(R.id.tvAritstNameItem);
        }
    }

    @NonNull
    @Override
    public Artist getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public DatabaseReference getRef(int position) {
        return super.getRef(position);
    }
}