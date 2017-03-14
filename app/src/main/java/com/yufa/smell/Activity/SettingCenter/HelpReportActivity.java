package com.yufa.smell.Activity.SettingCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/21.
 * 帮助与反馈页面
 */

public class HelpReportActivity extends BaseActivity {


    @BindView(R.id.help_search)
    SearchView helpSearch;
    @BindView(R.id.help_login)
    Button helpLogin;
    @BindView(R.id.help_repassword)
    Button helpRepassword;
    @BindView(R.id.help_friendgroup)
    Button helpFriendgroup;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_helpandreport);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("帮助与反馈");
    }

    @OnClick({R.id.help_login, R.id.help_repassword, R.id.help_friendgroup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.help_login:
                tonew();
                break;
            case R.id.help_repassword:
                tonew();
                break;
            case R.id.help_friendgroup:
                tonew();
                break;
        }
    }

    @Override
    public void isShowToolBar() {
    }
    private void tonew(){
        Intent intent = new Intent();
        intent.setClass(HelpReportActivity.this,InfoActivity.class);
        startActivity(intent);
    }

}
