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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bongtoo.adapter.HelpAdapter;
import com.example.bongtoo.model.Help;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class HelpListFragment extends Fragment implements AbsListView.OnScrollListener {

    List<Help> help_list;
    ListView helpListView;
    HelpAdapter helpAdapter;
    boolean[] isOpens;
    AsyncHttpClient client;
    HttpResponse response;
    int event_type = 2;
    MainActivity activity;
    String helpURL;
    Help help;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_help_list, container, false);
        helpURL = "http://"+activity.SERVERIP+"/bongtoo_server/event/eventListJson.a";
// 도움말 리스트 객체 초기화(프래그먼트 리스트로 보여주기)
        help_list = new ArrayList<>();
        helpListView = rootView.findViewById(R.id.helpListView);
        helpListView.setOnScrollListener(HelpListFragment.this);
        helpAdapter = new HelpAdapter(getActivity(), R.layout.list_item_help_list, help_list);
        helpListView.setAdapter(helpAdapter);
        client = new AsyncHttpClient();
        response = new HttpResponse(activity);

        helpAdapter.clear();
        RequestParams params = new RequestParams();
        params.put("pg", PAGE);
        params.put("event_type", event_type);
        client.post(helpURL, params, response);



        helpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout help_contents = (LinearLayout) view.findViewById(R.id.help_contents);
                TextView textViewHelpContent = view.findViewById(R.id.textViewHelpContent);
                if(isOpens[position]){
                    textViewHelpContent.setVisibility(View.GONE);
                    isOpens[position]=false;
                } else {
                    textViewHelpContent.setVisibility(View.VISIBLE);
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

    int PAGE = 1;
    int totalAll = 0;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        pageablePage = totalAll / 10 + 1;
        RequestParams params = new RequestParams();

        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                PAGE++;
                params.put("pg",PAGE);
                params.put("event_type",event_type);
                client.post(helpURL, params, response);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    // 통신 응답 클래스
    class HttpResponse extends AsyncHttpResponseHandler {
        ProgressDialog dialog;
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
                        help = new Help();
                        help.setHelp_subject(temp.getString("event_subject1"));
                        help.setHelp_content(temp.getString("event_content"));
                        helpAdapter.add(help);
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
}