package com.example.bongtoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bongtoo.R;
import com.example.bongtoo.model.FAQ;

import java.util.List;

public class FAQListAdapter extends ArrayAdapter<FAQ> {
    Activity activity;
    int resource;

    public FAQListAdapter(@NonNull Context context, int resource, @NonNull List<FAQ> objects) {
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

        FAQ item = getItem(position);

        if (item != null) {
            TextView faq_list_txtSubject = convertView.findViewById(R.id.faq_list_txtSubject);
            TextView textViewFAQContent = convertView.findViewById(R.id.textViewFAQContent);
            faq_list_txtSubject.setText(item.getFaqSubject());
            textViewFAQContent.setText(item.getFaqContent());
            textViewFAQContent.setVisibility(View.GONE);
        }

        return convertView;
    }
}
