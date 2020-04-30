package com.example.bongtoo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bongtoo.adapter.MyBongTooAdapter;
import com.example.bongtoo.model.Community;
import com.example.bongtoo.model.Member;
import com.example.bongtoo.model.MyBongToo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyBongtooFragment extends Fragment implements AbsListView.OnScrollListener {

    MyBongToo myBongToo;
    Member member;
    String URL, memeberURL;
    AsyncHttpClient client;
    HttpResponseList listResponse;
    HttpResponseMemberInfo memberResponse;
    int PAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_bongtoo, container, false);
        URL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashListJson.a";
        memeberURL = "http://" + activity.SERVERIP + "/bongtoo_server/member/memberViewJson.a";
        init();
        // 마이 봉투 리스트 세팅
        setupMyBongTooList();
        return rootView;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    // myBongtoo 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    /*myBongtoo 이미지 클릭 이벤트*/
    /*
    * myBong_BtnGoGradeDetail.setOnClickListener(myBongtooEvent);
        myBong_BtnGoInsert.setOnClickListener(myBongtooEvent);
        myBong_BtnGoMyPage.setOnClickListener(myBongtooEvent);
        myBong_BtnGoSummary.setOnClickListener(myBongtooEvent);
    */

    LinearLayout.OnClickListener myBongtooEvent = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.isSettingsOpen = true;
            activity.openSettings();
            switch (v.getId()) {
                case R.id.myBong_BtnGoGradeDetail:
                    activity.setFragment(5);
                    break;
                case R.id.myBong_BtnGoInsert:
                    activity.setFragment(activity.MYBONGTOO_INSERT);
                    break;
                case R.id.myBong_BtnGoMyPage:
                    activity.setFragment(activity.MYPAGE);
                    activity.setUpBtnBack(true, activity.MAINMYBONGTOO);
                    break;
                case R.id.myBong_BtnOrder: // 초기화면 참석날짜순 정렬 , 클릭시 금액순 정렬
                    orderMyBongToo();
                    break;
            }
        }
    };

    private void orderMyBongToo() {

        String orderBy = myBong_TxtOrder.getText().toString().trim();
        RequestParams params = new RequestParams();

        if (orderBy.equals("금액순으로 보기")) {
            myBongTooAdapter.clear();
            params.put("member_num", activity.member_num);
            params.put("pg", PAGE);
            params.put("money", 1);
            client.post(URL, params, listResponse);
            myBong_TxtOrder.setText("날짜순으로 보기");
        } else if (orderBy.equals("날짜순으로 보기")) {
            myBongTooAdapter.clear();
            params.put("pg", PAGE);
            params.put("member_num", activity.member_num);
            client.post(URL, params, listResponse);
            myBong_TxtOrder.setText("금액순으로 보기");
        }
    }


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

    //////////////////////////////////////////////////////////////////////////////////////
    // 컴포넌트 전역변수
    //////////////////////////////////////////////////////////////////////////////////////
    /*이미지 버튼*/
    /*레이아웃 버튼*/
    LinearLayout myBong_BtnGoInsert, myBong_BtnGoSummary, myBong_BtnGoGradeDetail, myBong_BtnGoMyPage, myBong_BtnOrder;
    /*이미지뷰*/
    ImageView myBong_ImageProfile, myBong_ImageGrade;
    /*텍스트뷰*/
    TextView myBong_TxtGradeName, myBong_TxtMyAccount, myBong_TxtMyGrade, myBong_TxtOrder;

    /**
     * 컴포넌트 초기화
     **/
    private void init() {

        myBong_ImageProfile = rootView.findViewById(R.id.myBong_ImageProfile);
        myBong_ImageGrade = rootView.findViewById(R.id.myBong_ImageGrade);

        myBong_BtnGoGradeDetail = rootView.findViewById(R.id.myBong_BtnGoGradeDetail);
        myBong_BtnGoInsert = rootView.findViewById(R.id.myBong_BtnGoInsert);
        myBong_BtnGoMyPage = rootView.findViewById(R.id.myBong_BtnGoMyPage);
        myBong_BtnOrder = rootView.findViewById(R.id.myBong_BtnOrder);

        myBong_TxtGradeName = rootView.findViewById(R.id.myBong_TxtGradeName);
        myBong_TxtMyAccount = rootView.findViewById(R.id.myBong_TxtMyAccount);
        myBong_TxtMyGrade = rootView.findViewById(R.id.myBong_TxtMyGrade);
        myBong_TxtOrder = rootView.findViewById(R.id.myBong_TxtOrder);

        //이벤트 설정
        myBong_BtnGoGradeDetail.setOnClickListener(myBongtooEvent);
        myBong_BtnGoInsert.setOnClickListener(myBongtooEvent);
        myBong_BtnGoMyPage.setOnClickListener(myBongtooEvent);
        myBong_BtnOrder.setOnClickListener(myBongtooEvent);

        member = new Member();
        client = new AsyncHttpClient();
        listResponse = new HttpResponseList();
        memberResponse = new HttpResponseMemberInfo();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 보여주기
    //////////////////////////////////////////////////////////////////////////////////////
    /*커뮤니티 - 리스트*/
    //객체선언 List
    List<MyBongToo> myBongTooList;
    ScrollView mainScrollView;
    ListView myBong_ListView;
    MyBongTooAdapter myBongTooAdapter;
    ListFragment listFragment;
    MyBongtooShowFragment myBongtooShowFragment;

    private void setupMyBongTooList() {
        //헤더 세팅
        activity.setHeaderTitle("MY 봉투");
        activity.setUpBtnBack(true, activity.MAINHOME);

        // 리스트 객체 초기화(프래그먼트 리스트로 보여주기)
        myBongTooList = new ArrayList<>();
        mainScrollView = rootView.findViewById(R.id.mainScrollView);
        myBong_ListView = (ListView) rootView.findViewById(R.id.myBong_ListView);
        myBongTooAdapter = new MyBongTooAdapter(getActivity(), R.layout.list_item_my_bongtoo, myBongTooList);
        listFragment = new ListFragment();
        myBong_ListView.setAdapter(myBongTooAdapter);
        myBong_ListView.setOnScrollListener(this);
        // 메인 스크롤뷰와 리스트뷰 스크롤 중복 방지 코드
        myBong_ListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        myBong_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myBongtooShowFragment = new MyBongtooShowFragment();
                MyBongToo item = myBongTooAdapter.getItem(position);
                if (item != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", item);
                    Log.d("[ListCheck]", "groupIndex" + item.getGroupIndex());
                    myBongtooShowFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, myBongtooShowFragment).addToBackStack(null).commit();
                    activity.setHeaderTitle("MY 경조사비");
                    activity.setUpBtnBack(true, activity.MAINMYBONGTOO);
                }
            }
        });

        //myBongTooAdapter.clear();
        RequestParams params = new RequestParams();
        params.put("pg", PAGE);
        params.put("member_num", activity.member_num);
        client.post(URL, params, listResponse);
        params.remove("pg");
        client.post(memeberURL, params, memberResponse);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        myBongTooAdapter.clear();
//        RequestParams params = new RequestParams();
//        params.put("pg", PAGE);
//        params.put("member_num", activity.member_num);
//        client.post(URL, params, listResponse);
//        params.remove("pg");
//        client.post(memeberURL, params, memberResponse);
//    }

    /**
     * 프로필 이미지&등급왕관
     **/
    public void setUpProfile() {

        Log.d("[member]", "member_num:" + member.getMember_num());
        Log.d("[member]", "name:" + member.getName());
        Log.d("[member]", "member_id:" + member.getMember_id());
        Log.d("[member]", "nickname:" + member.getNickname());
        Log.d("[member]", "member_phone:" + member.getMember_phone());
        Log.d("[member]", "email:" + member.getEmail());
        Log.d("[member]", "member_img_path:" + member.getMember_img_path());
        Log.d("[member]", "member_origin_img:" + member.getMember_origin_img());
        Log.d("[member]", "grade:" + member.getGrade());
        Log.d("[member]", "first_logtime:" + member.getFirst_logtime());
        Log.d("[member]", "edit_logtime:" + member.getEdit_logtime());

        //프로필 이미지 세팅
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            myBong_ImageProfile.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            myBong_ImageProfile.setClipToOutline(true);
        }
        // 프로필이미지 가져오기
        Glide.with(this).load(member.getMember_img_path()).error(R.drawable.profile1).placeholder(R.mipmap.ic_launcher_round).into(myBong_ImageProfile);
        // 프로필 이미지 배지 설정 + 그 아래줄 큰 등급별 왕관 표시
        String grade = null;
        switch (member.getGrade()) {
            case 1:
                Glide.with(this).load(R.drawable.icon_crownmain_blue).into(myBong_ImageGrade);
                grade = "파란봉투";
                break;
            case 2:
                Glide.with(this).load(R.drawable.icon_crownmain_green).into(myBong_ImageGrade);
                grade = "초록봉투";
                break;
            case 3:
                Glide.with(this).load(R.drawable.icon_crownmain_bronze).into(myBong_ImageGrade);
                grade = "동빛봉투";
                break;
            case 4:
                Glide.with(this).load(R.drawable.icon_crownmain_silver).into(myBong_ImageGrade);
                grade = "은빛봉투";
                break;
            case 5:
                Glide.with(this).load(R.drawable.icon_crownmain_gold).into(myBong_ImageGrade);
                grade = "금빛봉투";
                break;
        }
        // 프로필 사진 옆 등급 & 닉네임 설정
        myBong_TxtGradeName.setText(grade + ", " + member.getNickname()+"님");
        myBong_TxtMyGrade.setText(grade);


    }

    // MY봉투 리스트 Response(등록 날짜순 정렬, 클라이언트가 money params 삽입 시 금액순 정렬)
    class HttpResponseList extends AsyncHttpResponseHandler {

        ProgressDialog dialog;

        @Override
        public void onStart() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage("잠시만 기다려주세요");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        public void onFinish() {
            dialog.dismiss();
            dialog = null;
        }
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {

            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                int totalAll = json.getInt("totalAll");
                int total_money = json.getInt("total_money");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        myBongToo = new MyBongToo();
                        myBongToo.setNum(temp.getInt("occ_cash_num"));
                        myBongToo.setMember_num(temp.getInt("member_num"));
                        myBongToo.setName(temp.getString("occ_cash_name"));
                        myBongToo.setImageURL(temp.getString("occ_cash_img_path"));
                        myBongToo.setGroup(temp.getString("occ_cash_group"));
                        myBongToo.setGroupIndex(temp.getInt("occ_cash_group_index"));
                        myBongToo.setAttendance(temp.getInt("occ_cash_attendance"));
                        myBongToo.setInviteWay(temp.getString("occ_cash_invite_way"));
                        myBongToo.setPlace(temp.getString("occ_cash_place"));
                        myBongToo.setDate(temp.getString("occ_cash_date"));
                        myBongToo.setMoney(temp.getInt("occ_cash_money"));
                        myBongToo.setMemo(temp.getString("occ_cash_memo"));
                        myBongToo.setTotalAll(totalAll);

                        myBongTooAdapter.add(myBongToo);

                        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
                        String str_total_money = numberFormat.format(total_money);
                        myBong_TxtMyAccount.setText("￦ " + str_total_money);

                        Log.d("[List]", "occ_cash_name:" + temp.getString("occ_cash_name"));
                        Log.d("[List]", "occ_cash_place:" + temp.getString("occ_cash_place"));
                        Log.d("[List]", "occ_cash_money:" + temp.getInt("occ_cash_money"));
                        Log.d("[List]", "occ_cash_attendance:" + temp.getInt("occ_cash_attendance"));
                        Log.d("[List]", "occ_cash_invite_way:" + temp.getString("occ_cash_invite_way"));
                        Log.d("[List]", "occ_cash_date:" + temp.getString("occ_cash_date"));
                        Log.d("[List]", "occ_cash_memo:" + temp.getString("occ_cash_memo"));
                        Log.d("[List]", "occ_cash_origin_img:" + temp.getString("occ_cash_img_path"));
                        Log.d("[List]", "occ_cash_group:" + temp.getString("occ_cash_group"));
                        Log.d("[List]", "occ_cash_group_index:" + temp.getInt("occ_cash_group_index"));
                        Log.d("[List]", "totalAll: " + totalAll);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "[통신 실패] 연결에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
            Log.d("[ERROR]", "throwable: " + throwable);
        }
    }


    // 프로필 설정에 사용할 멤버
    class HttpResponseMemberInfo extends AsyncHttpResponseHandler {

        ProgressDialog dialog;

        @Override
        public void onStart() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage("잠시만 기다려주세요");
            dialog.setCancelable(false);
            dialog.show();
        }

        // 통신 종료
        @Override
        public void onFinish() {
            dialog.dismiss();
            dialog = null;
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                Log.d("[TEST]", "TEST");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    JSONObject temp = item.getJSONObject(0);
                    //member = new Member();
                    member.setMember_num(temp.getInt("member_num"));
                    member.setName(temp.getString("name"));
                    member.setMember_id(temp.getString("member_id"));
                    member.setNickname(temp.getString("nickname"));
                    member.setMember_phone(temp.getString("member_phone"));
                    member.setEmail(temp.getString("email"));
                    member.setMember_origin_img(temp.getString("member_origin_img"));
                    member.setMember_img_path(temp.getString("member_img_path"));
                    member.setGrade(temp.getInt("grade"));
                    member.setFirst_logtime(temp.getString("first_logtime"));
                    member.setEdit_logtime(temp.getString("edit_logtime"));

                    setUpProfile();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "[통신 실패] 연결에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
            Log.d("[ERROR]", "throwable: " + throwable);
        }
    }

    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        pageablePage = myBongToo.getTotalAll() / 10 + 1;
        RequestParams params = new RequestParams();
        String orderBy = myBong_TxtOrder.getText().toString().trim();
        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                PAGE++;
                Log.d("[scroll]", "PAGE: " + PAGE);
                Log.d("[scroll]", "orderBy: " + orderBy);
                if (orderBy.equals("날짜순으로 보기")) { // 금액순 정렬값 계속 불러와야 함
                    Log.d("[scroll]", "PAGE 증가 금액순 정렬 진입: " + PAGE);
                    params.put("member_num", activity.member_num);
                    params.put("money", 1);
                    params.put("pg", PAGE);
                    client.post(URL, params, listResponse);
                } else if (orderBy.equals("금액순으로 보기")) { // 날짜순 정렬값 계속 불러와야 함
                    Log.d("[scroll]", "PAGE 증가 날짜순 정렬 진입: " + PAGE);
                    params.put("member_num", activity.member_num);
                    params.put("pg", PAGE);
                    client.post(URL, params, listResponse);
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


}