package com.example.projectandroidmusicapp.adapter.artist;

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
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.SongsFragment;
import com.example.projectandroidmusicapp.entity.Artist;

import java.util.HashMap;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private Context mContext;
    private List<Artist> artists;

    public ArtistAdapter(Context mContext, List<Artist> artists) {
        this.mContext = mContext;
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    private Bundle bundle;

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        if (artist == null) {
            return;
        }

        //Title
        holder.tvTitleArtist.setText(artist.getName());

        //Image
        Glide.with(mContext).load(artist.getUrlImage()).into(holder.ivArtist);

        //Click artist -> list song of artist
        holder.artistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle = new Bundle();
                bundle.putSerializable("artist", artist);

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
        if (artists != null) {
            return artists.size();
        }
        return 0;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout artistItem;
        private TextView tvTitleArtist;
        private ImageView ivArtist;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitleArtist = itemView.findViewById(R.id.tvTitleArtist);
            ivArtist = itemView.findViewById(R.id.ivArtist);
            artistItem = itemView.findViewById(R.id.artistItem);
        }
    }


}
