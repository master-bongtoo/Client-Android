package com.example.bongtoo.model;

import java.io.Serializable;

public class Member implements Serializable {
    private int member_num;
    private String member_id;
    private String member_phone;
    private String member_origin_img;
    private String member_img_path;
    private String member_pw;
    private String name;
    private String nickname;
    private String email;
    private int money_give;
    private int money_take;
    private String first_logtime;
    private String edit_logtime;
    private int grade;

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getMember_origin_img() {
        return member_origin_img;
    }

    public void setMember_origin_img(String member_origin_img) {
        this.member_origin_img = member_origin_img;
    }

    public String getMember_img_path() {
        return member_img_path;
    }

    public void setMember_img_path(String member_img_path) {
        this.member_img_path = member_img_path;
    }

    public String getMember_pw() {
        return member_pw;
    }

    public void setMember_pw(String member_pw) {
        this.member_pw = member_pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMoney_give() {
        return money_give;
    }

    public void setMoney_give(int money_give) {
        this.money_give = money_give;
    }

    public int getMoney_take() {
        return money_take;
    }

    public void setMoney_take(int money_take) {
        this.money_take = money_take;
    }

    public String getFirst_logtime() {
        return first_logtime;
    }

    public void setFirst_logtime(String first_logtime) {
        this.first_logtime = first_logtime;
    }

    public String getEdit_logtime() {
        return edit_logtime;
    }

    public void setEdit_logtime(String edit_logtime) {
        this.edit_logtime = edit_logtime;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Member(int member_num, String member_id, String member_phone, String member_origin_img, String member_img_path, String member_pw, String name, String nickname, String email, int money_give, int money_take, String first_logtime, String edit_logtime, int grade) {
        this.member_num = member_num;
        this.member_id = member_id;
        this.member_phone = member_phone;
        this.member_origin_img = member_origin_img;
        this.member_img_path = member_img_path;
        this.member_pw = member_pw;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.money_give = money_give;
        this.money_take = money_take;
        this.first_logtime = first_logtime;
        this.edit_logtime = edit_logtime;
        this.grade = grade;
    }

    public Member() {
    }
}
