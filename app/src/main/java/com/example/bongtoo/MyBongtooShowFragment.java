package com.example.bongtoo;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
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
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyBongtooShowFragment extends Fragment {
    String updateURL, deleteURL, groupViewURL, myGroupURL, myGroupUpdateURL;;

    AsyncHttpClient client;
    HttpResponseUpdate updateResponse;  //수정
    HttpResponseDelete deleteResponse;  //싹제
    HttpResponseMyGroup myGroupResponse; // 그룹 등록
    HttpResponseGroupView groupViewResponse;

    //////////////////////////////////////////////////////////////////////////////////////
    // 컴포넌트 전역변수
    //////////////////////////////////////////////////////////////////////////////////////
    ImageView myBong_show_imageProfile;
    EditText myBong_show_eTxtName, myBong_show_eTxtPlace, myBong_show_eTxtMoney, myBong_show_eTxtMemo;
    TextView myBong_show_eTxtDate;
    Spinner myBong_show_spinGroup;
    CheckBox myBong_show_checkBoxAttend, myBong_show_checkBoxNotAttend;
    CheckBox myBong_show_checkBoxMobile, myBong_show_checkBoxDirect, myBong_show_checkBoxEtc;
    FrameLayout myBong_show_BtnAddGroup, myBong_show_BtnAdd10000,
            myBong_show_BtnAdd50000, myBong_show_BtnAdd100000, myBong_show_BtnUpdate, myBong_show_BtnDelete, myBong_show_BtnCancel;

    String filePath = "";
    int YEAR = 0, MONTH = 0, DAY = 0;
    ArrayList<String> groupList;
    ArrayAdapter<String> groupAdapter;
    Bitmap bitmap;
    // DTO에 담을 전역변수
    int num; // 삭제에도 필요
    String name;
    String imageURL;
    String group;
    int groupIndex;
    int attendance;
    String inviteWay;
    String place;
    String date;
    int money;
    String memo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_bongtoo_show, container, false);
        updateURL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashUpdateJson.a";
        deleteURL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashDeleteJson.a";
        groupViewURL = "http://" + activity.SERVERIP + "/bongtoo_server/occasion/occasionCashGroupViewJson.a";

        init();
        getBundleAndSetData();
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

    /**
     * 컴포넌트 초기화
     **/
    private void init() {

        myBong_show_imageProfile = rootView.findViewById(R.id.myBong_show_imageProfile);
        myBong_show_eTxtName = rootView.findViewById(R.id.myBong_show_eTxtName);
        myBong_show_eTxtPlace = rootView.findViewById(R.id.myBong_show_eTxtPlace);
        myBong_show_eTxtMoney = rootView.findViewById(R.id.myBong_show_eTxtMoney);
        myBong_show_eTxtMemo = rootView.findViewById(R.id.myBong_show_eTxtMemo);
        myBong_show_eTxtDate = rootView.findViewById(R.id.myBong_show_eTxtDate);
        myBong_show_spinGroup = rootView.findViewById(R.id.myBong_show_spinGroup);

        myBong_show_checkBoxAttend = rootView.findViewById(R.id.myBong_show_checkBoxAttend);
        myBong_show_checkBoxNotAttend = rootView.findViewById(R.id.myBong_show_checkBoxNotAttend);
        myBong_show_checkBoxMobile = rootView.findViewById(R.id.myBong_show_checkBoxMobile);
        myBong_show_checkBoxDirect = rootView.findViewById(R.id.myBong_show_checkBoxDirect);
        myBong_show_checkBoxEtc = rootView.findViewById(R.id.myBong_show_checkBoxEtc);
        myBong_show_BtnAddGroup = rootView.findViewById(R.id.myBong_show_BtnAddGroup);
        myBong_show_BtnAdd10000 = rootView.findViewById(R.id.myBong_show_BtnAdd10000);
        myBong_show_BtnAdd50000 = rootView.findViewById(R.id.myBong_show_BtnAdd50000);
        myBong_show_BtnAdd100000 = rootView.findViewById(R.id.myBong_show_BtnAdd100000);
        myBong_show_BtnUpdate = rootView.findViewById(R.id.myBong_show_BtnUpdate);
        myBong_show_BtnDelete = rootView.findViewById(R.id.myBong_show_BtnDelete);
        myBong_show_BtnCancel = rootView.findViewById(R.id.myBong_show_BtnCancel);

        // 스피너 사용을 위한 초기화 및 그룹 생성
        myBong_show_spinGroup = rootView.findViewById(R.id.myBong_show_spinGroup);
        myBong_show_spinGroup.setSelection(groupIndex, false);
        myBong_show_spinGroup.setOnItemSelectedListener(spinnerEvent);
        groupList = new ArrayList<>();

        groupAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, groupList);
        myBong_show_spinGroup.setAdapter(groupAdapter);

        client = new AsyncHttpClient();
        updateResponse = new HttpResponseUpdate();
        deleteResponse = new HttpResponseDelete();
        groupViewResponse = new HttpResponseGroupView();

        // 이벤트 설정
        myBong_show_imageProfile.setOnClickListener(profileImageInsertEvent);

        myBong_show_spinGroup.setOnItemSelectedListener(spinnerEvent);
        myBong_show_checkBoxAttend.setOnClickListener(attendCBEvent);
        myBong_show_checkBoxNotAttend.setOnClickListener(attendCBEvent);

        myBong_show_checkBoxMobile.setOnClickListener(inviteWayCBEvent);
        myBong_show_checkBoxDirect.setOnClickListener(inviteWayCBEvent);
        myBong_show_checkBoxEtc.setOnClickListener(inviteWayCBEvent);

        myBong_show_eTxtDate.setOnClickListener(dateInsertEvent);

        myBong_show_BtnAddGroup.setOnClickListener(addGroup);
        myBong_show_BtnAdd10000.setOnClickListener(moneyInputEvent);
        myBong_show_BtnAdd50000.setOnClickListener(moneyInputEvent);
        myBong_show_BtnAdd100000.setOnClickListener(moneyInputEvent);

        myBong_show_BtnUpdate.setOnClickListener(frameLayoutEvent);
        myBong_show_BtnDelete.setOnClickListener(frameLayoutEvent);
        myBong_show_BtnCancel.setOnClickListener(frameLayoutEvent);
    }


    // MY봉투에서 넘어온 Bundle data 얻기 및 세팅
    private void getBundleAndSetData() {

        Bundle bundle = getArguments();
        if (bundle != null) {

            loadGroup();

            MyBongToo item = (MyBongToo) bundle.getSerializable("item");

            num = item.getNum();
            Log.d("[INFO]", "num: " + num);
            filePath = item.getImageURL();
            Glide.with(activity)
                    .load(item.getImageURL())
                    .placeholder(R.drawable.profile1)
                    .into(myBong_show_imageProfile);

            myBong_show_eTxtName.setText(item.getName());
            groupIndex = item.getGroupIndex();
            myBong_show_spinGroup.setSelection(groupIndex);
            Log.d("[show]", "groupIndex" + groupIndex);

            if (item.getAttendance() == 0) {
                myBong_show_checkBoxAttend.setChecked(true);
            } else {
                myBong_show_checkBoxNotAttend.setChecked(true);
            }

            if (item.getInviteWay().equals("모바일")) {
                myBong_show_checkBoxMobile.setChecked(true);
                inviteWay = "모바일";
            } else if (item.getInviteWay().equals("직접수령")) {
                myBong_show_checkBoxDirect.setChecked(true);
                inviteWay = "직접수령";
            } else if (item.getInviteWay().equals("기타")) {
                myBong_show_checkBoxEtc.setChecked(true);
                inviteWay = "기타";
            }

            myBong_show_eTxtPlace.setText(item.getPlace());
            myBong_show_eTxtDate.setText(item.getDate());
            myBong_show_eTxtMoney.setText(String.valueOf(item.getMoney()));
            myBong_show_eTxtMemo.setText(item.getMemo());

            Log.d("[ITEM]", "num: " + item.getNum());
            Log.d("[ITEM]", "imageURL: " + item.getImageURL());

        }
    }

    // 그룹 선택값을 얻기 위한 이벤트
    Spinner.OnItemSelectedListener spinnerEvent = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            group = (String) parent.getSelectedItem();
            groupIndex = parent.getSelectedItemPosition();
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
                    EditText myBong_show_eTxtAddGroup = addGroupView.findViewById(R.id.myBong_insert_eTxtAddGroup);
                    String groupName = myBong_show_eTxtAddGroup.getText().toString().trim();
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

                    myBong_show_spinGroup.setSelection(groupAdapter.getCount()); // 추가한 그룹으로 바로 세팅
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
                case R.id.myBong_show_checkBoxAttend:
                    myBong_show_checkBoxNotAttend.setChecked(false);
                    attendance = 0;
                    break;
                case R.id.myBong_show_checkBoxNotAttend:
                    myBong_show_checkBoxAttend.setChecked(false);
                    attendance = 1;
                    break;
            }
        }
    };

    // 초대 방식 선택 이벤트
    CheckBox.OnClickListener inviteWayCBEvent = new CheckBox.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.myBong_show_checkBoxMobile:
                    myBong_show_checkBoxDirect.setChecked(false);
                    myBong_show_checkBoxEtc.setChecked(false);
                    inviteWay = "모바일";
                    break;
                case R.id.myBong_show_checkBoxDirect:
                    myBong_show_checkBoxMobile.setChecked(false);
                    myBong_show_checkBoxEtc.setChecked(false);
                    inviteWay = "직접수령";
                    break;
                case R.id.myBong_show_checkBoxEtc:
                    myBong_show_checkBoxMobile.setChecked(false);
                    myBong_show_checkBoxDirect.setChecked(false);
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
            String temp_str_money = myBong_show_eTxtMoney.getText().toString().trim();
            int temp_money = 0;
            String total_money;
            if (temp_str_money.equals("")) {
                temp_str_money = "0";
            } else {
                temp_str_money = myBong_show_eTxtMoney.getText().toString().trim();
            }
            switch (v.getId()) {
                case R.id.myBong_show_BtnAdd10000:
                    temp_money = Integer.parseInt(temp_str_money);
                    temp_money += 10000;
                    total_money = String.valueOf(temp_money);
                    myBong_show_eTxtMoney.setText(total_money);
                    break;
                case R.id.myBong_show_BtnAdd50000:
                    temp_money = Integer.parseInt(temp_str_money);
                    temp_money += 50000;
                    total_money = String.valueOf(temp_money);
                    myBong_show_eTxtMoney.setText(total_money);
                    break;
                case R.id.myBong_show_BtnAdd100000:
                    temp_money = Integer.parseInt(temp_str_money);
                    temp_money += 100000;
                    total_money = String.valueOf(temp_money);
                    myBong_show_eTxtMoney.setText(total_money);
                    break;
            }
        }
    };

    // 버튼 이벤트
    FrameLayout.OnClickListener frameLayoutEvent = new FrameLayout.OnClickListener() {

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            AlertDialog alertDialog;
            switch (v.getId()) {
                //삭제 버튼
                case R.id.myBong_show_BtnDelete:
                    builder.setTitle("정말 삭제하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestParams params = new RequestParams();
                            params.put("occ_cash_num", num);
                            client.post(deleteURL, params, deleteResponse);

                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    break;

                case R.id.myBong_show_BtnUpdate:
                    builder.setTitle("수정하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateData();
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    break;
                // 취소 버튼
                case R.id.myBong_show_BtnCancel:
                    activity.setFragment(12);
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
        String[] select = {"기본 이미지로 변경", "새로 촬영하기", "갤러리에서 가져오기"};

        builder.setItems(select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0: // 기본 이미지로 변경
                        Glide.with(activity)
                                .load(R.drawable.icon_loco_s)
                                .skipMemoryCache(true)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                                .into(myBong_show_imageProfile);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Log.d("[mybong_camera]", "requestCode: " + requestCode);
            Log.d("[mybong_camera]", "resultCode: " + resultCode);
            Intent photoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath));
            activity.sendBroadcast(photoIntent);
            myBong_show_imageProfile.setImageURI(null);
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
                    .into(myBong_show_imageProfile);

        } else if (requestCode == 101) {

            filePath = FileUtils.getPath(activity, data.getData());
            Glide.with(this)
                    .load(filePath)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .placeholder(R.drawable.icon_loco_s)
                    .into(myBong_show_imageProfile);
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
                myBong_show_eTxtDate.setText(date);
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

    private void updateData() {

        name = myBong_show_eTxtName.getText().toString().trim();
        imageURL = filePath;
        place = myBong_show_eTxtPlace.getText().toString().trim();
        date = myBong_show_eTxtDate.getText().toString().trim();
        money = Integer.parseInt(myBong_show_eTxtMoney.getText().toString().trim());
        memo = myBong_show_eTxtMemo.getText().toString().trim();

        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        params.put("occ_cash_num", num);
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
        client.post(updateURL, params, updateResponse);

        Log.d("[update]", "occ_cash_num:" + num);
        Log.d("[update]", "occ_cash_name:" + name);
        Log.d("[update]", "occ_cash_place:" + place);
        Log.d("[update]", "occ_cash_money:" + money);
        Log.d("[update]", "occ_cash_num:" + num);
        Log.d("[update]", "occ_cash_attendance:" + attendance);
        Log.d("[update]", "occ_cash_invite_way:" + inviteWay);
        Log.d("[update]", "occ_cash_date:" + date);
        Log.d("[update]", "occ_cash_memo:" + memo);
        Log.d("[update]", "occ_cash_origin_img:" + imageURL);
        Log.d("[update]", "occ_cash_group:" + group);
        Log.d("[update]", "occ_cash_group_index:" + groupIndex);

    }

    private void loadGroup() {
        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        client.post(groupViewURL, params, groupViewResponse);
    }

    class HttpResponseUpdate extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");

                if (rt.equals("OK") && total > 0) {
                    activity.setFragment(12);
                } else {
                    Toast.makeText(activity, "수정실패", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    }

    class HttpResponseDelete extends AsyncHttpResponseHandler {

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

        }
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
}