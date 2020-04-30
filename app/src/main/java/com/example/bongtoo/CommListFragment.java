package com.example.bongtoo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
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


public class CommListFragment extends Fragment implements AbsListView.OnScrollListener {              //--용 0331 카테고리별 리스트 더보기 추가
    // WriteURL
    MainActivity activity;
    String ListURL, commMemURL, searchURL,hotURL, categoryURL;
    AsyncHttpClient client;
    ListResponse listResponse;
    SearchResponse searchResponse;
    boolean hot;
    LinearLayout hot_layout,hot_layout2,hot_layout3,hot_layout5;
    TextView hot_layout4, hot_layout0;
    View hot_layout6;
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_comm_list,container,false);

        //서버 초기화
        ListURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/boardListJson.a";
        commMemURL = "http://"+activity.SERVERIP+"/bongtoo_server/member/memberViewJson.a";
        categoryURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/boardListCategoryJson.a";
        hotURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/boardLikeListJson.a";
        client = new AsyncHttpClient();
        listResponse = new ListResponse(getActivity());
        searchURL= "http://"+activity.SERVERIP+"/bongtoo_server/board/boardSearchListJson.a";
        searchResponse = new SearchResponse(activity);
        searchView = rootView.findViewById(R.id.searchView);

        //초기 화면 설정
        setupCommunityList();
        hot_layout= rootView.findViewById(R.id.hot_layout);
        hot_layout2 =rootView.findViewById(R.id.hot_layout2);
        hot_layout3 =rootView.findViewById(R.id.hot_layout3);
        hot_layout4 =rootView.findViewById(R.id.hot_layout4);
        hot_layout5 =rootView.findViewById(R.id.hot_layout5);
        hot_layout6 =rootView.findViewById(R.id.hot_layout6);
        hot_layout0 =rootView.findViewById(R.id.hot_layout0);
        communityListAdapter.clear();
        hot = getArguments().getBoolean("hot", false);
        if(hot){    //인기글로 들어옴
            hot_layout2.setVisibility(View.GONE);
            hot_layout3.setVisibility(View.GONE);
            hot_layout4.setVisibility(View.GONE);
            hot_layout5.setVisibility(View.GONE);
            hot_layout6.setVisibility(View.GONE);
            hot_layout0.setVisibility(View.VISIBLE);
            commList_TxtCategory.setVisibility(View.GONE);
            commList_BtnGoWrite.setVisibility(View.GONE);
            hot_layout.setVisibility(View.GONE);
            commList_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*position값에 해당하는 board_num 가져와서 Bundle*/
                    CommDetailFragment commDetailFragment = new CommDetailFragment();
                    Community item = communityListAdapter.getItem(position);
                    if (item != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("item", item);
                        bundle.putInt("positionback",3);
                        commDetailFragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, commDetailFragment).addToBackStack(null).commit();
                    }
                }
            });
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            params.weight =1;
//            commList_List.setLayoutParams(params);
            category_code = 99;
            client.post(hotURL, listResponse);
        } else{     //커뮤니티로 들어옴
            client.post(ListURL, listResponse);
        }
        //기본 카테고리 지정
        setCategoryColor(commList_button1, commList_text1);                                             //--용 0331 카테고리별 리스트 더보기 추가

        return rootView;
    }
    private void searchData(String query) {
        RequestParams params4 = new RequestParams();
        params4.put("search_word",query);
        client.post(searchURL,params4,searchResponse);
    }
    // 서치뷰 응답 클래스
    class SearchResponse extends AsyncHttpResponseHandler {
        Activity activity;

        public SearchResponse(Activity activity) {
            this.activity = activity;
        }

        // 통신 성공시 호출
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            communityListAdapter.clear();
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        Community community = new Community();
                        community.setBoard_title(temp.getString("board_title"));
                        community.setBoard_description(temp.getString("board_description"));
                        community.setBoard_category(temp.getString("board_category"));
                        community.setBoard_firstdate(temp.getString("board_first_date"));
                        community.setBoard_img_path(temp.getString("board_img_path"));
                        community.setMember_num(Integer.parseInt(temp.getString("member_num")));
                        community.setNickname(temp.getString("nickname"));
                        community.setGrade(Integer.parseInt(temp.getString("grade")));
                        community.setBoard_hit(Integer.parseInt(temp.getString("board_hit")));
                        community.setBoard_like(Integer.parseInt(temp.getString("board_like")));
                        community.setIsnew(temp.getBoolean("isnew"));
                        communityListAdapter.add(community);
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
    int soon1 = 0;
    //////////////////////////////////////////////////////////////////////////////////////
    // commList 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    FrameLayout.OnClickListener commListEvent = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            final RequestParams params = new RequestParams();
            PAGE = 1;
            switch (v.getId()) {
                case R.id.commList_BtnGoWrite:
                    if(activity.member_num==-1) {
                        Toast.makeText(activity, "로그인 후 이용하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        activity.setFragment(activity.COMMWRITE);
                    }
                    break;
                case R.id.commList_button1:                                                             //--용 0331 카테고리별 리스트 더보기 추가
                    category_code = 1;
                    setCategoryColor(commList_button1, commList_text1);
                    commList_TxtCategory.setText("커뮤니티 > 전체");
                    communityListAdapter.clear();
                    params.put("pg", PAGE);
                    client.post(ListURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("pg", PAGE);
                                params.put("up_down", 1);
                                client.post(ListURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("pg", PAGE);
                                client.post(ListURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button2:
                    category_code = 2;
                    setCategoryColor(commList_button2, commList_text2);
                    commList_TxtCategory.setText("커뮤니티 > 자유게시판");
                    communityListAdapter.clear();
                    params.put("board_category", "자유게시판");
                    params.put("up_down", 0);
                    params.put("pg", PAGE);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "자유게시판");
                                params.put("up_down", 1);
                                params.put("pg", PAGE);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "자유게시판");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button3:
                    category_code = 3;
                    setCategoryColor(commList_button3, commList_text3);
                    commList_TxtCategory.setText("커뮤니티 > 결혼식");
                    communityListAdapter.clear();
                    params.put("board_category", "결혼식");
                    params.put("up_down", 0);
                    params.put("pg", PAGE);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "결혼식");
                                params.put("up_down", 1);
                                params.put("pg", PAGE);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "결혼식");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button4:
                    category_code = 4;
                    setCategoryColor(commList_button4, commList_text4);
                    commList_TxtCategory.setText("커뮤니티 > 하객");
                    communityListAdapter.clear();
                    params.put("board_category", "하객");
                    params.put("pg", 1);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "하객");
                                params.put("pg", PAGE);
                                params.put("up_down", 1);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "하객");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button5:
                    category_code = 5;
                    setCategoryColor(commList_button5, commList_text5);
                    commList_TxtCategory.setText("커뮤니티 > 장례식");
                    communityListAdapter.clear();
                    params.put("board_category", "장례식");
                    params.put("pg", PAGE);
                    params.put("up_down", 0);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "장례식");
                                params.put("pg", PAGE);
                                params.put("up_down", 1);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "장례식");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button6:
                    category_code = 6;
                    setCategoryColor(commList_button6, commList_text6);
                    commList_TxtCategory.setText("커뮤니티 > 돌잔치");
                    communityListAdapter.clear();
                    params.put("board_category", "돌잔치");
                    params.put("pg", PAGE);
                    params.put("up_down", 0);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "돌잔치");
                                params.put("pg", PAGE);
                                params.put("up_down", 1);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "돌잔치");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button7:
                    category_code = 7;
                    setCategoryColor(commList_button7, commList_text7);
                    commList_TxtCategory.setText("커뮤니티 > 궁금해요");
                    communityListAdapter.clear();
                    params.put("board_category", "궁금해요");
                    params.put("pg", PAGE);
                    params.put("up_down", 0);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "궁금해요");
                                params.put("pg", PAGE);
                                params.put("up_down", 1);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "궁금해요");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;
                case R.id.commList_button8:
                    category_code = 8;
                    setCategoryColor(commList_button8, commList_text8);
                    commList_TxtCategory.setText("커뮤니티 > TIP");
                    communityListAdapter.clear();
                    params.put("board_category", "TIP");
                    params.put("pg", PAGE);
                    params.put("up_down", 0);
                    client.post(categoryURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                    commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sortWay = commList_TxtSortNew.getText().toString().trim();
                            PAGE = 1;
                            RequestParams params = new RequestParams();
                            if(sortWay.equals("작성순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "TIP");
                                params.put("pg", PAGE);
                                params.put("up_down", 1);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("최신순으로 보기↑↓");
                            } else if (sortWay.equals("최신순으로 보기↑↓")) {
                                communityListAdapter.clear();
                                params.put("board_category", "TIP");
                                params.put("pg", PAGE);
                                params.put("up_down", 0);
                                client.post(categoryURL, params, listResponse);
                                commList_TxtSortNew.setText("작성순으로 보기↑↓");
                            }
                        }
                    });
                    break;                                                                              //--용 0331 카테고리별 리스트 더보기 추가 끝
            }
        }
    };

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


    //////////////////////////////////////////////////////////////////////////////////////
    // 컴포넌트 전역변수
    //////////////////////////////////////////////////////////////////////////////////////
    FrameLayout commList_BtnGoWrite;

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 보여주기
    //////////////////////////////////////////////////////////////////////////////////////
    /*커뮤니티 - 리스트*/
    //객체선언 List
    List<Community> communityList;
    ListView commList_List;
    ScrollView mainScrollView;
    CommunityListAdapter communityListAdapter;
    ListFragment listFragment;
    FrameLayout commList_button1,commList_button2,commList_button3,commList_button4,commList_button5,commList_button7,commList_button6,commList_button8;
    TextView commList_TxtCategory;
    SearchView searchView;
    /*최신순 정렬*/
    TextView commList_TxtSortNew;
    boolean soon = true;

    private void setupCommunityList() {
        //헤더 세팅
        activity.setUpBtnBack(true, activity.MAINHOME);
        //객체 초기화
        commList_TxtSortNew = rootView.findViewById(R.id.commList_TxtSortNew);
        commList_button1 = rootView.findViewById(R.id.commList_button1);
        commList_button2 = rootView.findViewById(R.id.commList_button2);
        commList_button3 = rootView.findViewById(R.id.commList_button3);
        commList_button4 = rootView.findViewById(R.id.commList_button4);
        commList_button5 = rootView.findViewById(R.id.commList_button5);
        commList_button6 = rootView.findViewById(R.id.commList_button6);
        commList_button7 = rootView.findViewById(R.id.commList_button7);
        commList_button8 = rootView.findViewById(R.id.commList_button8);
        commList_text1 = rootView.findViewById(R.id.commList_text1);
        commList_text2 = rootView.findViewById(R.id.commList_text2);
        commList_text3 = rootView.findViewById(R.id.commList_text3);
        commList_text4 = rootView.findViewById(R.id.commList_text4);
        commList_text5 = rootView.findViewById(R.id.commList_text5);
        commList_text6 = rootView.findViewById(R.id.commList_text6);
        commList_text7 = rootView.findViewById(R.id.commList_text7);
        commList_text8 = rootView.findViewById(R.id.commList_text8);
        commList_TxtCategory =rootView.findViewById(R.id.commList_TxtCategory);
        searchView = rootView.findViewById(R.id.searchView);
        commList_button1.setOnClickListener(commListEvent);
        commList_button2.setOnClickListener(commListEvent);
        commList_button3.setOnClickListener(commListEvent);
        commList_button4.setOnClickListener(commListEvent);
        commList_button5.setOnClickListener(commListEvent);
        commList_button6.setOnClickListener(commListEvent);
        commList_button7.setOnClickListener(commListEvent);
        commList_button8.setOnClickListener(commListEvent);

        commList_TxtListCount = rootView.findViewById(R.id.commList_TxtListCount);                  //--용 0331 카테고리별 리스트 더보기 추가
        commList_TxtSortNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_code = 0;
                String sortWay = commList_TxtSortNew.getText().toString().trim();
                PAGE = 1;
                RequestParams params = new RequestParams();
                if(sortWay.equals("작성순으로 보기↑↓")) {
                    communityListAdapter.clear();
                    params.put("pg", PAGE);
                    params.put("up_down", 1);
                    client.post(ListURL, params, listResponse);
                    commList_TxtSortNew.setText("최신순으로 보기↑↓");
                } else if (sortWay.equals("최신순으로 보기↑↓")) {
                    communityListAdapter.clear();
                    params.put("pg", PAGE);
                    client.post(ListURL, params, listResponse);
                    commList_TxtSortNew.setText("작성순으로 보기↑↓");
                }
            }
        });


        //서치뷰
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });

        communityList = new ArrayList<>();
        mainScrollView = rootView.findViewById(R.id.mainScrollView);
        commList_List = (ListView) rootView.findViewById(R.id.commList_List);
        commList_List.setOnScrollListener(this);
        // 메인 스크롤뷰와 리스트뷰 스크롤 중복 방지 코드
        commList_List.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        communityListAdapter = new CommunityListAdapter(getActivity(), R.layout.list_item_community, communityList);
        listFragment = new ListFragment();
        commList_List.setAdapter(communityListAdapter);
        commList_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*position값에 해당하는 board_num 가져와서 Bundle*/
                CommDetailFragment commDetailFragment = new CommDetailFragment();
                Community item = communityListAdapter.getItem(position);
                if (item != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", item);
                    bundle.putInt("positionback",1);
                    commDetailFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, commDetailFragment).addToBackStack(null).commit();
                }
            }
        });                                                                                             //--용 0331 카테고리별 리스트 더보기 추가 끝

        //이벤트 설정
        commList_BtnGoWrite = rootView.findViewById(R.id.commList_BtnGoWrite);
        commList_BtnGoWrite.setOnClickListener(commListEvent);


    }

    //////////////////////////////////////////////////////////////////////////////////////
    // ListResponse : 게시글 리스트 요청
    //  - Board, Member 테이블에서 DTO(Community)로 저장
    //////////////////////////////////////////////////////////////////////////////////////
    Community community;
    TextView commList_TxtListCount;
    /*ListResponse*/
    class ListResponse extends AsyncHttpResponseHandler {
        Activity activity;
        public ListResponse(Activity activity) {
            this.activity = activity;
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {                                  //--용 0331 카테고리별 리스트 더보기 추가
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                totalAll = json.getInt("totalAll");
                commList_TxtListCount.setText("총 " + totalAll + " 개");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        //Board 테이블에서 받기
                        community = new Community();
                        community.setMember_num(Integer.parseInt(temp.getString("member_num")));
                        community.setBoard_num(Integer.parseInt(temp.getString("board_num")));
                        community.setBoard_category(temp.getString("board_category"));
                        community.setBoard_title(temp.getString("board_title"));
                        community.setBoard_description(temp.getString("board_description"));
                        community.setNickname(temp.getString("nickname"));
                        community.setGrade(Integer.parseInt(temp.getString("grade")));
                        community.setBoard_firstdate(temp.getString("board_first_date"));
                        community.setBoard_origin_img(temp.getString("board_origin_img"));
                        community.setBoard_img_path(temp.getString("board_img_path"));
                        community.setBoard_origin_video(temp.getString("board_origin_video"));
                        community.setBoard_video_path(temp.getString("board_video_path"));
                        community.setBoard_hit(Integer.parseInt(temp.getString("board_hit")));
                        community.setBoard_like(Integer.parseInt(temp.getString("board_like")));
                        community.setBoard_editdate(temp.getString("board_edit_date"));
                        community.setTotalAll(totalAll);
                        community.setIsnew(temp.getBoolean("isnew"));

//                        setListViewHeightBasedOnChildren(commList_List);
                        communityListAdapter.add(community);
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
            //Log.d("[ERROR_COMM_LIST]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    /**카테고리 선택한 값만 색깔 켜지게 하기**/
    TextView commList_text1, commList_text2, commList_text3, commList_text4, commList_text5, commList_text6, commList_text7, commList_text8;
    private void setCategoryColor(FrameLayout category, TextView text) {
        commList_text1.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text2.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text3.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text4.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text5.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text6.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text7.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_text8.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commList_button1.setBackgroundResource(R.color.colorBtnDefault);
        commList_button2.setBackgroundResource(R.color.colorBtnDefault);
        commList_button3.setBackgroundResource(R.color.colorBtnDefault);
        commList_button4.setBackgroundResource(R.color.colorBtnDefault);
        commList_button5.setBackgroundResource(R.color.colorBtnDefault);
        commList_button6.setBackgroundResource(R.color.colorBtnDefault);
        commList_button7.setBackgroundResource(R.color.colorBtnDefault);
        commList_button8.setBackgroundResource(R.color.colorBtnDefault);
        category.setBackgroundResource(R.color.colorNavy);
        text.setTextColor(getResources().getColorStateList(R.color.colorWhite));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // 리스트뷰 자동 페이지 추가
    int PAGE = 1;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;
    int category_code;
    int totalAll;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        pageablePage = totalAll / 10 + 1;
        RequestParams params = new RequestParams();
        String sortWay = commList_TxtSortNew.getText().toString().trim();

        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {

            if (PAGE < pageablePage) {
                switch (category_code) {
                    case 0: // 화면 초기화 됐을 때
                    case 1: // 전체 카테고리 눌렀을 때
                        PAGE++;
                        if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        client.post(ListURL, params, listResponse);
                        break;
                    case 2: // 자유게시판
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "자유게시판");
                        client.post(categoryURL, params, listResponse);
                        break;
                    case 3: // 결혼식
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "결혼식");
                        client.post(categoryURL, params, listResponse);
                        break;
                    case 4: // 하객
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "하객");
                        client.post(categoryURL, params, listResponse);
                        break;
                    case 5: // 장례식
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "장례식");
                        client.post(categoryURL, params, listResponse);
                        break;
                    case 6: // 돌잔치
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "돌잔치");
                        client.post(categoryURL, params, listResponse);
                        break;
                    case 7: // 궁금해요
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "궁금해요");
                        client.post(categoryURL, params, listResponse);
                        break;
                    case 8: // TIP
                        PAGE++;
                        if(sortWay.equals("작성순으로 보기↑↓")) { // 최신순으로 보는 상태
                            params.put("up_down", 0);
                        } else if (sortWay.equals("최신순으로 보기↑↓")) { // 작성순으로 보는 상태
                            params.put("up_down", 1);
                        }
                        params.put("pg", PAGE);
                        params.put("board_category", "TIP");
                        client.post(categoryURL, params, listResponse);
                        break;
                }


            } else if (PAGE == pageablePage) {
                //Toast.makeText(activity, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

}
