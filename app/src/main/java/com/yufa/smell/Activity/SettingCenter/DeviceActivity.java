package com.yufa.smell.Activity.SettingCenter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/25.
 * 通用（设备）
 */

public class DeviceActivity extends BaseActivity {

    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("设备");
    }

    @Override
    public void isShowToolBar() {
    }
}
