package com.example.bongtoo;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bongtoo.helper.DateTimeHelper;
import com.example.bongtoo.helper.RegexHelper;
import com.example.bongtoo.model.AlimiTag;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AlimiInputFragment extends Fragment {
    //객체선언
    EditText myBong_insert_name,myBong_insert_tag,myBong_insert_palace,myBong_insert_eTxtMemo;
    TextView myBong_insert_date;
    CheckBox myBong_insert_wedding,myBong_insert_funeral;
    FrameLayout myBong_insert_button1,myBong_insert_button2;

    Spinner alimi_input_spinner;
    String tag;
    int tagIndex;
    List<String> tagList;
    ArrayAdapter<String> tagAdapter;
    AlimiTag alimiTag;

    AsyncHttpClient client;
    Response Response;
    MainActivity activity;
    String URL;
    int alimi_type;
    int YEAR = 0, MONTH = 0, DAY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_alimi_input,container,false);
        URL = "http://"+activity.SERVERIP+"/bongtoo_server/alimi/alimiWriteJson.a";
        client = new AsyncHttpClient();
        Response = new Response(getActivity());
        myBong_insert_name = rootView.findViewById(R.id.myBong_insert_name);
        myBong_insert_date = rootView.findViewById(R.id.myBong_insert_date);
        myBong_insert_palace = rootView.findViewById(R.id.myBong_insert_palace);
        myBong_insert_eTxtMemo = rootView.findViewById(R.id.myBong_insert_eTxtMemo);
        myBong_insert_wedding = rootView.findViewById(R.id.myBong_insert_wedding);
        myBong_insert_funeral = rootView.findViewById(R.id.myBong_insert_funeral);
        myBong_insert_button1 = rootView.findViewById(R.id.myBong_insert_button1);
        myBong_insert_button2 = rootView.findViewById(R.id.myBong_insert_button2);
        myBong_insert_button1.setOnClickListener(InputAlimi);
        myBong_insert_button2.setOnClickListener(InputAlimi);
        myBong_insert_wedding.setOnClickListener(check);
        myBong_insert_funeral.setOnClickListener(check);

        myBong_insert_date.setText(DateTimeHelper.getInstance().getStringDate());
        myBong_insert_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 그룹 스피너
        alimi_input_spinner = rootView.findViewById(R.id.alimi_input_spinner);
        alimi_input_spinner.setOnItemSelectedListener(spinnerEvent);
        tagList = new ArrayList<>();
        tagAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, tagList);
        alimi_input_spinner.setAdapter(tagAdapter);
        String spinnerDefault = "대표 태그를 선택하세요";
        tagAdapter.add(spinnerDefault);

        getBundle();

        return rootView;
    }

    Spinner.OnItemSelectedListener spinnerEvent = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            tag = (String) parent.getSelectedItem();
            tagIndex = parent.getSelectedItemPosition();
            alimi_input_spinner.setSelection(tagIndex);

            Log.d("[TEST]", "groupIndex" + tagIndex);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(activity, "그룹을 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    };

    FrameLayout.OnClickListener InputAlimi = new FrameLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.myBong_insert_button1:
                    sendData();
                    break;
                case R.id.myBong_insert_button2:
                    break;
            }
        }
    };
    CheckBox.OnClickListener check =new CheckBox.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.myBong_insert_wedding:
                    alimi_type =0;
                    myBong_insert_funeral.setChecked(false);
                    break;
                case R.id.myBong_insert_funeral:
                    alimi_type =1;
                    myBong_insert_wedding.setChecked(false);
                    break;
            }
        }
    };
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
        public Response(Activity activity) {
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
                String rt= json.getString("rt");
                if (rt.equals("OK")) {
                    Toast.makeText(activity, "알리미가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    myBong_insert_name.setText("");
                    alimi_input_spinner.setSelection(0);
                    myBong_insert_date.setText("");
                    myBong_insert_palace.setText("");
                    myBong_insert_eTxtMemo.setText("");
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
        }
    }

    private void showDatePickerDialog() {

        final int[] int_date = DateTimeHelper.getInstance().getDate();
        YEAR = int_date[0];
        MONTH = int_date[1];
        DAY = int_date[2];

        //원복 처리에 사용될 임시값 - 원본 데이터 백업
        final int temp_yy = YEAR;
        final int temp_mm = MONTH;
        final int temp_dd = DAY;

        // DatePickerDialog 객체 생성
        // DatePickerDialog(Context(Context는 Activity로 설정하면 됨. 여기서는 this), event Handeler, year, month, date)
        DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // 선택값을 전역 변수에 저장
                YEAR = year;
                MONTH = month + 1;
                DAY = dayOfMonth;
                String date = null;
                // 버튼에서 날짜 출력
                if (MONTH < 10) {
                    date = YEAR + "년 " + "0" + MONTH + "월 " + DAY + "일";
                }
                if (DAY < 10) {
                    date = YEAR + "년 " + MONTH + "월 " + "0" + DAY + "일";
                }
                if (DAY < 10 && MONTH < 10) {
                    date = YEAR + "년 " + "0" + MONTH + "월 " + "0" + DAY + "일";
                }
                myBong_insert_date.setText(date);
            }
        }, YEAR, MONTH - 1, DAY);

        // 백키나 캔슬을 눌렀을 때 동작하는 이벤트 설정
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //백업해 두었던 값을 원복시킴.
                YEAR = temp_yy;
                MONTH = temp_mm;
                DAY = temp_dd;
            }
        });

        dialog.show();
    }

    private void sendData() {
        String strmyBong_insert_name = myBong_insert_name.getText().toString().trim();
        String strmyBong_insert_tag = tag;
        String strmyBong_insert_date =myBong_insert_date.getText().toString().trim();
        String strmyBong_insert_palace =myBong_insert_palace.getText().toString().trim();
        String strmyBong_insert_eTxtMemo =myBong_insert_eTxtMemo.getText().toString().trim();

        // 입력값 검사
        String msg = null;
        if (!RegexHelper.getInstance().isValue(strmyBong_insert_name)) {
            msg = "이름을 입력하세요";
        }else if (tagIndex == 0) {
            msg = "대표 태그를 선택하세요";
        } else if (!RegexHelper.getInstance().isValue(strmyBong_insert_date)) {
            msg = "날짜를 입력하세요";
        }else if (!RegexHelper.getInstance().isValue(strmyBong_insert_palace)) {
            msg = "장소를 입력하세요";
        }else if (!RegexHelper.getInstance().isValue(strmyBong_insert_eTxtMemo)) {
            msg = "내용을 입력하세요";
        }
        // 에러가 있으면
        if (msg != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        // 에러가 없으면, 서버에 전송
        RequestParams params = new RequestParams();
        params.put("member_num",activity.member_num);
        params.put("alimi_type",alimi_type );
        params.put("alimi_tag", strmyBong_insert_tag);
        params.put("alimi_place", strmyBong_insert_palace);
        params.put("alimi_content", strmyBong_insert_eTxtMemo);
        params.put("alimi_date", strmyBong_insert_date);
        params.put("alimi_who", strmyBong_insert_name);

        // multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(URL,params,Response);
        activity.setFragment(activity.MAINALIMI);

        Log.d("[alimi]", "alimi_tag: " + strmyBong_insert_tag);
    }

    private void getBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {

            alimiTag = (AlimiTag) bundle.getSerializable("item");
            bundle.getSerializable("item");

            String myTag1 = alimiTag.getTag1();
            String myTag2 = alimiTag.getTag2();
            String myTag3 = alimiTag.getTag3();
            String myTag4 = alimiTag.getTag4();
            String myTag5 = alimiTag.getTag5();

            if (myTag1 != null) {
                tagAdapter.add(myTag1);
            }
            if (myTag2 != null) {
                tagAdapter.add(myTag2);
            }
            if (myTag3 != null) {
                tagAdapter.add(myTag3);
            }
            if (myTag4 != null) {
                tagAdapter.add(myTag4);
            }
            if (myTag5 != null) {
                tagAdapter.add(myTag5);
            }


        }
    }
}