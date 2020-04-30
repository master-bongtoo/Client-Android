package com.example.bongtoo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bongtoo.model.Shop;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ShopDetailFragment extends Fragment implements View.OnClickListener {
    // 화면 객체 선언
    TextView shopdetial_shopname, shopdetial_shopAddress, shopdetial_score, shopdetial_content, shopdetial_score_view, shopdetail_result;
    ImageView shopdetial_image;
    FrameLayout shopdetial_score_insert;
    LinearLayout shop_homepage, shop_call, shop_email;
    RatingBar ratingbar;
    boolean touch = false;
    float score;
    // 인텐트용 객체 선언
    String email, phone, homepage;

    // 통신 관련 객체 선언
    AsyncHttpClient client;
    HttpResponse response;
    String shopDetailURL, shopDetailLike;
    Shop shop;

    public ShopDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_shop_detail, container, false);
        // 화면 객체 초기화
        int shop_index = getArguments().getInt("shop_index");
        score = 5;
        shopdetial_shopname = rootView.findViewById(R.id.shopdetial_shopname);
        shopdetial_shopAddress = rootView.findViewById(R.id.shopdetial_shopAddress);
        shopdetial_score = rootView.findViewById(R.id.shopdetial_score);
        shopdetial_content = rootView.findViewById(R.id.shopdetial_content);
        shopdetial_score_view = rootView.findViewById(R.id.shopdetial_score_view);
        shopdetial_image = rootView.findViewById(R.id.shopdetial_image);
        shopdetial_score_insert = rootView.findViewById(R.id.shopdetial_score_insert);
        ratingbar = rootView.findViewById(R.id.ratingbar);
        shop_homepage = rootView.findViewById(R.id.shop_homepage);
        shop_call = rootView.findViewById(R.id.shop_call);
        shop_email = rootView.findViewById(R.id.shop_email);
        shopdetail_result = rootView.findViewById(R.id.shopdetail_result);
        shopdetail_result.setVisibility(View.GONE);
        // 클릭시 해당기능 이동 및 발동
        shop_homepage.setOnClickListener(this);
        shop_call.setOnClickListener(this);
        shop_email.setOnClickListener(this);
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                touch = true;
                shopdetial_score_view.setText(""+rating);
                score = rating;
            }
        });
        shopdetial_score_insert.setOnClickListener(this);

        // 통신 객체 초기화
        shopDetailURL = "http://"+activity.SERVERIP+"/bongtoo_server/shop/shopViewJson.a";
        shopDetailLike = "http://"+activity.SERVERIP+"/bongtoo_server/shop/shopUpdateScoreJson.a";
        client = new AsyncHttpClient();
        response = new HttpResponse(getActivity());

        RequestParams params = new RequestParams();
        params.put("shop_index", shop_index);
        params.put("member_num", activity.getMember_num());
        client.post(shopDetailURL, params, response);

        //헤더 세팅   //03-31 청일
        activity.setHeaderTitle("업체상세보기");
        boolean shop_search = getArguments().getBoolean("shop_search");
        if (shop_search){//
            activity.setUpBtnBack(true, activity.SHOPSEARCH);
        }else{
            activity.setUpBtnBack(true, activity.SHOPLIST);
        }

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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.shop_email :
                if(email.equals("")){
                    Toast.makeText(activity, "등록된 이메일이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+email));
                    startActivity(intent);
                }
                break;
            case R.id.shop_call :
                if(phone.equals("")){
                    Toast.makeText(activity, "등록된 연락처가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                    startActivity(intent);
                }
                break;
            case R.id.shop_homepage :
                if(homepage.equals("")){
                    Toast.makeText(activity, "등록된 홈페이지가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
                    startActivity(intent);
                }
                break;
            case R.id.shopdetial_score_insert :
                if(activity.getMember_num() < 0) {
                    Toast.makeText(getActivity(), "먼저 로그인을 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("member_num", activity.getMember_num());
                params.put("shop_index", shop.getShop_index());

                if(touch) {
                    params.put("score", score);
                } else {
                    params.put("score", ""+5.0);
                }
                client.post(shopDetailLike, params, response);
                break;
        }
    }

    class HttpResponse extends AsyncHttpResponseHandler {
        Activity response_activity;

        public HttpResponse(Activity activity) {
            response_activity = activity;
        }

        // 통신 성공시 호출
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {

            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if(rt.equals("OK") && total > 0) {

                    JSONArray item = json.getJSONArray("item");
                    JSONObject temp = item.getJSONObject(0);

                    shop = new Shop();
                    shop.setShop_name(temp.getString("shop_name"));
                    shop.setShop_type(temp.getString("shop_type"));
                    shop.setShop_detail(temp.getString("shop_detail"));
                    shop.setShop_phone(temp.getString("shop_phone"));
                    shop.setShop_email(temp.getString("shop_email"));
                    shop.setShop_img_path(temp.getString("shop_img_path"));
                    shop.setShop_grade(temp.getString("shop_grade"));
                    shop.setShop_index(temp.getInt("shop_index"));
                    shop.setShop_addr(temp.getString("shop_addr"));
                    shop.setShop_url(temp.getString("shop_url"));
                    String score_str = temp.getString("shop_member_score");
                    score = Float.valueOf(score_str);
                    // 셋팅하기
                    Glide.with(response_activity).load(shop.getShop_img_path()).into(shopdetial_image);
                    shopdetial_shopname.setText(shop.getShop_name());
                    shopdetial_content.setText(shop.getShop_detail());
                    shopdetial_shopAddress.setText(shop.getShop_addr());
                    shopdetial_score.setText(shop.getShop_grade());

                    email = shop.getShop_email();
                    phone = shop.getShop_phone();
                    homepage = shop.getShop_url();
                    if((activity.getMember_num() > 0 && 0 <= score) || activity.getMember_num() == -1) {
                        if (activity.getMember_num() == -1) {
                            shopdetail_result.setText("로그인 후 업체 평가가 가능합니다!");
                            shopdetail_result.setVisibility(View.VISIBLE);
                            shopdetial_score_insert.setVisibility(View.GONE);
                            ratingbar.setIsIndicator(true);
                        } else if (activity.getMember_num()!=-1) {
                            if(score == -1) {
                                score = 5;
                            }
                            shopdetial_score_view.setText(""+score);
                            ratingbar.setRating(score);
                            ratingbar.setIsIndicator(true);
                            shopdetial_score_insert.setVisibility(View.GONE);
                            shopdetail_result.setText("이미 이 업체를 평가하셨습니다!");
                            shopdetail_result.setVisibility(View.VISIBLE);
                        }

                    }

                } else if (rt.equals("LIKE_OK")) {
                    Toast.makeText(getActivity(), "추천하였습니다~", Toast.LENGTH_SHORT).show();
                    shopdetial_score_insert.setVisibility(View.GONE);
                    shopdetail_result.setVisibility(View.VISIBLE);
                    ratingbar.setIsIndicator(true);
                } else if (rt.equals("LIKE_FAIL")) {
                    Toast.makeText(getActivity(), "이미 추천하였습니다", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(response_activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_SHOP_DETAIL]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

}