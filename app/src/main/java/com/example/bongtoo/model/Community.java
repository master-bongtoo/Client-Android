package com.example.bongtoo.model;

import java.io.Serializable;

public class Community implements Serializable {
    private int member_num;
    private int board_num;
    private int grade;
    private String nickname;
    private String board_title;
    private String board_description;
    private String board_firstdate;
    private String board_editdate;
    private String board_origin_img;
    private String board_img_path;
    private String board_origin_video;
    private String board_video_path;
    private String board_category;
    private int board_hit;
    private int board_like;
    private int totalAll;
    private boolean isnew;


    public Community() {
    }

    public Community(int member_num, int board_num, int grade, String nickname, String board_title, String board_description, String board_firstdate, String board_editdate, String board_origin_img, String board_img_path, String board_origin_video, String board_video_path, String board_category, int board_hit, int board_like, int totalAll) {
        this.member_num = member_num;
        this.board_num = board_num;
        this.grade = grade;
        this.nickname = nickname;
        this.board_title = board_title;
        this.board_description = board_description;
        this.board_firstdate = board_firstdate;
        this.board_editdate = board_editdate;
        this.board_origin_img = board_origin_img;
        this.board_img_path = board_img_path;
        this.board_origin_video = board_origin_video;
        this.board_video_path = board_video_path;
        this.board_category = board_category;
        this.board_hit = board_hit;
        this.board_like = board_like;
        this.totalAll = totalAll;
    }

    public boolean isIsnew() {
        return isnew;
    }

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public int getBoard_num() {
        return board_num;
    }

    public void setBoard_num(int board_num) {
        this.board_num = board_num;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBoard_title() {
        return board_title;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public String getBoard_description() {
        return board_description;
    }

    public void setBoard_description(String board_description) {
        this.board_description = board_description;
    }

    public String getBoard_firstdate() {
        return board_firstdate;
    }

    public void setBoard_firstdate(String board_firstdate) {
        this.board_firstdate = board_firstdate;
    }

    public String getBoard_editdate() {
        return board_editdate;
    }

    public void setBoard_editdate(String board_editdate) {
        this.board_editdate = board_editdate;
    }

    public String getBoard_origin_img() {
        return board_origin_img;
    }

    public void setBoard_origin_img(String board_origin_img) {
        this.board_origin_img = board_origin_img;
    }

    public String getBoard_img_path() {
        return board_img_path;
    }

    public void setBoard_img_path(String board_img_path) {
        this.board_img_path = board_img_path;
    }

    public String getBoard_origin_video() {
        return board_origin_video;
    }

    public void setBoard_origin_video(String board_origin_video) {
        this.board_origin_video = board_origin_video;
    }

    public String getBoard_video_path() {
        return board_video_path;
    }

    public void setBoard_video_path(String board_video_path) {
        this.board_video_path = board_video_path;
    }

    public String getBoard_category() {
        return board_category;
    }

    public void setBoard_category(String board_category) {
        this.board_category = board_category;
    }

    public int getBoard_hit() {
        return board_hit;
    }

    public void setBoard_hit(int board_hit) {
        this.board_hit = board_hit;
    }

    public int getBoard_like() {
        return board_like;
    }

    public void setBoard_like(int board_like) {
        this.board_like = board_like;
    }

    public int getTotalAll() {
        return totalAll;
    }

    public void setTotalAll(int totalAll) {
        this.totalAll = totalAll;
    }
}