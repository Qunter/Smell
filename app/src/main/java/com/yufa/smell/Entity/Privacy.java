package com.yufa.smell.Entity;

import android.content.Context;

import com.yufa.smell.Util.SharedPreferencesHelper;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/2/2.
 */

public class Privacy extends BmobObject{

    private Boolean addFriend;
    private Boolean searchMe;
    private Boolean recommendFriend;
    private Boolean stranger;
    private String account;

    public Privacy(Context context) {
        addFriend = false;
        searchMe = false;
        recommendFriend = false;
        stranger = false;
        account = getAccount(context);
    }

    private String getAccount(Context context){
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        return sharedPreferencesHelper.getString("account","no account");
    }

    public Boolean getAddFriend() {
        return addFriend;
    }

    public void setAddFriend(Boolean addFriend) {
        this.addFriend = addFriend;
    }

    public Boolean getSearchMe() {
        return searchMe;
    }

    public void setSearchMe(Boolean searchMe) {
        this.searchMe = searchMe;
    }

    public Boolean getRecommendFriend() {
        return recommendFriend;
    }

    public void setRecommendFriend(Boolean recommendFriend) {
        this.recommendFriend = recommendFriend;
    }

    public Boolean getStranger() {
        return stranger;
    }

    public void setStranger(Boolean stranger) {
        this.stranger = stranger;
    }
}
