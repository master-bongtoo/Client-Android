package com.example.bongtoo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.bongtoo.helper.RegexHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class LoginFormFragment extends Fragment {
    String rohwa;
    String rohwa2;
    FrameLayout loginForm_BtnLogin, loginForm_BtnCancel, loginForm_BtnJoin;
    EditText loginform_id,loginform_pw;
    AsyncHttpClient client;
    HttpResponse response;
    String member_num;
    String nickname;
    MainActivity activity11;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login_form, container,false);
        loginForm_BtnLogin = rootView.findViewById(R.id.loginForm_BtnLogin);
        loginForm_BtnCancel = rootView.findViewById(R.id.loginForm_BtnCancel);
        loginForm_BtnJoin = rootView.findViewById(R.id.loginForm_BtnJoin);
        loginform_id = rootView.findViewById(R.id.loginform_id);
        loginform_pw = rootView.findViewById(R.id.loginform_pw);
        client = new AsyncHttpClient();
        response = new HttpResponse(activity);
        loginForm_BtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.gotoJoin();
            }
        });
        loginForm_BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.login_main.setVisibility(View.VISIBLE);
            }
        });
        loginForm_BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_form_id = loginform_id.getText().toString().trim();
                String login_form_pw = loginform_pw.getText().toString().trim();
                RequestParams params = new RequestParams();
                params.put("member_id", login_form_id);
                params.put("member_pw", login_form_pw);
                String msg = null;
                if(msg==null && !RegexHelper.getInstance().isValue(login_form_id)) {
                    msg = "아이디를 입력하세요";
                }
                if(msg==null && !RegexHelper.getInstance().isValue(login_form_pw)) {
                    msg = "비밀번호를 입력하세요";
                }
                if (msg != null) {
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                activity11 = new MainActivity();
                client.post("http://"+activity11.SERVERIP+"/bongtoo_server/member/memberLoginJson.a", params, response);
            }
        });

        return rootView;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 기본 세팅
    //////////////////////////////////////////////////////////////////////////////////////
    LoginActivity activity;
    ViewGroup rootView;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (LoginActivity) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.login_main.setVisibility(View.VISIBLE);
    }
    class HttpResponse extends AsyncHttpResponseHandler {
        LoginActivity activity;


        public HttpResponse(Activity activity) {
            this.activity = (LoginActivity)activity;
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                if(rt.equals("OK")) {
                    Toast.makeText(activity, "로그인 성공", Toast.LENGTH_SHORT).show();
                    JSONArray item = json.getJSONArray("item");
                    JSONObject temp = item.getJSONObject(0);

                    member_num = temp.getString("member_num");
                    nickname = temp.getString("nickname");
                    activity.logininfo(member_num, nickname);
                    activity.setFragment(0);
                }else {
                    Toast.makeText(activity, "아이디 와 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_LOGIN]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
}