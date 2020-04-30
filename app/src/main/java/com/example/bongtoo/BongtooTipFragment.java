package com.example.bongtoo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bongtoo.adapter.TipAdapter;
import com.rd.PageIndicatorView;
import com.rd.draw.controller.DrawController;

import java.util.ArrayList;
import java.util.List;

import me.angeldevil.autoscrollviewpager.AutoScrollViewPager;


public class BongtooTipFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_bongtoo_tip,container,false);
        /*viewPager 설정*/
        viewPager = (AutoScrollViewPager) rootView.findViewById(R.id.bongTip_viewPager);
        pageIndicatorView = (PageIndicatorView) rootView.findViewById(R.id.bongTip_pageIndicatorView);
        setViewPager();

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
    // 뷰페이저 세팅
    //////////////////////////////////////////////////////////////////////////////////////
    /** 팁 viewPager 설정**/
    private void setViewPager() {
        List<Integer> list = new ArrayList<>();
        TipAdapter adapter = new TipAdapter(activity, list);
        //이미지 추가///////////////////////////////
        list.add(R.drawable.cardnews2_tip);
        list.add(R.drawable.cardnews3_tip);
        list.add(R.drawable.cardnews4_tip);
        list.add(R.drawable.cardnews_tip);
        list.add(R.drawable.funeral_tip);
        list.add(R.drawable.manner_tip_);
        /////////////////////////////////////////////
        pageIndicatorView.setClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        viewPager.setAdapter(adapter);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 컴포넌트 전역변수
    //////////////////////////////////////////////////////////////////////////////////////
    /*viewPager 버튼*/
    AutoScrollViewPager viewPager;
    PageIndicatorView pageIndicatorView;
}
