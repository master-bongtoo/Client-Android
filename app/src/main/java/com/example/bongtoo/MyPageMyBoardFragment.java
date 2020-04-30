package com.example.bongtoo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bongtoo.adapter.CommunityListAdapter;
import com.example.bongtoo.model.Community;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;


public class MyPageMyBoardFragment extends Fragment implements AbsListView.OnScrollListener {
    TextView myinfo_board_TxtListCount,myinfo_board_TxtSortNew;
    CommunityListAdapter adapter;
    List<Community> list;
    ListView myinfo_board_listview;
    ScrollView myBoard_scrollView;
    AsyncHttpClient client;
    ListResponse response;
    boolean soon = true;
    int PAGE = 1; // 0401 박성용
    String myBoardURL;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_board,container,false);
        myBoardURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/myboardListJson.a";
        myinfo_board_TxtListCount = rootView.findViewById(R.id.myinfo_board_TxtListCount);
        myinfo_board_TxtSortNew = rootView.findViewById(R.id.myinfo_board_TxtSortNew);
        client = new AsyncHttpClient();
        response = new ListResponse(activity);
        list = new ArrayList<>();
        myinfo_board_listview = rootView.findViewById(R.id.myinfo_board_listview);
        // 0401 박성용~
        myBoard_scrollView = rootView.findViewById(R.id.myBoard_scrollView);
        // 메인 스크롤뷰와 리스트뷰 스크롤 중복 방지 코드
        myinfo_board_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myBoard_scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        myinfo_board_listview.setOnScrollListener(MyPageMyBoardFragment.this);
        // ~0401 박성용
        adapter = new CommunityListAdapter(getActivity(), R.layout.list_item_community, list);
        myinfo_board_listview.setAdapter(adapter);
        myinfo_board_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommDetailFragment commDetailFragment = new CommDetailFragment();
                Community item = adapter.getItem(position);
                if (item != null){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item",item);
                    commDetailFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place,commDetailFragment).addToBackStack(null).commit();
                    activity.setUpBtnBack(true, activity.MYPAGE_MYBOARD);
                }
            }
        });

        // 0401 박성용~
        //최신순 정렬
        myinfo_board_TxtSortNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                PAGE = 1;
                params.put("member_num",activity.member_num);
                params.put("pg", PAGE);
                if (!soon){ // 최신순 정렬
                    adapter.clear();
                    client.post(myBoardURL, params, response);
                    myinfo_board_TxtSortNew.setText("최신순으로 보기↑↓");
                    soon = true;
                } else { // 작성순 정렬
                    adapter.clear();
                    params.put("up_down",1);
                    client.post(myBoardURL, params, response);
                    myinfo_board_TxtSortNew.setText("작성순으로 보기↑↓");
                    soon= false;
                }
            }
        });
        RequestParams params = new RequestParams();
        params.put("member_num",activity.member_num);
        params.put("pg", PAGE);
        client.post(myBoardURL, params, response);
        return rootView;

    }
    // ~0401 박성용


    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 기본 세팅
    //////////////////////////////////////////////////////////////////////////////////////
    MainActivity activity;
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

    Community community;

    // 0401 박성용~
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
                params.put("pg", PAGE);
                params.put("member_num", activity.member_num);
                if(soon) { // 최신순으로 보는 상태
                    params.put("up_down", 1);
                }
                client.post(myBoardURL, params, response);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }
    // ~0401 박성용

    class ListResponse extends AsyncHttpResponseHandler {
        Activity activity;

        public ListResponse(Activity activity) {
            this.activity = activity;
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                totalAll = json.getInt("totalAll");
                myinfo_board_TxtListCount.setText("총 " + totalAll + " 개");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
//Board 테이블에서 받기
                        community = new Community(); // 0401 박성용
                        community.setBoard_title(temp.getString("board_title"));
                        community.setBoard_description(temp.getString("board_description"));
                        community.setBoard_category(temp.getString("board_category"));
                        community.setBoard_firstdate(temp.getString("board_first_date"));
                        community.setBoard_img_path(temp.getString("board_img_path"));
                        community.setMember_num(Integer.parseInt(temp.getString("member_num")));
                        community.setNickname(temp.getString("nickname"));
                        community.setGrade(Integer.parseInt(temp.getString("grade")));
                        adapter.add(community);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // 통신 실패
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패 - 리스트", Toast.LENGTH_SHORT).show();
        }
    }

}