package com.example.bongtoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    // ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ
    String babo;
    /*Fragment*/
    LoginFormFragment loginFormFragment;
    /*layout*/
    LinearLayout login_main;

    String member_num;
    String nickname;

    public void logininfo(String member_num,String nickname){
        this.member_num = member_num;
        this.nickname = nickname;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideNav();
        login_main = findViewById(R.id.login_main);
        login_main.setVisibility(View.VISIBLE);

        /*로그인 폼으로*/
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(1);
                login_main.setVisibility(View.GONE);
            }
        });

    }

    public void hideNav() {
        int uiOptions =getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    /**화면 이동 : Intent로 이동**/
    private void gotoMain() {
        Intent intent = new Intent();
        if(member_num != null) {
            intent.putExtra("member_num", member_num);
            intent.putExtra("nickname", nickname);
            setResult(50, intent);
        }
        finish();
    }
    /**화면 이동 : Intent로 이동**/
    public void gotoJoin() {
        Intent intent1 = new Intent(this, JoinActivity.class);
        finish();
        startActivity(intent1);
    }

    /*setFragment(viewCode) : 프래그먼트 교체 함수*/
    public void setFragment(int viewCode) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (viewCode) {
            /*메인화면으로 이동*/
            case 0 :
                gotoMain();
                break;
            /*LOGINFORM*/
            case 1 :
                loginFormFragment = new LoginFormFragment();
                ft.replace(R.id.login_fragment_place, loginFormFragment).addToBackStack(null);
                break;
        }
        ft.commit();
    }
}
