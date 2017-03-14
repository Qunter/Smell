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
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2017/1/27.
 */

public class AddBoothActivity extends BaseActivity {


    @BindView(R.id.addBooth_title)
    EditText addBoothTitle;
    @BindView(R.id.addBooth_data)
    EditText addBoothData;
    @BindView(R.id.addBooth_radius)
    EditText addBoothRadius;
    @BindView(R.id.addBooth_image)
    Button addBoothImage;
    @BindView(R.id.addBooth_who)
    Button addBoothWho;
    @BindView(R.id.addBooth_time)
    SeekBar addBoothTime;
    @BindView(R.id.addBooth_show)
    TextView addBoothShow;
    @BindView(R.id.addBooth_builder)
    Button addBoothBuilder;
    int index;
    private BmobFile image;
    private ImageTool imageTool;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_addbooth);
        ButterKnife.bind(this);
        imageTool = new ImageTool(this,"Booth");
        seekbar();
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }
    private void seekbar() {
        addBoothTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                addBoothShow.setText("气泡持续（" + progress + "小时)");
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

    @OnClick({R.id.addBooth_image, R.id.addBooth_who, R.id.addBooth_builder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBooth_image:
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
                break;
            case R.id.addBooth_who:
                break;
            case R.id.addBooth_builder:
                double longitude = getIntent().getDoubleExtra("Longitude", 0.0);
                double latitude = getIntent().getDoubleExtra("Latitude", 0.0);
                if (addBoothTitle.getText().toString()==null||addBoothTitle.getText().toString().equals("")||
                        addBoothData.getText().toString()==null||addBoothData.getText().toString().equals("")||
                        addBoothRadius.getText().toString()==null||addBoothRadius.getText().toString().equals("")){
                    break;
                }else {
                    final Bubble bubble = new Bubble();
                    bubble.setPoint(new BmobGeoPoint(longitude, latitude));
                    bubble.setTitle(addBoothTitle.getText().toString());
                    bubble.setData(addBoothData.getText().toString());
                    bubble.setTime(index);
                    bubble.setType("BOOTH");
                    if (image!=null){
                        image.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                bubble.setUrl1(image.getFileUrl());
                                bubble.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Log.d("", "Add Activity Bubble Success");
                                            AddBoothActivity.this.finish();
                                        } else {
                                            Log.d("AddBoothActivity","upload image err" +  e.getErrorCode() + e.getMessage());
                                        }
                                    }
                                });
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
