package com.example.bongtoo.model;

import java.io.Serializable;

public class Alimi implements Serializable {
    private int alimi_index;
    private String alimi_place;
    private String alimi_content;
    private String alimi_date;
    private String alimi_tag;
    private String alimi_writerID;
    private String alimi_who;
    private int alimi_type;

    public int getAlimi_index() {
        return alimi_index;
    }

    public void setAlimi_index(int alimi_index) {
        this.alimi_index = alimi_index;
    }

    public String getAlimi_place() {
        return alimi_place;
    }

    public void setAlimi_place(String alimi_place) {
        this.alimi_place = alimi_place;
    }

    public String getAlimi_content() {
        return alimi_content;
    }

    public void setAlimi_content(String alimi_content) {
        this.alimi_content = alimi_content;
    }

    public String getAlimi_date() {
        return alimi_date;
    }

    public void setAlimi_date(String alimi_date) {
        this.alimi_date = alimi_date;
    }

    public String getAlimi_tag() {
        return alimi_tag;
    }

    public void setAlimi_tag(String alimi_tag) {
        this.alimi_tag = alimi_tag;
    }

    public String getAlimi_writerID() {
        return alimi_writerID;
    }

    public void setAlimi_writerID(String alimi_writerID) {
        this.alimi_writerID = alimi_writerID;
    }

    public String getAlimi_who() {
        return alimi_who;
    }

    public void setAlimi_who(String alimi_who) {
        this.alimi_who = alimi_who;
    }

    public int getAlimi_type() {
        return alimi_type;
    }

    public void setAlimi_type(int alimi_type) {
        this.alimi_type = alimi_type;
    }

    public Alimi(int alimi_index, String alimi_place, String alimi_content, String alimi_date, String alimi_tag, String alimi_writerID, String alimi_who, int alimi_type) {
        this.alimi_index = alimi_index;
        this.alimi_place = alimi_place;
        this.alimi_content = alimi_content;
        this.alimi_date = alimi_date;
        this.alimi_tag = alimi_tag;
        this.alimi_writerID = alimi_writerID;
        this.alimi_who = alimi_who;
        this.alimi_type = alimi_type;
    }

    public Alimi() {
    }
}
