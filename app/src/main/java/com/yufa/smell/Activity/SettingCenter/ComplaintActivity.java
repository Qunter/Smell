package com.yufa.smell.Activity.SettingCenter;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Entity.Complaint;
import com.yufa.smell.R;
import com.yufa.smell.Util.ImageTool;
import com.yufa.smell.Util.ShowTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by luyufa on 2016/12/14.
 * 投诉页面
 */

public class ComplaintActivity extends BaseActivity {

    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.complaint_question)
    EditText complaintQuestion;
    @BindView(R.id.complaint_addImage)
    ImageButton complaintAddImage;
    @BindView(R.id.complaint_phone)
    EditText complaintPhone;
    @BindView(R.id.complaint_submit)
    Button complaintSubmit;
    private BmobFile image;
    private ImageTool imageTool;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_complaint);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("投诉");
        imageTool = new ImageTool(this,"Complaint");
    }

    @Override
    public void isShowToolBar() {
    }

    @OnClick({R.id.complaint_addImage, R.id.complaint_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.complaint_addImage:
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
                break;
            case R.id.complaint_submit:
                String data = complaintQuestion.getText().toString();
                String phone = complaintPhone.getText().toString();
                final Complaint complaint = new Complaint(data,phone);
                image.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            complaint.setUrl(image.getFileUrl());
                            complaint.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if(e == null){
                                        ShowTool showTool = new ShowTool();
                                        showTool.showToast(ComplaintActivity.this,"提交成功");
                                    }
                                }
                            });
                        }  else {
                           Log.d("ComplaintActivity","Upload Image" + e.getErrorCode() + ":" + e.getMessage());
                        }
                    }
                });
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
}
