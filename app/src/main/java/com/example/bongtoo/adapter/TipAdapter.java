package com.example.bongtoo.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.bongtoo.R;

import java.util.List;

public class TipAdapter extends PagerAdapter {
    Activity activity;
    List<Integer> list;

    public TipAdapter(Activity activity, List<Integer> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = activity.getLayoutInflater().inflate(R.layout.list_item_bongtoo_tip, null);

        ImageView imageView = itemView.findViewById(R.id.tip_ImageIcon);
        imageView.setImageResource(list.get(position));
        container.addView(itemView, 0);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}