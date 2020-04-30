package com.example.bongtoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.bongtoo.adapter.CommunityDetailListAdapter;
import com.example.bongtoo.adapter.CommunityListAdapter;
import com.example.bongtoo.helper.RegexHelper;
import com.example.bongtoo.model.Community;
import com.example.bongtoo.model.CommunityReply;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class CommDetailFragment extends Fragment implements AbsListView.OnScrollListener{

    ListFragment listFragment;
    AsyncHttpClient client;
    DeleteResponse deleteResponse;
    ReplyResponse replyResponse;
    CommentResponse commentResponse;
    LikeResponse likeResponse;
    ReplyLikeResponse replyLikeResponse;
    //서버
    String deleteURL, viewURL, replyURL, writeURL,likeURL;
    //현재 글번호, 멤버정보
    //int member_num;
    int grade;
    int board_num;
    Community community;
    CommunityReply communityReply;


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
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comm_detail, container,false);
        deleteURL = "http://"+activity.SERVERIP+"/bongtoo_server/board/boardDeleteJson.a";
        viewURL= "http://"+activity.SERVERIP+"/bongtoo_server/board/boardViewJson.a";
        replyURL ="http://"+activity.SERVERIP+"/bongtoo_server/board/boardReplyListJson.a";
        writeURL = "http://" + activity.SERVERIP + "/bongtoo_server/board/boardReplyWriteJson.a";
        likeURL = "http://" + activity.SERVERIP + "/bongtoo_server/board/boardLikeJson.a";
        //화면 세팅
        setupCommunityDetail();
        //번들로 데이터 받아온 후 세팅
        getcommDetail();

        //디테일 리스트
        RequestParams params = new RequestParams();
        params.put("board_num",board_num);
        client.post(replyURL,params,replyResponse);

        return rootView;
    }
    //////////////////////////////////////////////////////////////////////////////////////
    // CommunityDetail 클릭 이벤트 정의
    //////////////////////////////////////////////////////////////////////////////////////
    /*CommunityDetail클릭 이벤트*/
    FrameLayout.OnClickListener commDetailEvent = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.commDetail_BtnCommentCommit:
                    if(activity.member_num==-1) {
                        Toast.makeText(activity, "로그인 후 이용하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        comment();
                    }
                    break;
                case R.id.commDetail_BtnDelete:
                    deleteAlertDialog();
                    break;
                case R.id.commDetail_BtnModify:
                    gotoModify();
                    break;
                case R.id.commDetail_Board_Like:
                    if(activity.islogin) {
                        RequestParams params = new RequestParams();
                        params.put("member_num", activity.member_num);
                        params.put("board_num", board_num);
                        client.post(likeURL, params ,likeResponse);
                    } else {
                        Toast.makeText(activity, "로그인 후 이용하세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void gotoModify() {
        CommModifyFragment commModifyFragment = new CommModifyFragment();
        if (community != null) {
            Bundle bundle = new Bundle();
            int back = getArguments().getInt("positionback");
            bundle.putSerializable("item", community);
            bundle.putInt("back",back);
            commModifyFragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, commModifyFragment).addToBackStack(null).commit();
        }
    }


    private void delete() {
        // 에러가 없으면, 서버에 전송
        RequestParams params = new RequestParams();
        params.put("member_num", activity.member_num);
        params.put("board_num", board_num);
        // multipart로 보내기 설정
        params.setForceMultipartEntityContentType(true);
        client.post(deleteURL, params, deleteResponse);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 삭제 시 다이얼로그
    //////////////////////////////////////////////////////////////////////////////////////
    private void deleteAlertDialog() {
        // Dialog 생성 객체
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("글삭제"); // 제목
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();
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

    /* 2020.04.01 박성용 */
    int PAGE = 1;
    int pageablePage = 0;
    boolean lastItemVisibleFlag = false;
    int totalAll = 0;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        pageablePage = totalAll / 10 + 1;
        RequestParams params = new RequestParams();
        if (scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if (PAGE < pageablePage) {
                PAGE++;
                params.put("pg", PAGE);
                params.put("board_num", board_num);
                client.post(replyURL, params, replyResponse);
            } else if (PAGE == pageablePage) {
                Toast.makeText(activity, "마지막 댓글입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }
    /* 2020.04.01 박성용 */

    class DeleteResponse extends AsyncHttpResponseHandler {
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

        // 통신 성공시 호출
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");

                if (rt.equals("OK") && total > 0) {
                    Toast.makeText(activity, "글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    activity.setFragment(11);
                } else {
                    Toast.makeText(activity, "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 통신 실패시 호출
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패 - 삭제", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_BOARD]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    /*댓글*/
    private void comment() {
        String reply_description = commDetail_eTxtCommentWrite.getText().toString().trim();
        RequestParams params =new RequestParams();
        params.put("member_num",activity.member_num);
        params.put("board_num",board_num);
        params.put("reply_description", reply_description);
        client.post(writeURL,params,commentResponse);
    }
    class CommentResponse extends AsyncHttpResponseHandler {
        Activity activity;

        public CommentResponse(Activity activity) {
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
                    Toast.makeText(activity, "댓글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    commDetail_eTxtCommentWrite.setText("");

                    refresh();
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
            //Log.d("[ERROR_REPLY]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
    private void refresh() {
        PAGE=1;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


    /*커뮤니티 - 상세보기*/
    FrameLayout commDetail_BtnCommentCommit, commDetail_BtnDelete, commDetail_BtnModify;
    TextView commDetail_TxtCategory, commDetail_TxtSubject, commDetail_TxtTime, commDetail_TxtNickName,
            commDetail_TxtHit, commDetail_TxtLike, commDetail_TxtComment, commDetail_TxtContent,
            commDetail_TxtHeart, commDetail_TxtCommentCount, commDetail_TxtCommentWriteByte,
            commdetailItem_Like_Text, commDetail_new;
    EditText commDetail_eTxtCommentWrite;
    ListView commDetail_List;
    ScrollView commDetail_ScrollView;
    ImageView commDetail_ImageIcon, commDetail_ImageUpload;
    VideoView commDetail_VideoUpload;
    CommunityDetailListAdapter communityDetailListAdapter;
    List<CommunityReply> communityReplyList;
    LinearLayout commDetail_Board_Like;
    int board_like;



    public void setupCommunityDetail() {
        //헤더 세팅
        activity.setHeaderTitle("커뮤니티");
        activity.setUpBtnBack(true, activity.MYPAGE_MYBOARD);
        int positionback = getArguments().getInt("positionback");
        if (positionback ==1){
            activity.setUpBtnBack(true, activity.COMMLIST);
        }else if (positionback ==2){
            activity.setUpBtnBack(true, activity.MYPAGE_MYBOARD);
        }else if (positionback==3){
            activity.setUpBtnBack(true, activity.HOTCOMMLIST);
        }

        int backmidify = getArguments().getInt("backmodify");  //2020-04-01 청일
        if (backmidify ==1){
            activity.setUpBtnBack(true, activity.COMMLIST); //커뮤니티리스트 //2020-04-01 청일
        }else if (backmidify ==2){
            activity.setUpBtnBack(true, activity.MYPAGE_MYBOARD); //마이페이지 내가쓴글 //2020-04-01 청일
        }else if (backmidify==3){
            activity.setUpBtnBack(true, activity.HOTCOMMLIST); //인기글 //2020-04-01 청일
        }

        // 화면 초기화
        commentResponse = new CommentResponse(getActivity());
        replyResponse =new ReplyResponse(getActivity());
        likeResponse =new LikeResponse(getActivity());
        replyLikeResponse = new ReplyLikeResponse(getActivity());
        communityReplyList = new ArrayList<>();
        commDetail_TxtCategory = rootView.findViewById(R.id.commDetail_TxtCategory);
        commDetail_TxtSubject = rootView.findViewById(R.id.commDetail_TxtSubject);
        commDetail_TxtTime = rootView.findViewById(R.id.commDetail_TxtTime);
        commDetail_TxtNickName = rootView.findViewById(R.id.commDetail_TxtNickName);
        commDetail_TxtHit = rootView.findViewById(R.id.commDetail_TxtHit);
        commDetail_TxtLike = rootView.findViewById(R.id.commDetail_TxtLike);
        commDetail_TxtComment = rootView.findViewById(R.id.commDetail_TxtComment);
        commDetail_TxtContent = rootView.findViewById(R.id.commDetail_TxtContent);
        commDetail_TxtHeart = rootView.findViewById(R.id.commDetail_TxtHeart);
        commDetail_TxtCommentCount = rootView.findViewById(R.id.commDetail_TxtCommentCount);
        commDetail_TxtCommentWriteByte = rootView.findViewById(R.id.commDetail_TxtCommentWriteByte);
        commDetail_ImageIcon = rootView.findViewById(R.id.commDetail_ImageIcon);
        commDetail_eTxtCommentWrite = rootView.findViewById(R.id.commDetail_eTxtCommentWrite);
        commDetail_Board_Like = rootView.findViewById(R.id.commDetail_Board_Like);
        commDetail_new = rootView.findViewById(R.id.commDetail_new);

        communityDetailListAdapter = new CommunityDetailListAdapter(getActivity(),R.layout.list_item_communitydetail,communityReplyList);
        commDetail_ImageUpload = rootView.findViewById(R.id.commDetail_ImageUpload);
        commDetail_VideoUpload = rootView.findViewById(R.id.commDetail_VideoUpload);

        client = new AsyncHttpClient();
        deleteResponse = new DeleteResponse();

        //이벤트 설정
        commDetail_BtnCommentCommit = rootView.findViewById(R.id.commDetail_BtnCommentCommit);
        commDetail_BtnDelete = rootView.findViewById(R.id.commDetail_BtnDelete);
        commDetail_BtnModify = rootView.findViewById(R.id.commDetail_BtnModify);
        commDetail_BtnCommentCommit.setOnClickListener(commDetailEvent);
        commDetail_BtnDelete.setOnClickListener(commDetailEvent);
        commDetail_BtnModify.setOnClickListener(commDetailEvent);
        commDetail_Board_Like.setOnClickListener(commDetailEvent);

        //글자수
        commDetail_eTxtCommentWrite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = commDetail_eTxtCommentWrite.getText().toString();
                commDetail_TxtCommentWriteByte.setText(input.length() + " / 200");
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 댓글 리스트뷰 세팅
        commDetail_List = rootView.findViewById(R.id.commDetail_List);
        commDetail_ScrollView = rootView.findViewById(R.id.commDetail_ScrollView);
        listFragment = new ListFragment();
        commDetail_ScrollView = rootView.findViewById(R.id.commDetail_ScrollView);
        commDetail_List.setAdapter(communityDetailListAdapter);
        commDetail_List.setOnScrollListener(this);
        commDetail_List.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                commDetail_ScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        communityDetailListAdapter.setOnItemClickListener(new CommunityDetailListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(activity.islogin) {
                    CommunityReply item = communityDetailListAdapter.getItem(position);
                    commdetailItem_Like_Text = view.findViewById(R.id.commdetailItem_Like_Text);
                    RequestParams params = new RequestParams();
                    params.put("member_num", activity.member_num);
                    params.put("board_num", board_num);
                    params.put("reply_num", item.getReply_num());
                    params.put("reply_member_num", item.getMember_num());
                    client.post(likeURL, params, replyLikeResponse);
                } else {
                    Toast.makeText(activity, "로그인 후 이용하세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //댓글리스트

    class ReplyResponse extends AsyncHttpResponseHandler {
        Activity activity;
        public ReplyResponse(Activity activity) {
            this.activity = activity;
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                totalAll = json.getInt("totalAll");
                commDetail_TxtComment.setText("댓글수 "+totalAll);
                commDetail_TxtCommentCount.setText("총 " + totalAll + " 개");
                if (rt.equals("OK") && total > 0) {
                    JSONArray item = json.getJSONArray("item");
                    for (int x = 0; x < item.length(); x++) {
                        JSONObject temp = item.getJSONObject(x);
                        communityReply = new CommunityReply();
                        communityReply.setMember_num(Integer.parseInt(temp.getString("member_num")));
                        communityReply.setBoard_num(Integer.parseInt(temp.getString("board_num")));
                        communityReply.setReply_description(temp.getString("reply_description"));
                        communityReply.setNickname(temp.getString("nickname"));
                        communityReply.setReply_num(Integer.parseInt(temp.getString("reply_num")));
                        communityReply.setReply_like(Integer.parseInt(temp.getString("reply_like")));
                        communityReply.setGrad(Integer.parseInt(temp.getString("grade")));
                        communityDetailListAdapter.add(communityReply);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 통신 실패
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패 - 리플", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_REPLY]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    private void getcommDetail() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            community = (Community) bundle.getSerializable("item");
            board_num = community.getBoard_num();
            grade = community.getGrade();
            if(!community.isIsnew()) {
                commDetail_new.setText("");
            }
            commDetail_TxtCategory.setText("["+community.getBoard_category()+"]");
            commDetail_TxtSubject.setText(community.getBoard_title());
            commDetail_TxtTime.setText(community.getBoard_firstdate() + "  |");
            commDetail_ImageIcon.setImageResource(R.drawable.icon_crownmain_silver);
            commDetail_TxtNickName.setText(community.getNickname() + "  |");
            commDetail_TxtHit.setText("조회수 " + (community.getBoard_hit()+1) + "  |");
            commDetail_TxtLike.setText("추천수 " + community.getBoard_like() + "  |");
            board_like = community.getBoard_like();
            commDetail_TxtHeart.setText(String.valueOf(board_like));
            commDetail_TxtContent.setText(""+community.getBoard_description());
            if (!community.getBoard_img_path().equals("")) {
                commDetail_ImageUpload.setVisibility(View.VISIBLE);
                Glide.with(activity).load(community.getBoard_img_path()).into(commDetail_ImageUpload);
            }

            /*글작성자가 아니면 수정/삭제버튼 GONE*/
            if(activity.member_num!=community.getMember_num()){
                commDetail_BtnModify.setVisibility(View.GONE);
                commDetail_BtnDelete.setVisibility(View.GONE);
            }
        }
    }

    class LikeResponse extends AsyncHttpResponseHandler {
        Activity activity;
        public LikeResponse(Activity activity) {
            this.activity = activity;
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                String board_like = json.getString("board_like");
                if (rt.equals("OK") && total > 0) {
                    Toast.makeText(activity, "추천하였습니다.", Toast.LENGTH_SHORT).show();
                    commDetail_TxtLike.setText("추천수 " + board_like + "  |");
                    commDetail_TxtHeart.setText(board_like);
                } else if(rt.equals("FAIL") && total > 0){
                    Toast.makeText(activity, "추천을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    commDetail_TxtLike.setText("추천수 " + board_like + "  |");
                    commDetail_TxtHeart.setText(board_like);
                } else {
                    Toast.makeText(activity, "오류 발생", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 통신 실패
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패 - 게시글 좋아요", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_BOARD_LIKE]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }

    class ReplyLikeResponse extends AsyncHttpResponseHandler {
        Activity activity;
        public ReplyLikeResponse(Activity activity) {
            this.activity = activity;
        }
        // 통신 성공
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String str = new String(bytes);
            try {
                JSONObject json = new JSONObject(str);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                String reply_like = json.getString("reply_like");
                if (rt.equals("OK") && total > 0) {
                    Toast.makeText(activity, "댓글을 추천하였습니다.", Toast.LENGTH_SHORT).show();
                    commdetailItem_Like_Text.setText(reply_like);
                } else if(rt.equals("FAIL") && total > 0){
                    Toast.makeText(activity, "댓글 추천을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    commdetailItem_Like_Text.setText(reply_like);
                } else {
                    Toast.makeText(activity, "오류 발생", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 통신 실패
        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Toast.makeText(activity, "통신 실패 - 댓글 좋아요", Toast.LENGTH_SHORT).show();
            //Log.d("[ERROR_REPLY_LIKE]", "ERROR CODE : "+i + ", ERROR DETAIL : " + throwable.getLocalizedMessage());
        }
    }
}