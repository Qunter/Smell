package com.yufa.smell.Activity.ChatCenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.ImageTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.rong.methods.User;

/**
 * Created by Administrator on 2017/3/16.
 */

public class UserInfoActivity extends BaseActivity {


    @BindView(R.id.userinfo_image)
    TextView userinfoImage;
    @BindView(R.id.userinfo_nickname)
    EditText userinfoNickName;
    @BindView(R.id.userinfo_permsg)
    EditText userinfoPerMsg;
    @BindView(R.id.userinfo_age)
    EditText userinfoAge;
    @BindView(R.id.userinfo_profession)
    EditText userinfoProfession;
    @BindView(R.id.userinfo_save)
    Button userinfoSave;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ImageTool imageTool;
    private BmobFile image=null;
    private String imgUrl,userNickName;
    private String newToken="";
    private final int GETIMGURL=0x01,GETRONGTOKEN=0x02,BMOBUPDATEALL=0x03;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GETIMGURL:
                    saveUserImg();
                    break;
                case GETRONGTOKEN:
                    new getNewRongToken(UserInformation.getCurrentUser(UserInformation.class).getPhone(),userNickName,imgUrl).start();
                    break;
                case BMOBUPDATEALL:
                    bmobUpdateALL();
                    break;

            }
        }
    };
    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("个人设置");
        imageTool = new ImageTool(this,"UserImage");
    }

    @Override
    public void isShowToolBar() {

    }

    @OnClick({R.id.userinfo_image, R.id.userinfo_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userinfo_image:
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
                break;
            //逻辑为1头像昵称无修改2头像修改昵称无修改3头像无修改昵称修改4头像昵称皆修改
            //1直接保存
            //2联网获取url本地获取昵称
            //3本地获取url控件获取昵称
            //4联网获取url控件获取昵称
            case R.id.userinfo_save:
                if (userinfoNickName.getText().toString().equals("")){
                    if(image==null)
                        handler.sendEmptyMessage(BMOBUPDATEALL);
                    else{
                        userNickName = UserInformation.getCurrentUser(UserInformation.class).getNickName();
                        handler.sendEmptyMessage(GETIMGURL);
                    }
                }
                else{
                    userNickName = userinfoNickName.getText().toString();
                    if(image==null){
                        imgUrl = UserInformation.getCurrentUser(UserInformation.class).getImage();
                        handler.sendEmptyMessage(GETRONGTOKEN);
                    }
                    else
                        handler.sendEmptyMessage(GETIMGURL);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 2:
                //获取图片后裁剪图片
                startActivityForResult(imageTool.clipperBigPic(this, data.getData()), 1);
                break;
            case 1:
                //获取图片后保存图片到本地，是否需要保存看情况而定
                image = imageTool.saveBitmap(data);
                break;
        }
    }

    /**
     * 上传头像至bmob，获取该文件url
     */
    private void saveUserImg(){
        image.uploadblock(new UploadFileListener() {
            @Override
            public void onProgress(Integer arg0) {

            }
            @Override
            public void done(BmobException e) {
                imgUrl = image.getUrl();
                handler.sendEmptyMessage(GETRONGTOKEN);
                Log.i("上传头像文件结束，url为", image.getUrl() );
            }

        });
    }
    /**
     * 上传新资料至融云获取新token
     */
    class getNewRongToken extends Thread{
        String userId;//定义线程内变量
        String nickName;
        String imgUrl="";
        User rongAppInformation = new User("z3v5yqkbzc1i0", "rAZ2RNIWtWCNYq");//融云APP信息
        String rongToken = "";
        public getNewRongToken(String userId, String nickName,String imgUrl){//定义带参数的构造函数,达到初始化线程内变量的值
            this.userId=userId;
            this.nickName=nickName;
            this.imgUrl=imgUrl;
        }
        @Override
        public void run() {
            try {
                rongToken=rongAppInformation.getToken(userId,nickName,imgUrl).getToken();
                newToken=rongToken ;
                handler.sendEmptyMessage(BMOBUPDATEALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 将所有修改保存至bmob
     */
    private void bmobUpdateALL(){
        UserInformation newUser = new UserInformation();
        newUser.setImage(imgUrl);
        if (newToken.equals("")){
        }else{
            newUser.setToken(newToken);
        }
        if(userinfoPerMsg.getText()!=null){
            newUser.setNickName(userinfoPerMsg.getText().toString());
        }else if(userinfoAge.getText()!=null){
            newUser.setNickName(userinfoAge.getText().toString());
        }else if(userinfoProfession.getText()!=null){
            newUser.setNickName(userinfoProfession.getText().toString());
        }
        UserInformation loginUser = UserInformation.getCurrentUser(UserInformation.class);
        newUser.update(loginUser.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("bmob储存修改失败", e+"" );
                }
            }
        });
    }

}
