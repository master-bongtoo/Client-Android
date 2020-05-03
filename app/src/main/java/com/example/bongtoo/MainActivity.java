package com.example.bongtoo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bongtoo.model.Member;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //////////////////////////////////////////////////////////////////////////////////////
    // 전역 변수 설정
    //////////////////////////////////////////////////////////////////////////////////////
    /**전역 변수 설정**/
    /*Client Version*/
    final String VERSION = "0.1.7";
    // 깃허브 업로드시 ip 0으로 수정
    final String SERVERIP = "000.000.000.000:8080";

    /*레이아웃&헤더&푸터*/
    TextView txtTitle, main_nickname;
    ImageView btn_settings, btnHeaderBack;
    LinearLayout footer_BtnHome, footer_BtnAlarm, footer_BtnMypage, footer_BtnMore;
    ImageView footer_iconHome, footer_iconAlarm, footer_iconMypage, footer_iconMore;
    TextView footer_txtHome, footer_txtAlarm, footer_txtMypage, footer_txtMore;
    FrameLayout header_settings, setting_BtnGoSettings, setting_BtnGoHelp, setting_BtnGoLogin;
    boolean isSettingsOpen;

    /*Back 버튼*/
    private long backBtnTime = 0;

    /*Fragment*/
    MainHomeFragment mainHomeFragment;
    CommListFragment commListFragment;
    ShopListFragment shopListFragment;
    MyPageFragment myPageFragment;
    MyPageUpdateFragment myPageUpdateFragment;
    MyPageMyBoardFragment myPageMyBoardFragment;
    MyBongtooFragment myBongtooFragment;
    MyBongtooInsertFragment myBongtooInsertFragment;
    MyBongtooShowFragment myBongtooShowFragment;
    MyBongtooSummaryFragment myBongtooSummaryFragment;
    BongtooTipFragment bongtooTipFragment;
    MyPageNoticeFragment myPageNoticeFragment;
    MyPageQuestionFragment myPageQuestionFragment;
    MyPageFAQFragment myPageFAQFragment;
    MoreEventFragment moreEventFragment;
    MoreB2bFragment moreB2bFragment;
    HelpListFragment helpListFragment;
    AlimiFragment alimiFragment;
    ShopDetailFragment shopDetailFragment;
    AlimiInputFragment alimiInputFragment;
    CommDetailFragment commDetailFragment;
    CommModifyFragment commModifyFragment;
    CommWriteFragment commWriteFragment;


    /*fragment&Footer Code*/
    //HOME
    final int MAINHOME = 1;
    final int COMMLIST = 11;
    final int COMMDETAIL = 111;
    final int COMMMODIFY = 112;
    final int COMMWRITE = 113;
    final int MAINMYBONGTOO = 12;
    final int MYBONGTOO_INSERT = 121;
    final int MYBONGTOO_SUMMARY = 122;
    final int MYBONGTOO_SHOW = 124;
    final int SHOPLIST = 13;
    final int SHOPDETAIL = 14;
    final int BONGTOO_TIP = 15;
    final int SHOPSEARCH = 16;
    final int HOTCOMMLIST = 17;
    //ALIMI
    final int MAINALIMI = 2;
    final int ALIMIINPUT = 21;
    //MYPAGE
    final int MYPAGE = 3;
    final int MYPAGE_UPDATE = 31;
    final int MYPAGE_MYBOARD = 32;
    final int MYPAGE_NOTICE = 33;
    final int MYPAGE_QUESTION = 34;
    final int MYPAGE_FAQ = 35;
    //MORE
    final int MAINMORE = 4;
    final int EVENT =41;
    final int EVALUATION =42;
    //SETTINGS
    final int HELPLIST = 5 ;

    /*User Status*/
    boolean isAlimiTagset = false;

    /*Server URL*/

    final String URL_comm_delete = "";
    final String URL_member_login = "http://"+SERVERIP+"/bongtoo_server/member/memberViewJson.a";
    final String URL_member_hastag = "http://"+SERVERIP+"/bongtoo_server/alimi/alimiTagViewJson.a";

    /*Client Setting*/
    AsyncHttpClient client;
    MemberInfoHttpResponse memberInfoHttpResponse;
    HasTagHttpResponse hasTagHttpResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*클라이언트, 서버 초기화*/
        client = new AsyncHttpClient();
        memberInfoHttpResponse = new MemberInfoHttpResponse();
        hasTagHttpResponse = new HasTagHttpResponse();

        /*퍼미션 체크*/
        permissionCheck();

        /*헤더&푸터 초기화*/
        headFootInitialize();

        /*프래그먼트 세팅*/
        setFragment(MAINHOME);

        /*에니메이션 세팅*/
        setAnimation();

        /*더보기 세팅*/
        more(1);

        /*로그인 세팅*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        member_num = preferences.getInt("member_num", -1);
        Log.d("[LOGINSETTING]", "member_num="+member_num);
        if(member_num > -1) {
            setLogin(1);
        } else {
            setLogin(0);
        }

        /*닉네임 세팅*/
        if(islogin) {
            setLogin(1);
        } else {
            setLogin(0);
        }
    }

    /**Back키 2번 누르면 종료**/
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            ActivityCompat.finishAffinity(this);
            System.runFinalization();
            System.exit(0);
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }

    /**Onclick 이벤트**/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*설정 페이지*/
            case R.id.setting_BtnGoSettings:
                gotoSettings();
                break;
            case R.id.setting_BtnGoHelp:
                setFragment(HELPLIST);
                break;
            case R.id.setting_BtnGoLogin:
                setLogin(0);
                gotoLogin();
                break;
            /*더보기 페이지*/
            case R.id.footer_BtnMore:                       //더보기 버튼 클릭 시 애니메이션 출력
                openCloseMorePage();
                break;
            case R.id.page_back:
                openCloseMorePage();
                break;
            /*헤더 버튼*/
            case R.id.btn_settings:             //Home -> Login 이동
                openSettings();
                break;
            case R.id.btnHeaderBack:
                setFragment(0);
                break;
            /*푸터 버튼*/
            case R.id.footer_BtnHome:
                setUpFooterImage(MAINHOME);
                setFragment(MAINHOME);
                break;
            case R.id.footer_BtnAlarm:
                if(islogin) {
                    setUpFooterImage(MAINALIMI);
                    setFragment(MAINALIMI);
                } else {
                    Toast.makeText(this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show();
                }
                
                break;
            case R.id.footer_BtnMypage:
                setUpFooterImage(MYPAGE);
                setFragment(MYPAGE);
                break;
            /*더보기 버튼*/
            case R.id.more_button1_gongsi:
                openCloseMorePage();
                setUpFooterImage(MAINMORE);
                setFragment(MYPAGE_NOTICE);
                setUpBtnBack(false, 0);
                break;
            case R.id.more_button2_event:
                openCloseMorePage();
                setUpFooterImage(MAINMORE);
                setFragment(EVENT);
                setUpBtnBack(false, 0);
                break;
            case R.id.more_button3_company:
                openCloseMorePage();
                setUpFooterImage(MAINMORE);
                setFragment(EVALUATION);
                setUpBtnBack(false, 0);
                break;
            case R.id.more_button4_evaluation:
                setUpBtnBack(false, 0);
                Toast.makeText(this, "평가는 현재 준비중입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.more_button5_settings:
                gotoSettings();
                break;
            case R.id.more_button6_logout:
                setLogin(0);
                gotoLogin();
                break;
        }
    }

    /*setFragment(viewCode) : 프래그먼트 교체 함수*/
    public void setFragment(int viewCode) {
        isSettingsOpen=true;
        openSettings();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        switch (viewCode) {
            /*viewCode == 0*/
            //아무것도 하지 않음
            case 0 :
                break;
            /*MAINHOME*/
            case MAINHOME :
                onResume();
                setHeaderTitle("봉 투");
                mainHomeFragment = new MainHomeFragment();
                ft.replace(R.id.fragment_place, mainHomeFragment).addToBackStack(null);
                break;
            //커뮤니티
            case HOTCOMMLIST:
                setHeaderTitle("인기글");
                setUpBtnBack(true, MAINHOME);
                commListFragment = new CommListFragment();
                bundle.putBoolean("hot", true);
                commListFragment.setArguments(bundle);
                ft.replace(R.id.fragment_place, commListFragment).addToBackStack(null);
                break;
            case COMMLIST :
                setHeaderTitle("커뮤니티");
                setUpBtnBack(true, MAINHOME);
                commListFragment = new CommListFragment();
                bundle.putBoolean("hot", false);
                commListFragment.setArguments(bundle);
                ft.replace(R.id.fragment_place, commListFragment).addToBackStack(null);
                break;
            case COMMDETAIL :
                setHeaderTitle("상세보기");
                setUpBtnBack(true, COMMLIST);
                break;
            case COMMMODIFY :
                setHeaderTitle("글 수정하기");
                /*[작업필요] 글 상세보기 넘어갈 때 값 넘기기*/
                setUpBtnBack(true, COMMDETAIL);
                break;
            case COMMWRITE :
                setHeaderTitle("글쓰기");
                setUpBtnBack(true, COMMLIST);
                commWriteFragment = new CommWriteFragment();
                ft.replace(R.id.fragment_place, commWriteFragment).addToBackStack(null);
                break;
            //MY봉투
            case MAINMYBONGTOO :
                setHeaderTitle("MY 봉투");
                setUpBtnBack(true, MAINHOME);
                myBongtooFragment = new MyBongtooFragment();
                ft.replace(R.id.fragment_place, myBongtooFragment).addToBackStack(null);
                break;
            case MYBONGTOO_INSERT :
                setHeaderTitle("경조사비 입력");
                setUpBtnBack(true, MAINMYBONGTOO);
                myBongtooInsertFragment = new MyBongtooInsertFragment();
                ft.replace(R.id.fragment_place, myBongtooInsertFragment).addToBackStack(null);
                break;
            case MYBONGTOO_SUMMARY :
                setHeaderTitle("경조사비 종합");
                setUpBtnBack(true, MAINMYBONGTOO);
                myBongtooSummaryFragment = new MyBongtooSummaryFragment();
                ft.replace(R.id.fragment_place, myBongtooSummaryFragment).addToBackStack(null);
                break;
            case MYBONGTOO_SHOW :
                setHeaderTitle("MY 경조사비");
                setUpBtnBack(true, MAINMYBONGTOO);
                myBongtooShowFragment = new MyBongtooShowFragment();
                ft.replace(R.id.fragment_place, myBongtooShowFragment).addToBackStack(null);
                break;
            //업체목록
            case SHOPSEARCH :
                setHeaderTitle("업체 검색");
                setUpBtnBack(true, MAINHOME);
                shopListFragment = new ShopListFragment();
                bundle.putBoolean("shop_search", true);
                shopListFragment.setArguments(bundle);
                ft.replace(R.id.fragment_place, shopListFragment).addToBackStack(null);
                break;
            case SHOPLIST :
                setHeaderTitle("업체 랭킹");
                setUpBtnBack(true, MAINHOME);
                shopListFragment = new ShopListFragment();
                bundle.putBoolean("shop_search", false);
                shopListFragment.setArguments(bundle);
                ft.replace(R.id.fragment_place, shopListFragment).addToBackStack(null);
                break;
//            case SHOPDETAIL :
//                setHeaderTitle("업체 목록");
//                setUpBtnBack(true, MAINHOME);
//                shopDetailFragment = new ShopDetailFragment();
//                ft.replace(R.id.fragment_place, shopDetailFragment).addToBackStack(null);
//                break;
            //봉투 팁
            case BONGTOO_TIP:      //롸 추가
                setHeaderTitle("TIP");
                setUpBtnBack(true, MAINHOME);
                bongtooTipFragment = new BongtooTipFragment();
                ft.replace(R.id.fragment_place, bongtooTipFragment).addToBackStack(null);
                break;
            /*MYPAGE*/
            case MYPAGE:
                setHeaderTitle("내 정보");
                setUpBtnBack(false, 0);
                myPageFragment = new MyPageFragment();
                ft.replace(R.id.fragment_place, myPageFragment).addToBackStack(null);
                break;
            case MYPAGE_UPDATE :
                setHeaderTitle("내 정보 수정");
                setUpBtnBack(true, MYPAGE);
                myPageUpdateFragment = new MyPageUpdateFragment();
                ft.replace(R.id.fragment_place, myPageUpdateFragment).addToBackStack(null);
                break;
            case MYPAGE_MYBOARD:
                setHeaderTitle("내가 작성한 글");
                setUpBtnBack(true, MYPAGE);
                myPageMyBoardFragment = new MyPageMyBoardFragment();
                ft.replace(R.id.fragment_place, myPageMyBoardFragment).addToBackStack(null);
                break;
            case MYPAGE_NOTICE:
                setHeaderTitle("고객센터");
                setUpBtnBack(true, MYPAGE);
                myPageNoticeFragment = new MyPageNoticeFragment();
                ft.replace(R.id.fragment_place, myPageNoticeFragment).addToBackStack(null);
                break;
            case MYPAGE_QUESTION:
                setHeaderTitle("고객센터");
                setUpBtnBack(true, MYPAGE);
                myPageQuestionFragment = new MyPageQuestionFragment();
                ft.replace(R.id.fragment_place, myPageQuestionFragment).addToBackStack(null);
                break;
            case MYPAGE_FAQ:
                setHeaderTitle("고객센터");
                setUpBtnBack(true, MYPAGE);
                myPageFAQFragment = new MyPageFAQFragment();
                ft.replace(R.id.fragment_place,myPageFAQFragment).addToBackStack(null);
                break;
            /*MORE*/
            case EVENT:
                setHeaderTitle("이벤트");
                setUpBtnBack(true, EVENT);
                moreEventFragment = new MoreEventFragment();
                ft.replace(R.id.fragment_place, moreEventFragment).addToBackStack(null);
                break;
            case EVALUATION:
                setHeaderTitle("업체 문의");
                setUpBtnBack(true, MAINHOME);
                moreB2bFragment = new MoreB2bFragment();
                ft.replace(R.id.fragment_place,moreB2bFragment).addToBackStack(null);
                break;
            /*HELP*/
            case HELPLIST:
                setHeaderTitle("도움말");
                setUpBtnBack(true, MAINHOME);
                helpListFragment = new HelpListFragment();
                ft.replace(R.id.fragment_place, helpListFragment).addToBackStack(null);
                break;
            /*ALIMI*/
            case MAINALIMI:
                setHeaderTitle("알리미");
                setUpBtnBack(false, 0);
                alimiFragment = new AlimiFragment();
                ft.replace(R.id.fragment_place, alimiFragment).addToBackStack(null);
                break;
            case ALIMIINPUT:
                setHeaderTitle("알리미 등록");
                setUpBtnBack(true, MAINALIMI);
                alimiInputFragment = new AlimiInputFragment();
                ft.replace(R.id.fragment_place, alimiInputFragment).addToBackStack(null);
                break;
        }
        ft.commit();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 설정 함수
    //////////////////////////////////////////////////////////////////////////////////////
    /**타이틀 세팅**/
    public void setHeaderTitle(String headerTitle) {
        txtTitle.setText(headerTitle);
    }
    /**화면 이동 : Intent로 이동**/
    /*로그인 화면*/
    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 50);
    }
    /*설정 화면*/
    private void gotoSettings() {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("member_num", member_num);

        startActivity(intent);
    }

    /**하단바 색상 변경**/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyColors();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void applyColors() {
        getWindow().setStatusBarColor(Color.parseColor("#274555"));
        getWindow().setNavigationBarColor(Color.parseColor("#274555"));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 헤더 & 푸터 설정
    //////////////////////////////////////////////////////////////////////////////////////
    /**헤더&푸터 컴포넌트 초기화**/
    private void headFootInitialize() {
        /*헤더 초기화*/
        main_nickname= findViewById(R.id.main_nickname);
        setting_txtLogin = findViewById(R.id.setting_txtLogin);
        more_txtLogin = findViewById(R.id.more_txtLogin);
        main_welcome  = findViewById(R.id.main_welcome);
        txtTitle = findViewById(R.id.txtTitle);
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        btn_settings =findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(this);
        setUpBtnBack(false, 0);

        /*푸터 초기화*/
        footer_BtnHome = findViewById(R.id.footer_BtnHome);
        footer_BtnAlarm = findViewById(R.id.footer_BtnAlarm);
        footer_BtnMypage = findViewById(R.id.footer_BtnMypage);
        footer_BtnMore = findViewById(R.id.footer_BtnMore);
        footer_iconHome= findViewById(R.id.footer_iconHome);
        footer_iconAlarm= findViewById(R.id.footer_iconAlarm);
        footer_iconMypage= findViewById(R.id.footer_iconMypage);
        footer_iconMore= findViewById(R.id.footer_iconMore);
        footer_txtHome = findViewById(R.id.footer_txtHome);
        footer_txtAlarm = findViewById(R.id.footer_txtAlarm);
        footer_txtMypage = findViewById(R.id.footer_txtMypage);
        footer_txtMore = findViewById(R.id.footer_txtMore);
        footer_BtnHome.setOnClickListener(this);
        footer_BtnAlarm.setOnClickListener(this);
        footer_BtnMypage.setOnClickListener(this);
        footer_BtnMore.setOnClickListener(this);

        /*설정화면 초기화*/
        header_settings = findViewById(R.id.header_settings);
        setting_BtnGoSettings = findViewById(R.id.setting_BtnGoSettings);
        setting_BtnGoHelp = findViewById(R.id.setting_BtnGoHelp);
        setting_BtnGoLogin = findViewById(R.id.setting_BtnGoLogin);
        header_settings.setOnClickListener(this);
        setting_BtnGoSettings.setOnClickListener(this);
        setting_BtnGoHelp.setOnClickListener(this);
        setting_BtnGoLogin.setOnClickListener(this);
        header_settings.setVisibility(View.GONE);
        isSettingsOpen = false;
    }
    /**헤더**/
    /*헤더 백버튼 표시 & 이벤트*/
    public void setUpBtnBack(boolean show, final int afterView) {
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        if(show) {
            btnHeaderBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFragment(afterView);
                }
            });
            btnHeaderBack.setImageResource(R.drawable.icon_back);
        } else {
            btnHeaderBack.setOnClickListener(null);
            btnHeaderBack.setImageResource(0);
        }
    }
    public void openSettings(){
        if(isSettingsOpen) {
            header_settings.setVisibility(View.GONE);
            isSettingsOpen = false;
        } else {
            header_settings.setVisibility(View.VISIBLE);
            isSettingsOpen = true;
        }
    }
    /**푸터**/
    public void setUpFooterImage(int footerCode) {
        //모두 흰색으로
        footer_iconHome.setImageResource(R.drawable.icon_home);
        footer_iconAlarm.setImageResource(R.drawable.icon_notice);
        footer_iconMypage.setImageResource(R.drawable.icon_person);
        footer_iconMore.setImageResource(R.drawable.icon_more);
        footer_txtHome.setTextColor(Color.parseColor("#A7A198"));
        footer_txtAlarm.setTextColor(Color.parseColor("#A7A198"));
        footer_txtMypage.setTextColor(Color.parseColor("#A7A198"));
        footer_txtMore.setTextColor(Color.parseColor("#A7A198"));
        switch (footerCode) {
            case MAINHOME :
                footer_iconHome.setImageResource(R.drawable.icon_home_s);
                footer_txtHome.setTextColor(Color.parseColor("#f9a11b"));
                break;
            case MAINALIMI :
                footer_iconAlarm.setImageResource(R.drawable.icon_notice_s);
                footer_txtAlarm.setTextColor(Color.parseColor("#f9a11b"));
                break;
            case MYPAGE :
                footer_iconMypage.setImageResource(R.drawable.icon_person_s);
                footer_txtMypage.setTextColor(Color.parseColor("#f9a11b"));
                break;
            case MAINMORE :
                footer_iconMore.setImageResource(R.drawable.icon_more_s);
                footer_txtMore.setTextColor(Color.parseColor("#f9a11b"));
                break;
        }
    }

    /**더보기 애니메이션**/
    /*변수*/
    boolean isPageOpen = false;
    Animation translateLeftAnim;
    Animation translateRightAnim;
    LinearLayout page;
    View page_back;
    /*더보기 버튼*/
    FrameLayout more_button1_gongsi,more_button2_event,more_button3_company,more_button4_evaluation,more_button5_settings,more_button6_logout;
    private void setAnimation() {
        page = findViewById(R.id.page);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);
    }
    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                page.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else {
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    }

    /*더보기 초기화*/
    private  void more(int status){
        more_button1_gongsi =findViewById(R.id.more_button1_gongsi);
        more_button2_event =findViewById(R.id.more_button2_event);
        more_button3_company =findViewById(R.id.more_button3_company);
        more_button4_evaluation =findViewById(R.id.more_button4_evaluation);
        more_button5_settings =findViewById(R.id.more_button5_settings);
        more_button6_logout =findViewById(R.id.more_button6_logout);
        page_back = findViewById(R.id.page_back);
        page_back.setOnClickListener(this);
        if(status==0) {
            more_button1_gongsi.setOnClickListener(null);
            more_button2_event.setOnClickListener(null);
            more_button3_company.setOnClickListener(null);
            more_button4_evaluation.setOnClickListener(null);
            more_button5_settings.setOnClickListener(null);
            more_button6_logout.setOnClickListener(null);
        } else if(status==1) {
            more_button1_gongsi.setOnClickListener(this);
            more_button2_event.setOnClickListener(this);
            more_button3_company.setOnClickListener(this);
            more_button4_evaluation.setOnClickListener(this);
            more_button5_settings.setOnClickListener(this);
            more_button6_logout.setOnClickListener(this);
        }
    }

    /*더보기 열기, 닫기*/
    public void openCloseMorePage() {
        isSettingsOpen=true;
        openSettings();
        if (isPageOpen) {
            page.startAnimation(translateRightAnim);
            page_back.setVisibility(View.GONE);
            page.setVisibility(View.GONE);
            more(0);
        } else {
            page.setVisibility(View.VISIBLE);
            page.startAnimation(translateLeftAnim);
            page_back.setVisibility(View.VISIBLE);
            more(1);
        }
    }

    /*통신용 함수*/
    // 로그인 정보 받아오기
    String nickname;

    /*통신용 함수*/
    // 로그인 정보 받아오기
    /*  */
    int member_num=-1;

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 50 :
                if (resultCode == 50) {
                    /*받아온 닉네임 적용*/
                    nickname = "";
                    nickname = data.getStringExtra("nickname");
                    member_num = Integer.parseInt(data.getStringExtra("member_num"));
                    Toast.makeText(this, "환영합니다. "+nickname+"님!", Toast.LENGTH_SHORT).show();
                    Log.d("[TEST]", "지이이이이이니이이입");
                    main_nickname.setText(nickname + "님!");
                    /*로그인 상태로 변경*/
                    setLogin(1);
                    /*메인 프레그먼트*/
                    setUpFooterImage(MAINHOME);
                    setFragment(MAINHOME);
                }
                break;
        }
    }


    //퍼미션
    private void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
    }

    /**로그인**/
    TextView setting_txtLogin, more_txtLogin, main_welcome;
    boolean islogin=false;
    public void setLogin(int status){
        if(status==0) {             // 로그아웃
            /*로그아웃 상태로 변경*/
            islogin=false;
            member_num = -1;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.remove("member_num");
            editor.remove("setting_switch_check");
            editor.apply();
            //로그인
            /*로그아웃 ->  로그인상태로 변경*/
            setting_txtLogin.setText("로그인");
            more_txtLogin.setText("로그인");
            main_welcome.setText("로그인 후 이용하세요.");
            main_nickname.setText("봉 투");
        } else if(status==1) {
            /*로그인 상태로 변경*/
            islogin=true;                           //로그아웃
            /*로그인버튼 ->  로그아웃버튼으로 변경*/
            setting_txtLogin.setText("로그아웃");
            more_txtLogin.setText("로그아웃");
            main_welcome.setText("환영합니다!");
            /*로그인 정보 받아오기*/
            RequestParams params = new RequestParams();
            params.put("member_num", member_num);
            Log.d("[TESTTTTTTTTTT]","member_num : "+member_num);
            client.post(URL_member_login, params, memberInfoHttpResponse);
        }
    }

    /**로그인한 계정의 정보 받아오기**/
    Member memberInfo;
    class MemberInfoHttpResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String content = new String(bytes);
            try {
                JSONObject json = new JSONObject(content);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                Log.d("[TESTTTTTTTTTT]","member_num111 : "+member_num);
                JSONArray item = json.getJSONArray("item");
                if(item.length() > 0) {
                    JSONObject temp = item.getJSONObject(0);
                    Log.d("[TESTTTTTTTTTT]","member_num222 : "+member_num);
                    memberInfo = new Member();
                    memberInfo.setMember_num(temp.getInt("member_num"));
                    memberInfo.setMember_id(temp.getString("member_id"));
                    memberInfo.setMember_phone(temp.getString("member_phone"));
                    memberInfo.setMember_origin_img(temp.getString("member_origin_img"));
                    memberInfo.setMember_img_path(temp.getString("member_img_path"));
                    memberInfo.setName(temp.getString("name"));
                    memberInfo.setNickname(temp.getString("nickname"));
                    memberInfo.setEmail(temp.getString("email"));
                    memberInfo.setGrade(temp.getInt("grade"));
                    memberInfo.setFirst_logtime(temp.getString("first_logtime"));
                    memberInfo.setMoney_give(temp.getInt("money_give"));
                    Log.d("[TEST - memberInfo]","member_id "+ memberInfo.getMember_img_path());
                    main_nickname.setText(memberInfo.getNickname() + "님!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(MainActivity.this, "연결 실패 - member", Toast.LENGTH_SHORT).show();
            Log.d("[ERROR]", "에러코드 : " + i + ", 에러내용 : " + throwable.getLocalizedMessage());
        }
    }

    /**로그인한 계정의 태그정보 받아오기**/
    class HasTagHttpResponse extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Log.d("HasTagHttpResponse"," member_num : " + member_num);
            String content = new String(bytes);
            try {
                JSONObject json = new JSONObject(content);
                int total = json.getInt("total");
                if(total==0) {
                    Log.d("HasTagHttpResponse","태그없음 ");
                    isAlimiTagset = false;
                } else {
                    Log.d("HasTagHttpResponse","태그있음 ");
                    isAlimiTagset = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(MainActivity.this, "연결 실패 - member", Toast.LENGTH_SHORT).show();
            Log.d("[ERROR]", "에러코드 : " + i + ", 에러내용 : " + throwable.getLocalizedMessage());
        }
    }

    /**화면 새로고침**/
    @Override
    protected void onResume() {
        super.onResume();
        /*로그인 정보 받아오기*/
        if(member_num!=-1){
            RequestParams params = new RequestParams();
            params.put("member_num", member_num);
            client.post(URL_member_login, params, memberInfoHttpResponse);
            client.post(URL_member_hastag, params,hasTagHttpResponse);
        }

    }
}

