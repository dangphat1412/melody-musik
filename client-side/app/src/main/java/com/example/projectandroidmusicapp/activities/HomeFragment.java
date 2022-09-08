package com.example.projectandroidmusicapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.adapter.artist.ArtistAdapter;
import com.example.projectandroidmusicapp.adapter.category.CategoryAdapter;
import com.example.projectandroidmusicapp.adapter.slide.SlideAdapter;
import com.example.projectandroidmusicapp.entity.Artist;
import com.example.projectandroidmusicapp.entity.Category;
import com.example.projectandroidmusicapp.entity.Constants;
import com.example.projectandroidmusicapp.entity.Song;
import com.example.projectandroidmusicapp.validator.SortMapByValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    DatabaseReference mDatabaseRef;

    //slider
    private SlideAdapter sliderAdapter;
    private SliderView sliderView;

    //Artist
    private ArtistAdapter artistAdapter;
    private RecyclerView rvSinger;

    //Category
    private RecyclerView rvCategory;
    private CategoryAdapter categoryAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Slider
        sliderView = (SliderView) rootView.findViewById(R.id.imageSlider);

        showSongBanner();

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        //Categories
        rvCategory = rootView.findViewById(R.id.rvCategory);

        showCategories();

        //Artist
        rvSinger = (RecyclerView) rootView.findViewById(R.id.rvSinger);

        showArtists();
        //Display HORIZONTAL
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rvSinger.setLayoutManager(linearLayoutManager2);

        return rootView;
    }

    private void showSongBanner() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_SONGS);
        Query query = mDatabaseRef.orderByChild("name").limitToLast(5);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Song> songs = new ArrayList<>();
                    HashMap<String, Song> mapSongs = new HashMap<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        mapSongs.put(s.getKey(),s.getValue(Song.class));
                    }
                    LinkedHashMap<String,Song> sortedMap = SortMapByValue.sortHashMapByValues(mapSongs);
                    Set<String> str = sortedMap.keySet();
                    for (String s: str) {
                        songs.add(sortedMap.get(s));
                    }
                    sliderAdapter = new SlideAdapter(getContext(), songs);
                    sliderView.setSliderAdapter(sliderAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showArtists() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_ARTISTS);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Artist> artists = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        artists.add(s.getValue(Artist.class));
                    }
                    artistAdapter = new ArtistAdapter(getContext(), artists);
                    rvSinger.setAdapter(artistAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showCategories() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_CATEGORIES);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        categories.add(s.getValue(Category.class));
                    }
                    categoryAdapter = new CategoryAdapter(getContext(), categories);
                    GridLayoutManager grid = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                    rvCategory.setLayoutManager(grid);
                    rvCategory.setAdapter(categoryAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}