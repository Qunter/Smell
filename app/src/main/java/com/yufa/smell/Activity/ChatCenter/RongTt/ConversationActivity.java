package com.yufa.smell.Activity.ChatCenter.RongTt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yufa.smell.R;

import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2016/11/15.
 */
public class ConversationActivity extends FragmentActivity {

    private TextView mName;
    private ImageView mPersionInformationIv;
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent = getIntent();
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        //Log.e("log", mConversationType.getName());
        mName = (TextView) findViewById(R.id.name);
        mPersionInformationIv = (ImageView) findViewById(R.id.persionInformationIv);
        if(mConversationType.getName().equals("private")){
            mPersionInformationIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(getApplicationContext(),UserInformationPage.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("userNickName", getIntent().getData().getQueryParameter("title"));
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                }
            });
        }else if(mConversationType.getName().equals("discussion")){
            mPersionInformationIv.setVisibility(View.GONE);
        }
        String sId = getIntent().getData().getQueryParameter("targetId");//targetId:单聊即对方ID，群聊即群组ID
        String sName = getIntent().getData().getQueryParameter("title");//获取昵称
        if (!TextUtils.isEmpty(sName)){
            mName.setText(sName);
        }else {
//            sId
            //TODO 拿到id 去请求自己服务端
        }
    }
}