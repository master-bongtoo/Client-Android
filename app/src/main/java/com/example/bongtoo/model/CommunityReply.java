package com.example.bongtoo.model;

import java.io.Serializable;

public class CommunityReply implements Serializable {
    private int member_num;
    private int board_num;
    private int reply_num;
    private String reply_description;
    private int reply_like;
    private int grad;
    private String nickname;

    public CommunityReply() {
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

    public int getReply_num() {
        return reply_num;
    }

    public void setReply_num(int reply_num) {
        this.reply_num = reply_num;
    }

    public String getReply_description() {
        return reply_description;
    }

    public void setReply_description(String reply_description) {
        this.reply_description = reply_description;
    }

    public int getReply_like() {
        return reply_like;
    }

    public void setReply_like(int reply_like) {
        this.reply_like = reply_like;
    }

    public int getGrad() {
        return grad;
    }

    public void setGrad(int grad) {
        this.grad = grad;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}