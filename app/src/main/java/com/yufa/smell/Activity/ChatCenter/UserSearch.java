package com.yufa.smell.Activity.ChatCenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.Entity.UserFriend;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/3/2.
 */

public class UserSearch extends BaseActivity {
    @BindView(R.id.seachEt)
    EditText seachEt;
    @BindView(R.id.seachBtn)
    Button seachBtn;
    LinearLayout userSearchLinLayout;
    final int ADDFRIENDASK = 0x00, DELFRIENDASK = 0x01, IFGETFRIENDLIST = 0x02, NOGETFRIENDLIST = 0x03 ,SAVEUSERFRIENDLIST=0x04,GETFRIENDFRIENDOBJECT=0x05,SAVEFRIENDFRIENDLIST=0x06;
    String txUrl, name;
    LinearLayout friendView;
    UserInformation searchUser;
    String loginUserFriendObjectID = "",loginFriendFriendObjectID="";
    //用于标记是否已显示用户信息
    int flag = 0;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IFGETFRIENDLIST:
                    searchUserFriendInformation();
                    break;
                case NOGETFRIENDLIST:
                    //Toast.makeText(getApplicationContext(), "执行NOGETFRIENDLIST", Toast.LENGTH_SHORT).show();
                    buildUserFriendList();
                    break;
                case ADDFRIENDASK:
                    if (flag == 1) {
                        userSearchLinLayout.removeView(friendView);
                        flag = 0;
                    }
                    userSearchLinLayout.addView(addFriendView(txUrl, name));
                    flag = 1;
                    break;
                case DELFRIENDASK:
                    userSearchLinLayout.removeView(friendView);
                    break;
                case SAVEUSERFRIENDLIST:
                    saveUserFriendList();
                    break;
                case GETFRIENDFRIENDOBJECT:
                    searchFriendFriendInformation();
                    break;
                case SAVEFRIENDFRIENDLIST:
                    saveFriendFriendList();
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
        setContentView(R.layout.acticity_usersearch);
        userSearchLinLayout = (LinearLayout) findViewById(R.id.userSearchLinLayout);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("添加好友");
        handler.sendEmptyMessage(IFGETFRIENDLIST);
    }

    @OnClick({R.id.seachBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.seachBtn:
                if(seachEt.getText().toString().equals(BmobUser.getCurrentUser(UserInformation.class).getPhone())){
                    Toast.makeText(getApplicationContext(), "您不能添加自己为好友", Toast.LENGTH_SHORT).show();
                }else{
                    searchUserInformation(seachEt.getText().toString());
                }
                break;
        }
    }

    /**
     * bmob查询某用户信息
     */
    private void searchUserInformation(String userNameOrPhone) {
        final BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("phone", userNameOrPhone);
        query.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object, BmobException e) {
                if (e == null) {
                    searchUser = object.get(0);
                    txUrl = searchUser.getImage();
                    name = searchUser.getNickName();
                    handler.sendEmptyMessage(ADDFRIENDASK);
                    //Toast.makeText(getApplicationContext(), user.getNickName()+"查询成功", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "查询失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * bmob查询登录用户好友信息
     */
    private void searchUserFriendInformation() {
        final BmobQuery<UserFriend> query = new BmobQuery<UserFriend>();
        query.addWhereEqualTo("userID", BmobUser.getCurrentUser(UserInformation.class).getPhone());
        query.findObjects(new FindListener<UserFriend>() {
            @Override
            public void done(List<UserFriend> object, BmobException e) {
                if (e == null) {
                    loginUserFriendObjectID = object.get(0).getObjectId();
                } else {
                    handler.sendEmptyMessage(NOGETFRIENDLIST);
                }
            }
        });
    }
    /**
     * bmob查询想添加为好友的用户的好友信息
     */
    private void searchFriendFriendInformation() {
        final BmobQuery<UserFriend> query = new BmobQuery<UserFriend>();
        query.addWhereEqualTo("userID", searchUser.getPhone());
        query.findObjects(new FindListener<UserFriend>() {
            @Override
            public void done(List<UserFriend> object, BmobException e) {
                if (e == null) {
                    loginFriendFriendObjectID = object.get(0).getObjectId();
                    handler.sendEmptyMessage(SAVEFRIENDFRIENDLIST);
                } else {

                }
            }
        });
    }

    /**
     * 如果用户此前没有生成好友列表的情况下，执行方法生成好友列表
     */
    private void buildUserFriendList() {
        UserInformation loginUser = BmobUser.getCurrentUser(UserInformation.class);
        String loginUserID = loginUser.getPhone();
        UserFriend userFriend = new UserFriend();
        userFriend.setUserID(loginUserID);
        BmobRelation relation = new BmobRelation();
        userFriend.setUserFriend(relation);
        userFriend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    //Toast.makeText(getApplicationContext(), "执行", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(IFGETFRIENDLIST);
                } else {
                    //Toast.makeText(getApplicationContext(), e+"错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 储存登录用户好友关系
     */
    private void saveUserFriendList(){
        UserFriend userFriend = new UserFriend();
        userFriend.setObjectId(loginUserFriendObjectID);
        BmobRelation relation = new BmobRelation();
        //将当前用户添加到多对多关联中
        relation.add(searchUser);
        //多对多关联指向`post`的`likes`字段
        userFriend.setUserFriend(relation);
        userFriend.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //Toast.makeText(getApplicationContext(), "更新登录用户好友列表成功", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(GETFRIENDFRIENDOBJECT);
                } else {
                    //Toast.makeText(getApplicationContext(), e+"添加失败", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), loginUserFriendObjectID, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    /**
     * 储存好友用户好友关系
     */
    private void saveFriendFriendList(){
        UserFriend userFriend = new UserFriend();
        userFriend.setObjectId(loginFriendFriendObjectID);
        BmobRelation relation = new BmobRelation();
        //将当前用户添加到多对多关联中
        relation.add(BmobUser.getCurrentUser(UserInformation.class));
        //多对多关联指向`post`的`likes`字段
        userFriend.setUserFriend(relation);
        userFriend.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "成功添加好友", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), e+"添加失败", Toast.LENGTH_SHORT).show();
                    Log.e("错误",e+"" );
                    Toast.makeText(getApplicationContext(), e+"", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    /**
     * 动态生成是否添加好友（显示用户信息）的控件
     */
    private LinearLayout addFriendView(String txUrl, String name) {
        friendView = new LinearLayout(this);
        friendView.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        friendView.setLayoutParams(llparams);
        CircleView touxiang = new CircleView(this);
        DownImage di = (DownImage) new DownImage(touxiang, txUrl).execute(txUrl);
        LinearLayout.LayoutParams txparams = new LinearLayout.LayoutParams(100, 100);
        txparams.setMargins(30, 15, 15, 15);
        touxiang.setLayoutParams(txparams);
        LinearLayout.LayoutParams unparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        unparams.setMargins(30, 0, 30, 0);
        TextView userName = new TextView(this);
        userName.setText(name);
        userName.setLayoutParams(unparams);
        userName.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.setMargins(30, 0, 30, 0);
        Button add = new Button(this);
        add.setText("添加好友");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(SAVEUSERFRIENDLIST);
            }
        });
        add.setLayoutParams(btnparams);
        add.setGravity(Gravity.CENTER_VERTICAL);
        Button noAdd = new Button(this);
        noAdd.setText("取消");
        noAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(DELFRIENDASK);
            }
        });
        noAdd.setLayoutParams(btnparams);
        noAdd.setGravity(Gravity.CENTER_VERTICAL);
        friendView.addView(touxiang);
        friendView.addView(userName);
        friendView.addView(add);
        friendView.addView(noAdd);
        return friendView;
    }

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
