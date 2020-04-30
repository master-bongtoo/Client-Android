package com.example.bongtoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bongtoo.helper.FileUtils;
import com.example.bongtoo.helper.PhotoHelper;
import com.example.bongtoo.model.Member;
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
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

public class MyPageUpdateFragment extends Fragment {
    //버튼
    FrameLayout myinfo_setting_BtnSubmit, myinfo_setting_BtnCancel;
    FrameLayout myinfo_setting_button1_nick, myinfo_setting_button2_email, myinfo_myimg;
    ImageView myinfo_imgbefore;
    TextView myinfo_name, myinfo_nickname, myinfo_email, myinfo_logtime, myinfo_settings_pw;
    EditText myinfo_setting_eTxtnick, myinfo_setting_eTxtemail, myinfo_setting_eTxtPWNew, myinfo_setting_eTxtPWconfirmNew;
    LinearLayout myinfo_setting_newPWlayout, myinfo_setting_newPWconfirmlayout;
    Bitmap bitmap;
    //로그인한 멤버 정보
    Member memberInfo;
    //수정상태 여부
    boolean isOpen_nickname = false;
    boolean isOpen_email = false;
    boolean isOpen_pw = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_my_page_update,container,false);
        /*메인에서 memberInfo 받아오기*/
        memberInfo = activity.memberInfo;
        //초기화
        myinfo_setting_BtnSubmit = rootView.findViewById(R.id.myinfo_setting_BtnSubmit);
        myinfo_setting_BtnCancel = rootView.findViewById(R.id.myinfo_setting_BtnCancel);
        myinfo_setting_button1_nick = rootView.findViewById(R.id.myinfo_setting_button1_nick);
        myinfo_setting_button2_email = rootView.findViewById(R.id.myinfo_setting_button2_email);
        myinfo_imgbefore = rootView.findViewById(R.id.myinfo_imgbefore);
        myinfo_name = rootView.findViewById(R.id.myinfo_name);
        myinfo_email = rootView.findViewById(R.id.myinfo_email);
        myinfo_logtime = rootView.findViewById(R.id.myinfo_logtime);
        myinfo_nickname = rootView.findViewById(R.id.myinfo_nickname);
        myinfo_setting_eTxtnick = rootView.findViewById(R.id.myinfo_setting_eTxtnick);
        myinfo_setting_eTxtemail = rootView.findViewById(R.id.myinfo_setting_eTxtemail);
        myinfo_setting_eTxtPWNew = rootView.findViewById(R.id.myinfo_setting_eTxtPWNew);
        myinfo_setting_newPWlayout = rootView.findViewById(R.id.myinfo_setting_newPWlayout);
        myinfo_myimg = rootView.findViewById(R.id.myinfo_myimg);
        myinfo_setting_newPWconfirmlayout = rootView.findViewById(R.id.myinfo_setting_newPWconfirmlayout);
        myinfo_setting_eTxtPWconfirmNew = rootView.findViewById(R.id.myinfo_setting_eTxtPWconfirmNew);
        myinfo_settings_pw = rootView.findViewById(R.id.myinfo_settings_pw);

        //클릭이벤트
        myinfo_setting_BtnSubmit.setOnClickListener(myPageUpdateEvent);
        myinfo_setting_BtnCancel.setOnClickListener(myPageUpdateEvent);
        myinfo_setting_button1_nick.setOnClickListener(myPageUpdateEvent);
        myinfo_setting_button2_email.setOnClickListener(myPageUpdateEvent);
        myinfo_myimg.setOnClickListener(myPageUpdateEvent);

        myinfo_setting_eTxtPWconfirmNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (myinfo_setting_eTxtPWNew.getText().toString().trim().equals(myinfo_setting_eTxtPWconfirmNew.getText().toString().trim())){
                    myinfo_setting_eTxtPWconfirmNew.setTextColor(Color.BLACK);
                }else {
                    myinfo_setting_eTxtPWconfirmNew.setTextColor(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });



        //받아온 정보 세팅
        if(memberInfo!=null) {
            myinfo_nickname.setText(memberInfo.getNickname());
            myinfo_name.setText(memberInfo.getName());
            myinfo_email.setText(memberInfo.getEmail());
            myinfo_logtime.setText(memberInfo.getFirst_logtime());
            if(!(memberInfo.getMember_img_path().equals("")||memberInfo.getMember_img_path()==null)){
                Glide.with(activity).load(memberInfo.getMember_img_path()).into(myinfo_imgbefore);
            }
            myinfo_settings_pw.setText(memberInfo.getMember_pw());
        }

        //초기화면 VISIBLE 세팅 : TextView는 보이고 editText는 숨김
        myinfo_name.setVisibility(View.VISIBLE);
        myinfo_email.setVisibility(View.VISIBLE);
        myinfo_setting_eTxtnick.setVisibility(View.GONE);
        myinfo_setting_eTxtemail.setVisibility(View.GONE);
        myinfo_setting_newPWlayout.setVisibility(View.VISIBLE);
        myinfo_setting_newPWconfirmlayout.setVisibility(View.VISIBLE);

        //통신 초기화
        client = new AsyncHttpClient();
        response = new UpdateHttpResponse(activity);
        memberInfoHttpResponse = new MemberInfoHttpResponse();

        return rootView;
    }
    String input_nickname, input_email, input_pw1, input_pw2;
    FrameLayout.OnClickListener myPageUpdateEvent = new FrameLayout.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.myinfo_setting_BtnSubmit:
                    RequestParams params = new RequestParams();
                    params.put("member_num", memberInfo.getMember_num());
                    input_nickname = myinfo_setting_eTxtnick.getText().toString().trim();
                    input_email = myinfo_setting_eTxtemail.getText().toString().trim();
                    input_pw1 = myinfo_setting_eTxtPWNew.getText().toString().trim();
                    input_pw2 = myinfo_setting_eTxtPWconfirmNew.getText().toString().trim();

                    if(!input_nickname.equals("")){
                        params.put("nickname", input_nickname);
                    }
                    if(!input_email.equals("")){
                        params.put("email", input_email);
                    }
                    if((!input_pw1.equals("")) || (!input_pw2.equals(""))) {
                        if(!input_pw1.equals(input_pw2)) {
                            Toast.makeText(activity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        params.put("member_pw", input_pw2);
                    }
                    if (!filePath.equals("")){
                        try {
                            params.put("member_origin_img", new File(filePath));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    params.setForceMultipartEntityContentType(true);
                    client.post("http://"+activity.SERVERIP+"/bongtoo_server/member/memberUpdateJson.a", params, response);
                    break;
                case R.id.myinfo_setting_BtnCancel:
                    Toast.makeText(activity, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    activity.setFragment(activity.MYPAGE);
                    break;
                case R.id.myinfo_setting_button1_nick:
                    if(isOpen_nickname) {
                        myinfo_nickname.setVisibility(View.VISIBLE);
                        myinfo_setting_eTxtnick.setVisibility(View.GONE);
                        isOpen_nickname=false;
                    } else {
                        myinfo_nickname.setVisibility(View.GONE);
                        myinfo_setting_eTxtnick.setVisibility(View.VISIBLE);
                        isOpen_nickname=true;
                    }
                    break;
                case R.id.myinfo_setting_button2_email:
                    if(isOpen_email){
                        myinfo_email.setVisibility(View.VISIBLE);
                        myinfo_setting_eTxtemail.setVisibility(View.GONE);
                        isOpen_email=false;
                    } else {
                        myinfo_email.setVisibility(View.GONE);
                        myinfo_setting_eTxtemail.setVisibility(View.VISIBLE);
                        isOpen_email=true;
                    }
                    break;
                case R.id.myinfo_myimg:
                    myinfo_imgbefore.setImageURI(null);
                    showPhotoDialog();
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

    //////////////////////////////////////////////////////////////////////////////////////
    // 화면 기본 세팅
    //////////////////////////////////////////////////////////////////////////////////////
    AsyncHttpClient client;
    UpdateHttpResponse response;
    MemberInfoHttpResponse memberInfoHttpResponse;
    String filePath = "";
    File file;

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
    // 포토
    private void showPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items = {"기본 이미지로 변경", "새로 촬영하기", "갤러리에서 가져오기"};

        builder.setItems(items, new DialogInterface.OnClickListener() {
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
                                .into(myinfo_imgbefore);
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
                    case 2: // 갤러리에서 가져오기
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Log.d("[mybong_camera]", "requestCode: " + requestCode);
            Log.d("[mybong_camera]", "resultCode: " + resultCode);
            Intent photoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath));
            activity.sendBroadcast(photoIntent);
            myinfo_imgbefore.setImageURI(null);
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
                    .into(myinfo_imgbefore);

        } else if (requestCode == 101) {

            filePath = FileUtils.getPath(activity, data.getData());
            myinfo_imgbefore.setImageBitmap(null);
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
                    .into(myinfo_imgbefore);
        }
    }

    class UpdateHttpResponse extends AsyncHttpResponseHandler {
        Activity activity1;

        public UpdateHttpResponse(Activity activity) {
            this.activity1 = activity;
        }

        @Override
        public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt= json.getString("rt");
                if (rt.equals("OK")) {
                    Toast.makeText(activity1, "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    /*더보기 세팅*/
                    if(myinfo_setting_eTxtnick.getText().toString().trim().equals("")){
                        activity.main_nickname.setText(myinfo_nickname.getText().toString().trim()+"님");
                    } else {
                        activity.main_nickname.setText(myinfo_setting_eTxtnick.getText().toString().trim()+"님");
                    }

                    activity.setUpFooterImage(activity.MAINHOME);
                    activity.setFragment(activity.MAINHOME);
                } else {
                    Toast.makeText(activity1, "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity1, "통신 실패", Toast.LENGTH_SHORT).show();
        }
    }

    class MemberInfoHttpResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String content = new String(bytes);
            try {
                JSONObject json = new JSONObject(content);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                JSONArray item = json.getJSONArray("item");
                if(item.length() > 0) {
                    JSONObject temp = item.getJSONObject(0);
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
                    memberInfo.setMember_pw(temp.getString(""));
                    Log.d("[TEST - memberInfo]","member_id "+ memberInfo.getMember_img_path());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "연결 실패 - member", Toast.LENGTH_SHORT).show();
            Log.d("[ERROR]", "에러코드 : " + i + ", 에러내용 : " + throwable.getLocalizedMessage());
        }
    }
}