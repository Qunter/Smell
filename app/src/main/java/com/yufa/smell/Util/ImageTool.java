package com.yufa.smell.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/2/8.
 *图片处理类，获取相册图片，裁剪，保存
 */

public class ImageTool {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobFile saveBitmap(Intent data) {
        BmobFile image;
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            File file = new File(Environment.getExternalStorageDirectory(),name);
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            image = new BmobFile(file);
            return image;
        }
        return null;
    }

    public BmobFile saveBitmapFile(Intent data){
        BmobFile image;
        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        File file=new File(Environment.getExternalStorageDirectory(),name);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        image = new BmobFile(file);
        return image;
    }

    public Intent getImageFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return intent;
    }

    public Intent getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }
    public Intent clipperBigPic(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = PhotoClipperUtil.getPath(context, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }
        //发送裁剪命令
        intent.putExtra("crop", true);
        //X方向上的比例
        intent.putExtra("aspectX", 1);
        //Y方向上的比例
        intent.putExtra("aspectY", 1);
        //裁剪区的宽
        intent.putExtra("outputX", 124);
        //裁剪区的高
        intent.putExtra("outputY", 124);
        //是否保留比例
        intent.putExtra("scale", true);
        //返回数据
        intent.putExtra("return-data", true);
        //输出图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        //裁剪图片保存位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        //startActivityForResult(intent, 1);
        return intent;
    }

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    /**
     * 临时图片保存路径
     * @return
     */
    private File getTempFile() {
        File file = new File(Environment.getExternalStorageDirectory(), name);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public ImageTool() {
    }

    public ImageTool(Context context, String type){
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context);
        String account = sph.getString("account", "");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String time = sDateFormat.format(curDate);
        name = account + "-" + type + "-" + time + ".png";
    }
}
