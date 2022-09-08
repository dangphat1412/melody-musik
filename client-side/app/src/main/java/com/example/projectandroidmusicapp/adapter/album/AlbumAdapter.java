package com.example.projectandroidmusicapp.adapter.album;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.SongsFragment;
import com.example.projectandroidmusicapp.entity.Album;

import java.io.Serializable;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private Context mContext;
    private List<Album> albums;

    public AlbumAdapter(Context mContext, List<Album> list){
        this.mContext = mContext;
        this.albums = list;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        if (album == null) {
            return;
        }
        //Image
        Glide.with(mContext)
                .load(album.getUrlImage())
                .apply(new RequestOptions().override(140, 140))
                .centerCrop()
                .into(holder.ivAlbum);
         //Title
        holder.tvTitleAlbum.setText(album.getAlbumName());

        //Click album -> list album
        holder.albumItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("album", (Serializable) album);
                SongsFragment fragment = new SongsFragment();
                fragment.setArguments(bundle);
                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,fragment).addToBackStack(null)
                        .commit();


            }
        });
    }

    @Override
    public int getItemCount() {
        if (albums != null) {
            return albums.size();
        }
        return 0;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout albumItem;
        private TextView tvTitleAlbum;
        private ImageView ivAlbum;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitleAlbum = itemView.findViewById(R.id.tvTitleALbum);
            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            albumItem = itemView.findViewById(R.id.albumItem);
        }
    }
}

