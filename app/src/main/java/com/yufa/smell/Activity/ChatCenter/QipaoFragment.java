package com.yufa.smell.Activity.ChatCenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.yufa.smell.Adapter.DiscussionAdapter;
import com.yufa.smell.Entity.DiscussionIfm;
import com.yufa.smell.Entity.UserDis;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class QipaoFragment extends Fragment {

    View chatView;
    ListView qipaoLv;
    List<DiscussionIfm> userDiscussionInformationList;
    private String loginUserDiscussionObjectID;
    private List<UserDis> userDiscussionList;
    private final int DISCUSSIONINFORMATIONLISTDOWNOVER=0x20,GETLOGINUSEDISCUSSIONLIST=0x21;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==DISCUSSIONINFORMATIONLISTDOWNOVER){
                initDiscussionListViewAdapter();
            }else if (msg.what==GETLOGINUSEDISCUSSIONLIST){
                getLoginUserDiscussionList();
            }

        }
    };

    public static QipaoFragment instance = null;//单例模式

    public static QipaoFragment getInstance() {
        if (instance == null) {
            instance = new QipaoFragment();
        }

        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        chatView = inflater.inflate(R.layout.fragment_qipao, container,false);
        //initUserInfo();
        //获取登录用户token以及初始化融云服务，已转移至mapactivity，此处仅供备份
        //initUserToken();
        qipaoLv = (ListView) chatView.findViewById(R.id.qipaoLv);
        getLoginUserDiscussionObjectID();
        //initFriendListViewAdapter();
        return chatView;


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }
    /*
    private void initUserInfo() {
        userIdList = new ArrayList<Friend>();
        userIdList.add(new Friend("10010", "联通", "http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png"));//联通图标
        userIdList.add(new Friend("10086", "移动", "http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png"));//移动图标
        userIdList.add(new Friend("KEFU","官方客服","http://img02.tooopen.com/Download/2010/5/22/20100522103223994012.jpg"));
        //Token:vCYACJZW6N+6n/bWxTKJa7U/IPKPq/4/rzu3rTMXUSRr+45pWdMmjRNZqGa9SzdWUlX6awVGkOS9UH4AMaCELA==
        RongIM.setUserInfoProvider(this, true);
    }
    */

    /**
     * 获取用户好友列表的数据库表单中ID
     */
    private void getLoginUserDiscussionObjectID(){
        final BmobQuery<UserDis> userObjectIDQuery = new BmobQuery<UserDis>();
        userObjectIDQuery.addWhereEqualTo("userID", BmobUser.getCurrentUser(UserInformation.class).getPhone());
        userObjectIDQuery.findObjects(new FindListener<UserDis>() {
            @Override
            public void done(List<UserDis> object, BmobException e) {
                if(e==null){
                    loginUserDiscussionObjectID =object.get(0).getObjectId();
                    //Toast.makeText(getContext(), "loginUserFriendObjectID"+loginUserDiscussionObjectID, Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(GETLOGINUSEDISCUSSIONLIST);
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
    private void getLoginUserDiscussionList(){
        UserDis userDis = new UserDis();
        // 查询好友列表内的所有用户，因此查询的是用户表
        BmobQuery<DiscussionIfm> userFriendQuery = new BmobQuery<DiscussionIfm>();
        userDis.setObjectId(loginUserDiscussionObjectID);
        //userFriend是UserFriend表中的字段，用来存储所有该用户的好友关系的用户
        userFriendQuery.addWhereRelatedTo("userDis", new BmobPointer(userDis));
        userFriendQuery.findObjects(new FindListener<DiscussionIfm>() {
            @Override
            public void done(List<DiscussionIfm> object,BmobException e) {
                if(e==null){
                    userDiscussionInformationList=object;
                    //Toast.makeText(getContext(), "加载气泡群聊数据成功"+userDiscussionInformationList.size(), Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(DISCUSSIONINFORMATIONLISTDOWNOVER);
                }else{
                    Toast.makeText(getContext(), "加载气泡群聊数据失败"+e, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    /**
     * 好友列表控件adapter初始化
     */
    private void initDiscussionListViewAdapter(){
        DiscussionAdapter discussionAdapterAdapter = new DiscussionAdapter(getActivity(), userDiscussionInformationList, qipaoLv);
        qipaoLv.setAdapter(discussionAdapterAdapter);
    }
}
