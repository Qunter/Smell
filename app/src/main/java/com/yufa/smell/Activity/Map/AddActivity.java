package com.yufa.smell.Activity.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Entity.Bubble;
import com.yufa.smell.R;
import com.yufa.smell.Util.ImageTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2017/1/27.
 * 添加气泡-活动
 */

public class AddActivity extends BaseActivity {


    @BindView(R.id.addActivity_title)
    EditText addActivityTitle;
    @BindView(R.id.addActivity_data)
    EditText addActivityData;
    @BindView(R.id.addActivity_radius)
    EditText addActivityRadius;
    @BindView(R.id.addActivity_image)
    Button addActivityImage;
    @BindView(R.id.addActivity_who)
    Button addActivityWho;
    @BindView(R.id.addActivity_time)
    SeekBar addActivityTime;
    @BindView(R.id.addActivity_builders)
    Button addActivityBuilders;
    @BindView(R.id.addActivity_show)
    TextView addActivityShow;
    int index;
    private BmobFile image;
    private ImageTool imageTool;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_addactivity);
        ButterKnife.bind(this);
        Bmob.initialize(this, "f0fc59a153ba369c31798409902688bd");
        imageTool = new ImageTool(this, "Activity");
        seekbar();
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    private void seekbar() {
        addActivityTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                addActivityShow.setText("气泡持续（" + progress + "小时)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                index = seekBar.getProgress();
            }
        });
    }

    @OnClick({R.id.addActivity_image, R.id.addActivity_who, R.id.addActivity_builders})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addActivity_image:
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
                break;
            case R.id.addActivity_who:
                break;
            case R.id.addActivity_builders:
                double longitude = getIntent().getDoubleExtra("Longitude", 0.0);
                double latitude = getIntent().getDoubleExtra("Latitude", 0.0);
                if (addActivityTitle.getText().toString() == null || addActivityTitle.getText().toString().equals("") ||
                        addActivityData.getText().toString() == null || addActivityData.getText().toString().equals("") ||
                        addActivityRadius.getText().toString() == null || addActivityRadius.getText().toString().equals("")) {
                    break;
                } else {
                    final Bubble bubble = new Bubble();
                    bubble.setPoint(new BmobGeoPoint(longitude, latitude));
                    bubble.setTitle(addActivityTitle.getText().toString());
                    bubble.setData(addActivityData.getText().toString());
                    bubble.setTime(index);
                    bubble.setType("ACTIVITY");
                    if (image != null) {
                        image.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    bubble.setUrl1(image.getFileUrl());
                                    bubble.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                Log.d("", "Add Activity Bubble Success");
                                                AddActivity.this.finish();
                                            } else {
                                                Log.d("", e.getErrorCode() + e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("AddActivity", "upload image err" + e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    } else {
                        break;
                    }
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
}
