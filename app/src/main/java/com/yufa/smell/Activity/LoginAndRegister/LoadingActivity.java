package com.yufa.smell.Activity.LoginAndRegister;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.Map.MapActivity;
import com.yufa.smell.Activity.SettingCenter.ScreenLockActivity;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.SharedPreferencesHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by luyufa on 2016/11/14.
 * 加载页面
 */

public class LoadingActivity extends BaseActivity {

    @BindView(R.id.loading_login)
    Button loadingLogin;
    @BindView(R.id.loading_register)
    Button loadingRegister;
    @BindView(R.id.loading_group)
    LinearLayout loadingGroup;
    @BindView(R.id.loading_up)
    TextView loadingUp;
    @BindView(R.id.same)
    ImageView same;
    private GestureDetector gestureDetector;
    private Boolean isShow = false;
    private long exitTime = 0;

    private AnimationDrawable animationDrawable;


    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        Bmob.initialize(this, "f0fc59a153ba369c31798409902688bd");
        gestureDetector = new GestureDetector(LoadingActivity.this, onGestureListener);
        loadingGroup.setVisibility(View.GONE);
        animationDrawable = (AnimationDrawable) loadingUp.getBackground();
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.loading_login, R.id.loading_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loading_login:
                isLogins();
                break;
            case R.id.loading_register:
                startActivity(RegisterActivity.class);
                break;
        }
    }

    @SuppressLint("NewApi")
    private void isLogins() {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(this);
        final String isLock = sph.getString("drawpasw", null);
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            if (isLock != null) {
                Intent intent = new Intent();
                intent.putExtra("setOrValidate", 1);
                intent.setClass(LoadingActivity.this, ScreenLockActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoadingActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(LoadingActivity.this, LoginActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoadingActivity.this, same, "same").toBundle());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent begin, MotionEvent end, float velocityX, float velocityY) {

            if (begin.getX() < 1900) {
                float x = end.getX() - begin.getX();
                float y = end.getY() - begin.getY();

                if (x > 0) {
                    isShow = true;
                    showLogin();
                } else if (x < 0) {
                    isShow = false;
                    hideLogin();
                }
                return true;
            }
            return false;
        }
    };

    private void showLogin() {
        loadingUp.setVisibility(View.GONE);
        loadingGroup.setVisibility(View.VISIBLE);
    }

    private void hideLogin() {
        loadingUp.setVisibility(View.VISIBLE);
        loadingGroup.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isShow) {
                isShow = false;
                hideLogin();
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    getApplication().onTerminate();
                }
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            animationDrawable.start();
    }

}
