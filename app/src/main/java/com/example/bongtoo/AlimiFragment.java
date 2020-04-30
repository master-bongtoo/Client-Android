package com.example.bongtoo;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bongtoo.adapter.AlimiRecyclerViewAdapter;
import com.example.bongtoo.helper.RegexHelper;
import com.example.bongtoo.model.Alimi;
import com.example.bongtoo.model.AlimiTag;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class AlimiFragment extends Fragment {
    String test = "ok";
    String TAGURL;
    AlimiTag alimiTag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_alimi,container,false);
        /*태그선택 초기화*/
        TAGURL ="http://"+activity.SERVERIP+"/bongtoo_server/alimi/alimiTagWriteJson.a";

        /*화면 초기화*/
        alimi_noTagLayout = rootView.findViewById(R.id.alimi_noTagLayout);
        alimi_inputTagLayout = rootView.findViewById(R.id.alimi_inputTagLayout);
        alimi_listLayout = rootView.findViewById(R.id.alimi_listLayout);
        alimi_noTagLayout.setVisibility(View.GONE);
        alimi_inputTagLayout.setVisibility(View.GONE);
        alimi_listLayout.setVisibility(View.GONE);
        /*태그 체크 후 화면 표시*/
        isTagCheck();
        return rootView;
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
    // View 조건 선택
    //////////////////////////////////////////////////////////////////////////////////////
    /**선택한 레이아웃 보여주기**/
    private void setVisible(LinearLayout layout) {
        alimi_noTagLayout.setVisibility(View.GONE);
        alimi_inputTagLayout.setVisibility(View.GONE);
        alimi_listLayout.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
    }
    /**태그 설정한 상태인지 확인 후 화면 표시**/
    public void isTagCheck() {
        if(activity.isAlimiTagset) {
            //태그 설정한 상태이면 setAlimi_listLayout
            setAlimi_listLayout();
        } else {
            //태그 설정하지 않은 상태이면 setAlimi_noTagLayout
            setAlimi_noTagLayout();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 초기화
    //////////////////////////////////////////////////////////////////////////////////////
    LinearLayout alimi_noTagLayout, alimi_inputTagLayout, alimi_listLayout;

    /**alimi_noTagLayout**/
    private void setAlimi_noTagLayout() {
        alimi_noTagLayout = rootView.findViewById(R.id.alimi_noTagLayout);
        FrameLayout alimi_BtnToInputTag = rootView.findViewById(R.id.alimi_BtnToInputTag);
        alimi_BtnToInputTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlimi_inputTagLayout();
            }
        });
        setVisible(alimi_noTagLayout);
    }

    //태그선택 객체선언
    EditText alimi_editTxtTag;
    FrameLayout alimi_BtnInputTag;
    TextView alimi_tagView1,alimi_tagView2,alimi_tagView3,alimi_tagView4,alimi_tagView5;
    int count =0;
    AsyncHttpClient client;
    HttpResponse tagresponse;

    /**alimi_inputTagLayout**/
    private void setAlimi_inputTagLayout() {
        alimi_inputTagLayout = rootView.findViewById(R.id.alimi_inputTagLayout);
        FrameLayout alimi_BtnTagSubmit = rootView.findViewById(R.id.alimi_BtnTagSubmit);
        FrameLayout alimi_BtnTagCancel = rootView.findViewById(R.id.alimi_BtnTagCancel);

// 태그 초기화
        client = new AsyncHttpClient();
        tagresponse = new HttpResponse(activity);
        alimi_BtnInputTag = rootView.findViewById(R.id.alimi_BtnInputTag);
        alimi_editTxtTag = rootView.findViewById(R.id.alimi_editTxtTag);
        alimi_tagView1 = rootView.findViewById(R.id.alimi_tagView1);
        alimi_tagView2 = rootView.findViewById(R.id.alimi_tagView2);
        alimi_tagView3 = rootView.findViewById(R.id.alimi_tagView3);
        alimi_tagView4 = rootView.findViewById(R.id.alimi_tagView4);
        alimi_tagView5 = rootView.findViewById(R.id.alimi_tagView5);

// 등록 버튼
        alimi_BtnTagSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagData();
            }
        });

//태그 등록
        alimi_BtnInputTag.setOnClickListener(new View.OnClickListener() {
            ArrayList<String> tag = new ArrayList<>();

            @Override
            public void onClick(View v) {

                String stralimi_editTxtTag = alimi_editTxtTag.getText().toString().trim();
                if (!RegexHelper.getInstance().isValue(stralimi_editTxtTag)) {
                    Toast.makeText(activity, "태그를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i = 0; i < count; i++) {
                    if(tag.get(i).equals(stralimi_editTxtTag)) {
                        Toast.makeText(activity, "중복된 태그 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (count ==5){
                    Toast.makeText(activity, "태그 선택은 최대 5개 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                tag.add(stralimi_editTxtTag);
                if (count == 0){
                    alimi_tagView1.setText("#" +stralimi_editTxtTag);
                    alimi_tagView1.setVisibility(View.VISIBLE);
                    alimi_tagView1.requestFocus();
                    alimi_editTxtTag.setText("");
                    count++;
                } else if (count ==1){
                    alimi_tagView2.setText("#" +stralimi_editTxtTag);
                    alimi_tagView2.setVisibility(View.VISIBLE);
                    alimi_tagView2.requestFocus();
                    alimi_editTxtTag.setText("");
                    count++;
                }else if (count ==2) {
                    alimi_tagView3.setText("#" + stralimi_editTxtTag);
                    alimi_tagView3.setVisibility(View.VISIBLE);
                    alimi_tagView3.requestFocus();
                    alimi_editTxtTag.setText("");
                    count++;
                }else if (count ==3) {
                    alimi_tagView4.setText("#" + stralimi_editTxtTag);
                    alimi_tagView4.setVisibility(View.VISIBLE);
                    alimi_tagView4.requestFocus();
                    alimi_editTxtTag.setText("");
                    count++;
                }else if (count ==4) {
                    alimi_tagView5.setText("#" + stralimi_editTxtTag);
                    alimi_tagView5.setVisibility(View.VISIBLE);
                    alimi_tagView5.requestFocus();
                    alimi_editTxtTag.setText("");
                    count++;
                }
            }
        });
// 취소 버튼
        alimi_BtnTagCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "태그 설정이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                count =0;
                alimi_editTxtTag.setText("");
                alimi_tagView1.setVisibility(View.GONE);
                alimi_tagView2.setVisibility(View.GONE);
                alimi_tagView3.setVisibility(View.GONE);
                alimi_tagView4.setVisibility(View.GONE);
                alimi_tagView5.setVisibility(View.GONE);
                setAlimi_noTagLayout();
            }
        });

        setVisible(alimi_inputTagLayout);
    }

    /**태그 데이터 추가 요청**/
    private  void  tagData(){
        RequestParams params = new RequestParams();
        params.put("member_num",activity.member_num);
        params.put("alimi_tag1", alimi_tagView1.getText().toString().trim());
        params.put("alimi_tag2", alimi_tagView2.getText().toString().trim());
        params.put("alimi_tag3", alimi_tagView3.getText().toString().trim());
        params.put("alimi_tag4", alimi_tagView4.getText().toString().trim());
        params.put("alimi_tag5", alimi_tagView5.getText().toString().trim());
        client.post(TAGURL, params, tagresponse);
    }

    // 스크롤 리스너 사용을 위한 전역 변수
//    int PAGE = 1;
//    int pageablePage = 0;
//    boolean lastItemVisibleFlag = false;
//    int category_code;
//
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//        pageablePage = shop.getTotalAll() / 10 + 1;
//        RequestParams params = new RequestParams();
//        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
//            if (PAGE < pageablePage) {
//                PAGE++;
//
//                if (shop_search) { // 업체 목록인 경우 전체 페이지
//                    switch (category_code) {
//                        case 0:
//                        case 1: // 업체 검색이면서 전체 페이지인 경우
//                            params.put("pg", PAGE);
//                            client.post(shopURL, params, response);
//                            break;
//                        case 2: // 업체 검색이면서 예식장인 경우
//                            params.put("shop_type", "예식장");
//                            params.put("pg", PAGE);
//                            client.post(shopListTypeURL, params, response);
//                            break;
//                        case 3:
//                            params.put("shop_type", "패스트푸드");
//                            params.put("pg", PAGE);
//                            client.post(shopListTypeURL, params, response);
//                            break;
//                        case 4:
//                            params.put("shop_type", "한식");
//                            params.put("pg", PAGE);
//                            client.post(shopListTypeURL, params, response);
//                            break;
//                        case 5:
//                            params.put("shop_type", "중식");
//                            params.put("pg", PAGE);
//                            client.post(shopListTypeURL, params, response);
//                            break;
//                    }
//
//                }
//
//
//            } else if (PAGE == pageablePage) {
//                Toast.makeText(activity, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
//    }

    /*태그 추가 통신하기*/
    class HttpResponse extends AsyncHttpResponseHandler {
        Activity activity1;

        public HttpResponse(Activity activity) {
            this.activity1 = activity;
        }

        @Override
        public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt= json.getString("rt");
                if (rt.equals("OK")) {
                    Toast.makeText(activity1, "태그가 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    activity.isAlimiTagset=true;
                    setAlimi_listLayout();
                }else {
                    Toast.makeText(activity1, "태그가 중복됩니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity1, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }

    }


    /**alimi_listLayout**/
    List<Alimi> alimi_list1, alimi_list2, alimi_list3, alimi_list4, alimi_list5;
    TextView alimi_tag1,alimi_tag2,alimi_tag3,alimi_tag4,alimi_tag5;
    FrameLayout alimi_btnGoInput;
    AsyncHttpClient clientlist;
    AlimiResponse1 alimiResponse1;
    AlimiResponse2 alimiResponse2;
    AlimiResponse3 alimiResponse3;
    AlimiResponse4 alimiResponse4;
    AlimiResponse5 alimiResponse5;
    AlimiTagResponse alimiTagResponse;
    String alimilistURL;
    String alimitagurl;
    RecyclerView alimi_listView1, alimi_listView2, alimi_listView3, alimi_listView4, alimi_listView5;
    AlimiRecyclerViewAdapter alimiListAdapter1, alimiListAdapter2, alimiListAdapter3, alimiListAdapter4, alimiListAdapter5;

    private void setAlimi_listLayout(){
        alimitagurl = "http://"+activity.SERVERIP+"/bongtoo_server/alimi/alimiTagViewJson.a";
        alimilistURL= "http://"+activity.SERVERIP+"/bongtoo_server/alimi/alimiListJson.a";
        alimi_listLayout = rootView.findViewById(R.id.alimi_listLayout);
        alimi_btnGoInput = rootView.findViewById(R.id.alimi_btnGoInput);
        clientlist= new AsyncHttpClient();

        //태그
        alimiTagResponse = new AlimiTagResponse(activity);
        RequestParams params1 = new RequestParams();
        params1.put("member_num",activity.member_num);
        clientlist.post(alimitagurl, params1,alimiTagResponse);


        alimi_btnGoInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setFragment(activity.ALIMIINPUT);
                sendTags();
            }
        });


        //리스트 세팅
        alimi_list1 = new ArrayList<>();
        alimi_list2 = new ArrayList<>();
        alimi_list3 = new ArrayList<>();
        alimi_list4 = new ArrayList<>();
        alimi_list5 = new ArrayList<>();
        alimi_tag1 = rootView.findViewById(R.id.alimi_tag1);
        alimi_tag2 = rootView.findViewById(R.id.alimi_tag2);
        alimi_tag3 = rootView.findViewById(R.id.alimi_tag3);
        alimi_tag4 = rootView.findViewById(R.id.alimi_tag4);
        alimi_tag5 = rootView.findViewById(R.id.alimi_tag5);
        alimi_listView1 = rootView.findViewById(R.id.alimi_listView1);
        alimi_listView2 = rootView.findViewById(R.id.alimi_listView2);
        alimi_listView3 = rootView.findViewById(R.id.alimi_listView3);
        alimi_listView4 = rootView.findViewById(R.id.alimi_listView4);
        alimi_listView5 = rootView.findViewById(R.id.alimi_listView5);

        setVisible(alimi_listLayout);
    }

    /**알리미 리스트 불러오기**/
    // 1번태그
    class AlimiResponse1 extends AsyncHttpResponseHandler {
        // 통신 성공시 호출
        /*받은 데이터를 태그별 리스트에 추가했는지 확인하기 위한 변수*/
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {
                    /*받아온 데이터 : 태그(params.put)로 검색한 게시글 리스트*/
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        /**/
                        Alimi alimi = new Alimi();
                        alimi.setAlimi_tag(temp.getString("alimi_tag"));
                        alimi.setAlimi_content(temp.getString("alimi_content"));
                        alimi.setAlimi_index(temp.getInt("alimi_index"));
                        alimi.setAlimi_date(temp.getString("alimi_date"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi.setAlimi_who(temp.getString("alimi_who"));
                        alimi.setAlimi_type(temp.getInt("alimi_type"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi_list1.add(alimi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            alimiListAdapter1 = new AlimiRecyclerViewAdapter(activity, alimi_list1);
            alimi_listView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            alimi_listView1.scrollToPosition(alimi_list1.size()-1);
            alimi_listView1.setAdapter(alimiListAdapter1);
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG1]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
    // 2번태그
    class AlimiResponse2 extends AsyncHttpResponseHandler {
        // 통신 성공시 호출
        /*받은 데이터를 태그별 리스트에 추가했는지 확인하기 위한 변수*/
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {
                    /*받아온 데이터 : 태그(params.put)로 검색한 게시글 리스트*/
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        Alimi alimi = new Alimi();
                        alimi.setAlimi_tag(temp.getString("alimi_tag"));
                        alimi.setAlimi_content(temp.getString("alimi_content"));
                        alimi.setAlimi_index(temp.getInt("alimi_index"));
                        alimi.setAlimi_date(temp.getString("alimi_date"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi.setAlimi_who(temp.getString("alimi_who"));
                        alimi.setAlimi_type(temp.getInt("alimi_type"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi_list2.add(alimi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            alimiListAdapter2 = new AlimiRecyclerViewAdapter(activity, alimi_list2);
            alimi_listView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            alimi_listView2.scrollToPosition(alimi_list2.size()-1);
            alimi_listView2.setAdapter(alimiListAdapter2);
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG2]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
    // 3번태그
    class AlimiResponse3 extends AsyncHttpResponseHandler {
        // 통신 성공시 호출
        /*받은 데이터를 태그별 리스트에 추가했는지 확인하기 위한 변수*/
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {
                    /*받아온 데이터 : 태그(params.put)로 검색한 게시글 리스트*/
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        /**/
                        Alimi alimi = new Alimi();
                        alimi.setAlimi_tag(temp.getString("alimi_tag"));
                        alimi.setAlimi_content(temp.getString("alimi_content"));
                        alimi.setAlimi_index(temp.getInt("alimi_index"));
                        alimi.setAlimi_date(temp.getString("alimi_date"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi.setAlimi_who(temp.getString("alimi_who"));
                        alimi.setAlimi_type(temp.getInt("alimi_type"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi_list3.add(alimi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            alimiListAdapter3 = new AlimiRecyclerViewAdapter(activity, alimi_list3);
            alimi_listView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            alimi_listView3.scrollToPosition(alimi_list3.size()-1);
            alimi_listView3.setAdapter(alimiListAdapter3);
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG3]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
    // 4번태그
    class AlimiResponse4 extends AsyncHttpResponseHandler {
        // 통신 성공시 호출
        /*받은 데이터를 태그별 리스트에 추가했는지 확인하기 위한 변수*/
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {
                    /*받아온 데이터 : 태그(params.put)로 검색한 게시글 리스트*/
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        /**/
                        Alimi alimi = new Alimi();
                        alimi.setAlimi_tag(temp.getString("alimi_tag"));
                        alimi.setAlimi_content(temp.getString("alimi_content"));
                        alimi.setAlimi_index(temp.getInt("alimi_index"));
                        alimi.setAlimi_date(temp.getString("alimi_date"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi.setAlimi_who(temp.getString("alimi_who"));
                        alimi.setAlimi_type(temp.getInt("alimi_type"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi_list4.add(alimi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            alimiListAdapter4 = new AlimiRecyclerViewAdapter(activity, alimi_list4);
            alimi_listView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            alimi_listView4.scrollToPosition(alimi_list4.size()-1);
            alimi_listView4.setAdapter(alimiListAdapter4);
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG4]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
    // 5번태그
    class AlimiResponse5 extends AsyncHttpResponseHandler {
        // 통신 성공시 호출
        /*받은 데이터를 태그별 리스트에 추가했는지 확인하기 위한 변수*/
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {
                    /*받아온 데이터 : 태그(params.put)로 검색한 게시글 리스트*/
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        /**/
                        Alimi alimi = new Alimi();
                        alimi.setAlimi_tag(temp.getString("alimi_tag"));
                        alimi.setAlimi_content(temp.getString("alimi_content"));
                        alimi.setAlimi_index(temp.getInt("alimi_index"));
                        alimi.setAlimi_date(temp.getString("alimi_date"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi.setAlimi_who(temp.getString("alimi_who"));
                        alimi.setAlimi_type(temp.getInt("alimi_type"));
                        alimi.setAlimi_place(temp.getString("alimi_place"));
                        alimi_list5.add(alimi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            alimiListAdapter5 = new AlimiRecyclerViewAdapter(activity, alimi_list5);
            alimi_listView5.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            alimi_listView5.scrollToPosition(alimi_list5.size()-1);
            alimi_listView5.setAdapter(alimiListAdapter5);
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG5]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    /**로그인한 유저의 태그 불러오기**/
    // 통신 응답 클래스 어댑터
    class AlimiTagResponse extends AsyncHttpResponseHandler {
        Activity activity;

        public AlimiTagResponse(Activity activity) {
            this.activity = activity;
        }
        // 통신 시작시 호출
        @Override
        public void onStart() {
        }
        // 통신 종료시 호출
        @Override
        public void onFinish() {
        }

        // 통신 성공시 호출
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                alimiTag = new AlimiTag();
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                alimiTag.setTagcount(json.getInt("count"));
                /*태그 5개 불러오기 */
                if(rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        alimiTag.setTag1(temp.getString("alimi_tag1"));
                        alimiTag.setTag2(temp.getString("alimi_tag2"));
                        alimiTag.setTag3(temp.getString("alimi_tag3"));
                        alimiTag.setTag4(temp.getString("alimi_tag4"));
                        alimiTag.setTag5(temp.getString("alimi_tag5"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*태그 5개 세팅하기*/
            alimi_tag1.setText(alimiTag.getTag1());
            alimi_tag2.setText(alimiTag.getTag2());
            alimi_tag3.setText(alimiTag.getTag3());
            alimi_tag4.setText(alimiTag.getTag4());
            alimi_tag5.setText(alimiTag.getTag5());

            /*TagView1~5 GONE/VISIBLE 세팅&통신 요청*/
            setTagView(alimiTag.getTagcount(), alimiTag);
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG_CALL]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }



    /*알리미 태그 레이아웃 표시*/
    LinearLayout tagView1, tagView2, tagView3, tagView4, tagView5;
    private void setTagView(int mytagcount, AlimiTag alimiTag) {
        tagView1 = rootView.findViewById(R.id.tagView1);
        tagView2 = rootView.findViewById(R.id.tagView2);
        tagView3 = rootView.findViewById(R.id.tagView3);
        tagView4 = rootView.findViewById(R.id.tagView4);
        tagView5 = rootView.findViewById(R.id.tagView5);
        tagView1.setVisibility(View.GONE);
        tagView2.setVisibility(View.GONE);
        tagView3.setVisibility(View.GONE);
        tagView4.setVisibility(View.GONE);
        tagView5.setVisibility(View.GONE);
        switch (mytagcount) {
            case 1:
                setAlimiList(1, alimiTag.getTag1());
                break;
            case 2:
                setAlimiList(1, alimiTag.getTag1());
                setAlimiList(2, alimiTag.getTag2());
                break;
            case 3:
                setAlimiList(1, alimiTag.getTag1());
                setAlimiList(2, alimiTag.getTag2());
                setAlimiList(3, alimiTag.getTag3());
                break;
            case 4:
                setAlimiList(1, alimiTag.getTag1());
                setAlimiList(2, alimiTag.getTag2());
                setAlimiList(3, alimiTag.getTag3());
                setAlimiList(4, alimiTag.getTag4());
                break;
            case 5:
                setAlimiList(1, alimiTag.getTag1());
                setAlimiList(2, alimiTag.getTag2());
                setAlimiList(3, alimiTag.getTag3());
                setAlimiList(4, alimiTag.getTag4());
                setAlimiList(5, alimiTag.getTag5());
                break;
        }
    }

    private void setAlimiList(int tagIndex, String tag){
        alimiResponse1 = new AlimiResponse1();
        alimiResponse2 = new AlimiResponse2();
        alimiResponse3 = new AlimiResponse3();
        alimiResponse4 = new AlimiResponse4();
        alimiResponse5 = new AlimiResponse5();
        RequestParams params = new RequestParams();
        params.put("alimi_tag",tag);
        switch (tagIndex) {
            case 1:
                tagView1.setVisibility(View.VISIBLE);
                clientlist.post(alimilistURL,params,alimiResponse1);
                break;
            case 2:
                tagView2.setVisibility(View.VISIBLE);
                clientlist.post(alimilistURL,params,alimiResponse2);
                break;
            case 3:
                tagView3.setVisibility(View.VISIBLE);
                clientlist.post(alimilistURL,params,alimiResponse3);
                break;
            case 4:
                tagView4.setVisibility(View.VISIBLE);
                clientlist.post(alimilistURL,params,alimiResponse4);
                break;
            case 5:
                tagView5.setVisibility(View.VISIBLE);
                clientlist.post(alimilistURL,params,alimiResponse5);
                break;
        }
    }

    private void sendTags() {
        AlimiInputFragment alimiInputFragment = new AlimiInputFragment();
        AlimiTag item = alimiTag;
        if (item != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            alimiInputFragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, alimiInputFragment).addToBackStack(null).commit();
        }
    }
}