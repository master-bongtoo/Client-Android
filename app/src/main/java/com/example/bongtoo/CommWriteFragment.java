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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bongtoo.helper.FileUtils;
import com.example.bongtoo.helper.PhotoHelper;
import com.example.bongtoo.helper.RegexHelper;
import com.example.bongtoo.helper.VideoHelper;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CommWriteFragment extends Fragment {
    WriteResponse writeResponse;
    AsyncHttpClient client;
    //서버
    String writeURL;

    // 업로드할 사진파일의 경로 저장
    File file;
    Bitmap bitmap;
    String filePhotoPath;
    String fileVideoPath;

    // 글쓰기에 필요한 데이터
    String board_title, board_description, board_origin_img, board_origin_video, board_category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comm_write, container, false);
        client = new AsyncHttpClient();
        writeResponse = new WriteResponse();
        writeURL = "http://" + activity.SERVERIP + "/bongtoo_server/board/boardWriteJson.a";
        setupCommunityWrite();

        //기본 카테고리 지정
        board_category = "전체";
        setCategoryColor(commWrite_button1, commWrite_text1);

        return rootView;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // commWrite 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    /*commWrite 이미지 클릭 이벤트*/
    FrameLayout.OnClickListener commWriteEvent = new ImageView.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.commWrite_button1:
                    setCategoryColor(commWrite_button1, commWrite_text1);
                    board_category = "전체";
                    break;
                case R.id.commWrite_button2:
                    setCategoryColor(commWrite_button2, commWrite_text2);
                    board_category = "자유게시판";
                    break;
                case R.id.commWrite_button3:
                    setCategoryColor(commWrite_button3, commWrite_text3);
                    board_category = "결혼식";
                    break;
                case R.id.commWrite_button4:
                    setCategoryColor(commWrite_button4, commWrite_text4);
                    board_category = "하객";
                    break;
                case R.id.commWrite_button5:
                    setCategoryColor(commWrite_button5, commWrite_text5);
                    board_category = "장례식";
                    break;
                case R.id.commWrite_button6:
                    setCategoryColor(commWrite_button6, commWrite_text6);
                    board_category = "돌잔치";
                    break;
                case R.id.commWrite_button7:
                    setCategoryColor(commWrite_button7, commWrite_text7);
                    board_category = "궁금해요";
                    break;
                case R.id.commWrite_button8:
                    setCategoryColor(commWrite_button8, commWrite_text8);
                    board_category = "TIP";
                    break;
                case R.id.commWrite_photo:
                    showPhotoDialog();
                    break;
                case R.id.commWrite_video:
                    showVideoDialog();
                    break;
                case R.id.commWrite_BtnSubmit:
                    write();
                    break;
                case R.id.commWrite_BtnCancel:
                    activity.setFragment(11);
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

    /*커뮤니티 - 글쓰기*/
    FrameLayout commWrite_BtnCancel, commWrite_BtnSubmit;
    FrameLayout commWrite_button1, commWrite_button2, commWrite_button3, commWrite_button4, commWrite_button5, commWrite_button6, commWrite_button7, commWrite_button8, commWrite_photo, commWrite_video;
    EditText commWrite_eTxtSubject, commWrite_eTxtContent;
    TextView commWrite_TxtCurrentText;
    ImageView commWrite_ImageUpload;
    VideoView commWrite_VideoUpload;

    private void setupCommunityWrite() {
        //헤더 세팅
        activity.setHeaderTitle("글 작성하기");
        activity.setUpBtnBack(true, activity.COMMLIST);
        // 화면 초기화
        commWrite_TxtCurrentText = rootView.findViewById(R.id.commWrite_TxtCurrentText);
        commWrite_eTxtContent = rootView.findViewById(R.id.commWrite_eTxtContent);
        commWrite_eTxtSubject = rootView.findViewById(R.id.commWrite_eTxtSubject);
        commWrite_ImageUpload = rootView.findViewById(R.id.commWrite_ImageUpload);
        commWrite_VideoUpload = rootView.findViewById(R.id.commWrite_VideoUpload);
        commWrite_BtnCancel = rootView.findViewById(R.id.commWrite_BtnCancel);
        commWrite_BtnSubmit = rootView.findViewById(R.id.commWrite_BtnSubmit);
        commWrite_button1 = rootView.findViewById(R.id.commWrite_button1);
        commWrite_button2 = rootView.findViewById(R.id.commWrite_button2);
        commWrite_button3 = rootView.findViewById(R.id.commWrite_button3);
        commWrite_button4 = rootView.findViewById(R.id.commWrite_button4);
        commWrite_button5 = rootView.findViewById(R.id.commWrite_button5);
        commWrite_button6 = rootView.findViewById(R.id.commWrite_button6);
        commWrite_button7 = rootView.findViewById(R.id.commWrite_button7);
        commWrite_button8 = rootView.findViewById(R.id.commWrite_button8);
        commWrite_photo = rootView.findViewById(R.id.commWrite_photo);
        commWrite_video = rootView.findViewById(R.id.commWrite_video);
        commWrite_text1 = rootView.findViewById(R.id.commWrite_text1);
        commWrite_text2 = rootView.findViewById(R.id.commWrite_text2);
        commWrite_text3 = rootView.findViewById(R.id.commWrite_text3);
        commWrite_text4 = rootView.findViewById(R.id.commWrite_text4);
        commWrite_text5 = rootView.findViewById(R.id.commWrite_text5);
        commWrite_text6 = rootView.findViewById(R.id.commWrite_text6);
        commWrite_text7 = rootView.findViewById(R.id.commWrite_text7);
        commWrite_text8 = rootView.findViewById(R.id.commWrite_text8);

        //이벤트 설정
        commWrite_button1.setOnClickListener(commWriteEvent);
        commWrite_button2.setOnClickListener(commWriteEvent);
        commWrite_button3.setOnClickListener(commWriteEvent);
        commWrite_button4.setOnClickListener(commWriteEvent);
        commWrite_button5.setOnClickListener(commWriteEvent);
        commWrite_button6.setOnClickListener(commWriteEvent);
        commWrite_button7.setOnClickListener(commWriteEvent);
        commWrite_button8.setOnClickListener(commWriteEvent);
        commWrite_photo.setOnClickListener(commWriteEvent);
        commWrite_video.setOnClickListener(commWriteEvent);

        commWrite_ImageUpload.setOnClickListener(imageEvent);
        commWrite_VideoUpload.setOnClickListener(videoEvent);

        commWrite_BtnCancel.setOnClickListener(commWriteEvent);
        commWrite_BtnSubmit.setOnClickListener(commWriteEvent);

        //화면 초기화
//        setCommunityLayout(LAYOUTCOMMWRITE);

        commWrite_eTxtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = commWrite_eTxtContent.getText().toString();
                commWrite_TxtCurrentText.setText(input.length() + " / 2000");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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
                            commWrite_ImageUpload.setImageBitmap(null);
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            commWrite_ImageUpload.setVisibility(View.GONE);
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

    VideoView.OnClickListener videoEvent = new VideoView.OnClickListener() {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String[] items = {"동영상 삭제하기", "새로 촬영하기", "갤러리에서 가져오기"};

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = null;
                    switch (which) {
                        case 0:
                            commWrite_VideoUpload.setVideoURI(null);
                            commWrite_VideoUpload.setVisibility(View.GONE);
                            break;
                        case 1:
                            fileVideoPath = VideoHelper.getInstance().getNewVideoPath();
                            // 카메라 앱 호출을 위한 암묵적 인텐트 설정
                            File file = new File(fileVideoPath);
                            Uri uri = null;
                            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(activity.getApplicationContext(),
                                        activity.getApplicationContext().getPackageName()
                                                + ".fileprovider", file);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } else {
                                uri = Uri.fromFile(file);
                            }
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, 103);
                            break;
                        case 2: // 갤러리에서 가져오기
                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            // 이미지 필터링
                            intent.setType("video/*");
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                            startActivityForResult(intent, 104);
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

    //비디오
    private void showVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items = {"새로 촬영하기", "갤러리에서 가져오기"};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //새로 촬영하기
                        fileVideoPath = VideoHelper.getInstance().getNewVideoPath();
                        // 카메라 앱 호출을 위한 암묵적 인텐트 설정
                        File file = new File(fileVideoPath);
                        Uri uri = null;
                        Intent camera_intent =
                                new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(activity.getApplicationContext(),
                                    activity.getApplicationContext().getPackageName()
                                            + ".fileprovider", file);
                            camera_intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            camera_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            uri = Uri.fromFile(file);
                        }
                        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(camera_intent, 103);
                        break;
                    case 1: // 갤러리에서 가져오기
                        Intent gIntent = null;
                        gIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        gIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        // 이미지 필터링
                        gIntent.setType("video/*");
                        gIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        startActivityForResult(gIntent, 104);
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
                    commWrite_ImageUpload.setImageURI(null);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = PhotoHelper.getInstance().getThumb(activity, filePhotoPath);
                    commWrite_ImageUpload.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(bitmap)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .placeholder(R.drawable.icon_loco_s)
                            .into(commWrite_ImageUpload);
                }
                break;

            case 101: // 갤러리 앱
                if (resultCode == getActivity().RESULT_OK) {
                    // 선택한 파일 경로 얻기
                    filePhotoPath = FileUtils.getPath(getContext(), data.getData());
                    commWrite_ImageUpload.setImageURI(null);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = PhotoHelper.getInstance().getThumb(getActivity(), filePhotoPath);
                    commWrite_ImageUpload.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(filePhotoPath)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .placeholder(R.drawable.icon_loco_s)
                            .into(commWrite_ImageUpload);
                }
                break;
            case 103: // 비디오 앱
                if (resultCode == getActivity().RESULT_CANCELED) {
                    // 촬영 결과를 갤러리(MediaStore)에 등록
                    intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://" + fileVideoPath));
                    activity.sendBroadcast(intent);
                    commWrite_VideoUpload.setVideoURI(null);
                    file = new File(fileVideoPath);
                    commWrite_VideoUpload.setVideoURI(Uri.fromFile(file));
                    commWrite_VideoUpload.setVisibility(View.VISIBLE);
                    commWrite_VideoUpload.start();
                }
                break;
            case 104: // 비디오 앱
                if (resultCode == getActivity().RESULT_OK) {
                    // 선택한 파일 경로 얻기
                    fileVideoPath = FileUtils.getPath(getActivity(), data.getData());
                    commWrite_VideoUpload.setVideoURI(null);
                    file = new File(fileVideoPath);
                    Log.d("[TEST", "fileVideo" + fileVideoPath);
                    commWrite_VideoUpload.setVideoURI(Uri.fromFile(file));
                    commWrite_VideoUpload.setVisibility(View.VISIBLE);
                    commWrite_VideoUpload.start();
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    commWrite_VideoUpload.setVisibility(View.GONE);
                }
                break;
        }
    }
    // ~ 2020.04.01 박성용

    // 서버에 데이터 전송

    private void write() {

        board_title = commWrite_eTxtSubject.getText().toString().trim();
        board_description = commWrite_eTxtContent.getText().toString().trim();
        // 입력값 검사
        String msg = null;
        if (msg == null && !RegexHelper.getInstance().isValue(board_title)) {
            msg = "제목을 입력하세요";
        }
        if (msg == null && !RegexHelper.getInstance().isValue(board_description)) {
            msg = "내용을 입력하세요";
        }
        if (msg == null && !RegexHelper.getInstance().isValue(board_category)) {
            msg = "게시판 카테고리를 선택하세요";
        }
        // 에러가 있으면
        if (msg != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        // 에러가 없으면, 서버에 전송
        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        params.put("board_title", board_title);
        params.put("board_description", board_description);
        params.put("board_category", board_category);
        try {
            if (filePhotoPath != null) {
                params.put("board_origin_img", new File(filePhotoPath));
            }
            if (fileVideoPath != null) {
                params.put("board_origin_video", new File(fileVideoPath));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(writeURL, params, writeResponse);
    }

    class WriteResponse extends AsyncHttpResponseHandler {

        ProgressDialog dialog;

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
                String rt = json.getString("rt");
                int total = json.getInt("total");
                if (rt.equals("OK") && total > 0) {
                    Toast.makeText(activity, "글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    activity.setFragment(11);
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
            Toast.makeText(activity, "통신 실패 - 글쓰기", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 카테고리 선택한 값만 색깔 켜지게 하기
     **/
    TextView commWrite_text1, commWrite_text2, commWrite_text3, commWrite_text4, commWrite_text5, commWrite_text6, commWrite_text7, commWrite_text8;

    private void setCategoryColor(FrameLayout category, TextView text) {
        commWrite_text1.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text2.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text3.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text4.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text5.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text6.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text7.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_text8.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commWrite_button1.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button2.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button3.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button4.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button5.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button6.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button7.setBackgroundResource(R.color.colorBtnDefault);
        commWrite_button8.setBackgroundResource(R.color.colorBtnDefault);
        category.setBackgroundResource(R.color.colorNavy);
        text.setTextColor(getResources().getColorStateList(R.color.colorWhite));
    }

}