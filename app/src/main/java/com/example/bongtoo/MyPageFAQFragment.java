package com.example.bongtoo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.PrecomputedText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bongtoo.adapter.FAQListAdapter;
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


public class MyPageFAQFragment extends Fragment implements AbsListView.OnScrollListener {
    //객체선언
    SearchView searchview;
    FrameLayout servicecenter_button1_gongsi,servicecenter_button2_ildaeil,servicecenter_button3_faq,faq_button1,faq_button2,faq_button3,faq_button4;
    TextView faq_1,faq_2,faq_3,faq_4;
    //리스트 객체 선언
    FAQ faq;
    FAQListAdapter faqAdapter;
    List<FAQ> list;
    ListView faq_listView;
    AsyncHttpClient client;
    HttpResponse response;
    SearchResponse searchResponse;
    boolean[] isOpens;
    MainActivity activity;
    String FAQURL;
    int event_type =3;
//String keyword = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_my_page_faq,container,false);
        FAQURL = "http://"+activity.SERVERIP+"/bongtoo_server/event/eventListJson.a";
        servicecenter_button1_gongsi = rootView.findViewById(R.id.servicecenter_button1_gongsi);
        servicecenter_button2_ildaeil = rootView.findViewById(R.id.servicecenter_button2_ildaeil);
        servicecenter_button3_faq = rootView.findViewById(R.id.servicecenter_button3_faq);
        faq_button1 =rootView.findViewById(R.id.faq_button1);
        faq_button2 =rootView.findViewById(R.id.faq_button2);
        faq_button3 =rootView.findViewById(R.id.faq_button3);
        faq_button4 =rootView.findViewById(R.id.faq_button4);
        faq_1=rootView.findViewById(R.id.faq_1);
        faq_2=rootView.findViewById(R.id.faq_2);
        faq_3=rootView.findViewById(R.id.faq_3);
        faq_4=rootView.findViewById(R.id.faq_4);
        searchview =rootView.findViewById(R.id.searchview);
//이벤트처리
        servicecenter_button1_gongsi.setOnClickListener(servicecenterEvent);
        servicecenter_button2_ildaeil.setOnClickListener(servicecenterEvent);
        servicecenter_button3_faq.setOnClickListener(servicecenterEvent);
        faq_button1.setOnClickListener(faqCategory);
        faq_button2.setOnClickListener(faqCategory);
        faq_button3.setOnClickListener(faqCategory);
        faq_button4.setOnClickListener(faqCategory);

//리스트 처리
        list = new ArrayList<>();
        faq_listView = rootView.findViewById(R.id.faq_listView);
        faq_listView.setOnScrollListener(MyPageFAQFragment.this);
        faqAdapter = new FAQListAdapter(getActivity(), R.layout.list_item_faq, list);
        faq_listView.setAdapter(faqAdapter);
        client = new AsyncHttpClient();
        response = new HttpResponse(activity);
        searchResponse =new SearchResponse(activity);
        RequestParams params = new RequestParams();
        faq_type = 1;
        params.put("event_type",event_type);
        client.post(FAQURL,params,response);


        faq_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout faq_contents = (LinearLayout) view.findViewById(R.id.faq_contents);
                TextView textViewFAQContent = view.findViewById(R.id.textViewFAQContent);
                if(isOpens[position]){
                    textViewFAQContent.setVisibility(View.GONE);
                    isOpens[position]=false;
                } else {
                    textViewFAQContent.setVisibility(View.VISIBLE);
                    isOpens[position]=true;
                }
            }
        });


        buttoncolor(faq_button1);
//서치뷰
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchview.getWindowToken(), 0);
                searchview.setQuery("", false);
                searchview.setIconified(true);
                searchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });

        return  rootView;
    }

    private void searchData(String keyword) {
//파라미터 정보를 저장하는 객체
        RequestParams params4 = new RequestParams();
        params4.put("search_word",keyword);
//서버에 요청
        client.post("http://"+activity.SERVERIP+"/bongtoo_server/event/eventSearchListJson.a",params4,searchResponse);
    }


    FrameLayout.OnClickListener faqCategory =new FrameLayout.OnClickListener(){


        @Override
        public void onClick(View v) {
            final RequestParams params = new RequestParams();
            PAGE = 1;
            switch (v.getId()){
                case R.id.faq_button1:
                    faqAdapter.clear();
                    faq_type = 1;
                    params.put("pg", PAGE);
                    params.put("event_type", event_type);
                    client.post(FAQURL, params, response);
                    buttoncolor(faq_button1);
                    break;
                case R.id.faq_button2:
                    faqAdapter.clear();
                    faq_type = 2;
                    params.put("pg", PAGE);
                    params.put("event_subject2", faq_2.getText().toString().trim());
                    params.put("event_type", event_type);
                    client.post(FAQURL, params, response);
                    buttoncolor(faq_button2);
                    break;
                case R.id.faq_button3:
                    faqAdapter.clear();
                    faq_type = 3;
                    params.put("pg", PAGE);
                    params.put("event_subject2", faq_3.getText().toString().trim());
                    params.put("event_type", event_type);
                    client.post(FAQURL, params, response);
                    buttoncolor(faq_button3);
                    break;
                case R.id.faq_button4:
                    faqAdapter.clear();
                    faq_type = 4;
                    params.put("pg", PAGE);
                    params.put("event_subject2", faq_4.getText().toString().trim());
                    params.put("event_type", event_type);
                    client.post(FAQURL, params, response);
                    buttoncolor(faq_button4);
                    break;
            }
        }

    };


    FrameLayout.OnClickListener servicecenterEvent = new FrameLayout.OnClickListener(){
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

//////////////////////////////////////////////////////////////////////////////////////
// 화면 기본 세팅
//////////////////////////////////////////////////////////////////////////////////////

    /** 프래그먼트 새로고침 **/
    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


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


    // FAQ 추가 페이지 불러오기
    int totalAll = 0;
    int PAGE = 1;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;
    int faq_type;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        pageablePage = totalAll / 10 + 1;
        RequestParams params = new RequestParams();
        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                switch (faq_type) {
                    case 1: // 전체 페이지 증가
                        PAGE++;
                        params.put("pg", PAGE);
                        params.put("event_type", event_type);
                        client.post(FAQURL, params, response);
                        Log.d("[faq]", "전체 페이지 증가");
                        break;
                    case 2: // 계정 문의 페이지 증가
                        Log.d("[faq]", "계정 문의 페이지 증가 진입");
                        PAGE++;
                        Log.d("[faq]", "페이지 증가");
                        params.put("pg", PAGE);
                        params.put("event_type", event_type);
                        params.put("event_subject2",faq_2.getText().toString().trim());
                        client.post(FAQURL, params, response);
                        Log.d("[faq]", "계정 문의 페이지 증가");
                        break;
                    case 3: // 업체 제휴 페이지 증가
                        Log.d("[faq]", "업체 제휴 페이지 증가 진입");
                        PAGE++;
                        params.put("pg", PAGE);
                        params.put("event_type", event_type);
                        params.put("event_subject2",faq_3.getText().toString().trim());
                        client.post(FAQURL, params, response);
                        Log.d("[faq]", "업체 제휴 페이지 증가");
                        break;
                    case 4: // 고객의 소리 페이지 증가
                        Log.d("[faq]", "고객의 소리 페이지 진입");
                        PAGE++;
                        params.put("pg", PAGE);
                        params.put("event_type", event_type);
                        params.put("event_subject2",faq_4.getText().toString().trim());
                        client.post(FAQURL, params, response);
                        Log.d("[faq]", "고객의 소리 페이지 증가");
                        break;
                }
            } else if (PAGE == pageablePage) {
                Toast.makeText(activity, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
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
//            faqAdapter.clear();
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
                        faq = new FAQ();
                        faq.setFaqSubject(temp.getString("event_subject1"));
                        faq.setFaqContent(temp.getString("event_content"));
                        faqAdapter.add(faq);
                    }
//컨텐츠 리스트 오픈여부 초기화
                    isOpens = new boolean[total];
                    for(int y=0; y< total; y++) {
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
//    // 통신 응답 클래스
//    class FaqHttpResponse extends AsyncHttpResponseHandler {
//        Activity activity;
//
//        public FaqHttpResponse(Activity activity) {
//            this.activity = activity;
//        }
//
//        // 통신 성공시 호출
//        @Override
//        public void onSuccess(int i, Header[] headers, byte[] bytes) {
//            //faqAdapter.clear();
//            String str = new String(bytes);
//            try {
//                JSONObject json = new JSONObject(str);
//                String rt = json.getString("rt");
//                int total = json.getInt("total");
//                totalAll = json.getInt("totalAll");
//                if(rt.equals("OK") && total > 0) {
//                    JSONArray item = json.getJSONArray("item");
//                    for (int x = 0; x < item.length(); x++) {
//                        JSONObject temp = item.getJSONObject(x);
//                        faq = new FAQ();
//                        faq.setFaqSubject(temp.getString("event_subject1"));
//                        faq.setFaqContent(temp.getString("event_content"));
//                        faqAdapter.add(faq);
//                    }
////컨텐츠 리스트 오픈여부 초기화
//                    isOpens = new boolean[total];
//                    for(int y=0; y< total; y++) {
//                        isOpens[y] = false;
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        // 통신 실패시 호출
//        @Override
//        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
//        }
//    }

    // 서치뷰 응답 클래스
    class SearchResponse extends AsyncHttpResponseHandler {
        Activity activity;

        public SearchResponse(Activity activity) {
            this.activity = activity;
        }

        // 통신 성공시 호출
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            faqAdapter.clear();
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
                        faq = new FAQ();
                        faq.setFaqSubject(temp.getString("event_subject1"));
                        faq.setFaqContent(temp.getString("event_content"));
                        faqAdapter.add(faq);
                    }
//컨텐츠 리스트 오픈여부 초기화
                    isOpens = new boolean[total];
                    for(int y=0; y< total; y++) {
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

    /*버튼색상 지정*/
    private void buttoncolor(FrameLayout selected){
        faq_button1.setBackgroundColor(Color.parseColor("#3CBFBFC1"));
        faq_button2.setBackgroundColor(Color.parseColor("#3CBFBFC1"));
        faq_button3.setBackgroundColor(Color.parseColor("#3CBFBFC1"));
        faq_button4.setBackgroundColor(Color.parseColor("#3CBFBFC1"));
        selected.setBackgroundColor(Color.parseColor("#274555"));
    }
}