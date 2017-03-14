package com.yufa.smell.Activity.SettingCenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.LoginAndRegister.LoginActivity;
import com.yufa.smell.Adapter.Adapter;
import com.yufa.smell.Adapter.ViewHolder;
import com.yufa.smell.R;
import com.yufa.smell.Util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/22.
 * 账号管理
 */

public class AccentMangerActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.accentmanger_add)
    Button accentmangerAdd;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Context context;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_accentmanger);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("账号管理");
        context = AccentMangerActivity.this;
        List<String> list = new ArrayList<String>();
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(this);
        list.add(sph.getString("account","null"));
        Adapter<String> adapter = new Adapter<String>(context, list, R.layout.item_setting) {
            @Override
            public void convert(final ViewHolder holder, String s) {
                TextView text = (TextView) holder.getView(R.id.securityCenter);
                text.setText(s);
                holder.setOnClickListener(R.id.securityCenter, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @OnClick(R.id.accentmanger_add)
    public void onClick() {
        startActivity(LoginActivity.class);
    }

    @Override
    public void isShowToolBar() {
    }
}
