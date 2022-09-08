package com.example.projectandroidmusicapp.adapter.category;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectandroidmusicapp.R;
import com.example.projectandroidmusicapp.activities.SongsFragment;
import com.example.projectandroidmusicapp.entity.Category;
import com.example.projectandroidmusicapp.entity.Song;
import com.example.projectandroidmusicapp.validator.SortMapByValue;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    DatabaseReference mDatabaseRef;

    private Context mContext;
    private List<Category> categories;

    public CategoryAdapter(Context mContext, List<Category> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        if (category == null) {
            return;
        }

        //Title category
        holder.tvTitleCategory.setText(category.getName());

        //Image category
        Glide.with(mContext).load(category.getUrlImage()).into(holder.ivCategory);

        //Click category -> list songs of category
        holder.categoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseRef = FirebaseDatabase.getInstance().getReference();
                Query query = mDatabaseRef.child("songs").orderByChild("category").equalTo(category.getName());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Song> songs = new ArrayList<>();
                        HashMap<String, Song> mapSongs = new HashMap<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot s : snapshot.getChildren()) {
                                mapSongs.put(s.getKey(),s.getValue(Song.class));
                            }

                            //Sort by time
                            LinkedHashMap<String,Song> sortedMap = SortMapByValue.sortHashMapByValues(mapSongs);
                            Set<String> str = sortedMap.keySet();
                            for (String s: str) {
                                songs.add(sortedMap.get(s));
                            }
                        }

                        //
                        Bundle bundle = new Bundle();
                        SongsFragment fragment = new SongsFragment();
                        bundle.putSerializable("songs", (Serializable) songs);
                        bundle.putSerializable("category", (Serializable) category);

                        //
                        fragment.setArguments(bundle);
                        ((FragmentActivity)mContext).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,fragment).addToBackStack(null)
                                .commit();
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
        if (categories != null) {
            return categories.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout categoryItem;
        private TextView tvTitleCategory;
        private ShapeableImageView ivCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleCategory = itemView.findViewById(R.id.tvTitleCategory);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            categoryItem = itemView.findViewById(R.id.categoryItem);
        }
    }
}
