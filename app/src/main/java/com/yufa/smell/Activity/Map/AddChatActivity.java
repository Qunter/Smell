package com.yufa.smell.Activity.Map;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Adapter.Adapter;
import com.yufa.smell.Adapter.ViewHolder;
import com.yufa.smell.Entity.DiscussionIfm;
import com.yufa.smell.Entity.UserDis;
import com.yufa.smell.Entity.UserFriend;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by Administrator on 2017/1/27.
 */

public class AddChatActivity extends BaseActivity implements View.OnClickListener{
    private EditText discussionNameEt;
    private Button peopleInQipaoBtn,peopleInFriendBtn,discussionCreateBtn;
    private String loginUserFriendObjectID,loginUserDiscussionObjectID;
    private List<String> discussionPeopleIDList = new ArrayList<String>();
    private List<String> discussionPeopleObjIDList = new ArrayList<String>();
    private String[] discussionPeopleIDString;
    private String[] discussionPeopleNickNameString;
    private String[] discussionPeopleObjID;
    private boolean[] discussionPeopleIDBoolean;
    private List<UserInformation> userFriendInformationList;
    private List<UserDis> userFinalDiscussionList;
    private DiscussionIfm discussionInformation = new DiscussionIfm(),discussion;
    private final int DISCUSSIONNAMEIFNULL=0x07,DISCUSSIONNAMEISRIGHT=0x08,GETLOGINUSERFRIENDLIST=0x09,FRIENDINFORMATIONLISTDOWNOVER=0x10,SAVEDISCUSSIONINFORMATION=0x11,GETDISCUSSIONOBJECTID=0x12,
            SAVEUSERDISCUSSION=0x13,IFGETDISCUSSIONLIST=0x14,NOGETDISCUSSIONLIST=0x15,SEARCHCHOOSEUSERDISOBJECTID=0x16;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case IFGETDISCUSSIONLIST:
                    searchUserDiscussion();
                    break;
                case NOGETDISCUSSIONLIST:
                    buildUserDiscussionList();
                    break;
                case DISCUSSIONNAMEIFNULL:
                    if(discussionNameEt.getText().toString().equals(""))
                        Toast.makeText(getApplicationContext(), "讨论组名称不可为空  请重新输入", Toast.LENGTH_SHORT).show();
                    else
                        handler.sendEmptyMessage(DISCUSSIONNAMEISRIGHT);
                    break;
                case DISCUSSIONNAMEISRIGHT:
                    createDiscussion();
                    break;
                case SAVEDISCUSSIONINFORMATION:
                    saveDiscussionInformation();
                    break;
                case GETLOGINUSERFRIENDLIST:
                    getLoginUserFriendList();
                    break;
                case FRIENDINFORMATIONLISTDOWNOVER:
                    getLoginUserFriendIDList(userFriendInformationList);
                    break;
                case GETDISCUSSIONOBJECTID:
                    searchDiscussionInformation();
                    break;
                case SEARCHCHOOSEUSERDISOBJECTID:
                    searchChooseUserDisObjectID(discussionPeopleIDList);
                    break;
                case SAVEUSERDISCUSSION:
                    saveUserDiscussion();
                    break;
            }
        }
    };
    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_addchat);
        getLoginUserFriendObjectID();
        searchUserDiscussion();
        ButterKnife.bind(this);
        initView();

    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }
    /**
     * 初始化控件
     */
    private void initView(){
        discussionNameEt = (EditText) findViewById(R.id.discussionNameEt);
        peopleInQipaoBtn = (Button) findViewById(R.id.peopleInQipaoBtn);
        peopleInFriendBtn = (Button) findViewById(R.id.peopleInFriendBtn);
        discussionCreateBtn = (Button) findViewById(R.id.discussionCreateBtn);
        peopleInQipaoBtn.setOnClickListener(this);
        peopleInFriendBtn.setOnClickListener(this);
        discussionCreateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.peopleInQipaoBtn:
                new  AlertDialog.Builder(this)
                        .setTitle("气泡" )
                        .setMultiChoiceItems(new  String[] {"选项1", "选项2", "选项3" , "选项4" },  null ,  null )
                        .setPositiveButton("确定" , null)
                        .setNegativeButton("取消" ,  null )
                        .show();
                break;
            case R.id.peopleInFriendBtn:
                new  AlertDialog.Builder(this)
                        .setTitle("好友" )
                        .setMultiChoiceItems(discussionPeopleNickNameString, discussionPeopleIDBoolean, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // 来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容
                                discussionPeopleIDBoolean[which] = isChecked;
                            }
                        })
                        .setPositiveButton("确定", null)// 设置对话框[肯定]按钮
                        .setNegativeButton("取消", null)// 设置对话框[否定]按钮
                        .show();
                break;
            case R.id.discussionCreateBtn:
                handler.sendEmptyMessage(DISCUSSIONNAMEIFNULL);
                break;
        }
    }
    /**
     * 创建讨论组
     */
    private void createDiscussion(){
        for(int i=0;i<discussionPeopleIDBoolean.length;i++){
            int index=0;
            if(discussionPeopleIDBoolean[i]){
                discussionPeopleIDList.add(index,discussionPeopleIDString[i]);
                discussionPeopleObjIDList.add(index,discussionPeopleObjID[i]);
                Log.e(Integer.toString(index), discussionPeopleIDList.get(i) );
                Log.e(Integer.toString(index), discussionPeopleObjIDList.get(i) );
                index+=1;

            }
        }
        RongIM.getInstance().createDiscussion(discussionNameEt.getText().toString(),discussionPeopleIDList, new RongIMClient.CreateDiscussionCallback() {
            @Override
            public void onSuccess(String s) {
                discussionInformation.setDiscussionID(s);
                discussionInformation.setDiscussionName(discussionNameEt.getText().toString());
                handler.sendEmptyMessage(SAVEDISCUSSIONINFORMATION);
                //Toast.makeText(getApplicationContext(), "创建讨论组成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                //Toast.makeText(getApplicationContext(), errorCode+"创建讨论组失败", Toast.LENGTH_SHORT).show();
            }
        });
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
                    //Toast.makeText(getApplicationContext(), "loginUserFriendObjectID"+loginUserFriendObjectID, Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(GETLOGINUSERFRIENDLIST);
                }else{

                }
            }
        });

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
                    //Toast.makeText(getApplicationContext(), "成功加载好友列表数据"+userFriendInformationList.size(), Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(FRIENDINFORMATIONLISTDOWNOVER);
                }else{
                    //Toast.makeText(getApplicationContext(), e+"失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    /**
     * 初始化好友ID列表
     */
    private void getLoginUserFriendIDList(List<UserInformation> userFriendInformationList){
        //Toast.makeText(getApplicationContext(), "执行  size为:"+userFriendInformationList.size(), Toast.LENGTH_SHORT).show();
        discussionPeopleIDString = new String[userFriendInformationList.size()];
        discussionPeopleNickNameString=new String[userFriendInformationList.size()];
        discussionPeopleIDBoolean = new boolean[userFriendInformationList.size()];
        discussionPeopleObjID = new String[userFriendInformationList.size()];
        for(int i=0;i<userFriendInformationList.size();i++){
            discussionPeopleObjID[i]=userFriendInformationList.get(i).getObjectId();
            discussionPeopleIDString[i]=userFriendInformationList.get(i).getPhone();
            discussionPeopleNickNameString[i]=userFriendInformationList.get(i).getNickName();
        }
    }
    /**
     * 储存讨论组信息至融云
     */
    private void saveDiscussionInformation(){
        discussionInformation.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    handler.sendEmptyMessage(GETDISCUSSIONOBJECTID);
                    //Toast.makeText(getApplicationContext(), "成功储存讨论组数据", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), "储存讨论组数据失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * bmob查询讨论组数据
     */
    private void searchDiscussionInformation(){
        final BmobQuery<DiscussionIfm> disquery = new BmobQuery<DiscussionIfm>();
        disquery.addWhereEqualTo("discussionID", discussionInformation.getDiscussionID());
        disquery.findObjects(new FindListener<DiscussionIfm>() {
            @Override
            public void done(List<DiscussionIfm> object, BmobException e) {
                if(e==null){
                    discussion =object.get(0);
                    /*
                    Toast.makeText(getApplicationContext(), discussion.getObjectId(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), discussion.getDiscussionID(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), discussion.getDiscussionUrl(), Toast.LENGTH_SHORT).show();
                    Log.e(discussion.getObjectId(), "done: " );
                    Log.e(discussion.getDiscussionID(), "done: " );
                    Log.e(discussion.getDiscussionUrl(), "done: " );
                    */
                    //handler.sendEmptyMessage(SAVEUSERDISCUSSION);
                    handler.sendEmptyMessage(SEARCHCHOOSEUSERDISOBJECTID);
                }else{
                    Log.e(e+"",e+"" );
                }
            }
        });
    }
    /**
     * bmob储存用户参加的讨论组
     */
    private void saveUserDiscussion(){
        /*
        //此方法只能添加创建者的讨论组表单的讨论组字段的讨论组指针而不能设置其余成员，故修改
        UserDis userDiscussion = new UserDis();
        userDiscussion.setObjectId(loginUserDiscussionObjectID);
        BmobRelation relation = new BmobRelation();
        //将当前讨论组添加到多对多关联中
        relation.add(discussion);
        //多对多关联指向`post`的`likes`字段
        userDiscussion.setUserDis(relation);
        userDiscussion.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), e+"添加失败", Toast.LENGTH_SHORT).show();
                    Log.e(e+"", e+""+loginUserDiscussionObjectID );
                    Toast.makeText(getApplicationContext(), e+"讨论组储存失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
        */
        List<BmobObject> userDises = new ArrayList<BmobObject>();
        //添加选定好友列表内的好友的讨论组数据的objectID
        for(int i=0;i<userFinalDiscussionList.size();i++){
            UserDis userDis = new UserDis();
            BmobRelation relation = new BmobRelation();
            userDis.setObjectId(userFinalDiscussionList.get(i).getObjectId());
            relation.add(discussion);
            userDis.setUserDis(relation);
            userDises.add(userDis);
        }
        //添加登录用户的讨论组数据的objectID
        UserDis userDis = new UserDis();
        BmobRelation relation = new BmobRelation();
        userDis.setObjectId(loginUserDiscussionObjectID);
        relation.add(discussion);
        userDis.setUserDis(relation);
        userDises.add(userDis);
        new BmobBatch().updateBatch(userDises).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    Toast.makeText(getApplicationContext(),"成功储存所有用户的讨论组信息", Toast.LENGTH_SHORT).show();
                    /*
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            log("第"+i+"个数据批量更新成功："+result.getUpdatedAt());
                        }else{
                            log("第"+i+"个数据批量更新失败："+ex.getMessage()+","+ex.getErrorCode());
                        }

                    }
                    */
                }else{
                    Toast.makeText(getApplicationContext(), e+"储存所有用户的讨论组信息失败", Toast.LENGTH_SHORT).show();
                    //Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    /**
     * bmob查询某用户参加讨论组的信息
     */
    private void searchUserDiscussion(){
        final BmobQuery<UserDis> query = new BmobQuery<UserDis>();
        query.addWhereEqualTo("userID", BmobUser.getCurrentUser(UserInformation.class).getPhone());
        query.findObjects(new FindListener<UserDis>() {
            @Override
            public void done(List<UserDis> object, BmobException e) {
                if(e==null){
                    loginUserDiscussionObjectID =object.get(0).getObjectId();
                }else{
                    handler.sendEmptyMessage(NOGETDISCUSSIONLIST);
                }
            }
        });
    }
    /**
     * 如果用户此前没有生成讨论组列表的情况下，执行方法生成讨论组列表
     */
    private void buildUserDiscussionList(){
        String userID = BmobUser.getCurrentUser(UserInformation.class).getPhone();
        UserDis userDiscussion = new UserDis();
        BmobRelation bmobRelation = new BmobRelation();
        userDiscussion.setUserID(userID);
        userDiscussion.setUserDis(bmobRelation);
        userDiscussion.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    //Toast.makeText(getApplicationContext(), "创建", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(IFGETDISCUSSIONLIST);
                }else{
                    //Toast.makeText(getApplicationContext(), e+"错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 获取选定加入讨论组的用户列表中用户的讨论组表单中的objectID
     */
    private void searchChooseUserDisObjectID(List<String> chooseUserID){
        List<BmobQuery<UserDis>> queries = new ArrayList<BmobQuery<UserDis>>();
        for(int i=0;i<chooseUserID.size();i++){
            BmobQuery<UserDis> eq = new BmobQuery<UserDis>();
            eq.addWhereEqualTo("userID", chooseUserID.get(i));
            queries.add(eq);
        }
        BmobQuery<UserDis> mainQuery = new BmobQuery<UserDis>();
        mainQuery.or(queries);
        mainQuery.findObjects(new FindListener<UserDis>() {
            @Override
            public void done(List<UserDis> object, BmobException e) {
                if(e==null){
                    userFinalDiscussionList=object;
                    handler.sendEmptyMessage(SAVEUSERDISCUSSION);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

}
