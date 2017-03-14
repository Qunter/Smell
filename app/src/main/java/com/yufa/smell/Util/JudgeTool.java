package com.yufa.smell.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luyufa on 2016/9/3.
 */
public class JudgeTool {

    public boolean isPhoneNumber(String str){
        String regx = "0?(13|14|15|18)[0-9]{9}";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            if(matcher.group().equals(str)){
                return true;
            }
        }
        return false;
    }
    public Boolean isEmail(String email){
        String regx = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(email);
        while(matcher.find()){
            if(matcher.group().equals(email)){
                return true;
            }
        }
        return false;
    }
    public String isAccount(String str){
        if (str.length()<4){
            return "您输入的账号长度太短";
        }
        else if (str.length()>20){
            return "您输入的账号长度太长";
        }else {
            return null;
        }
    }
}
