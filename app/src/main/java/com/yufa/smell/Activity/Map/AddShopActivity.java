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

public class AddShopActivity extends BaseActivity {


    @BindView(R.id.addShop_title)
    EditText addShopTitle;
    @BindView(R.id.addShop_data)
    EditText addShopData;
    @BindView(R.id.addShop_radius)
    EditText addShopRadius;
    @BindView(R.id.addShop_image)
    Button addShopImage;
    @BindView(R.id.addShop_who)
    Button addShopWho;
    @BindView(R.id.addShop_time)
    SeekBar addShopTime;
    @BindView(R.id.addShop_show)
    TextView addShopShow;
    @BindView(R.id.addShop_builder)
    Button addShopBuilder;
    int index;
    private BmobFile image;
    private ImageTool imageTool;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_addshop);
        ButterKnife.bind(this);
        imageTool = new ImageTool(this,"Shop");
        seekbar();
    }

    private void seekbar() {
        addShopTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                addShopShow.setText("气泡持续（" + progress + "小时)");
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

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    @OnClick({R.id.addShop_image, R.id.addShop_who, R.id.addShop_builder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addShop_image:
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
                break;
            case R.id.addShop_who:
                break;
            case R.id.addShop_builder:
                double longitude = getIntent().getDoubleExtra("Longitude", 0.0);
                double latitude = getIntent().getDoubleExtra("Latitude", 0.0);
                if (addShopTitle.getText().toString()!=null||addShopTitle.getText().toString().equals("")||
                        addShopData.getText().toString()!=null||addShopData.getText().toString().equals("")||
                        addShopRadius.getText().toString()!=null||addShopRadius.getText().toString().equals("")){
                    break;
                }else {
                    final Bubble bubble = new Bubble();
                    bubble.setPoint(new BmobGeoPoint(longitude, latitude));
                    bubble.setTitle(addShopTitle.getText().toString());
                    bubble.setData(addShopData.getText().toString());
                    bubble.setTime(index);
                    bubble.setType("SHOP");
                    if (image!=null){
                        image.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e==null){
                                    bubble.setUrl1(image.getFileUrl());
                                    bubble.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                Log.d("", "Add Activity Bubble Success");
                                                AddShopActivity.this.finish();
                                            } else {
                                                Log.d("", e.getErrorCode() + e.getMessage());
                                            }
                                        }
                                    });
                                }else {
                                    Log.d("AddShopActivity","upload image err"  + e.getErrorCode() + e.getMessage());
                                }

                            }
                        });
                    }else {
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
