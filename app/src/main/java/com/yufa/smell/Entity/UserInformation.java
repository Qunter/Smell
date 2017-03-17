package com.yufa.smell.Entity;

import cn.bmob.v3.BmobUser;

/**
 * Created by luyufa on 2016/11/14.
 * 用户
 */

public class UserInformation extends BmobUser {

    //private String account;         //账号
    //private String username;
    //private String password;        //密码
    private String phone;           //手机号码
    private String nickName;        //昵称
    private double latitude;        //经度
    private double longitude;       //纬度
    private String token;           //融云token
    private String image;           //头像链接
    private String permsg="";       //个性签名
    private Boolean frozen;       //是否冻结
    private String age="";
    private String profession="";

    public UserInformation() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPermsg() {
        return permsg;
    }

    public void setPermsg(String permsg) {
        this.permsg = permsg;
    }

    public Boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
