package com.yufa.smell.Activity.Map;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Entity.Smell;
import com.yufa.smell.R;
import com.yufa.smell.Util.ImageTool;
import com.yufa.smell.Util.ShowTool;

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
 * Created by Administrator on 2017/1/28.
 * 添加气味
 */

public class AddSmellActivity extends BaseActivity {

    @BindView(R.id.smell_text)
    EditText smellText;
    @BindView(R.id.smell_image)
    TextView smellImage;
    @BindView(R.id.smell_camma)
    TextView smellCamma;
    @BindView(R.id.smell_favorite)
    TextView smellFavorite;
    @BindView(R.id.smell_time)
    SeekBar smellTime;
    @BindView(R.id.smell_show)
    TextView smellShow;
    @BindView(R.id.smell_submit)
    Button smellSubmit;
    int index;
    private BmobFile image;
    ImageTool imageTool;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_addsmell);
        ButterKnife.bind(this);
        Bmob.initialize(this, "f0fc59a153ba369c31798409902688bd");
        index = smellTime.getProgress();
        imageTool = new ImageTool(this,"Smell");
        seekbar();
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }


    private void seekbar() {
        smellTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                smellShow.setText("气泡持续（" + progress + "小时)");
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


    @OnClick({R.id.smell_image, R.id.smell_camma, R.id.smell_favorite, R.id.smell_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smell_image:
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
                break;
            case R.id.smell_camma:
                startActivityForResult(imageTool.getImageFromCamera(),3);
                break;
            case R.id.smell_favorite:
                break;
            case R.id.smell_submit:
                double latitude = getIntent().getDoubleExtra("Latitude", 0.1);
                double longitude = getIntent().getDoubleExtra("Longitude", 0.1);
                if (smellText.getText().toString()!=null||smellText.getText().toString().equals("")){
                    break;
                }else {
                    final Smell smell = new Smell();
                    smell.setTxt(smellText.getText().toString().trim());
                    smell.setPoint(new BmobGeoPoint(longitude, latitude));
                    smell.setTime(index);
                    if (image!=null){
                        image.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d("AddSmellActivity", "upload success");
                                    smell.setUrl(image.getFileUrl());
                                    smell.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                new ShowTool().showToast(AddSmellActivity.this, "提交成功");
                                                AddSmellActivity.this.finish();
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("AddSmellActivity", e.getErrorCode() + e.getMessage());
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
            case 1:
                //获取图片后保存图片到本地，是否需要保存看情况而定
                image = imageTool.saveBitmap(data);
                break;
            case 2:
                //获取图片后裁剪图片
                startActivityForResult(imageTool.clipperBigPic(this, data.getData()), 1);
                break;
            case 3:
                //调用系统相机获取图片
                if (data.getData()!=null)
                image = imageTool.saveBitmapFile(data);
                break;
        }
    }

}
