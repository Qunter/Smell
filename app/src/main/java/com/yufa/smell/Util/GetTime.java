package com.yufa.smell.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/13.
 */

public class GetTime {

    public String getNow(){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String time = dateFormater.format(date);
        return time;
    }

    public String getTime(int index){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy MM dd HH mm ss");
        Date date=new Date();
        String time = dateFormater.format(date);
        String[] data = time.split(" ");
        int [] idata = new int[data.length];
        for (int i = 0; i <data.length; i++) {
            idata[i] = Integer.valueOf(data[i]);
            System.out.println(idata[i]);
        }
        idata[3] = idata[3] + index;
        if (idata[3]>=24){
            System.out.println(idata[3]);
            idata[3] = idata[3]%24;
            idata[2] = idata[2] + 1;
            if (idata[2]>=getDay(idata[1])){
                System.out.println(idata[3]);
                idata[2] = idata[2]%getDay(idata[1]);
                idata[1] += 1;
                if (idata[1]>=12){
                    System.out.println(idata[3]);
                    idata[1] = idata[1]%12;
                    idata[0]+=1;
                }
            }
        }
        String day = "";
        if (idata[2]<10){
            day = "0" + idata[2];
        }else {
            day = idata[2]+"";
        }
        String month = "";
        if (idata[1]<10){
            month = "0" + idata[1];
        }else {
            month = idata[1]+"";
        }
        String s = idata[0]+"-"+month + "-" + day+" " + idata[3] + ":" + idata[4]+":" + idata[5];
        return s;
    }
    private int getDay(int month){
        int day = 0;
        switch (month){
            case 1:{
                day = 31;
                break;
            }
            case 2:{
                day = 31;
                break;
            }
            case 3:{
                day = 31;
                break;
            }
            case 4:{
                day = 31;
                break;
            }
            case 5:{
                day = 31;
                break;
            }
            case 6:{
                day = 31;
                break;
            }
            case 7:{
                day = 31;
                break;
            }
            case 8:{
                day = 31;
                break;
            }
            case 9:{
                day = 31;
                break;
            }
            case 10:{
                day = 31;
                break;
            }
            case 11:{
                day = 31;
                break;
            }
            case 12:{
                day = 31;
                break;
            }
        }
        return day;
    }
}
