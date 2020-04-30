package com.example.bongtoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.bongtoo.R;
import com.example.bongtoo.model.Community;
import com.example.bongtoo.model.Member;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class CommunityListAdapter extends ArrayAdapter<Community> {
    Activity activity;
    int resource;
    public CommunityListAdapter(@NonNull Context context, int resource, @NonNull List<Community> objects) {
        super(context, resource, objects);
        activity = (Activity) context;
        this.resource = resource;
    }
    ImageView commListItem_image, commListItem_ImageIcon, commListItem_video;
    TextView commListItem_TxtTitle, commListItem_TxtDescripton, commListItem_TxtLogtime, commListItem_TxtNickName, commListItem_txtLike;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView ==null){
            convertView =activity.getLayoutInflater().inflate(resource,null);
        }
        Community item = getItem(position);
        if (item != null){
            //초기화
            commListItem_txtLike = convertView.findViewById(R.id.commListItem_txtLike);
             commListItem_image =convertView.findViewById(R.id.commListItem_image);
             commListItem_ImageIcon = convertView.findViewById(R.id.commListItem_ImageIcon);
             commListItem_TxtTitle = convertView.findViewById(R.id.commListItem_TxtTitle);
             commListItem_TxtDescripton = convertView.findViewById(R.id.commListItem_TxtDescripton);
             commListItem_TxtLogtime = convertView.findViewById(R.id.commListItem_TxtLogtime);
             commListItem_TxtNickName = convertView.findViewById(R.id.commListItem_TxtNickName);
            commListItem_video = convertView.findViewById(R.id.commListItem_video);


            //이미지 아이콘 세팅
            if(item.getBoard_img_path().equals("")){
                commListItem_image.setVisibility(View.GONE);
            } else {
                commListItem_image.setVisibility(View.VISIBLE);
                Glide.with(convertView).load(R.drawable.icon_image).into(commListItem_image);
            }

            //비디오  아이콘  세팅
            if (item.getBoard_video_path() == null || item.getBoard_video_path().equals("")){
                commListItem_video.setVisibility(View.GONE);
            }else {
                commListItem_video.setVisibility(View.VISIBLE);
                Glide.with(convertView).load(R.drawable.icon_video).into(commListItem_video);
            }



            //데이터 세팅
            commListItem_TxtTitle.setText(item.getBoard_category());
            commListItem_TxtDescripton.setText(item.getBoard_title());
            commListItem_TxtLogtime.setText(item.getBoard_firstdate());
            commListItem_txtLike.setText(""+item.getBoard_like());
            commListItem_TxtNickName.setText(item.getNickname());

            //등급별 이미지 세팅
            switch (item.getGrade()) {
                case 1 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_bronze).into(commListItem_ImageIcon);
                    break;
                case 2 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_silver).into(commListItem_ImageIcon);
                    break;
                case 3 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_gold).into(commListItem_ImageIcon);
                    break;
                case 4 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_green).into(commListItem_ImageIcon);
                    break;
                case 5 :
                    Glide.with(activity).load(R.drawable.icon_crownmain_blue).into(commListItem_ImageIcon);
                    break;
            }

        }
        return convertView;
    }
}
