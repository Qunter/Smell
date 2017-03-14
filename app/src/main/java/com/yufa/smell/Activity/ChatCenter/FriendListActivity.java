package com.yufa.smell.Activity.ChatCenter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/12/21.
 */

public class FriendListActivity extends BaseActivity {
    @BindView(R.id.friend_image)
    CircleView friendImage;
    @BindView(R.id.user_nickname)
    TextView userNickname;
    @BindView(R.id.user_exclusive)
    TextView userExclusive;
    @BindView(R.id.friend_add)
    ImageButton friendAdd;
    @BindView(R.id.friendList_persion)
    LinearLayout friendListPersion;
    @Override
    public void isShowToolBar() {
    }

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_friendlist);
        ButterKnife.bind(this);
        userNickname.setText(BmobUser.getCurrentUser(UserInformation.class).getNickName());
        if (BmobUser.getCurrentUser(UserInformation.class).getExclusive().equals(""))
            userExclusive.setText("用户暂未设置个性签名");
        else
            userExclusive.setText(BmobUser.getCurrentUser(UserInformation.class).getExclusive());
    }

    @OnClick({R.id.friend_add, R.id.friendList_persion})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friend_add:
                startActivity(UserSearch.class);
                break;
            case R.id.friendList_persion:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
