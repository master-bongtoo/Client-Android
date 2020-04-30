package com.example.bongtoo.model;

import java.io.Serializable;

public class MyBongToo implements Serializable {

    private int num;
    private int member_num;
    private String name;
    private String imageURL;
    private String group;
    private int groupIndex;
    private int attendance;
    private String inviteWay;
    private String place;
    private String date;
    private int money;
    private String memo;
    private int totalAll;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public String getInviteWay() {
        return inviteWay;
    }

    public void setInviteWay(String inviteWay) {
        this.inviteWay = inviteWay;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getTotalAll() {
        return totalAll;
    }

    public void setTotalAll(int totalAll) {
        this.totalAll = totalAll;
    }

    public MyBongToo() {
    }

    public MyBongToo(int num, int member_num, String name, String imageURL, String group, int groupIndex, int attendance, String inviteWay, String place, String date, int money, String memo, int totalAll) {
        this.num = num;
        this.member_num = member_num;
        this.name = name;
        this.imageURL = imageURL;
        this.group = group;
        this.groupIndex = groupIndex;
        this.attendance = attendance;
        this.inviteWay = inviteWay;
        this.place = place;
        this.date = date;
        this.money = money;
        this.memo = memo;
        this.totalAll = totalAll;
    }
}