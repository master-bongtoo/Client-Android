package com.example.bongtoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.bongtoo.R;
import com.example.bongtoo.model.Shop;

import java.util.List;

public class ShopListAdapter extends ArrayAdapter {
    Activity activity;
    int resource;
    public ShopListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        activity = (Activity)context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = activity.getLayoutInflater().inflate(resource, null);
        }

        Shop item = (Shop) getItem(position);

        if(item != null) {
            ImageView shop_image = convertView.findViewById(R.id.shop_image);
            TextView shop_name = convertView.findViewById(R.id.shop_name);
            TextView shop_score = convertView.findViewById(R.id.shop_score);
            TextView shop_description = convertView.findViewById(R.id.shop_description);

            Glide.with(activity).load(item.getShop_img_path()).into(shop_image); //롸 수정: item.getShop_url() => item.getShop_img_path()
            shop_name.setText(item.getShop_name());
            shop_score.setText(item.getShop_grade()); //롸 수정: item.getScore() => item.getShop_grade()
            shop_description.setText(item.getShop_detail());
        }

        return convertView;
    }
}
