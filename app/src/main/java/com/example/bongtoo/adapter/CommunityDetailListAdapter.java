package com.example.bongtoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.bongtoo.MainActivity;
import com.example.bongtoo.R;
import com.example.bongtoo.model.Community;
import com.example.bongtoo.model.CommunityReply;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommunityDetailListAdapter extends ArrayAdapter<CommunityReply> {
    Activity activity;
    int resource;


    public interface OnItemClickListener {
        void onItemClick(View view, int position) ;
    }
    private OnItemClickListener mListener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }



    public CommunityDetailListAdapter(@NonNull Context context, int resource, @NonNull List<CommunityReply> objects) {
        super(context, resource, objects);
        activity = (Activity) context;
        this.resource = resource;
    }
    ImageView commdetalItem_ImageIcon;
    TextView commdetailItem_TxtNickName,commdetailItem_Like_Text,commdetailItem_Descripton;
    FrameLayout commdetalItem_Like;

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView ==null){
            convertView =activity.getLayoutInflater().inflate(resource,null);
        }
        final CommunityReply item = getItem(position);
        if (item != null){
//초기화
            commdetalItem_ImageIcon =convertView.findViewById(R.id.commdetalItem_ImageIcon);
            commdetailItem_TxtNickName = convertView.findViewById(R.id.commdetailItem_TxtNickName);
            commdetailItem_Like_Text = convertView.findViewById(R.id.commdetailItem_Like_Text);
            commdetailItem_Descripton = convertView.findViewById(R.id.commdetailItem_Descripton);
            commdetalItem_Like = convertView.findViewById(R.id.commdetalItem_Like);

            // 세팅
            commdetailItem_TxtNickName.setText(item.getNickname());
            commdetailItem_Descripton.setText(item.getReply_description());
            commdetailItem_Like_Text.setText(""+item.getReply_like());
            commdetalItem_Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, position);
                }
            });

            //등급별세팅
            switch (item.getGrad()) {
                case 1 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_bronze).into(commdetalItem_ImageIcon);
                    break;
                case 2 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_silver).into(commdetalItem_ImageIcon);
                    break;
                case 3 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_gold).into(commdetalItem_ImageIcon);
                    break;
                case 4 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_green).into(commdetalItem_ImageIcon);
                    break;
                case 5 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_blue).into(commdetalItem_ImageIcon);
                    break;
            }
        }
        return convertView;
    }
}