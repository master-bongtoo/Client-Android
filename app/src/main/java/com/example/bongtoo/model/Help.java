package com.example.bongtoo.model;

public class Help {
    private String help_subject;
    private String help_content;

    public Help() {
    }

    public Help(String help_subject, String help_content) {
        this.help_subject = help_subject;
        this.help_content = help_content;
    }

    public String getHelp_subject() {
        return help_subject;
    }

    public void setHelp_subject(String help_subject) {
        this.help_subject = help_subject;
    }

    public String getHelp_content() {
        return help_content;
    }

    public void setHelp_content(String help_content) {
        this.help_content = help_content;
    }
}