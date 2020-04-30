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
import android.widget.ListView;
import android.widget.Toast;

import com.example.bongtoo.adapter.EventAdapter;
import com.example.bongtoo.adapter.NoticeListAdapter;
import com.example.bongtoo.model.Event;
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
public class MoreEventFragment extends Fragment implements AbsListView.OnScrollListener {
    //리스트 객체 선언
    EventAdapter adapter;
    List<Event> list;
    ListView event_listView;
    boolean[] isOpens;
    AsyncHttpClient client;
    HttpResponse response;
    MainActivity activity;
    String EVENTURL;
    int event_type =4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_more_event,container,false);
        EVENTURL= "http://"+activity.SERVERIP+"/bongtoo_server/event/eventListJson.a";
        //리스트 처리
        list = new ArrayList<>();
        event_listView = rootView.findViewById(R.id.event_listView);
        event_listView.setOnScrollListener(MoreEventFragment.this);
        adapter = new EventAdapter(getActivity(), R.layout.list_item_more_event, list);
        event_listView.setAdapter(adapter);
        client = new AsyncHttpClient();
        response = new HttpResponse(activity);
        RequestParams params = new RequestParams();
        params.put("pg", PAGE);
        params.put("event_type", event_type);
        client.post(EVENTURL, params, response);
        event_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FrameLayout event_contents = (FrameLayout) view.findViewById(R.id.event_contents);
                if(isOpens[position]){
                    event_contents.setVisibility(View.GONE);
                    isOpens[position]=false;
                } else {
                    event_contents.setVisibility(View.VISIBLE);
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
                        Event event = new Event();
                        event.setEvent_subject1(temp.getString("event_subject1"));
                        event.setEvent_content(temp.getString("event_content"));
                        event.setEvent_subject2(temp.getString("event_subject2"));
                        if(!temp.getString("event_date").equals("")) {
                            event.setEvent_date(temp.getString("event_date"));
                        }
                        if(!temp.getString("event_img_path").equals("")) {
                            event.setEvent_img_path(temp.getString("event_img_pa" +
                                    "th"));
                        }
                        event.setIsnew(temp.getInt("isnew"));

                        adapter.add(event);
                    }
//컨텐츠 리스트 오픈여부 초기화
                    isOpens = new boolean[total];
                    for(int y=0; y<total; y++) {
                        isOpens[y] = false;
                    }
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

    int totalAll = 0;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;
    int PAGE = 1;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        pageablePage = totalAll / 10 + 1;
        RequestParams params = new RequestParams();
        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                PAGE++;
                params.put("pg", PAGE);
                params.put("event_type",event_type);
                client.post(EVENTURL,params,response);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

}