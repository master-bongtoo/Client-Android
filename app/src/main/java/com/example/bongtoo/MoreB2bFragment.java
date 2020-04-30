package com.example.bongtoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bongtoo.helper.RegexHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;


public class MoreB2bFragment extends Fragment {
    //객체선언
    ImageView evaluation_image;
    EditText evaluation_content,evaluation_email,evaluation_phon,evaluation_subject;
    FrameLayout evaluation_button_confirm;
    TextView evaluation_count;

    AsyncHttpClient client;
    Response Response;
    MainActivity activity;
    String URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_more_b2b,container,false);
        URL = "http://"+activity.SERVERIP+"/bongtoo_server/question/questionWriteJson.a";
        client = new AsyncHttpClient();
        Response = new Response(getActivity());
        evaluation_subject= rootView.findViewById(R.id.evaluation_subject);
        evaluation_phon = rootView.findViewById(R.id.evaluation_phon);
        evaluation_image = rootView.findViewById(R.id.evaluation_image);
        evaluation_content = rootView.findViewById(R.id.evaluation_content);
        evaluation_email = rootView.findViewById(R.id.evaluation_email);
        evaluation_button_confirm = rootView.findViewById(R.id.evaluation_button_confirm);
        evaluation_count = rootView.findViewById(R.id.evaluation_count);

        evaluation_button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B2B_writesendData();
            }
        });

//글자수 세기
        evaluation_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = evaluation_content.getText().toString();
                evaluation_count.setText(input.length()+" / 2000");
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return  rootView;
    }

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
    //통신
    class Response extends AsyncHttpResponseHandler {
        Activity activity;
        ProgressDialog dialog;

        public Response(Activity activity) {
            this.activity = activity;
        }

        // 통신 시작
        @Override
        public void onStart() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage("잠시만 기다려주세요...");
            dialog.setCancelable(false);
            dialog.show();
        }
        // 통신 종료
        @Override
        public void onFinish() {
            dialog.dismiss();
            dialog = null;
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt= json.getString("rt");
                if (rt.equals("OK")) {
                    Toast.makeText(activity, "글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    evaluation_content.setText("");
                } else {
                    Toast.makeText(activity, "저장 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 통신 실패
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_MORE_B2B]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
    //서버
    int member_num,question_type;
    String question_subject,question_content,question_email,question_phone,question_origin_img;

    private void B2B_writesendData() {
        String strevaluation_subject = evaluation_subject.getText().toString().trim();
        String strevaluation_content = evaluation_content.getText().toString().trim();
        String strevaluation_phon =evaluation_phon.getText().toString().trim();
        String strevaluation_email =evaluation_email.getText().toString().trim();
// 입력값 검사
        String msg = null;
        if (!RegexHelper.getInstance().isValue(strevaluation_subject)) {
            msg = "제목을 입력하세요";
        }else if (!RegexHelper.getInstance().isValue(strevaluation_content)) {
            msg = "내용을 입력하세요";
        } else if (!RegexHelper.getInstance().isValue(strevaluation_email) && !RegexHelper.getInstance().isValue(strevaluation_phon)) {
            msg = "휴대폰 번호 또는 이메일을 입력하세요";

        }
// 에러가 있으면
        if (msg != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
//데이터 받아오기
        member_num = 1;
        question_type =1;
        question_subject = strevaluation_subject;
        question_content = strevaluation_content;
        question_email = strevaluation_email;
        question_phone = strevaluation_phon;

// 에러가 없으면, 서버에 전송
        RequestParams params = new RequestParams();
        params.put("member_num",member_num);
        params.put("question_type", question_type);
        params.put("question_subject", question_subject);
        params.put("question_content", question_content);
        params.put("question_email", question_email);
        params.put("question_phone", question_phone);

// multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(URL,params,Response);
        Toast.makeText(activity, "문의를 제출하였습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
    }
}