package com.example.bongtoo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bongtoo.adapter.NoticeListAdapter;
import com.example.bongtoo.model.FAQ;
import com.example.bongtoo.model.Notice;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyPageNoticeFragment extends Fragment implements AbsListView.OnScrollListener {
    //객체선언
    FrameLayout servicecenter_button1_gongsi,servicecenter_button2_ildaeil,servicecenter_button3_faq;
    //리스트 객체 선언
    Notice notice;
    NoticeListAdapter adapter;
    List<Notice> list;
    ListView notice_listView;
    AsyncHttpClient client;
    HttpResponse response;
    boolean[] isOpens;
    MainActivity activity;
    String noticeURL;
    int event_type =1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_page_notice, container,false);
        noticeURL = "http://"+activity.SERVERIP+"/bongtoo_server/event/eventListJson.a";
//객체초기화
        servicecenter_button1_gongsi = rootView.findViewById(R.id.servicecenter_button1_gongsi);
        servicecenter_button2_ildaeil = rootView.findViewById(R.id.servicecenter_button2_ildaeil);
        servicecenter_button3_faq = rootView.findViewById(R.id.servicecenter_button3_faq);
//이벤트처리
        servicecenter_button1_gongsi.setOnClickListener(servicecenterEvent);
        servicecenter_button2_ildaeil.setOnClickListener(servicecenterEvent);
        servicecenter_button3_faq.setOnClickListener(servicecenterEvent);

        list = new ArrayList<>();
        notice_listView = rootView.findViewById(R.id.notice_listView);
        notice_listView.setOnScrollListener(MyPageNoticeFragment.this); // 0401 박성용
        adapter = new NoticeListAdapter(getActivity(), R.layout.list_item_notice, list);
        notice_listView.setAdapter(adapter);
        client = new AsyncHttpClient();
        response = new MyPageNoticeFragment.HttpResponse(activity);
        RequestParams params = new RequestParams();
        params.put("event_type",event_type);
        client.post(noticeURL, params, response);


        notice_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout notis_contents = (LinearLayout) view.findViewById(R.id.notis_contents);
                TextView notice_item_txtContent = view.findViewById(R.id.notice_item_txtContent);
                if(isOpens[position]){
                    notice_item_txtContent.setVisibility(View.GONE);
                    isOpens[position]=false;
                } else {
                    notice_item_txtContent.setVisibility(View.VISIBLE);
                    isOpens[position]=true;
                }
            }
        });


        return rootView;
    }



    //////////////////////////////////////////////////////////////////////////////////////
// 화면 기본 세팅
//////////////////////////////////////////////////////////////////////////////////////

    ViewGroup rootView;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    FrameLayout.OnClickListener servicecenterEvent = new ImageView.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.servicecenter_button1_gongsi:
                    activity.setFragment(activity.MYPAGE_NOTICE);
                    break;
                case R.id.servicecenter_button2_ildaeil:
                    activity.setFragment(activity.MYPAGE_QUESTION);
                    break;

                case R.id.servicecenter_button3_faq:
                    activity.setFragment(activity.MYPAGE_FAQ);
                    break;

            }
        }
    };

    // 0401 박성용~
    // 공지사항 추가 페이지 불러오기
    int totalAll = 0;
    int PAGE = 1;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        pageablePage = totalAll / 10 + 1;
        RequestParams params = new RequestParams();
        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                PAGE++;
                params.put("pg", PAGE);
                params.put("event_type", event_type);
                client.post(noticeURL, params, response);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }
    // ~0401 박성용

    // 통신 응답 클래스
    class HttpResponse extends AsyncHttpResponseHandler {

        Activity activity;

        public HttpResponse(Activity activity) {
            this.activity = activity;
        }

        // 통신 성공시 호출
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                totalAll = json.getInt("totalAll");
                if(rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        notice = new Notice(); // 0401 박성용
                        notice.setNoticeSubject(temp.getString("event_subject1"));
                        notice.setNoticeContent(temp.getString("event_content"));
                        notice.setNoticeDate(temp.getString("event_date"));
                        notice.setTotal(total);
                        adapter.add(notice);
                    }
                }
//컨텐츠 리스트 오픈여부 초기화
                isOpens = new boolean[total];
                for(int y=0; y< total; y++) {
                    isOpens[y] = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
        }
    }
}