package com.yufa.smell.Activity.LoginAndRegister;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.yufa.smell.Entity.UserDis;
import com.yufa.smell.Entity.UserFriend;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.JudgeTool;
import com.yufa.smell.Util.ShowTool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.rong.methods.User;

/**
 * Created by luyufa on 2016/11/14.
 * 注册
 */

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.register_custom)
    EditText registerCustom;
    @BindView(R.id.register_password)
    EditText registerPassword;
    @BindView(R.id.register_phone)
    TextInputLayout registerPhone;
    @BindView(R.id.register_nickname)
    EditText registerNickname;
    @BindView(R.id.register_enroll)
    Button registerEnroll;
    @BindView(R.id.register_login)
    Button registerLogin;
    final int BUILDUSERFRIENDLIST=0x00,BUILDUSERDISCUSSIONLIST=0x01,BUILDALLRIGHT=0x02,RSUCCESS=0x03,RFAIL=0x04,GETTOKEN=0x05;
    String Token="";
    String userID="";
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BUILDUSERFRIENDLIST:
                    buildUserFriendList();
                    break;
                case BUILDUSERDISCUSSIONLIST:
                    buildUserDiscussionList();
                    break;
                case BUILDALLRIGHT:
                    showDialog();
                    break;
                case RSUCCESS:
                    //Token=msg.obj.toString();
                    register();
                    break;
                case RFAIL:
                    Toast.makeText(RegisterActivity.this,"获取token失败",Toast.LENGTH_SHORT).show();
                    break;
                case GETTOKEN:
                    new getRongToken(registerPhone.getEditText().getText().toString(),registerNickname.getText().toString()).start();
                    break;
            }

        }
    };


    @Override
    public void initVariables() {
        super.initVariables();
    }

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
        registerPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (new JudgeTool().isPhoneNumber(s.toString())){
                    registerPhone.setError("");
                }else {
                    registerPhone.setError("您输入的手机号码格式不正确");
                }
            }
        });
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    @OnClick({R.id.register_enroll, R.id.register_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_enroll:
                if (new JudgeTool().isPhoneNumber(registerPhone.getEditText().getText().toString())){
                    new ShowTool().showToast(RegisterActivity.this,"执行");
                    isRegister();
                }else {
                    new ShowTool().showToast(RegisterActivity.this,"您输入的手机号码有误，请重新输入");
                }
                break;
            case R.id.register_login:
                startActivity(LoginActivity.class);
                break;
        }
    }
    private void isRegister(){
        String username = registerCustom.getText().toString().trim();
        BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("username",username);
        query.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> list, BmobException e) {
                if (e==null&&list.size()==0){
                    //handler.sendEmptyMessage(GETTOKEN);
                    new getRongToken(registerPhone.getEditText().getText().toString(),registerNickname.getText().toString()).start();
                }else {
                    new ShowTool().showToast(RegisterActivity.this,"您的账号已经被使用了");
                }
            }
        });
    }

    private void register(){
        UserInformation user = new UserInformation();
        user.setUsername(registerCustom.getText().toString());
        user.setNickName(registerNickname.getText().toString());
        user.setPhone(registerPhone.getEditText().getText().toString());
        userID = registerPhone.getEditText().getText().toString();
        user.setPassword(registerPassword.getText().toString());
        user.setImage("http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png");//未设置头像时默认用户头像
        user.setToken(Token);
        user.signUp(new SaveListener<UserInformation>() {
            @Override
            public void done(UserInformation s, BmobException e) {
                if(e==null){
                    handler.sendEmptyMessage(BUILDUSERFRIENDLIST);
                    //showDialog();
                    //Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    //finish();
                    //toast("注册成功:" +s.toString());
                }else{
                    Log.e("失败代码",e.toString());
                }

            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_simple,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.simple_image);
        TextView textView = (TextView)view.findViewById(R.id.simple_text);
        imageView.setImageResource(R.drawable.repassword);
        textView.setText("注册成功");
        builder.setPositiveButton("现在进入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData();
//                Intent intent = new Intent();
//                intent.setClass(RegisterActivity.this,MapActivity.class);
//                startActivity(intent);
//                RegisterActivity.this.finish();
                //Toast.makeText(RegisterActivity.this,"现在进入",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        builder.create().show();
    }

    private void saveData(){
        UserInformation loginUser = new UserInformation();
        loginUser.setUsername(registerCustom.getText().toString());
        loginUser.setPassword(registerPassword.getText().toString());
        //Toast.makeText(LoginActivity.this,username+"  "+password,Toast.LENGTH_SHORT).show();
        loginUser.login(new SaveListener<UserInformation>() {
            @Override
            public void done(UserInformation loginUser, BmobException e) {
                if(e==null){
                    Toast.makeText(RegisterActivity.this,"登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(MapActivity.class);
                    finish();
                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                }else{
                    //Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    //Log.e("失败代码",e.toString());
                    //loge(e);
                    showDialog();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(RegisterActivity.this,LoadingActivity.class);
            startActivity(intent);
            RegisterActivity.this.finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
    /**
     * 如果用户此前没有生成好友列表的情况下，执行方法生成好友列表
     */
    private void buildUserFriendList(){
        String loginUserID = userID;
        UserFriend userFriend = new UserFriend();
        userFriend.setUserID(loginUserID);
        BmobRelation relation = new BmobRelation();
        userFriend.setUserFriend(relation);
        userFriend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    //Toast.makeText(getApplicationContext(), "执行", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(BUILDUSERDISCUSSIONLIST);
                }else{
                    //Toast.makeText(getApplicationContext(), e+"错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 如果用户此前没有生成讨论组列表的情况下，执行方法生成讨论组列表
     */
    private void buildUserDiscussionList(){
        UserDis userDiscussion = new UserDis();
        BmobRelation bmobRelation = new BmobRelation();
        userDiscussion.setUserID(userID);
        userDiscussion.setUserDis(bmobRelation);
        userDiscussion.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    //Toast.makeText(getApplicationContext(), "创建", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(BUILDALLRIGHT);
                }else{
                    //Toast.makeText(getApplicationContext(), e+"错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    class getRongToken extends Thread{
        String userId;//定义线程内变量
        String name;
        User rongAppInformation = new User("z3v5yqkbzc1i0", "rAZ2RNIWtWCNYq");//融云APP信息
        String rongToken = "";
        public getRongToken(String userId, String name){//定义带参数的构造函数,达到初始化线程内变量的值
            this.userId=userId;
            this.name=name;
        }
        @Override
        public void run() {
            try {
                //使用默认头像，但函数需要三参
                rongToken=rongAppInformation.getToken(userId,name,"http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png").getToken();
                Token=rongToken ;//消息内容
                handler.sendEmptyMessage(RSUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(RFAIL);
            }
        }
    }
}
