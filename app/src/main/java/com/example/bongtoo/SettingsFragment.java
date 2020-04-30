package com.example.bongtoo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    int member_num;
    boolean setting_switch_check;
    LinearLayout settings_tagReset;
    MainActivity mainActivity;
    /*Client Setting*/
    AsyncHttpClient client;
    TagResetResponse tagResetResponse;
    String URL_tagReset = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        Switch setting_switch_login = rootview.findViewById(R.id.setting_switch_login);
        /*데이터 받아오기*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = preferences.edit();
        Bundle bundle = getArguments();
        member_num = bundle.getInt("member_num");

        /*자동로그인*/
        client = new AsyncHttpClient();
        tagResetResponse = new TagResetResponse(activity);
        setting_switch_check = preferences.getBoolean("setting_switch_check", false);
        setting_switch_login.setChecked(setting_switch_check);
        setting_switch_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(member_num>-1){
                        editor.putInt("member_num", member_num);
                        editor.putBoolean("setting_switch_check", true);
                        editor.apply();
                        Toast.makeText(getActivity(), "자동로그인 기능 활성화", Toast.LENGTH_SHORT).show();
                    } else {
                        if(member_num==-1) {
                            Toast.makeText(getActivity(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    editor.apply();
                    Toast.makeText(getActivity(), "자동로그인 기능 비활성화", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*푸시 알림 설정*/

        /*SMS 수신 동의*/

        /*이메일 수신 동의*/

        /*알리미 태그 초기화*/
        mainActivity = new MainActivity();
        URL_tagReset = "http://"+mainActivity.SERVERIP+"/bongtoo_server/alimi/alimiTagDeleteJson.a";
        settings_tagReset = rootview.findViewById(R.id.settings_tagReset);
        settings_tagReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(member_num==-1) {
                    //로그인하지 않음
                    Toast.makeText(getActivity(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    RequestParams params = new RequestParams();
                    params.put("member_num",member_num);
                    client.post(URL_tagReset, params, tagResetResponse);
                }
            }
        });
        return rootview;
    }

    //통신
    class TagResetResponse extends AsyncHttpResponseHandler {
        Activity activity;
        public TagResetResponse(Activity activity) {
            this.activity = activity;
        }

        // 통신 시작
        @Override
        public void onStart() {
        }
        // 통신 종료
        @Override
        public void onFinish() {
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                if (rt.equals("OK")) {
                    Toast.makeText(getActivity(), "알리미 태그가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "태그가 설정되지 않은 상태입니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패 - 태그 초기화", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_ALIMI_TAG1]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 기본 세팅
    //////////////////////////////////////////////////////////////////////////////////////
    ViewGroup rootView;
    SettingActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SettingActivity) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}