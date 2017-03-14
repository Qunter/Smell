package com.yufa.smell.Activity.LoginAndRegister;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.Map.MapActivity;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.ShowTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/12/22.
 * 重置密码
 */

public class ResetActivity extends BaseActivity {

    @BindView(R.id.reset_passowrd)
    EditText resetPassowrd;
    @BindView(R.id.reset_submit)
    EditText resetSubmit;
    @BindView(R.id.reset_sure)
    Button resetSure;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_textview)
    TextView headerTextview;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_reset);
        ButterKnife.bind(this);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
        showActionBar(toolbar);
        headerTextview.setText("重置密码");
    }

    @Override
    public void isShowToolBar() {

    }

    @OnClick(R.id.reset_sure)
    public void onClick() {
        if (resetPassowrd!=null&&resetSubmit!=null){
            String one = resetPassowrd.getText().toString();
            String two = resetSubmit.getText().toString();
            if (one.equals(two)){
                UserInformation userInformation = new UserInformation();
                userInformation.setPassword("one");
                userInformation.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null)  showDialog();
                    }
                });
            }else {
                ShowTool showTool = new ShowTool();
                showTool.showToast(ResetActivity.this,"两次输入不一致，请重新输入");
            }
        }

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_simple, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.simple_image);
        TextView textView = (TextView) view.findViewById(R.id.simple_text);
        imageView.setImageResource(R.drawable.repassword);
        textView.setText("密码重置成功");
        builder.setPositiveButton("现在进入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ResetActivity.this, "现在进入", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(ResetActivity.this, MapActivity.class);
                startActivity(intent);
                ResetActivity.this.finish();
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(ResetActivity.this,RePasswordActivity.class);
            startActivity(intent);
            ResetActivity.this.finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
