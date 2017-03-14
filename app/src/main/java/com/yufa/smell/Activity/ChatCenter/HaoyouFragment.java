package com.yufa.smell.Activity.ChatCenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yufa.smell.Activity.ChatCenter.RongTt.Friend;
import com.yufa.smell.Activity.ChatCenter.RongTt.HomeActivity;
import com.yufa.smell.Adapter.FriendAdapter;
import com.yufa.smell.Entity.UserFriend;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import static cn.bmob.v3.BmobConstants.TAG;

public class HaoyouFragment extends Fragment implements RongIM.UserInfoProvider{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;


    View chatView;
    ListView friendLv;
    List<UserInformation> userFriendInformationList;
    private String loginUserFriendObjectID;
    private List<Friend> userIdList;
    private final int FRIENDINFORMATIONLISTDOWNOVER=0x05,GETLOGINUSERFRIENDLIST=0x06;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==FRIENDINFORMATIONLISTDOWNOVER){
                initFriendListViewAdapter();
                initUserInfo();
            }else if (msg.what==GETLOGINUSERFRIENDLIST){
                getLoginUserFriendList();
            }

        }
    };

    public static HaoyouFragment instance = null;//单例模式

    public static HaoyouFragment getInstance() {
        if (instance == null) {
            instance = new HaoyouFragment();
        }
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        chatView = inflater.inflate(R.layout.fragment_haoyou, container,false);
        //initUserInfo();
        //获取登录用户token以及初始化融云服务，已转移至mapactivity，此处仅供备份
        //initUserToken();
        friendLv = (ListView) chatView.findViewById(R.id.friendLv);
        getLoginUserFriendObjectID();
        //initFriendListViewAdapter();
        return chatView;


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    private void initUserInfo() {
        userIdList = new ArrayList<Friend>();
        for(int i=0;i<userFriendInformationList.size();i++){
            userIdList.add(new Friend(userFriendInformationList.get(i).getPhone(),userFriendInformationList.get(i).getNickName(),userFriendInformationList.get(i).getImage()));
        }
        /*
        userIdList.add(new Friend("10010", "联通", "http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png"));//联通图标
        userIdList.add(new Friend("10086", "移动", "http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png"));//移动图标
        userIdList.add(new Friend("KEFU","官方客服","http://img02.tooopen.com/Download/2010/5/22/20100522103223994012.jpg"));
        */
        //Token:vCYACJZW6N+6n/bWxTKJa7U/IPKPq/4/rzu3rTMXUSRr+45pWdMmjRNZqGa9SzdWUlX6awVGkOS9UH4AMaCELA==
        RongIM.setUserInfoProvider(this, true);
    }

    @Override
    public UserInfo getUserInfo(String s) {

        for (Friend i : userIdList) {
            if (i.getUserId().equals(s)) {
                //Log.e(TAG, i.getPortraitUri());
                return new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
            }
        }
        return null;
    }


    /**
     * 获取用户好友列表的数据库表单中ID
     */
    private void getLoginUserFriendObjectID(){
        final BmobQuery<UserFriend> userObjectIDQuery = new BmobQuery<UserFriend>();
        userObjectIDQuery.addWhereEqualTo("userID", BmobUser.getCurrentUser(UserInformation.class).getPhone());
        userObjectIDQuery.findObjects(new FindListener<UserFriend>() {
            @Override
            public void done(List<UserFriend> object, BmobException e) {
                if(e==null){
                    loginUserFriendObjectID =object.get(0).getObjectId();
                    //Toast.makeText(getContext(), "loginUserFriendObjectID"+loginUserFriendObjectID, Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(GETLOGINUSERFRIENDLIST);
                }else{
                }
            }
        });
        /*
        // 查询好友列表内的所有用户，因此查询的是用户表
        BmobQuery<UserInformation> userFriendQuery = new BmobQuery<UserInformation>();
        userFriend.setObjectId(loginUserFriendObjectID);
        //userFriend是UserFriend表中的字段，用来存储所有该用户的好友关系的用户
        userFriendQuery.addWhereRelatedTo("userFriend", new BmobPointer(userFriend));
        userFriendQuery.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object,BmobException e) {
                if(e==null){
                    userFriendInformationList=object;
                    Toast.makeText(getContext(), "成功加载好友列表数据"+userFriendInformationList.size(), Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(FRIENDINFORMATIONLISTDOWNOVER);
                }else{
                    Toast.makeText(getContext(), e+"失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
        */

    }
    /**
     *获取用户好友列表数据
     */
    private void getLoginUserFriendList(){
        UserFriend userFriend = new UserFriend();
        // 查询好友列表内的所有用户，因此查询的是用户表
        BmobQuery<UserInformation> userFriendQuery = new BmobQuery<UserInformation>();
        userFriend.setObjectId(loginUserFriendObjectID);
        //userFriend是UserFriend表中的字段，用来存储所有该用户的好友关系的用户
        userFriendQuery.addWhereRelatedTo("userFriend", new BmobPointer(userFriend));
        userFriendQuery.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object,BmobException e) {
                if(e==null){
                    userFriendInformationList=object;
                    //Toast.makeText(getContext(), "成功加载好友列表数据"+userFriendInformationList.size(), Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(FRIENDINFORMATIONLISTDOWNOVER);
                }else{
                    Toast.makeText(getContext(), "加载好友列表数据失败"+e, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    /**
     * 好友列表控件adapter初始化
     */
    private void initFriendListViewAdapter(){
        FriendAdapter friendAdapter = new FriendAdapter(getActivity(), userFriendInformationList, friendLv);
        friendLv.setAdapter(friendAdapter);
    }

}
