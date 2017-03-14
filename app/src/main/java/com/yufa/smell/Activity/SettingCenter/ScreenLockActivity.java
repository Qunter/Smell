package com.yufa.smell.Activity.SettingCenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.Map.MapActivity;
import com.yufa.smell.CustomView.PasswordView;
import com.yufa.smell.R;
import com.yufa.smell.Util.SharedPreferencesHelper;


/**
 * Created by Administrator on 2016/12/6.
 */

public class ScreenLockActivity extends Activity {

    private PasswordView mPwdView;//九宫格界面
    private String password = "";
    private TextView hintTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawpaswset);
        hintTv = (TextView) findViewById(R.id.drawPaswHintTv);
        mPwdView = (PasswordView) this.findViewById(R.id.mPassWordView);
        Intent it = getIntent();
        int index = it.getIntExtra("setOrValidate",2);
        if(index!=1){
            initSetListener();
        }else if (index==1){
            SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(getApplicationContext());
            password = sph.getString("drawpasw","");
            //判断是否未设置密码
            if(password==""){
                Toast.makeText(getApplicationContext(),R.string.pasw_null, Toast.LENGTH_SHORT).show();
                ScreenLockActivity.this.finish();
            }else{
                initVerifyListener();
            }
        }

    }
    /**
     * 设置界面的监听事件
     * 监听是否画完图像
     */
    private void initSetListener(){
        mPwdView.setOnCompleteListener(new PasswordView.OnCompleteListener() {
            @Override
            public void onComplete(String mPassword) {
                if(password.equals("")){
                    password = mPassword;
                    mPwdView.clearPassword();
                    hintTv.setText(R.string.pasw_hint_again);
                }else{
                    if(password.equals(mPassword)){
                        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(getApplicationContext());
                        sph.putString("drawpasw",password);
                        Toast.makeText(getApplicationContext(),"密码设置成功", Toast.LENGTH_SHORT).show();
                        ScreenLockActivity.this.finish();
                    }else{
                        password = "";
                        mPwdView.clearPassword();
                        hintTv.setText(R.string.pasw_hint_error);
                    }
                }
            }
        });
    }
    /**
     * 验证界面的监听事件
     * 监听是否画完图像
     */
    private void initVerifyListener(){
        mPwdView.setOnCompleteListener(new PasswordView.OnCompleteListener() {
            @Override
            public void onComplete(String mPassword) {
                if(password.equals(mPassword)){
                    Toast.makeText(getApplicationContext(),R.string.pasw_verify_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(ScreenLockActivity.this, MapActivity.class);
                    startActivity(intent);
                    ScreenLockActivity.this.finish();
                }else{
                    mPwdView.clearPassword();
                    hintTv.setText(R.string.pasw_verify_error);
                }
            }
        });
    }

}
