package com.yufa.smell.Activity.SettingCenter;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by luyufa on 2016/12/14.
 * 实名认证
 */

public class CertificationActivity extends BaseActivity {


    @BindView(R.id.certification_IDcard)
    Button certificationIDcard;
    @BindView(R.id.certification_sure)
    Button certificationSure;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.simple_image)
    ImageView simpleImage;
    @BindView(R.id.simple_text)
    TextView simpleText;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_certification);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("实名认证");
        simpleImage.setImageResource(R.drawable.top);
        simpleText.setText("完成省份认证，可核对真实身份保障合法权益");
    }

    @OnClick({R.id.certification_IDcard, R.id.certification_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.certification_IDcard:
                break;
            case R.id.certification_sure:
                break;
        }
    }

    @Override
    public void isShowToolBar() {
    }
}
