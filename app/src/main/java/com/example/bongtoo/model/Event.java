package com.example.bongtoo.model;

import java.io.Serializable;

public class Event implements Serializable {
    private int event_index ;
    private int isnew;
    private String event_subject1;
    private String event_subject2;
    private String event_content;
    private String event_date;
    private String event_origin_img;
    private String event_uuid_img;
    private String event_img_path;
    private int event_type;

    public int getEvent_index() {
        return event_index;
    }

    public void setEvent_index(int event_index) {
        this.event_index = event_index;
    }

    public int getIsnew() {
        return isnew;
    }

    public void setIsnew(int isnew) {
        this.isnew = isnew;
    }

    public String getEvent_subject1() {
        return event_subject1;
    }

    public void setEvent_subject1(String event_subject1) {
        this.event_subject1 = event_subject1;
    }

    public String getEvent_subject2() {
        return event_subject2;
    }

    public void setEvent_subject2(String event_subject2) {
        this.event_subject2 = event_subject2;
    }

    public String getEvent_content() {
        return event_content;
    }

    public void setEvent_content(String event_content) {
        this.event_content = event_content;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_origin_img() {
        return event_origin_img;
    }

    public void setEvent_origin_img(String event_origin_img) {
        this.event_origin_img = event_origin_img;
    }

    public String getEvent_uuid_img() {
        return event_uuid_img;
    }

    public void setEvent_uuid_img(String event_uuid_img) {
        this.event_uuid_img = event_uuid_img;
    }

    public String getEvent_img_path() {
        return event_img_path;
    }

    public void setEvent_img_path(String event_img_path) {
        this.event_img_path = event_img_path;
    }

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
    }

    public Event() {
    }

    public Event(int event_index, int isnew, String event_subject1, String event_subject2, String event_content, String event_date, String event_origin_img, String event_uuid_img, String event_img_path, int event_type) {
        this.event_index = event_index;
        this.isnew = isnew;
        this.event_subject1 = event_subject1;
        this.event_subject2 = event_subject2;
        this.event_content = event_content;
        this.event_date = event_date;
        this.event_origin_img = event_origin_img;
        this.event_uuid_img = event_uuid_img;
        this.event_img_path = event_img_path;
        this.event_type = event_type;
    }
}
