package com.example.projectandroidmusicapp.adapter.slide;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.PlayerFragment;
import com.example.projectandroidmusicapp.entity.Song;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class SlideAdapter extends SliderViewAdapter<SlideAdapter.Holder> {

    private Context mContext;
    private List<Song> songs;

    public SlideAdapter(Context mContext, List<Song> songs) {
        this.mContext = mContext;
        this.songs = songs;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.slider_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Song song = songs.get(position);
        Glide.with(mContext).load(song.getUrlBanner()).into(viewHolder.ivSlider);
        viewHolder.ivSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("song", song);
                bundle.putSerializable("songs", (Serializable) songs);

                PlayerFragment fragment = new PlayerFragment();
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
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder {
        ImageView ivSlider;

        public Holder(View itemView) {
            super(itemView);
            ivSlider = itemView.findViewById(R.id.ivSlider);


        }
    }

}




