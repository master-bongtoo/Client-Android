package com.example.bongtoo.model;

import java.io.Serializable;

public class FAQ implements Serializable {
    private String faqSubject;
    private String faqContent;
    private int faqSeq;

    public FAQ() {
    }

    public FAQ(String faqSubject, String faqContent, int faqSeq) {
        this.faqSubject = faqSubject;
        this.faqContent = faqContent;
        this.faqSeq = faqSeq;
    }

    public String getFaqSubject() {
        return faqSubject;
    }

    public void setFaqSubject(String faqSubject) {
        this.faqSubject = faqSubject;
    }

    public String getFaqContent() {
        return faqContent;
    }

    public void setFaqContent(String faqContent) {
        this.faqContent = faqContent;
    }

    public int getFaqSeq() {
        return faqSeq;
    }

    public void setFaqSeq(int faqSeq) {
        this.faqSeq = faqSeq;
    }
}
