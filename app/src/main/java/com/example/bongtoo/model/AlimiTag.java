package com.example.bongtoo.model;

import java.io.Serializable;

public class AlimiTag implements Serializable {
    private int tagcount;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;

    public AlimiTag() {
    }

    public AlimiTag(int tagcount, String tag1, String tag2, String tag3, String tag4, String tag5) {
        this.tagcount = tagcount;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.tag4 = tag4;
        this.tag5 = tag5;
    }

    public int getTagcount() {
        return tagcount;
    }

    public void setTagcount(int tagcount) {
        this.tagcount = tagcount;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getTag4() {
        return tag4;
    }

    public void setTag4(String tag4) {
        this.tag4 = tag4;
    }

    public String getTag5() {
        return tag5;
    }

    public void setTag5(String tag5) {
        this.tag5 = tag5;
    }
}
