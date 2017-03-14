package com.yufa.smell.Entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/2/23.
 */

public class UserFriend extends BmobObject {
    private String userID;
    private BmobRelation  userFriend;


    public BmobRelation  getUserFriend() {
        return userFriend;
    }

    public void setUserFriend(BmobRelation userFriend) {
        this.userFriend = userFriend;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
