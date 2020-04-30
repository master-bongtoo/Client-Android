package com.example.bongtoo.model;

public class Question {
    private int question_index;
    private String question_type;
    private String subject;
    private String content;
    private String question_origin_img;
    private String question_uuid_img;

    public int getQuestion_index() {
        return question_index;
    }

    public void setQuestion_index(int question_index) {
        this.question_index = question_index;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuestion_origin_img() {
        return question_origin_img;
    }

    public void setQuestion_origin_img(String question_origin_img) {
        this.question_origin_img = question_origin_img;
    }

    public String getQuestion_uuid_img() {
        return question_uuid_img;
    }

    public void setQuestion_uuid_img(String question_uuid_img) {
        this.question_uuid_img = question_uuid_img;
    }

    public Question(int question_index, String question_type, String subject, String content, String question_origin_img, String question_uuid_img) {
        this.question_index = question_index;
        this.question_type = question_type;
        this.subject = subject;
        this.content = content;
        this.question_origin_img = question_origin_img;
        this.question_uuid_img = question_uuid_img;
    }

    public Question() {
    }
}
