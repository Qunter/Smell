package com.yufa.smell.Entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/3/5.
 */

public class UserDis extends BmobObject {
    private String userID;
    private BmobRelation userDis;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public BmobRelation getUserDis() {
        return userDis;
    }

    public void setUserDis(BmobRelation userDis) {
        this.userDis = userDis;
    }
}
