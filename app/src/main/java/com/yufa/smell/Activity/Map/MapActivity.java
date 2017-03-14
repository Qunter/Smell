package com.yufa.smell.Activity.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.TextOptions;
import com.yufa.smell.Activity.AgentApplication;
import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.ChatCenter.ViewPaperActivity;
import com.yufa.smell.Activity.SettingCenter.SettingActivity;
import com.yufa.smell.Entity.Bubble;
import com.yufa.smell.Entity.MenuItem;
import com.yufa.smell.Entity.Smell;
import com.yufa.smell.Entity.Type;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.Util.BitmapAdd;
import com.yufa.smell.Util.GetTime;
import com.yufa.smell.Util.ImageTool;
import com.yufa.smell.Util.MenuShow;
import com.yufa.smell.R;
import com.yufa.smell.Util.ShowTool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.amap.api.maps2d.AMapOptions.LOGO_POSITION_BOTTOM_RIGHT;

/**
 * AMap地图中简单介绍显示定位小蓝点
 */
public class MapActivity extends BaseActivity implements LocationSource,
        AMapLocationListener,View.OnClickListener {

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation aMapLocation;

    private  Boolean isDraw = true;
    private Boolean isClick = false;
    private long exitTime = 0;
    private GetTime time = new GetTime();

    int index;
    private  BmobFile image;
    ImageTool imageTool;

    private ImageButton swip;
    //新建菜单显示类对象
    private MenuShow mMenuShow;
    //定义菜单按钮控件
    private RelativeLayout menuBtn;
    //定义菜单列表中的四个Item的名称以及资源
    private MenuItem qiwei,qipao, haoyou, shezhi;
    private String[] MenuItemName = {"气味","气泡","好友","设置"};
    private int[] MenuItemIcon = {R.drawable.ic_speaker_notes_white,
            R.drawable.ic_filter_tilt_shift_white,R.drawable.ic_supervisor_account_white,R.drawable.ic_settings_white};
    private int[] MenuItemIconChange = {R.drawable.ic_speaker_notes_blue,
            R.drawable.ic_filter_tilt_shift_blue,R.drawable.ic_supervisor_account_blue,R.drawable.ic_settings_blue};
    //定义文字颜色的两种变换，未按下是白色，按下是蓝色
    private int textColor = Color.parseColor("#ffffff");
    private int textColorChange = Color.parseColor("#2196f3");
    //定义菜单MSG的信息，按下哪个发送哪条
    private final int QIWEI=0x00,QIPAO=0x01,HAOYOU=0x02,SHEZHI=0x03;
    //定义菜单按钮MSG
    private final int MENU=0x10;
    //菜单列表的布局
    private RelativeLayout menuList;
    private String userID;
    //用于恢复被点击按钮的颜色
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //菜单按钮
                case MENU:
                    mMenuShow.showList();
                    break;
                //一级菜单
                case QIWEI:
                    qiwei.setImageViewImg(MenuItemIcon[0]);
                    qiwei.setTextViewColor(textColor);
                    qiwei();
                    break;
                case QIPAO:
                    qipao.setImageViewImg(MenuItemIcon[1]);
                    qipao.setTextViewColor(textColor);
                    qipao();
                    break;
                case HAOYOU:
                    haoyou.setImageViewImg(MenuItemIcon[2]);
                    haoyou.setTextViewColor(textColor);
                    haoyou();
                    break;
                case SHEZHI:
                    shezhi.setImageViewImg(MenuItemIcon[3]);
                    shezhi.setTextViewColor(textColor);
                    shezhi();
                    break;
            }
            super.handleMessage(msg);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AgentApplication agentApplication = (AgentApplication) getApplication();
        agentApplication.addActivity(this);
        RongIM.init(this);
        setContentView(R.layout.activity_map);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
        initUserToken();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    /**
     * 初始化AMap对象
     */
    private void initView() {
        swip = (ImageButton)findViewById(R.id.swip);
        swip.setOnClickListener(this);
        menuBtn = (RelativeLayout) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(this);
        menuList = (RelativeLayout) findViewById(R.id.menulist);
        //传递菜单列表的对象
        mMenuShow = new MenuShow(menuList);
        menuList.setVisibility(View.INVISIBLE);//先将列表隐藏
        initMenuItem();

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setLogoPosition(LOGO_POSITION_BOTTOM_RIGHT);
        uiSettings.setMyLocationButtonEnabled(false);
    }
    //初始化菜单项
    private void initMenuItem(){
        qiwei = (MenuItem) findViewById(R.id.qiwei);
        qiwei.setImageViewImg(MenuItemIcon[0]);
        qiwei.setTextViewText(MenuItemName[0]);
        qiwei.setOnClickListener(this);

        qipao = (MenuItem) findViewById(R.id.qipao);
        qipao.setImageViewImg(MenuItemIcon[1]);
        qipao.setTextViewText(MenuItemName[1]);
        qipao.setOnClickListener(this);

        haoyou = (MenuItem) findViewById(R.id.haoyou);
        haoyou.setImageViewImg(MenuItemIcon[2]);
        haoyou.setTextViewText(MenuItemName[2]);
        haoyou.setOnClickListener(this);

        shezhi = (MenuItem) findViewById(R.id.shezhi);
        shezhi.setImageViewImg(MenuItemIcon[3]);
        shezhi.setTextViewText(MenuItemName[3]);
        shezhi.setOnClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.arrowhead));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0,0,0,0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setOnMarkerClickListener(markerClickListener);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);}

    private void qipao(){
        selectBubble();
    }
    private void qiwei(){
        newSmell();
    }
    private void haoyou(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this, ViewPaperActivity.class);
        startActivity(intent);
    }
    private void shezhi(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this,SettingActivity.class);
        startActivity(intent);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                //marks(amapLocation.getLatitude(),amapLocation.getLongitude());
                this.aMapLocation = amapLocation;
                if (aMapLocation == null){

                }else {
                    if (amapLocation.getLatitude()-aMapLocation.getLatitude()>0.00005||amapLocation.getLongitude()-aMapLocation.getLongitude()>0.00005){
                        updateLocation(amapLocation);
                    }
                }
                //updateLocation(amapLocation);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }
    private void addCircle(double latitude,double longitude,String txt){
        aMap.addText(new TextOptions()
                .position(new LatLng(latitude,longitude))
                .fontSize(30)
                .backgroundColor(Color.WHITE)
                .text(txt));
        aMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude,longitude))
                .radius(100)
                .fillColor(Color.argb(40,0,0,240)).strokeColor(Color.argb(90,255,255,255)).strokeWidth(0)
        );
    }
    private void addMarks(double latitude,double longitude,String string){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude,longitude));
        markerOption.title("气味").snippet(string);
        markerOption.draggable(true);
        BitmapAdd add = new BitmapAdd();
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(add.getBitmap(MapActivity.this)));
        aMap.addMarker(markerOption);
    }
    private void marks(double latitude,double longitude){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude,longitude));
        markerOption.draggable(true);
        //BitmapAdd add = new BitmapAdd();
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrowhead)));
        aMap.addMarker(markerOption);
    }

    private void getSmell(AMapLocation aMapLocation){
        marks(aMapLocation.getLatitude(),aMapLocation.getLongitude());
        BmobGeoPoint bmobGeoPoint = new BmobGeoPoint();
        bmobGeoPoint.setLongitude(aMapLocation.getLongitude());
        bmobGeoPoint.setLatitude(aMapLocation.getLatitude());
        BmobQuery<Smell> query = new BmobQuery<Smell>();
        query.addWhereWithinKilometers("point",bmobGeoPoint,50);
        query.addWhereGreaterThanOrEqualTo("endTime",time.getNow());
        query.findObjects(new FindListener<Smell>() {
            @Override
            public void done(List<Smell> list, BmobException e) {
                if (e==null){
                    for (Smell smell:list) {
                        addMarks(smell.getPoint().getLatitude(),smell.getPoint().getLongitude(),smell.getTxt());
                    }
                }else {
                    Log.d("MapActivity",e.getErrorCode() + ":" + e.getMessage());
                }
            }
        });
    }

    private void getBubble(AMapLocation aMapLocation){
        marks(aMapLocation.getLatitude(),aMapLocation.getLongitude());
        final BmobGeoPoint bmobGeoPoint = new BmobGeoPoint();
        bmobGeoPoint.setLongitude(aMapLocation.getLongitude());
        bmobGeoPoint.setLatitude(aMapLocation.getLatitude());
        BmobQuery<Bubble> query = new BmobQuery<Bubble>();
        query.addWhereWithinKilometers("point",bmobGeoPoint,50);
        query.addWhereGreaterThanOrEqualTo("endTime",time.getNow());
        query.findObjects(new FindListener<Bubble>() {
            @Override
            public void done(List<Bubble> list, BmobException e) {
                if (e==null){
                    for (Bubble bubble : list){
                        if (bubble.getRadius()>=50*1000){
                            addCircle(bubble.getPoint().getLatitude(),bubble.getPoint().getLongitude(),bubble.getTitle());
                        }
                    }
                }   else {
                    Log.d("MapActivity",e.getErrorCode() + e.getMessage());
                }
            }
        });
    }



    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setInterval(1000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    private void updateLocation(AMapLocation aMapLocation){
        UserInformation userInformation = new UserInformation();
        userInformation.setLatitude(aMapLocation.getLatitude());
        userInformation.setLongitude(aMapLocation.getLongitude());
        userInformation.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    Log.d("MapActivity","update location success");
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuBtn:
                mHandler.sendEmptyMessage(MENU);
                if (isClick){
                    isClick = false;
                    menuBtn.setBackgroundResource(R.drawable.menu);
                }else {
                    isClick = true;
                    menuBtn.setBackgroundResource(R.drawable.menu2);
                }
                //Toast.makeText(getApplicationContext(),"点击获取成功",Toast.LENGTH_SHORT);
                break;
            case R.id.qiwei:
                qiwei.setImageViewImg(MenuItemIconChange[0]);
                qiwei.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(QIWEI,300);
                break;
            case R.id.qipao:
                qipao.setImageViewImg(MenuItemIconChange[1]);
                qipao.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(QIPAO,300);
                break;
            case R.id.haoyou:
                haoyou.setImageViewImg(MenuItemIconChange[2]);
                haoyou.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(HAOYOU,300);
                break;
            case R.id.shezhi:
                shezhi.setImageViewImg(MenuItemIconChange[3]);
                shezhi.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(SHEZHI,300);
                break;
            case R.id.swip:{
                swip();
                break;
            }
        }
    }

    private void swip(){
        Log.d("MapActivity","on click success!");
        if (isDraw){
            isDraw = false;
            aMap.clear();
            aMap.invalidate();
            swip.setBackgroundResource(R.drawable.cut2);
            if (aMapLocation!=null){
                getSmell(aMapLocation);
            }
        }else {
            isDraw = true;
            aMap.clear();
            aMap.invalidate();
            swip.setBackgroundResource(R.drawable.cut1);
            if (aMapLocation!=null){
                getBubble(aMapLocation);
            }
        }
    }

    private void newSmell(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_smell,null);
        SeekBar smellTime = (SeekBar) view.findViewById(R.id.smell_time);
        final TextView smellShow = (TextView) view.findViewById(R.id.smell_show);
        final EditText smellText = (EditText) view.findViewById(R.id.smell_text);
        index = smellTime.getProgress();
        imageTool = new ImageTool(this,"Smell");
        smellTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                smellShow.setText("气味持续（" + progress + "小时)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                index = seekBar.getProgress();
            }
        });
        TextView smellImage = (TextView) view.findViewById(R.id.smell_image);
        TextView smellCamma = (TextView) view.findViewById(R.id.smell_camma);
        TextView smellFavorite = (TextView) view.findViewById(R.id.smell_favorite);
        smellImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(imageTool.getImageFromAlbum(), 2);
            }
        });
        smellCamma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(imageTool.getImageFromCamera(),3);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                final Smell smell = new Smell();
                String creater = BmobUser.getCurrentUser(UserInformation.class).getNickName();
                smell.setTxt(smellText.getText().toString().trim());
                smell.setPoint(new BmobGeoPoint(longitude, latitude));
                smell.setTime(index);
                smell.setComment("评论内容");
                smell.setCommenter("评论人");
                smell.setCommentTime(time.getNow());
                smell.setCreater(creater);
                smell.setEndtime(time.getTime(index));
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
                                        new ShowTool().showToast(MapActivity.this, "提交成功");
                                    }
                                }
                            });
                        } else {
                            Log.d("AddSmellActivity", e.getErrorCode() + e.getMessage());
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        builder.create().show();
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
                startActivityForResult(imageTool.clipperBigPic(MapActivity.this, data.getData()), 1);
                break;
            case 3:
                //调用系统相机获取图片
                image = imageTool.saveBitmapFile(data);
                break;
        }
    }

    private void selectBubble(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("一请选择您要创建的气泡一");
        builder.setIcon(R.drawable.ic_filter_tilt_shift_blue);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.content_selectmenu,null);
        RadioGroup group = (RadioGroup) view.findViewById(R.id.qipao_group);
        final Type type = new Type();
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton button = (RadioButton)view.findViewById(id);
                switch (button.getText().toString()){
                    case "搭建商铺":{
                        type.setType("Shop");
                        break;
                    }
                    case "搭建摊位":{
                        type.setType("Booth");
                        break;
                    }
                    case "搭建活动":{
                        type.setType("Activity");
                        break;
                    }
                    case "搭建群聊":{
                        type.setType("Chat");
                        break;
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (type.getType()){
                    case "Activity":{
                        activityBubble();
                        break;
                    }
                    case "Booth":{
                        boothBubble();
                        break;
                    }
                    case "Shop":{
                        shopBubble();
                        break;
                    }
                    case "Chat":{
                        chatBubble();
                        break;
                    }
                }
                //startActivity(intent);
            }
        });
        builder.setView(view);
        builder.create().show();
    }

    private void activityBubble(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_activity,null);
        builder.setView(view);
        builder.create();
        final AlertDialog dialog = builder.show();
        imageTool = new ImageTool(MapActivity.this,"Activity");
        final EditText title = (EditText) view.findViewById(R.id.addActivity_title);
        final EditText data = (EditText) view.findViewById(R.id.addActivity_data);
        final EditText radius = (EditText) view.findViewById(R.id.addActivity_radius);
        final Button image = (Button) view.findViewById(R.id.addActivity_image);
        Button who = (Button) view.findViewById(R.id.addActivity_who);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.addActivity_time);
        Button submit = (Button) view.findViewById(R.id.addActivity_builders);
        final TextView time = (TextView) view.findViewById(R.id.addActivity_show);
        index = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText("气泡持续（" + progress + "小时)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                index = seekBar.getProgress();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(imageTool.getImageFromAlbum(),2);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString() == null || title.getText().toString().equals("") ||
                        data.getText().toString() == null || data.getText().toString().equals("") ||
                        radius.getText().toString() == null || radius.getText().toString().equals("")) {
                    return;
                } else {
                    addBubble(title.getText().toString(),data.getText().toString(),radius.getText().toString(),index,"Activity");
                    dialog.dismiss();
                }
            }
        });
    }

    private void boothBubble(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_booth,null);
        builder.setView(view);
        builder.create();
        final AlertDialog dialog = builder.show();
        imageTool = new ImageTool(MapActivity.this,"Booth");
        final EditText title = (EditText) view.findViewById(R.id.addBooth_title);
        final EditText data = (EditText) view.findViewById(R.id.addBooth_data);
        final EditText radius = (EditText) view.findViewById(R.id.addBooth_radius);
        final Button image = (Button) view.findViewById(R.id.addBooth_image);
        Button who = (Button) view.findViewById(R.id.addBooth_who);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.addBooth_time);
        Button submit = (Button) view.findViewById(R.id.addBooth_builder);
        final TextView time = (TextView) view.findViewById(R.id.addBooth_show);
        index = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText("气泡持续（" + progress + "小时)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                index = seekBar.getProgress();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(imageTool.getImageFromAlbum(),2);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString() == null || title.getText().toString().equals("") ||
                        data.getText().toString() == null || data.getText().toString().equals("") ||
                        radius.getText().toString() == null || radius.getText().toString().equals("")) {
                    return;
                } else {
                    addBubble(title.getText().toString(),data.getText().toString(),radius.getText().toString(),index,"Booth");
                    dialog.dismiss();
                }
            }
        });
    }

    private void shopBubble(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_shop,null);
        builder.setView(view);
        builder.create();
        final AlertDialog dialog = builder.show();
        imageTool = new ImageTool(MapActivity.this,"Shop");
        final EditText title = (EditText) view.findViewById(R.id.addShop_title);
        final EditText data = (EditText) view.findViewById(R.id.addShop_data);
        final EditText radius = (EditText) view.findViewById(R.id.addShop_radius);
        final Button image = (Button) view.findViewById(R.id.addShop_image);
        Button who = (Button) view.findViewById(R.id.addShop_who);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.addShop_time);
        Button submit = (Button) view.findViewById(R.id.addShop_builder);
        final TextView time = (TextView) view.findViewById(R.id.addShop_show);
        index = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText("气泡持续（" + progress + "小时)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                index = seekBar.getProgress();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(imageTool.getImageFromAlbum(),2);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString() == null || title.getText().toString().equals("") ||
                        data.getText().toString() == null || data.getText().toString().equals("") ||
                        radius.getText().toString() == null || radius.getText().toString().equals("")) {
                    return;
                } else {
                    addBubble(title.getText().toString(),data.getText().toString(),radius.getText().toString(),index,"Shop");
                    dialog.dismiss();
                }
            }
        });
    }

    private void addBubble(String title,String data,String radius,int index,String type){
        final Bubble bubble = new Bubble();
        bubble.setPoint(new BmobGeoPoint(aMapLocation.getLongitude(), aMapLocation.getLatitude()));
        bubble.setTitle(title);
        bubble.setData(data);
        bubble.setTime(index);
        bubble.setType(type);
        bubble.setRadius(Integer.valueOf(radius));
        bubble.setEndTime(time.getTime(index));
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
                                    Log.d("", "Add  Bubble Success");
                                    return;
                                } else {
                                    Log.d("", e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    } else {
                        Log.d("AddBubble", "upload image err" + e.getErrorCode() + e.getMessage());
                    }
                }
            });
        }
    }

    private void chatBubble(){
        //聊天气泡界面逻辑代码
        Intent intent = new Intent();
        intent.setClass(MapActivity.this,AddChatActivity.class);
        startActivity(intent);
    }

    /**
     * 获取登录用户的融云token
     */
    private void initUserToken(){
        UserInformation loginUser = BmobUser.getCurrentUser(UserInformation.class);
        userID = loginUser.getPhone();
        connectRongServer(loginUser.getToken());
    }

    /**
     * 初始化登录用户的融云服务
     */
    private void connectRongServer(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                if (userId.equals(userID)){
                    Toast.makeText(MapActivity.this, userID+"成功连接", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MapActivity.this, userID+"连接失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                // Log.e("onError", "onError userid:" + errorCode.getValue());//获取错误的错误码
                Toast.makeText(MapActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("MapActivity", "connect failure errorCode is : " + errorCode.getValue());
            }


            @Override
            public void onTokenIncorrect() {
                Toast.makeText(MapActivity.this, "TokenError", Toast.LENGTH_SHORT).show();
                Log.e("MapActivity", "token is error ,please check token and appkey");
            }
        });

    }

    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @SuppressLint("NewApi")
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getSnippet()!=null||!marker.getSnippet().equals("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MapActivity.this);
                View view = inflater.inflate(R.layout.content_smellinfo,null);
                builder.setView(view);
                builder.create();
                final Dialog dialog = builder.show();
                TextView textView = (TextView)view.findViewById(R.id.smellinfo_snippet);
                textView.setText(marker.getSnippet());
                TextInputLayout layout = (TextInputLayout)view.findViewById(R.id.smellinfo_text);
                String text = layout.getEditText().getText().toString();
                Button comment = (Button)view.findViewById(R.id.smellinfo_comment);
                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //提交评论
                        dialog.dismiss();
                    }
                });
            }
            return true;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
               getApplication().onTerminate();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
