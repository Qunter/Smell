package com.yufa.smell.Entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/2/2.
 */

public class Complaint extends BmobObject {

    private String question;
    private String phone;

    private String url;

    public Complaint() {
    }

    public Complaint(String question, String phone) {
        this.question = question;
        this.phone = phone;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
