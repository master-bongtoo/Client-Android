package com.example.bongtoo.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bongtoo.MainActivity;
import com.example.bongtoo.R;
import com.example.bongtoo.model.Help;
import com.example.bongtoo.model.Member;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HelpAdapter extends ArrayAdapter<Help> {
    Activity activity;
    int resource;


    public HelpAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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

        Help item = getItem(position);

        if (item != null) {
            TextView textViewHelpSubject = convertView.findViewById(R.id.textViewHelpSubject);
            final TextView textViewHelpContent = convertView.findViewById(R.id.textViewHelpContent);

            textViewHelpSubject.setText(item.getHelp_subject());
            textViewHelpContent.setText(item.getHelp_content());
            textViewHelpContent.setVisibility(View.GONE);

        }
        return convertView;
    }
}
