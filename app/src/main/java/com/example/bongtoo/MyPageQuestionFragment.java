package com.example.bongtoo;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class MyPageQuestionFragment extends Fragment {
    //객체선언
    FrameLayout servicecenter_button1_gongsi,servicecenter_button2_ildaeil,servicecenter_button3_faq, question_BtnSubmit,question_BtnInsertFile;
    EditText question_eTxtSubject,question_eTxtContent,question_eTxtphone,question_eTxtemail;
    TextView question_count;
    ImageView question_ImageUpload;
    AsyncHttpClient client;
    Response Response;
    File file;
    Bitmap bitmap;
    String filePhotoPath;

    MainActivity activity;
    String URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_my_page_question,container,false);
        URL= "http://"+activity.SERVERIP+"/bongtoo_server/question/questionWriteJson.a";
        servicecenter_button1_gongsi = rootView.findViewById(R.id.servicecenter_button1_gongsi);
        servicecenter_button2_ildaeil = rootView.findViewById(R.id.servicecenter_button2_ildaeil);
        servicecenter_button3_faq = rootView.findViewById(R.id.servicecenter_button3_faq);
        question_BtnSubmit = rootView.findViewById(R.id.question_BtnSubmit);
        question_eTxtSubject = rootView.findViewById(R.id.question_eTxtSubject);
        question_eTxtContent = rootView.findViewById(R.id.question_eTxtContent);
        question_eTxtphone =rootView.findViewById(R.id.question_eTxtphone);
        question_eTxtemail = rootView.findViewById(R.id.question_eTxtemail);
        question_count = rootView.findViewById(R.id.question_count);
        question_ImageUpload =rootView.findViewById(R.id.question_ImageUpload);
        question_BtnInsertFile = rootView.findViewById(R.id.question_BtnInsertFile);

        client = new AsyncHttpClient();
        Response = new Response(getActivity());

//이벤트처리
        question_ImageUpload.setOnClickListener(imageEvent);
        servicecenter_button1_gongsi.setOnClickListener(servicecenterEvent);
        servicecenter_button2_ildaeil.setOnClickListener(servicecenterEvent);
        servicecenter_button3_faq.setOnClickListener(servicecenterEvent);
        question_BtnSubmit.setOnClickListener(servicecenterEvent);
        question_BtnInsertFile.setOnClickListener(servicecenterEvent);

//글자수 세기
        question_eTxtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = question_eTxtContent.getText().toString();
                question_count.setText(input.length()+" / 2000");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return  rootView;
    }
    FrameLayout.OnClickListener servicecenterEvent = new FrameLayout.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.servicecenter_button1_gongsi:
                    activity.setFragment(activity.MYPAGE_NOTICE);
                    break;
                case R.id.servicecenter_button2_ildaeil:
                    activity.setFragment(activity.MYPAGE_QUESTION);
                    break;
                case R.id.question_BtnInsertFile:
                    showPhotoDialog();
                    break;
                case R.id.servicecenter_button3_faq:
                    activity.setFragment(activity.MYPAGE_FAQ);
                    break;
                case R.id.question_BtnSubmit:
                    question_writesendData();
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

    // 2020.04.01 박성용   ~ (다음 2020.04.01 박성용까지)
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
        filePhotoPath = image.getAbsolutePath();
        return image;
    }
    // 사진 이벤트
    ImageView.OnClickListener imageEvent = new ImageView.OnClickListener() {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String[] items = {"사진 삭제하기", "새로 촬영하기", "갤러리에서 가져오기"};

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = null;
                    switch (which) {
                        case 0:
                            question_ImageUpload.setImageBitmap(null);
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            question_ImageUpload.setVisibility(View.GONE);
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
    };

    // 포토
    private void showPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items = {"새로 촬영하기", "갤러리에서 가져오기"};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                    case 0: //새로 촬영하기
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
                    case 1: // 갤러리에서 가져오기
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
        Intent intent = null;
        switch (requestCode) {
            case 100: // 카메라 앱
                if (resultCode == getActivity().RESULT_OK) {
                    Intent photoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePhotoPath));
                    activity.sendBroadcast(photoIntent);
                    question_ImageUpload.setImageURI(null);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = PhotoHelper.getInstance().getThumb(activity, filePhotoPath);
                    question_ImageUpload.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(bitmap)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .placeholder(R.drawable.icon_loco_s)
                            .into(question_ImageUpload);
                }
                break;

            case 101: // 갤러리 앱
                if (resultCode == getActivity().RESULT_OK) {
                    // 선택한 파일 경로 얻기
                    filePhotoPath = FileUtils.getPath(getContext(), data.getData());
                    question_ImageUpload.setImageURI(null);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = PhotoHelper.getInstance().getThumb(getActivity(), filePhotoPath);
                    question_ImageUpload.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(filePhotoPath)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .placeholder(R.drawable.icon_loco_s)
                            .into(question_ImageUpload);
                }
                break;
        }
    }

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
            dialog.setMessage("잠시만 기다려주세요");
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
                    initWrite();
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

    //서버
    int member_num,question_type;
    String question_subject,question_content,question_email,question_phone,question_origin_img;

    private void question_writesendData() {
        String strquestion_eTxtSubject = question_eTxtSubject.getText().toString().trim();
        String strquestion_eTxtContent = question_eTxtContent.getText().toString().trim();
        String strquestion_email =question_eTxtemail.getText().toString().trim();
        String strquestion_eTxtphone =question_eTxtphone.getText().toString().trim();

// 입력값 검사
        String msg = null;
        if (!RegexHelper.getInstance().isValue(strquestion_eTxtSubject)) {
            msg = "제목을 입력하세요";
        }else if (!RegexHelper.getInstance().isValue(strquestion_eTxtContent)) {
            msg = "내용을 입력하세요";
        } else if (!RegexHelper.getInstance().isValue(strquestion_email) || !RegexHelper.getInstance().isValue(strquestion_eTxtphone)) {
            msg = "휴대폰 번호 또는 이메일을 입력하세요";
        }
// 에러가 있으면
        if (msg != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
//데이터 받아오기
        member_num = 1;
        question_type =0;
        question_subject = strquestion_eTxtSubject;
        question_content = strquestion_eTxtContent;
        question_email = strquestion_email;
        question_phone = strquestion_eTxtphone;

// 에러가 없으면, 서버에 전송
        RequestParams params = new RequestParams();
        params.put("member_num",member_num);
        params.put("question_type", question_type);
        params.put("question_subject", question_subject);
        params.put("question_content", question_content);
        params.put("question_email", question_email);
        params.put("question_phone", question_phone);
        try {
            if(filePhotoPath!=null){
                params.put("question_origin_img", new File(filePhotoPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
// multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(URL,params,Response);

        Toast.makeText(activity, "문의를 제출하였습니다.", Toast.LENGTH_SHORT).show();
        initWrite();
        activity.setFragment(activity.MYPAGE);
    }
    /**write 성공 후 컴포넌트 등 초기화**/
    private void initWrite() {
        question_eTxtContent.setText("");
        question_eTxtSubject.setText("");
        question_eTxtemail.setText("");
        question_eTxtphone.setText("");
        question_ImageUpload.setImageURI(null);
        question_ImageUpload.setVisibility(View.GONE);

    }
}