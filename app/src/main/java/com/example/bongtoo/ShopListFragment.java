package com.example.bongtoo;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.bongtoo.adapter.ShopIndexAdapter;
import com.example.bongtoo.adapter.ShopListAdapter;
import com.example.bongtoo.model.Shop;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ShopListFragment extends Fragment implements AbsListView.OnScrollListener {
    // 상점인덱스 리스트 가로구현을 위한 객체 선언
    RecyclerView horizontal_list;
    ShopIndexAdapter shopIndexAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String> shop_index;
    ListFragment listFragment;
    MainActivity activity;

    // 상점 리스트 객체 선언
    Shop shop;
    List<Shop> shop_list;
    ListView shop_listView;
    ShopListAdapter shopListAdapter;
    //서치뷰
    SearchView searchView;
    SearchResponse searchResponse;
    //서버
    AsyncHttpClient client;
    HttpResponse response;
    String shopURL;
    String shopListTypeURL;
    String shopRankURL;
    String shopRankTypeURL;
    boolean shop_search;

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

    @Override
    public void onResume() {
        super.onResume();
        shopListAdapter.clear();

        if (shop_search) {
            category_code = 0;
            client.post(shopURL, response);
        } else {
            category_code = 0;
            client.post(shopRankURL, response);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_shop_list, container, false);
        // shop_search 여부 판단후 화면 셋팅을 위한 객체 초기화
        shop_search = getArguments().getBoolean("shop_search", false);
        searchView = rootView.findViewById(R.id.searchView_shopList);
        // 상점인덱스 리스트 가로구현을 위한 객체 초기화
        horizontal_list = rootView.findViewById(R.id.horizontal_list);
        shopURL = "http://" + activity.SERVERIP + "/bongtoo_server/shop/shopListJson.a";
        shopListTypeURL = "http://" + activity.SERVERIP + "/bongtoo_server/shop/shopListTypeJson.a";
        shopRankURL = "http://" + activity.SERVERIP + "/bongtoo_server/shop/shopListScoreJson.a";
        shopRankTypeURL = "http://" + activity.SERVERIP + "/bongtoo_server/shop/shopListTypeScoreJson.a";

        shop_index = new ArrayList<>();
        addShopIndex();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontal_list.setLayoutManager(linearLayoutManager);
        shopIndexAdapter = new ShopIndexAdapter(getActivity(), shop_index);
        horizontal_list.setAdapter(shopIndexAdapter);

        if (shop_search) {
            // 검색일 경우
            shopIndexAdapter.setOnItemClickListener(new ShopIndexAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                    PAGE = 1;
                    String category = shopIndexAdapter.getItem(position);
                    shopListAdapter.clear();
                    RequestParams params = new RequestParams();
                    switch (category) {
                        case "전체":
                            category_code = 1;
                            client.post(shopURL, response);
                            break;
                        case "예식장":
                            category_code = 2;
                            params.put("shop_type", category);
                            client.post(shopListTypeURL, params, response);
                            break;
                        case "패스트푸드":
                            category_code = 3;
                            params.put("shop_type", category);
                            client.post(shopListTypeURL, params, response);
                            break;
                        case "한식":
                            category_code = 4;
                            params.put("shop_type", category);
                            client.post(shopListTypeURL, params, response);
                            break;
                        case "중식":
                            category_code = 5;
                            params.put("shop_type", category);
                            client.post(shopListTypeURL, params, response);
                            break;
                    }

                }
            });
        } else {
            // 랭킹일 경우
            shopIndexAdapter.setOnItemClickListener(new ShopIndexAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                    PAGE = 1;
                    String category = shopIndexAdapter.getItem(position);
                    shopListAdapter.clear();
                    RequestParams params = new RequestParams();
                    switch (category) {
                        case "전체":
                            category_code = 1;
                            client.post(shopRankURL, response);
                            break;
                        case "예식장":
                            category_code = 2;
                            params.put("shop_type", category);
                            client.post(shopRankTypeURL, params, response);
                            break;
                        case "패스트푸드":
                            category_code = 3;
                            params.put("shop_type", category);
                            client.post(shopRankTypeURL, params, response);
                            break;
                        case "한식":
                            category_code = 4;
                            params.put("shop_type", category);
                            client.post(shopRankTypeURL, params, response);
                            break;
                        case "중식":
                            category_code = 5;
                            params.put("shop_type", category);
                            client.post(shopRankTypeURL, params, response);
                            break;
                    }
                }
            });
        }

//서치뷰
        if (shop_search) {
            searchResponse = new SearchResponse(activity);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        } else {
            searchView.setVisibility(View.GONE);
        }

// 상점 리스트 객체 초기화(프래그먼트 리스트로 보여주기)
        response = new HttpResponse(activity);
        client = new AsyncHttpClient();
        shop_list = new ArrayList<>();
        shop_listView = rootView.findViewById(R.id.shop_listview);
        shop_listView.setOnScrollListener(this);
        shopListAdapter = new ShopListAdapter(getActivity(), R.layout.list_item_shoplist, shop_list);
        listFragment = new ListFragment();

        shop_listView.setAdapter(shopListAdapter);
        shop_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 아이템 받기
                Shop item = (Shop) shopListAdapter.getItem(position);
                int shop_index = item.getShop_index();
                // 이동할 프레그먼트 및 이동후 사용할 데이터 준비
                ShopDetailFragment shopDetailFragment = new ShopDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("shop_index", shop_index);
                bundle.putBoolean("shop_search",shop_search);  //03-31 청일
                shopDetailFragment.setArguments(bundle);
                //백버튼 정의
                activity.setUpBtnBack(true, activity.SHOPLIST);
                // 프레그먼트 전환
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_place, shopDetailFragment).addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    private void searchData(String query) {
        //파라미터 정보를 저장하는 객체
        RequestParams params4 = new RequestParams();
        params4.put("search_word", query);
        //서버에 요청
        client.post("http://" + activity.SERVERIP + "/bongtoo_server/shop/shopListSearchJson.a", params4, searchResponse);
    }

    // 스크롤 리스너 사용을 위한 전역 변수
    int PAGE = 1;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;
    int category_code;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        pageablePage = shop.getTotalAll() / 10 + 1;
        RequestParams params = new RequestParams();
        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                PAGE++;

                if (shop_search) { // 업체 목록인 경우 전체 페이지
                    switch (category_code) {
                        case 0:
                        case 1: // 업체 검색이면서 전체 페이지인 경우
                            params.put("pg", PAGE);
                            client.post(shopURL, params, response);
                            break;
                        case 2: // 업체 검색이면서 예식장인 경우
                            params.put("shop_type", "예식장");
                            params.put("pg", PAGE);
                            client.post(shopListTypeURL, params, response);
                            break;
                        case 3:
                            params.put("shop_type", "패스트푸드");
                            params.put("pg", PAGE);
                            client.post(shopListTypeURL, params, response);
                            break;
                        case 4:
                            params.put("shop_type", "한식");
                            params.put("pg", PAGE);
                            client.post(shopListTypeURL, params, response);
                            break;
                        case 5:
                            params.put("shop_type", "중식");
                            params.put("pg", PAGE);
                            client.post(shopListTypeURL, params, response);
                            break;
                    }
                } else { // 업체 랭킹인 경우
                    switch (category_code) {
                        case 0:
                        case 1: // 업체 랭킹이면서 전체 페이지인 경우
                            params.put("pg", PAGE);
                            client.post(shopRankURL, params, response);
                            break;
                        case 2: // 업체 검색이면서 예식장인 경우
                            params.put("shop_type", "예식장");
                            params.put("pg", PAGE);
                            client.post(shopRankTypeURL, params, response);
                            break;
                        case 3:
                            params.put("shop_type", "패스트푸드");
                            params.put("pg", PAGE);
                            client.post(shopRankTypeURL, params, response);
                            break;
                        case 4:
                            params.put("shop_type", "한식");
                            params.put("pg", PAGE);
                            client.post(shopRankTypeURL, params, response);
                            break;
                        case 5:
                            params.put("shop_type", "중식");
                            params.put("pg", PAGE);
                            client.post(shopRankTypeURL, params, response);
                            break;
                    }
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
                int totalAll = json.getInt("totalAll");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        shop = new Shop();
                        shop.setShop_name(temp.getString("shop_name"));
                        shop.setShop_type(temp.getString("shop_type"));
                        shop.setShop_detail(temp.getString("shop_detail"));
                        shop.setShop_phone(temp.getString("shop_phone"));
                        shop.setShop_email(temp.getString("shop_email"));
                        shop.setShop_img_path(temp.getString("shop_img_path"));
                        shop.setShop_grade(temp.getString("shop_grade"));
                        shop.setShop_index(temp.getInt("shop_index"));
                        shop.setTotalAll(totalAll);
                        shopListAdapter.add(shop);
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
            //Log.d("[ERROR_SHOP]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
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
            shopListAdapter.clear();
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        shop.setShop_name(temp.getString("shop_name"));
                        shop.setShop_type(temp.getString("shop_type"));
                        shop.setShop_detail(temp.getString("shop_detail"));
                        shop.setShop_phone(temp.getString("shop_phone"));
                        shop.setShop_email(temp.getString("shop_email"));
                        shop.setShop_img_path(temp.getString("shop_img_path"));
                        shop.setShop_grade(temp.getString("shop_grade"));
                        shopListAdapter.add(shop);
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
            //Log.d("[ERROR_SHOP_SEARCH]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    public void addShopIndex() {
        shop_index.add("전체");
        shop_index.add("예식장");
        shop_index.add("패스트푸드");
        shop_index.add("한식");
        shop_index.add("중식");
    }
}