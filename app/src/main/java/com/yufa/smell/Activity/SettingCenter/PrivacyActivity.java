package com.yufa.smell.Activity.SettingCenter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Entity.Privacy;
import com.yufa.smell.R;
import com.yufa.smell.Util.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/12/21.
 * 隐私页面
 */

public class PrivacyActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.privacy_addFriend)
    Switch privacyAddFriend;
    @BindView(R.id.privacy_searchMe)
    Switch privacySearchMe;
    @BindView(R.id.privacy_recommend)
    Switch privacyRecommend;
    @BindView(R.id.privacy_noSee)
    TextView privacyNoSee;
    @BindView(R.id.seeNo)
    TextView seeNo;
    @BindView(R.id.privacy_stranger)
    Switch privacyStranger;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    SharedPreferencesHelper sph;
    Privacy privacy;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_privacy);
        ButterKnife.bind(this);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
        showActionBar(toolbar);
        headerTextview.setText("隐私");
        privacyAddFriend.setOnCheckedChangeListener(this);
        privacySearchMe.setOnCheckedChangeListener(this);
        privacyRecommend.setOnCheckedChangeListener(this);
        privacyStranger.setOnCheckedChangeListener(this);
        sph = SharedPreferencesHelper.getInstance(this);
        privacy = new Privacy(this);
        init();
    }

    private void init(){
        Boolean addFriend = sph.getBoolean("AddFriend",false);
        Boolean searchMe = sph.getBoolean("SearchMe",false);
        Boolean recommendFriend = sph.getBoolean("RecommendFriend",false);
        Boolean stranger = sph.getBoolean("Stranger",false);
        privacyAddFriend.setChecked(addFriend);
        privacySearchMe.setChecked(searchMe);
        privacyRecommend.setChecked(recommendFriend);
        privacyStranger.setChecked(stranger);
    }

    @OnClick({R.id.privacy_noSee, R.id.seeNo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.privacy_noSee:
                break;
            case R.id.seeNo:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.privacy_addFriend: {
                if (isChecked) {
                    privacy.setAddFriend(true);
                    save("AddFriend",true);
                } else {
                    privacy.setAddFriend(false);
                    save("AddFriend",false);
                }
                break;
            }
            case R.id.privacy_searchMe: {
                if (isChecked) {
                    privacy.setSearchMe(true);
                    save("SearchMe",true);
                } else {
                    privacy.setSearchMe(false);
                    save("SearchMe",false);
                }
                break;
            }
            case R.id.privacy_recommend: {
                if (isChecked) {
                    privacy.setRecommendFriend(true);
                    save("RecommendFriend",true);
                } else {
                    privacy.setRecommendFriend(false);
                    save("RecommendFriend",false);
                }
                break;
            }
            case R.id.privacy_stranger: {
                if (isChecked) {
                    privacy.setStranger(true);
                    save("Stranger",true);
                } else {
                    privacy.setStranger(false);
                    save("Stranger",false);
                }
                break;
            }
        }
    }

    private void save(String key,Boolean isCheck){
        sph.putBoolean(key,isCheck);
        doit(privacy);
    }

    private void doit(Privacy privacy){
        privacy.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    Log.d("privacyActivity","success");
                }
            }
        });
    }

    @Override
    public void isShowToolBar() {
    }

}
