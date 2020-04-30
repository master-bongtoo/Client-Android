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
import com.example.bongtoo.model.MyBongToo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MyBongTooAdapter extends ArrayAdapter<MyBongToo> {

    Activity activity;
    int resource;

    public MyBongTooAdapter(@NonNull Context context, int resource, @NonNull List<MyBongToo> objects) {
        super(context, resource, objects);
        activity = (Activity) context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(resource, null);
        }

        MyBongToo item = getItem(position);

        if (item != null) {
            ImageView imageView = convertView.findViewById(R.id.imageView);
            TextView textViewName = convertView.findViewById(R.id.textViewName);
            TextView textViewGroup = convertView.findViewById(R.id.textViewGroup);
            TextView textViewPlace = convertView.findViewById(R.id.textViewPlace);
            TextView textViewMoney = convertView.findViewById(R.id.textViewMoney);
            TextView textViewDate = convertView.findViewById(R.id.textViewDate);

            Glide.with(activity).load(item.getImageURL()).placeholder(R.drawable.icon_loco_s).into(imageView);
            textViewName.setText(item.getName());
            textViewGroup.setText(item.getGroup());
            textViewPlace.setText(item.getPlace());
            textViewDate.setText(item.getDate());
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            String str_total_money = numberFormat.format(item.getMoney());
            textViewMoney.setText("￦ " + str_total_money);
        }

        return convertView;
    }
}