package com.yufa.smell.Activity.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;
import com.yufa.smell.Util.JudgeTool;
import com.yufa.smell.Util.ShowTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/12/5.
 * 找回密码
 */

public class RePasswordActivity extends BaseActivity {
    @BindView(R.id.repassword_phone)
    TextInputLayout repasswordPhone;
    @BindView(R.id.repassword_registerCode)
    EditText repasswordRegisterCode;
    @BindView(R.id.repassword_send)
    Button repasswordSend;
    @BindView(R.id.repassword_sumit)
    Button repasswordSumit;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_repassword);
        SMSSDK.initSDK(this,"1b42f46c9d964","1a6eeeb1cb284b1618af192c58ba1ce6");
        ButterKnife.bind(this);
        SMSSDK.registerEventHandler(eh); //注册短信回调
        showActionBar(toolbar);
        headerTextview.setText("身份验证");
        repasswordPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!new JudgeTool().isPhoneNumber(s.toString())){
                    repasswordPhone.setError("您输入的手机号码格式不正确");
           }else {
                    repasswordPhone.setError("");
                }
            }
        });
    }

    @Override
    public void isShowToolBar() {

    }

    @Override
    public void isTransition(Boolean isShow) {
        super.isTransition(false);
    }

    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Log.d("RePassword","提交验证码成功");
                    toNew();
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Log.d("RePassword","获取验证码成功");
                }
            }else{
                ((Throwable)data).printStackTrace();
            }
        }
    };

    @OnClick({R.id.repassword_send, R.id.repassword_sumit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repassword_send:
                String phone = repasswordPhone.getEditText().getText().toString();
                if (phone!=null&&!phone.equals("")){
                    doSendMessage(phone);
                }
                break;
            case R.id.repassword_sumit:
                String phon = repasswordPhone.getEditText().getText().toString();
                String str = repasswordRegisterCode.getText().toString();
                if (phon!=null&&str!=null&&!phon.equals("")&&!str.equals("")){
                    submit(phon,str);
                }
                break;
        }
    }

    private void doSendMessage(String phone) {
        SMSSDK.getVerificationCode("86", phone);
    }

    private void submit(String phone,String str) {
        SMSSDK.submitVerificationCode("86", phone, str);
    }

    private void toNew(){
        Intent intent = new Intent();
        intent.setClass(RePasswordActivity.this, ResetActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(RePasswordActivity.this,LoginActivity.class);
            startActivity(intent);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
