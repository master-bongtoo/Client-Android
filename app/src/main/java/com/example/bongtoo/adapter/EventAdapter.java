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
import com.example.bongtoo.model.Event;
import com.example.bongtoo.model.Notice;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
    Activity activity;
    int resource;

    public EventAdapter(@NonNull Context context, int resource, @NonNull List<Event> objects) {
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

        Event item = getItem(position);

        if (item != null) {
            TextView event_new = convertView.findViewById(R.id.event_new);
            TextView event_subject1 = convertView.findViewById(R.id.event_subject1);
            TextView event_subject2 = convertView.findViewById(R.id.event_subject2);
            TextView event_content = convertView.findViewById(R.id.event_content);
            TextView event_date = convertView.findViewById(R.id.event_date);

            ImageView event_thumbnail = convertView.findViewById(R.id.event_thumbnail);
            Glide.with(activity).load(item.getEvent_img_path()).into(event_thumbnail);
            ImageView eventImg = convertView.findViewById(R.id.eventImg);
            Glide.with(activity).load(item.getEvent_img_path()).into(eventImg);
//new 버튼 표시
            if(item.getIsnew()==0){
                event_new.setVisibility(View.VISIBLE);
            } else {
                event_new.setVisibility(View.GONE);
            }
//썸네일, 이벤트이미지 표시
            if(item.getEvent_img_path()==null){
                event_thumbnail.setVisibility(View.GONE);
                eventImg.setVisibility(View.GONE);
            } else {
                event_thumbnail.setVisibility(View.VISIBLE);
                eventImg.setVisibility(View.VISIBLE);
            }
//텍스트 표시
            event_subject1.setText(item.getEvent_subject1());
            event_subject2.setText(item.getEvent_subject2());
            event_content.setText(item.getEvent_content());
            event_date.setText(item.getEvent_date());
        }

        return convertView;
    }
}