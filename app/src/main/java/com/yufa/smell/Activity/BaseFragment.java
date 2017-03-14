package com.yufa.smell.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.yufa.smell.R;

/**
 * Created by Administrator on 2017/1/26.
 */

public abstract class BaseFragment extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //isShowToolBar();
        isTransition(false);
        initVariables();
        initViews();
        loadData();
    }

    //public abstract void isShowToolBar();    //是否显示ToolBar，默认显示


    public void isTransition(Boolean isShow){           //是否显示切换动画效果，默认不显示
        if (isShow){
            transition();
        }
    }

    protected void transition(){                        //显示动画切换代码
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.LEFT));
            getWindow().setExitTransition(new Slide(Gravity.RIGHT));
        }
    }

    public void initVariables(){
        //接收页面之间的数据传递
    }

    public void initViews(){
        //初始化控件
    }

    public void loadData(){
        //访问API数据
    }
    @SuppressLint("NewApi")
    protected void startActivity(Class classes){            //通用切换Activity方法,带切换动画
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), classes);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    /*
    public void hideActionBar() {              //隐藏ActionBar
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
    }
    public void showActionBar(){               //显示ActionBar

    }
    @SuppressLint("NewApi")
    public void showActionBar(Toolbar toolbar){
        toolbar.setBackground(getResources().getDrawable(R.drawable.top));
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        this.setSupportActionBar(toolbar);
    }
    */
}
