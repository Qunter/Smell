package com.yufa.smell.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by luyufa on 2016/9/7.
 *
 */
public class ShowTool {

    public void showToast(Context context,String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
    public  void showSnackbar(View view,String str){
        Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show();
    }
    public  void showDialog(Context context,String str){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(str);
    }

}
