package com.example.bongtoo.model;

import java.io.Serializable;

public class Notice implements Serializable {
    private String noticeSubject;
    private String noticeContent;
    private String noticeDate;
    private int noticeSeq;
    private int total;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getNoticeSubject() {
        return noticeSubject;
    }

    public void setNoticeSubject(String noticeSubject) {
        this.noticeSubject = noticeSubject;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    public int getNoticeSeq() {
        return noticeSeq;
    }

    public void setNoticeSeq(int noticeSeq) {
        this.noticeSeq = noticeSeq;
    }

    public Notice(String noticeSubject, String noticeContent, String noticeDate, int noticeSeq) {
        this.noticeSubject = noticeSubject;
        this.noticeContent = noticeContent;
        this.noticeDate = noticeDate;
        this.noticeSeq = noticeSeq;
    }

    public Notice() {
    }
}
