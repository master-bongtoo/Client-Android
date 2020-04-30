package com.example.bongtoo;

import android.Manifest;
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
import androidx.fragment.app.FragmentTransaction;

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
import com.example.bongtoo.helper.VideoHelper;
import com.example.bongtoo.model.Community;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CommModifyFragment extends Fragment {

    File file;
    String filePhotoPath;
    String fileVideoPath;
    Bitmap bitmap;

    Community community;
    int board_num;
    int grade;
    String nickname;
    String board_title;
    String board_description;
    String board_firstdate;
    String board_editdate;
    String board_origin_img;
    String board_img_path;
    String board_video_path;
    String board_origin_video;
    String board_category;
    int board_hit;
    int board_like;

    int category_code;
    //서버
    String modifyURL, viewURL;
    AsyncHttpClient client;
    HttpResponseModify modifyResponse;



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
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comm_modify,container,false);
        modifyURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/boardUpdateJson.a";
        viewURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/boardViewJson.a";
        setupCommunityModify();
        getData();
        return rootView;
    }


    /*커뮤니티 - 수정하기*/
    FrameLayout commModify_BtnCancel, commModify_BtnSubmit;
    LinearLayout commModify_button1,commModify_button2,commModify_button3,commModify_button4,commModify_button5,commModify_button6,commModify_button7,commModify_button8,commModify_photo,commModify_video;
    EditText commModify_EditSubject,commModify_EditContent;
    TextView commModify_TxtCurrentText;
    ImageView commModify_ImageUpload;
    VideoView commModify_VideoUpload;

    private void setupCommunityModify() {

        //헤더 세팅
        activity.setHeaderTitle("글 수정하기");
        ImageView btnHeaderBack = getActivity().findViewById(R.id.btnHeaderBack);
        btnHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToDetail();
            }
        });
        // 화면 초기화
        commModify_TxtCurrentText = rootView.findViewById(R.id.commModify_TxtCurrentText);
        commModify_EditContent = rootView.findViewById(R.id.commModify_EditContent);
        commModify_EditSubject = rootView.findViewById(R.id.commModify_EditSubject);
        commModify_ImageUpload = rootView.findViewById(R.id. commModify_ImageUpload);
        commModify_VideoUpload = rootView.findViewById(R.id.commModify_VideoUpload);
        commModify_BtnCancel = rootView.findViewById(R.id.commModify_BtnCancel);
        commModify_BtnSubmit = rootView.findViewById(R.id.commModify_BtnSubmit);
        commModify_button1 = rootView.findViewById(R.id.commModify_button1);
        commModify_button2 = rootView.findViewById(R.id.commModify_button2);
        commModify_button3 = rootView.findViewById(R.id.commModify_button3);
        commModify_button4 = rootView.findViewById(R.id.commModify_button4);
        commModify_button5 = rootView.findViewById(R.id.commModify_button5);
        commModify_button6 = rootView.findViewById(R.id.commModify_button6);
        commModify_button7 = rootView.findViewById(R.id.commModify_button7);
        commModify_button8 = rootView.findViewById(R.id.commModify_button8);
        commModify_text1 = rootView.findViewById(R.id.commModify_text1);
        commModify_text2 = rootView.findViewById(R.id.commModify_text2);
        commModify_text3 = rootView.findViewById(R.id.commModify_text3);
        commModify_text4 = rootView.findViewById(R.id.commModify_text4);
        commModify_text5 = rootView.findViewById(R.id.commModify_text5);
        commModify_text6 = rootView.findViewById(R.id.commModify_text6);
        commModify_text7 = rootView.findViewById(R.id.commModify_text7);
        commModify_text8 = rootView.findViewById(R.id.commModify_text8);
        commModify_photo = rootView.findViewById(R.id.commModify_photo);
        commModify_video = rootView.findViewById(R.id.commModify_video);

        client = new AsyncHttpClient();
        modifyResponse = new HttpResponseModify();

        // 이벤트 설정
        commModify_button1.setOnClickListener(commModifyEvent);
        commModify_button2.setOnClickListener(commModifyEvent);
        commModify_button3.setOnClickListener(commModifyEvent);
        commModify_button4.setOnClickListener(commModifyEvent);
        commModify_button5.setOnClickListener(commModifyEvent);
        commModify_button6.setOnClickListener(commModifyEvent);
        commModify_button7.setOnClickListener(commModifyEvent);
        commModify_button8.setOnClickListener(commModifyEvent);
        commModify_photo.setOnClickListener(commModifyEvent);
        commModify_video.setOnClickListener(commModifyEvent);

        commModify_ImageUpload.setOnClickListener(imageEvent);
        commModify_VideoUpload.setOnClickListener(videoEvent);

        commModify_BtnCancel.setOnClickListener(commModifyEvent);
        commModify_BtnSubmit.setOnClickListener(commModifyEvent);
        //Data 세팅
        /*[함수사용] modify 화면 세팅(선택한 글 정보 dto로 가져오기)*/



        //화면 초기화
//        setCommunityLayout(LAYOUTCOMMMODIFY);
        commModify_EditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = commModify_EditContent.getText().toString();
                commModify_TxtCurrentText.setText(input.length()+" / 2000");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // 글 상세보기에 넘어온 데이터 받은 후 세팅
    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {

            community = (Community) bundle.getSerializable("item");
            board_num = community.getBoard_num();
            Log.d("[modify]", "board_num" + board_num);
            grade = community.getGrade();
            nickname = community.getNickname();
            board_category = community.getBoard_category();

            board_firstdate = community.getBoard_firstdate();
            board_hit = community.getBoard_hit();
            board_like = community.getBoard_like();
            board_title = community.getBoard_title();
            board_category = community.getBoard_category();
            board_description = community.getBoard_description();
            board_img_path = community.getBoard_img_path();
            board_video_path = community.getBoard_video_path();
            board_origin_img = community.getBoard_origin_img();
            board_origin_video = community.getBoard_origin_video();

            commModify_EditSubject.setText(board_title);
            commModify_EditContent.setText(board_description);
            if (!community.getBoard_img_path().equals("")) {
                commModify_ImageUpload.setVisibility(View.VISIBLE);
                Glide.with(activity).load(board_img_path).into(commModify_ImageUpload);
            }
            switch (board_category) {
                case "전체":
                    setCategoryColor(commModify_button1, commModify_text1);
                    break;
                case "자유게시판":
                    setCategoryColor(commModify_button2, commModify_text2);
                    break;
                case "결혼식":
                    setCategoryColor(commModify_button3, commModify_text3);
                    break;
                case "하객":
                    setCategoryColor(commModify_button4, commModify_text4);
                    break;
                case "장례식":
                    setCategoryColor(commModify_button5, commModify_text5);
                    break;
                case "돌잔치":
                    setCategoryColor(commModify_button6, commModify_text6);
                    break;
                case "궁금해요":
                    setCategoryColor(commModify_button7, commModify_text7);
                    break;
                case "TIP":
                    setCategoryColor(commModify_button8, commModify_text8);
                    break;
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////
    // commModify 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    /*commModify 이미지 클릭 이벤트*/
    FrameLayout.OnClickListener commModifyEvent = new ImageView.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.commModify_button1:
                    setCategoryColor(commModify_button1, commModify_text1);
                    board_category = "전체";
                    category_code = 1;
                    break;
                case R.id.commModify_button2:
                    setCategoryColor(commModify_button2, commModify_text2);
                    board_category = "자유게시판";
                    category_code = 2;
                    break;
                case R.id.commModify_button3:
                    setCategoryColor(commModify_button3, commModify_text3);
                    board_category = "결혼식";
                    category_code = 3;
                    break;
                case R.id.commModify_button4:
                    setCategoryColor(commModify_button4, commModify_text4);
                    board_category = "하객";
                    category_code = 4;
                    break;
                case R.id.commModify_button5:
                    setCategoryColor(commModify_button5, commModify_text5);
                    board_category = "장례식";
                    category_code = 5;
                    break;
                case R.id.commModify_button6:
                    setCategoryColor(commModify_button6, commModify_text6);
                    board_category = "돌잔치";
                    category_code = 6;
                    break;
                case R.id.commModify_button7:
                    setCategoryColor(commModify_button7, commModify_text7);
                    board_category = "궁금해요";
                    category_code = 7;
                    break;
                case R.id.commModify_button8:
                    setCategoryColor(commModify_button8, commModify_text8);
                    board_category = "TIP";
                    category_code = 8;
                    break;
                case R.id.commModify_photo:
                    showPhotoDialog();
                    break;
                case R.id.commModify_video:
                    showVideoDialog();
                    break;
                case R.id.commModify_BtnSubmit:
                    modifyAlertDialog();
                    break;
                case R.id.commModify_BtnCancel:
                    backToDetail();
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
        filePhotoPath = image.getAbsolutePath();
        return image;
    }

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
                            commModify_ImageUpload.setImageBitmap(null);
                            board_img_path = null;
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            commModify_ImageUpload.setVisibility(View.GONE);
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
                            commModify_VideoUpload.setVideoURI(null);
                            commModify_VideoUpload.setVisibility(View.GONE);
                            break;
                        case 1:
                            fileVideoPath = VideoHelper.getInstance().getNewVideoPath();
                            // 카메라 앱 호출을 위한 암묵적 인텐트 설정
                            File file = new File(fileVideoPath);
                            Uri uri = null;
                            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
    private void modify() {

        board_title = commModify_EditSubject.getText().toString().trim();
        board_description = commModify_EditContent.getText().toString().trim();
        board_origin_img = community.getBoard_origin_img();
        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        params.put("board_num", board_num);
        params.put("board_category", board_category);
        params.put("board_title", board_title);
        params.put("board_description", board_description);

        if (filePhotoPath != null) {
            try {
                params.put("board_origin_img", new File(filePhotoPath));
                board_img_path = filePhotoPath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            params.put("board_origin_img", "");
        }
        if (fileVideoPath != null) {
            try {
                params.put("board_origin_video", new File(fileVideoPath));
                board_video_path = fileVideoPath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            params.put("board_origin_video", "");
        }
        // multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(modifyURL, params, modifyResponse);

        community.setBoard_num(board_num);
        community.setMember_num(activity.member_num);
        community.setGrade(grade);
        community.setNickname(nickname);
        community.setBoard_category(board_category);
        community.setBoard_title(board_title);
        community.setBoard_description(board_description);
        community.setBoard_origin_img(board_origin_img);
        community.setBoard_img_path(board_img_path);
        community.setBoard_origin_video(board_origin_video);
        community.setBoard_video_path(board_video_path);
        community.setBoard_firstdate(board_firstdate);
        community.setBoard_editdate(board_editdate);
        community.setBoard_hit(board_hit);
        community.setBoard_like(board_like);
    }

    private void modifyAlertDialog() {
        // Dialog 생성 객체
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("글 수정"); // 제목
        builder.setMessage("수정하시겠습니까?");
        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*[함수사용] 글 수정 데이터 처리*/
                modify();
            }
        }); // 확인 버튼 추가 및 이벤트 정의
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create(); // 설정한 정보로 알림창 생성
        alertDialog.show(); // 알림창을 화면에 표시
    }

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
    private void showVideoDialog(){
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

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
        switch (requestCode) {
            case 100: // 카메라 앱
                if (resultCode == getActivity().RESULT_OK) {
                    Intent photoIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePhotoPath));
                    activity.sendBroadcast(photoIntent);
                    commModify_ImageUpload.setImageURI(null);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = PhotoHelper.getInstance().getThumb(activity, filePhotoPath);
                    commModify_ImageUpload.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(bitmap)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                            .placeholder(R.drawable.icon_loco_s)
                            .into(commModify_ImageUpload);
                }
                break;

            case 101: // 갤러리 앱
                if (resultCode == getActivity().RESULT_OK) {
                    // 선택한 파일 경로 얻기
                    filePhotoPath = FileUtils.getPath(getContext(), data.getData());
                    commModify_ImageUpload.setImageURI(null);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = PhotoHelper.getInstance().getThumb(getActivity(), filePhotoPath);
                    commModify_ImageUpload.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(filePhotoPath)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .placeholder(R.drawable.icon_loco_s)
                            .into(commModify_ImageUpload);
                }
                break;
            case 103: // 비디오 앱
                if (resultCode == getActivity().RESULT_OK) {
                    // 촬영 결과를 갤러리(MediaStore)에 등록
                    Intent videoIntent =
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.parse("file://" + fileVideoPath));
                    activity.sendBroadcast(videoIntent);
                    commModify_VideoUpload.setVideoURI(null);
                    file = new File(fileVideoPath);
                    Log.d("[TEST", "fileVideo" + fileVideoPath);
                    commModify_VideoUpload.setVideoURI(Uri.fromFile(file));
                    commModify_VideoUpload.setVisibility(View.VISIBLE);
                    commModify_VideoUpload.start();
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    commModify_VideoUpload.setVisibility(View.GONE);
                }
                break;
            case 104: // 비디오 앱
                if (resultCode == getActivity().RESULT_OK) {
                    // 선택한 파일 경로 얻기
                    fileVideoPath = FileUtils.getPath(getActivity(), data.getData());
                    commModify_VideoUpload.setVideoURI(null);
                    file = new File(fileVideoPath);
                    Log.d("[TEST", "fileVideo" + fileVideoPath);
                    commModify_VideoUpload.setVideoURI(Uri.fromFile(file));
                    commModify_VideoUpload.setVisibility(View.VISIBLE);
                    commModify_VideoUpload.start();
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    commModify_VideoUpload.setVisibility(View.GONE);
                }
                break;
        }
    }

    class HttpResponseModify extends AsyncHttpResponseHandler {

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

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");

                if (rt.equals("OK") && total > 0) {
                    activity.setFragment(111);
                    activity.setUpBtnBack(true, activity.COMMLIST);
                    backToDetail();
                } else {
                    Toast.makeText(activity, "수정 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    }

    private void backToDetail() {
        CommDetailFragment commDetailFragment = new CommDetailFragment();
        if (community != null) {
            Bundle bundle = new Bundle();
            int backmodify = getArguments().getInt("back"); //2020-04-01 청일
            bundle.putSerializable("backmodify", backmodify);    //2020-04-01 청일
            bundle.putSerializable("item", community);
            commDetailFragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, commDetailFragment).addToBackStack(null).commit();
        }
    }

    TextView commModify_text1, commModify_text2, commModify_text3, commModify_text4, commModify_text5, commModify_text6, commModify_text7, commModify_text8;

    /**카테고리 선택한 값만 색깔 켜지게 하기**/
    private void setCategoryColor(LinearLayout category, TextView text) {
        commModify_text1.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text2.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text3.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text4.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text5.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text6.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text7.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_text8.setTextColor(getResources().getColorStateList(R.color.colorTextDefault));
        commModify_button1.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button2.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button3.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button4.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button5.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button6.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button7.setBackgroundResource(R.color.colorBtnDefault);
        commModify_button8.setBackgroundResource(R.color.colorBtnDefault);
        category.setBackgroundResource(R.color.colorNavy);
        text.setTextColor(getResources().getColorStateList(R.color.colorWhite));
    }
}