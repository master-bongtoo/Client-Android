package com.example.bongtoo;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bongtoo.model.Member;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MyPageFragment extends Fragment {
    FrameLayout myinfo_button1_setting, myinfo_button2_board, myinfo_button3_notice, myinfo_button4_FAQ, myinfo_button5_question;
    LinearLayout myinfo_layout_login, myinfo_layout_logout;
    ImageView myinfo_myimg, myinfo_crown;
    TextView myinfo_nickname;
    Member memberInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_my_page,container,false);
        imageLoaderInit();
        /*메인에서 memberInfo 받아오기*/
        memberInfo = activity.memberInfo;
        /*초기화*/
        myinfo_layout_login = rootView.findViewById(R.id.myinfo_layout_login);
        myinfo_layout_logout = rootView.findViewById(R.id.myinfo_layout_logout);
        myinfo_button1_setting = rootView.findViewById(R.id.myinfo_button1_setting);
        myinfo_button2_board = rootView.findViewById(R.id.myinfo_button2_board);
        myinfo_button3_notice = rootView.findViewById(R.id.myinfo_button3_notice);
        myinfo_button4_FAQ = rootView.findViewById(R.id.myinfo_button4_FAQ);
        myinfo_button5_question = rootView.findViewById(R.id.myinfo_button5_question);
        myinfo_nickname = rootView.findViewById(R.id.myinfo_nickname);
        myinfo_crown = rootView.findViewById(R.id.myinfo_crown);
        myinfo_button1_setting.setOnClickListener(myPageEvent);
        myinfo_button2_board.setOnClickListener(myPageEvent);
        myinfo_button3_notice.setOnClickListener(myPageEvent);
        myinfo_button4_FAQ.setOnClickListener(myPageEvent);
        myinfo_button5_question.setOnClickListener(myPageEvent);
        myinfo_layout_login.setVisibility(View.GONE);
        myinfo_layout_logout.setVisibility(View.GONE);

        //프로필 이미지 세팅
        myinfo_myimg = rootView.findViewById(R.id.myinfo_myimg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            myinfo_myimg.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if(Build.VERSION.SDK_INT >= 21) {
            myinfo_myimg.setClipToOutline(true);
        }
        /*memberInfo 세팅*/
        if (memberInfo!=null) {
            myinfo_layout_login.setVisibility(View.VISIBLE);
            myinfo_nickname.setText(memberInfo.getNickname());
            myinfo_myimg.setVisibility(View.VISIBLE);
            //프로필 사진이 없으면 기본 사진으로
            Log.d("[MyPageFragment]", "memberInfo.getMember_img_path() : " +memberInfo.getMember_img_path());
            if(memberInfo.getMember_img_path().equals("")){
                Glide.with(activity).load(R.drawable.icon_person).into(myinfo_myimg);
            } else {
                Glide.with(activity).load(memberInfo.getMember_img_path()).into(myinfo_myimg);
//                imageLoader.displayImage(memberInfo.getMember_img_path(), myinfo_myimg, options);
            }
            //회원 등급에 따라 왕관이미지 세팅
            switch (memberInfo.getGrade()) {
                case 1 :
                    myinfo_crown.setImageResource(R.drawable.icon_crowntop_bronze);
                    break;
                case 2 :
                    myinfo_crown.setImageResource(R.drawable.icon_crowntop_silver);
                    break;
                case 3 :
                    myinfo_crown.setImageResource(R.drawable.icon_crowntop_gold);
                    break;
                case 4 :
                    myinfo_crown.setImageResource(R.drawable.icon_crowntop_green);
                    break;
                case 5 :
                    myinfo_crown.setImageResource(R.drawable.icon_crowntop_blue);
                    break;
            }

        } else {
            myinfo_layout_logout.setVisibility(View.VISIBLE);
        }



        //버전 세팅
        TextView myinfo_button6_version = rootView.findViewById(R.id.myinfo_button6_version);
        myinfo_button6_version.setText("현재 버전 : "+activity.VERSION);

        return rootView;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // myPage 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    /*myPage 이미지 클릭 이벤트*/
    FrameLayout.OnClickListener myPageEvent = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.myinfo_button1_setting:
                    if(memberInfo==null) {
                        Toast.makeText(activity, "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        activity.setFragment(activity.MYPAGE_UPDATE);
                    }
                    break;
                case R.id.myinfo_button2_board:
                    if(memberInfo==null) {
                        Toast.makeText(activity, "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        activity.setFragment(activity.MYPAGE_MYBOARD);
                    }
                    break;
                case R.id.myinfo_button3_notice:
                    activity.setFragment(activity.MYPAGE_NOTICE);
                    break;
                case R.id.myinfo_button4_FAQ:
                    activity.setFragment(activity.MYPAGE_FAQ);
                    break;
                case R.id.myinfo_button5_question:
                    if(memberInfo==null) {
                        Toast.makeText(activity, "로그인 후 이용할 수 있습니다.", Toast.LENGTH_SHORT).show(); //03-31 청일
                    } else {
                        activity.setFragment(activity.MYPAGE_QUESTION);
                    }
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
    ImageLoader imageLoader;
    DisplayImageOptions options;
    private void imageLoaderInit() {
        // 이미지로더 초기화
        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(activity);
            imageLoader.init(configuration);
        }
        // 옵션 설정
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
//        builder.showImageOnLoading(R.drawable.ic_stub);
//        builder.showImageForEmptyUri(R.drawable.ic_empty);
//        builder.showImageOnFail(R.drawable.ic_error);
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);
        options = builder.build();
    }


    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

}
