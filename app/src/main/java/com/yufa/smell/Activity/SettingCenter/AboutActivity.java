package com.yufa.smell.Activity.SettingCenter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/21.
 * 关于Smell
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("关于我们");
    }

    @Override
    public void isShowToolBar() {
    }
}
