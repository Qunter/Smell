package com.yufa.smell.Activity.SettingCenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.ChatCenter.ViewPaperActivity;
import com.yufa.smell.Activity.LoginAndRegister.LoginActivity;
import com.yufa.smell.Activity.Map.MapActivity;
import com.yufa.smell.Adapter.Adapter;
import com.yufa.smell.Adapter.ViewHolder;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import io.rong.imkit.RongIM;

/**
 * Created by luyufa on 2016/12/4.
 * 设置页面
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<String> list;
    Context context;

    /**
     * CertificationActivity  实名认证
     * ScreenLockActivity 屏幕锁
     * RePasswordActivity 找回密码
     * ComplaintActivity 投诉页面
     * PrivacyActivity 隐私页面
     * HelpReportActivity 帮助与反馈页面
     * AboutActivity 关于Smell页面
     * DeviceActivity 设备页面
     */

    Class[] toNew = new Class[]{PrivacyActivity.class, DeviceActivity.class, HelpReportActivity.class,
            AboutActivity.class, AccentMangerActivity.class, CertificationActivity.class,
            ScreenLockActivity.class, ComplaintActivity.class};

    String[] items = new String[]{"隐私", "通用", "帮助与反馈", "关于Smell", "账号管理", "实名认证", "设备锁", "投诉"};
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.setting_exit)
    Button settingExit;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("设置");
        context = SettingActivity.this;
        list = new ArrayList<String>();
        Collections.addAll(list, items);
        Adapter<String> adapter = new Adapter<String>(context, list, R.layout.item_setting) {
            @Override
            public void convert(final ViewHolder holder, String s) {
                TextView text = (TextView) holder.getView(R.id.securityCenter);
                text.setText(s);
                holder.setOnClickListener(R.id.securityCenter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, toNew[holder.getPosition()]);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void isShowToolBar() {
    }

    @OnClick(R.id.setting_exit)
    public void onClick() {
        RongIM.getInstance().logout();//断开融云登录用户连接
        UserInformation.logOut();   //清除bmob登录用户缓存
        //BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了，应是用于测试currentUser是否为null

        /*
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(SettingActivity.this);
        sharedPreferencesHelper.putString("account","");
        sharedPreferencesHelper.putString("password","");
        sharedPreferencesHelper.getString("drawpasw","");
        */
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, LoginActivity.class);
        intent.putExtra("isBack",false);
        startActivity(intent);
        this.finish();
    }
}
