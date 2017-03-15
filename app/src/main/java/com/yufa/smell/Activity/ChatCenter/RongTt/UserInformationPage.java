package com.yufa.smell.Activity.ChatCenter.RongTt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.ChatCenter.UserSearch;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/3/13.
 */

public class UserInformationPage extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.userImg)
    CircleView userImg;
    @BindView(R.id.userNameTv)
    TextView userNameTv;
    @BindView(R.id.userExclusiveTv)
    TextView userExclusiveTv;
    String userNickName;
    UserInformation friendInformation;
    final int SEARCHUSERINFORMATIONOVER=0x20,SEARCHUSERINFORMATIONSTART=0x21;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCHUSERINFORMATIONSTART:
                    searchUserInformation();
                    break;
                case SEARCHUSERINFORMATIONOVER:
                    loadingUserInformation();
                    break;
            }
        }
    };
    @Override
    public void isShowToolBar() {
    }
    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_userinforpage);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("好友信息");
        handler.sendEmptyMessage(SEARCHUSERINFORMATIONSTART);
    }
    /**
     * 加载用户信息
     */
    private void loadingUserInformation(){
        new DownImage(userImg, friendInformation.getImage()).execute(friendInformation.getImage());
        userNameTv.setText(friendInformation.getNickName());
        if(friendInformation.getExclusive().equals("")){
            userExclusiveTv.setText("该用户暂未设置个性签名");
        }else{
            userExclusiveTv.setText(friendInformation.getExclusive());
        }

    }
    /**
     * bmob查询某用户信息
     */
    private void searchUserInformation() {
        Intent intent=getIntent();
        userNickName=intent.getStringExtra("userNickName");
        final BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("nickName", userNickName);
        query.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object, BmobException e) {
                if (e == null) {
                    friendInformation = object.get(0);
                    handler.sendEmptyMessage(SEARCHUSERINFORMATIONOVER);
                    //Toast.makeText(getApplicationContext(), user.getNickName()+"查询成功", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "查询失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 加载网络图片（好友头像）
     */
    class DownImage extends AsyncTask<String, Void, Bitmap> {

        private CircleView imageView;

        public DownImage(CircleView imageView, String url) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = null;
            try {
                //加载一个网络图片
                InputStream is = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
