package com.example.bongtoo;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bongtoo.adapter.PageAdapter;
import com.rd.PageIndicatorView;
import com.rd.draw.controller.DrawController;

import java.util.ArrayList;
import java.util.List;

import me.angeldevil.autoscrollviewpager.AutoScrollViewPager;

public class MainHomeFragment extends Fragment{


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_main_home,container,false);
        /*viewPager 설정*/
        viewPager = (AutoScrollViewPager) rootView.findViewById(R.id.viewPager);
        pageIndicatorView = (PageIndicatorView) rootView.findViewById(R.id.pageIndicatorView);
        setViewPager();

        /*초기화&이벤트 설정*/
        ImageView btnGoHotCommunityList =(ImageView)rootView.findViewById(R.id.btnGoHotCommunityList);
        ImageView btnGoCommunityList = (ImageView)rootView.findViewById(R.id.btnGoCommunityList);
        ImageView btnGoMybongtoo = (ImageView)rootView.findViewById(R.id.btnGoMybongtoo);
        ImageView btnGoShopSearch = (ImageView)rootView.findViewById(R.id.btnGoShopSearch);
        ImageView btnGoShopList = (ImageView)rootView.findViewById(R.id.btnGoShopList);
        ImageView btnGoBongTip = (ImageView)rootView.findViewById(R.id.btnGoBongTip);
        btnGoHotCommunityList.setOnClickListener(mainHomeEvent);
        btnGoCommunityList.setOnClickListener(mainHomeEvent);
        btnGoMybongtoo.setOnClickListener(mainHomeEvent);
        btnGoShopSearch.setOnClickListener(mainHomeEvent);
        btnGoShopList.setOnClickListener(mainHomeEvent);

        btnGoBongTip.setOnClickListener(mainHomeEvent);


        /*Header설정*/
        activity.setUpBtnBack(false, activity.MAINHOME);
        return rootView;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // mainHome 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    /*mainHome 이미지 클릭 이벤트*/
    ImageView.OnClickListener mainHomeEvent = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnGoCommunityList:
                    activity.setFragment(activity.COMMLIST);
                    break;
                case R.id.btnGoMybongtoo:
                    if(activity.islogin) {
                        activity.setFragment(activity.MAINMYBONGTOO);
                    } else {
                        Toast.makeText(activity, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnGoShopSearch:
                    activity.setFragment(activity.SHOPSEARCH);
                    break;
                case R.id.btnGoShopList:
                    activity.setFragment(activity.SHOPLIST);
                    break;
                case R.id.btnGoBongTip:
                    activity.setFragment(activity.BONGTOO_TIP);
                    break;
                case R.id.btnGoHotCommunityList:
                    activity.setFragment(activity.HOTCOMMLIST);
                    activity.setHeaderTitle("인기글");
                    break;
            }
        }
    };

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
    // 뷰페이저 세팅
    //////////////////////////////////////////////////////////////////////////////////////
    /**Main - 광고영역 viewPager 설정**/
    private void setViewPager() {
        List<Integer> list = new ArrayList<>();
        PageAdapter adapter = new PageAdapter(activity, list);
        //이미지 추가///////////////////////////////
        list.add(R.drawable.advertise_marriage);
        list.add(R.drawable.advertise_flower2);
        list.add(R.drawable.advertise_flower3);
        list.add(R.drawable.advertise_funeral);
        list.add(R.drawable.advertise_letter_of_invitation1);
        list.add(R.drawable.advertise_letter_of_invitation2);
        list.add(R.drawable.ads1);
        list.add(R.drawable.ads2);
        /////////////////////////////////////////////
        pageIndicatorView.setClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 컴포넌트 전역변수
    //////////////////////////////////////////////////////////////////////////////////////
    /*viewPager 버튼*/
    AutoScrollViewPager viewPager;
    PageIndicatorView pageIndicatorView;
}
