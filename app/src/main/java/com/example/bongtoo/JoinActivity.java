package com.example.bongtoo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bongtoo.helper.FileUtils;
import com.example.bongtoo.helper.PhotoHelper;
import com.example.bongtoo.helper.RegexHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView btnHeaderBack;
    FrameLayout join_BtnOK;
    TextView id_check;
    EditText editText_Id,editTextPW_1,editText_Email,editText_Name,editText_Nickname,editText_Phone,editTextPW_2;
    ImageView button_Image;
    AsyncHttpClient client;
    HttpResponse response;
    String filePhotoPath;
    String URL, URL2;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        MainActivity activity11 = new MainActivity();
        URL = "http://"+activity11.SERVERIP+"/bongtoo_server/member/memberWriteJson.a";
        URL2 = "http://"+activity11.SERVERIP+"/bongtoo_server/member/memberIDCheckJson.a";
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        join_BtnOK = findViewById(R.id.join_BtnOK);
        id_check=findViewById(R.id.id_check);
        editText_Id = findViewById(R.id.editText_Id);
        editTextPW_1 = findViewById(R.id.editTextPW_1);
        editTextPW_2 = findViewById(R.id.editTextPW_2);
        editText_Email= findViewById(R.id.editText_Email);
        editText_Name = findViewById(R.id.editText_Name);
        editText_Nickname = findViewById(R.id.editText_Nickname);
        editText_Phone =findViewById(R.id.editText_Phone);
        button_Image=findViewById(R.id.button_Image);
        client = new AsyncHttpClient();
        response = new HttpResponse(this);

// 이벤트 설정
        join_BtnOK.setOnClickListener(this);
        button_Image.setOnClickListener(this);
        btnHeaderBack.setOnClickListener(this);
        join_BtnOK.setOnClickListener(this);
        id_check.setOnClickListener(this);
//확인
        editTextPW_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextPW_1.getText().toString().trim().equals(editTextPW_2.getText().toString().trim())){
                    editTextPW_2.setTextColor(getResources().getColorStateList(R.color.colorWhite));
                }else {
                    editTextPW_2.setTextColor(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnHeaderBack:
                finish();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.join_BtnOK:
                sendData();
                break;

            case R.id.button_Image:
                showListDialog();
                break;

            case R.id.id_check:
                idcheck();
                break;
        }
    }

    private void idcheck() {
        String struser_id = editText_Id.getText().toString().trim();
        if(struser_id.equals("")){
            Toast.makeText(this, "아이디가 공백입니다. 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!RegexHelper.getInstance().isEngNum(struser_id)) {
            Toast.makeText(this, "아이디는 한글사용이 불가 합니다", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("member_id", struser_id);
        client.post(URL2, params, response);
    }

    private void showListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"기본이미지로 설정", "새로 촬영하기", "갤러리에서 가져오기"};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0:
                        button_Image.setImageBitmap(null);
                        if (bitmap != null) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                        button_Image.setImageResource(R.drawable.icon_loco_s);
                        break;
                    case 1: //새로 촬영하기
                        filePhotoPath = PhotoHelper.getInstance().getNewPhotoPath();
                        File file = new File(filePhotoPath);
                        Uri uri = null;
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", file);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            uri = Uri.fromFile(file);
                        }

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, 100);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: // 카메라 앱
// 촬영 결과를 갤러리(MediaStore)에 등록
                Intent photoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePhotoPath));
                sendBroadcast(photoIntent);
                button_Image.setImageURI(null);
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                bitmap = PhotoHelper.getInstance().getThumb(this, filePhotoPath);
                button_Image.setImageBitmap(bitmap);
                Glide.with(this).load(bitmap).placeholder(R.drawable.icon_loco_s).override(100, 100).into(button_Image);
                break;
            case 101: // 갤러리 앱
                filePhotoPath = FileUtils.getPath(this, data.getData());
                button_Image.setImageBitmap(null);
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                bitmap = PhotoHelper.getInstance().getThumb(this, filePhotoPath);
                Glide.with(this).load(bitmap).placeholder(R.drawable.icon_loco_s).override(100, 100).into(button_Image);
                break;
        }
    }
    // 서버에 데이터 전송
    private void sendData() {
        String struser_id = editText_Id.getText().toString().trim();
        String struser_pwd  = editTextPW_1.getText().toString().trim();
        String swruser_pwd_check = editTextPW_2.getText().toString().trim();
        String struser_email = editText_Email.getText().toString().trim();
        String struser_name = editText_Name.getText().toString().trim();
        String struser_nickname = editText_Nickname.getText().toString().trim();
        String struser_phon = editText_Phone.getText().toString().trim();
// 입력값 검사
        String msg = null;
        if(msg==null && !RegexHelper.getInstance().isValue(struser_id)) {
            msg = "아이디를 입력하세요";
        }
        if(msg==null && !RegexHelper.getInstance().isEngNum(struser_id)) { //03-31 청일
            msg = "아이디는 한글 사용이 불가합니다";
        }
        if(msg==null && !RegexHelper.getInstance().isValue(struser_pwd)) {
            msg = "비밀번호를 입력하세요";
        }
        if(!struser_pwd.equals(swruser_pwd_check)) {
            msg = "비밀번호가 일치하지 않습니다.";
        }
        if(msg==null && !RegexHelper.getInstance().isValue(struser_email)) {
            msg = "이메일을 입력하세요";
        }
        if(msg==null && !RegexHelper.getInstance().isEmail(struser_email)) {
            msg = "이메일 형식에 맞지 않습니다.";
        }
        if(msg==null && !RegexHelper.getInstance().isValue(struser_nickname)) {
            msg = "닉네임을 입력하세요";
        }
        if(msg==null && !RegexHelper.getInstance().isCellPhone(struser_phon)) {
            msg = "휴대폰번호를 입력하세요";
        }
// 에러가 있으면
        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
// 에러가 없으면, 서버에 전송
        RequestParams params = new RequestParams();
        params.put("member_id", struser_id);
        params.put("member_pw", struser_pwd);
        params.put("email", struser_email);
        params.put("name", struser_name);
        params.put("nickname", struser_nickname);
        params.put("member_phone",struser_phon);
        try {
            if (filePhotoPath!=null){
                params.put("member_origin_img", new File(filePhotoPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
// multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(URL, params, response);
    }

    class HttpResponse extends AsyncHttpResponseHandler {
        Activity activity;


        public HttpResponse(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt= json.getString("rt");
                if (rt.equals("IDOK")){
                    Toast.makeText(activity, "사용하실수 있는 아이디입니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (rt.equals("IDFAIL")){
                    Toast.makeText(activity, "사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rt.equals("OK")) {
                    Toast.makeText(activity, "회원가입 성공! 로그인하세요.", Toast.LENGTH_SHORT).show();
                    gotoMain();
                }else {
                    Toast.makeText(activity, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_JOIN]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }

    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}