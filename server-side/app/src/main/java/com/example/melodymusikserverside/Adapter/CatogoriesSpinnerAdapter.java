package com.example.melodymusikserverside.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.melodymusikserverside.Models.Catogories;
import com.example.melodymusikserverside.R;

import java.util.List;

public class CatogoriesSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Catogories> catogoriesList;

    public CatogoriesSpinnerAdapter(Context context, List<Catogories> catogoriesList) {
        this.context = context;
        this.catogoriesList = catogoriesList;
    }

    @Override
    public int getCount() {
        return catogoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return catogoriesList.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_catogories_item;
        TextView tv_catogories_name_item;

        View rootView = LayoutInflater.from(context).
                inflate(R.layout.catogories_item_spinner_layout, parent, false);

        iv_catogories_item = rootView.findViewById(R.id.iv_catogories_item);
        tv_catogories_name_item = rootView.findViewById(R.id.tv_catogories_name_item);

        tv_catogories_name_item.setText(catogoriesList.get(position).getName());
        Glide.with(context).load(catogoriesList.get(position).getUrlImage()).into(iv_catogories_item);

        return rootView;
    }


}
