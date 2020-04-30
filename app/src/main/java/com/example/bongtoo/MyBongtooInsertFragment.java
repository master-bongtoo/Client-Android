package com.example.bongtoo;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bongtoo.helper.DateTimeHelper;
import com.example.bongtoo.helper.FileUtils;
import com.example.bongtoo.helper.PhotoHelper;
import com.example.bongtoo.model.MyBongToo;
import com.example.bongtoo.model.MyGroup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyBongtooInsertFragment extends Fragment {

    ImageView myBong_insert_imageProfile;
    EditText myBong_insert_eTxtName, myBong_insert_eTxtPlace, myBong_insert_eTxtMoney, myBong_insert_eTxtMemo;
    TextView myBong_insert_eTxtDate;
    Spinner myBong_insert_spinGroup;
    CheckBox myBong_insert_checkBoxAttend, myBong_insert_checkBoxNotAttend;
    CheckBox myBong_insert_checkBoxMobile, myBong_insert_checkBoxDirect, myBong_insert_checkBoxEtc;
    FrameLayout myBong_insert_BtnAddGroup, myBong_insert_BtnAdd10000, myBong_insert_BtnAdd50000, myBong_insert_BtnAdd100000, myBong_insert_BtnSubmit, myBong_insert_BtnCancel;

    String filePath = "";
    int YEAR = 0, MONTH = 0, DAY = 0;

    MyGroup myGroup;
    ArrayAdapter<String> groupAdapter;
    Bitmap bitmap;

    // DTO에 담을 전역변수
    String name;
    String imageURL;
    List<String> groupList;
    String group;
    int groupIndex;
    int attendance;
    String inviteWay;
    String place;
    String date;
    int money;
    String memo;

    String URL, myGroupURL, myGroupUpdateURL, groupViewURL;
    AsyncHttpClient client;
    HttpResponseInsert insertResponse;
    HttpResponseMyGroup myGroupResponse;
    HttpResponseGroupView groupViewResponse;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_bongtoo_insert, container, false);
        URL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashWriteJson.a";
        myGroupURL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashGroupWriteJson.a";
        myGroupUpdateURL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashGroupUpdateJson.a";
        groupViewURL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashGroupViewJson.a";

        init();
        loadGroup();
        return rootView;

    }

    /* 컴포넌트 초기화 */
    private void init() {
        /* 화면 초기화 */
        myBong_insert_imageProfile = rootView.findViewById(R.id.myBong_insert_imageProfile);
        myBong_insert_eTxtName = rootView.findViewById(R.id.myBong_insert_eTxtName);
        myBong_insert_eTxtPlace = rootView.findViewById(R.id.myBong_insert_eTxtPlace);
        myBong_insert_eTxtDate = rootView.findViewById(R.id.myBong_insert_eTxtDate);
        myBong_insert_eTxtMoney = rootView.findViewById(R.id.myBong_insert_eTxtMoney);
        myBong_insert_eTxtMemo = rootView.findViewById(R.id.myBong_insert_eTxtMemo);

        myBong_insert_checkBoxAttend = rootView.findViewById(R.id.myBong_insert_checkBoxAttend);
        myBong_insert_checkBoxNotAttend = rootView.findViewById(R.id.myBong_insert_checkBoxNotAttend);
        myBong_insert_checkBoxMobile = rootView.findViewById(R.id.myBong_insert_checkBoxMobile);
        myBong_insert_checkBoxDirect = rootView.findViewById(R.id.myBong_insert_checkBoxDirect);
        myBong_insert_checkBoxEtc = rootView.findViewById(R.id.myBong_insert_checkBoxEtc);

        myBong_insert_BtnAddGroup = rootView.findViewById(R.id.myBong_insert_BtnAddGroup);
        myBong_insert_BtnAdd10000 = rootView.findViewById(R.id.myBong_insert_BtnAdd10000);
        myBong_insert_BtnAdd50000 = rootView.findViewById(R.id.myBong_insert_BtnAdd50000);
        myBong_insert_BtnAdd100000 = rootView.findViewById(R.id.myBong_insert_BtnAdd100000);
        myBong_insert_BtnSubmit = rootView.findViewById(R.id.myBong_insert_BtnSubmit);
        myBong_insert_BtnCancel = rootView.findViewById(R.id.myBong_insert_BtnCancel);
        myBong_insert_BtnSubmit = rootView.findViewById(R.id.myBong_insert_BtnSubmit);
        myBong_insert_BtnCancel = rootView.findViewById(R.id.myBong_insert_BtnCancel);

        /* 이벤트 설정 */
        myBong_insert_imageProfile.setOnClickListener(profileImageInsertEvent);

        myBong_insert_checkBoxAttend.setOnClickListener(attendCBEvent);
        myBong_insert_checkBoxNotAttend.setOnClickListener(attendCBEvent);

        myBong_insert_checkBoxMobile.setOnClickListener(inviteWayCBEvent);
        myBong_insert_checkBoxDirect.setOnClickListener(inviteWayCBEvent);
        myBong_insert_checkBoxEtc.setOnClickListener(inviteWayCBEvent);

        myBong_insert_eTxtDate.setOnClickListener(dateInsertEvent);

        myBong_insert_BtnAddGroup.setOnClickListener(addGroup);
        myBong_insert_BtnAdd10000.setOnClickListener(moneyInputEvent);
        myBong_insert_BtnAdd50000.setOnClickListener(moneyInputEvent);
        myBong_insert_BtnAdd100000.setOnClickListener(moneyInputEvent);

        myBong_insert_BtnSubmit.setOnClickListener(submitORcancelEvent);
        myBong_insert_BtnCancel.setOnClickListener(submitORcancelEvent);

        // 날짜입력 초기 화면을 현재 날짜로 세팅
        myBong_insert_eTxtDate.setText(DateTimeHelper.getInstance().getStringDate());

        // 스피너 사용을 위한 초기화 및 그룹 생성
        myBong_insert_spinGroup = rootView.findViewById(R.id.myBong_insert_spinGroup);
        myBong_insert_spinGroup.setOnItemSelectedListener(spinnerEvent);
        groupList = new ArrayList<>();

        groupAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, groupList);
        myBong_insert_spinGroup.setAdapter(groupAdapter);

        client = new AsyncHttpClient();
        insertResponse = new HttpResponseInsert();
        myGroupResponse = new HttpResponseMyGroup();
        groupViewResponse = new HttpResponseGroupView();

    }

    // 그룹 선택값을 얻기 위한 이벤트
    Spinner.OnItemSelectedListener spinnerEvent = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            group = (String) parent.getSelectedItem();
            groupIndex = parent.getSelectedItemPosition();

            Log.d("[TEST]", "groupIndex" + groupIndex);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(activity, "그룹을 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    };


    // + 버튼으로 그룹 추가
    FrameLayout.OnClickListener addGroup = new FrameLayout.OnClickListener() {

        @Override
        public void onClick(View v) {
            final View addGroupView = getLayoutInflater().inflate(R.layout.addgroup, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            //ArrayList<String> groups = new ArrayList<String>(Arrays.asList("친구", "직장동료", "동호회"));

            builder.setTitle("추가할 그룹명을 입력하세요");
            builder.setView(addGroupView);
            builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText myBong_insert_eTxtAddGroup = addGroupView.findViewById(R.id.myBong_insert_eTxtAddGroup);
                    String groupName = myBong_insert_eTxtAddGroup.getText().toString().trim();
                    Log.d("[TEST]", "grouList.size(): " + groupList.size());
                    groupAdapter.add(groupName);
                    Log.d("[TEST - after adding]", "grouList.size(): " + groupList.size());
                    RequestParams params = new RequestParams();
                    if (groupList.size() == 1) {
                        params.put("member_num", activity.member_num);
                        params.put("occ_cash_group1", groupName);
                        client.post(myGroupURL, params, myGroupResponse);
                    }
                    if (groupList.size() > 1) {
                        for (int i = 2; i < 11; i++) {
                            params.put("member_num", activity.member_num);
                            params.put("occ_cash_group" + groupList.size(), groupName);
                            client.post(myGroupUpdateURL, params, myGroupResponse);
                        }
                    }
                    if (groupList.size() > 10) {
                        Toast.makeText(activity, "그룹은 10개까지만 추가할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        groupAdapter.remove(groupName);
                        return;
                    }

                    myBong_insert_spinGroup.setSelection(groupAdapter.getCount()); // 추가한 그룹으로 바로 세팅
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };
    // 참석여부 선택 이벤트
    CheckBox.OnClickListener attendCBEvent = new CheckBox.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.myBong_insert_checkBoxAttend:
                    myBong_insert_checkBoxNotAttend.setChecked(false);
                    attendance = 0;
                    break;
                case R.id.myBong_insert_checkBoxNotAttend:
                    myBong_insert_checkBoxAttend.setChecked(false);
                    attendance = 1;
                    break;
            }
        }
    };

    // 초대 방식 선택 이벤트
    CheckBox.OnClickListener inviteWayCBEvent = new CheckBox.OnClickListener() {

        @Override
        public void onClick(View v) {
            inviteWay = "";
            switch (v.getId()) {
                case R.id.myBong_insert_checkBoxMobile:
                    myBong_insert_checkBoxDirect.setChecked(false);
                    myBong_insert_checkBoxEtc.setChecked(false);
                    inviteWay = "모바일";
                    break;
                case R.id.myBong_insert_checkBoxDirect:
                    myBong_insert_checkBoxMobile.setChecked(false);
                    myBong_insert_checkBoxEtc.setChecked(false);
                    inviteWay = "직접수령";
                    break;
                case R.id.myBong_insert_checkBoxEtc:
                    myBong_insert_checkBoxMobile.setChecked(false);
                    myBong_insert_checkBoxDirect.setChecked(false);
                    inviteWay = "기타";
                    break;

            }
        }
    };

    // 날짜입력 (Date Picker)
    TextView.OnClickListener dateInsertEvent = new TextView.OnClickListener() {

        @Override
        public void onClick(View v) {
            showDatePickerDialog();
        }
    };

    // MY 경조사비 입력 대상의 사진 업로드
    ImageView.OnClickListener profileImageInsertEvent = new ImageView.OnClickListener() {

        @Override
        public void onClick(View v) {
            showDialog();
        }
    };

    // MY 경조사비 금액 추가 버튼 이벤트
    FrameLayout.OnClickListener moneyInputEvent = new FrameLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            String temp_str_money = myBong_insert_eTxtMoney.getText().toString().trim();
            int temp_money = 0;
            String total_money;
            if (temp_str_money.equals("")) {
                temp_str_money = "0";
            } else {
                temp_str_money = myBong_insert_eTxtMoney.getText().toString().trim();
            }
            switch (v.getId()) {
                case R.id.myBong_insert_BtnAdd10000:
                    temp_money = Integer.parseInt(temp_str_money);
                    temp_money += 10000;
                    total_money = String.valueOf(temp_money);
                    myBong_insert_eTxtMoney.setText(total_money);
                    break;
                case R.id.myBong_insert_BtnAdd50000:
                    temp_money = Integer.parseInt(temp_str_money);
                    temp_money += 50000;
                    total_money = String.valueOf(temp_money);
                    myBong_insert_eTxtMoney.setText(total_money);
                    break;
                case R.id.myBong_insert_BtnAdd100000:
                    temp_money = Integer.parseInt(temp_str_money);
                    temp_money += 100000;
                    total_money = String.valueOf(temp_money);
                    myBong_insert_eTxtMoney.setText(total_money);
                    break;
            }
        }
    };

    // 등록 or 취소
    FrameLayout.OnClickListener submitORcancelEvent = new FrameLayout.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.myBong_insert_BtnSubmit:
                    insert();
                    break;
                case R.id.myBong_insert_BtnCancel:
                    activity.setFragment(activity.MAINMYBONGTOO);
                    break;
            }
        }
    };

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filePath = image.getAbsolutePath();
        return image;
    }

    // 이미지뷰 클릭시 호출되는 함수(사진 등록)

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String[] select = {"기본 이미지로 변경", "새로 촬영하기", "갤러리에서 불러오기"};

        builder.setItems(select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0:
                        Glide.with(activity)
                                .load(bitmap)
                                .skipMemoryCache(true)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                .placeholder(R.drawable.icon_loco_s)
                                .into(myBong_insert_imageProfile);
                        break;
                    case 1:
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(activity.getApplicationContext(),
                                        activity.getApplicationContext().getPackageName()
                                                + ".fileprovider", photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(intent, 100);
                            }
                        }
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT < 19) {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);
                        } else {
                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                        }

                        intent.setType("image/*");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        }

                        startActivityForResult(intent, 101);
                        break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 촬영 혹은 갤러리에서 불러온 이미지를 이미지뷰에 세팅
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 촬영시
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Intent photoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath));
            activity.sendBroadcast(photoIntent);
            myBong_insert_imageProfile.setImageURI(null);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = PhotoHelper.getInstance().getThumb(activity, filePath);
            Glide.with(this)
                    .load(bitmap)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .placeholder(R.drawable.icon_loco_s)
                    .into(myBong_insert_imageProfile);

        } else if (requestCode == 101) {

            filePath = FileUtils.getPath(activity, data.getData());
            myBong_insert_imageProfile.setImageBitmap(null);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            bitmap = PhotoHelper.getInstance().getThumb(activity, filePath);
            Glide.with(this)
                    .load(filePath)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .placeholder(R.drawable.icon_loco_s)
                    .into(myBong_insert_imageProfile);
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

                myBong_insert_eTxtDate.setText(date);
            }
        }, YEAR, MONTH - 1, DAY);

        // 백키나 캔슬을 눌렀을 때 동작하는 이벤트 설정
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                YEAR = temp_yy;
                MONTH = temp_mm;
                DAY = temp_dd;
            }
        });

        dialog.show();
    }

    private void loadGroup() {
        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        client.post(groupViewURL, params, groupViewResponse);
    }

    class HttpResponseInsert extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");

                if (rt.equals("OK") && total > 0) {
                    activity.setFragment(12);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "[통신 실패] 알루에게 문의하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    class HttpResponseMyGroup extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");

                if (rt.equals("OK") && total > 0) {
                    //Toast.makeText(activity, "그룹 추가 성공", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "[통신 실패] 알루에게 문의하세요.", Toast.LENGTH_SHORT).show();
            Log.d("[TEST]", "오류: " + throwable.toString());
        }
    }

    private void insert() {
        name = myBong_insert_eTxtName.getText().toString().trim();
        imageURL = filePath;
        place = myBong_insert_eTxtPlace.getText().toString().trim();
        date = myBong_insert_eTxtDate.getText().toString().trim();
        money = Integer.parseInt(myBong_insert_eTxtMoney.getText().toString().trim());
        memo = myBong_insert_eTxtMemo.getText().toString().trim();

        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        if (filePath != null) {
            try {
                params.put("occ_cash_origin_img", new File(filePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            params.put("occ_cash_origin_img", "");
        }
        params.put("occ_cash_name", name);
        params.put("occ_cash_group", group);
        params.put("occ_cash_group_index", groupIndex);
        params.put("occ_cash_place", place);
        params.put("occ_cash_money", money);
        params.put("occ_cash_invite_way", inviteWay);
        params.put("occ_cash_attendance", attendance);
        params.put("occ_cash_date", date);
        params.put("occ_cash_memo", memo);
        params.setForceMultipartEntityContentType(true);
        client.post(URL, params, insertResponse);
    }

    class HttpResponseGroupView extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");

                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        groupAdapter.add(temp.getString("occ_cash_group1"));
                        groupAdapter.add(temp.getString("occ_cash_group2"));
                        groupAdapter.add(temp.getString("occ_cash_group3"));
                        groupAdapter.add(temp.getString("occ_cash_group4"));
                        groupAdapter.add(temp.getString("occ_cash_group5"));
                        groupAdapter.add(temp.getString("occ_cash_group6"));
                        groupAdapter.add(temp.getString("occ_cash_group7"));
                        groupAdapter.add(temp.getString("occ_cash_group8"));
                        groupAdapter.add(temp.getString("occ_cash_group9"));
                        groupAdapter.add(temp.getString("occ_cash_group10"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    }


}