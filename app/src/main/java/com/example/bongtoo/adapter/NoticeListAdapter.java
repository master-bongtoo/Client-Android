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

import com.example.bongtoo.R;
import com.example.bongtoo.model.Notice;

import java.util.List;

public class NoticeListAdapter extends ArrayAdapter<Notice> {
    Activity activity;
    int resource;

    public NoticeListAdapter(@NonNull Context context, int resource, @NonNull List<Notice> objects) {
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

        Notice item = getItem(position);

        if (item != null) {
            TextView notice_item_txtSubject = convertView.findViewById(R.id.notice_item_txtSubject);
            TextView notice_item_txtContent = convertView.findViewById(R.id.notice_item_txtContent);
            TextView notice_item_txtDate = convertView.findViewById(R.id.notice_item_txtDate);

            notice_item_txtSubject.setText(item.getNoticeSubject());
            notice_item_txtContent.setText(item.getNoticeContent());
            notice_item_txtContent.setVisibility(View.GONE);
            notice_item_txtDate.setText(item.getNoticeDate());
        }

        return convertView;
    }
}
