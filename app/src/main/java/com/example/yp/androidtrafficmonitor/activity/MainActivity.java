package com.example.yp.androidtrafficmonitor.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yp.androidtrafficmonitor.R;
import com.example.yp.androidtrafficmonitor.application.AppApplication;
import com.example.yp.androidtrafficmonitor.beans.AppInfo;
import com.example.yp.androidtrafficmonitor.service.TrafficMonitorService;
import com.example.yp.androidtrafficmonitor.service.TrafficSpeedService;
import com.example.yp.androidtrafficmonitor.ui.CircleView;
import com.example.yp.androidtrafficmonitor.ui.FloatView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private Button showBtn;
    private Button queryTrafficBtn;
    private Button aboutUsBtn;
    private Button settingBtn;
    private Button interConnMonitorBtn;
    private Button lineChartBtn;
    private Switch floatStBtn;
    private Switch mobileStBtn;
    private Switch wifiStBtn;

    private TextView trafficUseTV;
    private TextView allTrafficTV;
    private CircleView mCircleView;



    private SharedPreferences trafficSP;
    private SharedPreferences settingSP;


    private FloatView floatView;
    private boolean isSwitchFloat = false;

    private long trafficTemp;
    private long mobileTraffic;
    private long mobileTemp;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //allTrafficTV.setText(Formatter.formatFileSize(getApplicationContext(),traffic)+",其中手机流量为"+Formatter.formatFileSize(getApplicationContext(),mobile));
            if(msg.what == 1){
                //allTrafficTV.setText(Formatter.formatFileSize(getApplicationContext(),));
                //view.tv_show.setText("网速");
                //Log.v("USE",mobileTraffic + "/" + settingSP.getString("通用流量","0") + "M");
                long allGenTraffic = settingSP.getLong("通用流量",0);
                trafficUseTV.setText(Formatter.formatFileSize(getApplicationContext(),mobileTraffic) + "/" + Formatter.formatFileSize(getApplicationContext(),allGenTraffic));
                mCircleView.setmWaterLevel((float) mobileTraffic / allGenTraffic);
            }
        }
    };


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        initView();
        initService();


        //createFloatView();
        /*getAppTrafficList();
        Intent intent = new Intent(MainActivity.this,TrafficMonitorBindService.class);
        intent.putExtra("applist",appList);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);*/
        trafficSP = getSharedPreferences("trafficInfor", Context.MODE_PRIVATE);
        settingSP = getSharedPreferences("settingInfor", Context.MODE_PRIVATE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //traffic = sharedPreferences.getLong("traffic",0);
                    mobileTraffic = trafficSP.getLong("mobile", 0);
                    Log.v("mobileTraffic", mobileTraffic + "");
                    Log.v("mobileTemp", mobileTemp + "");
                    if (mobileTraffic != mobileTemp) {
                        mobileTemp = mobileTraffic;
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void initService(){
        ActivityManager mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);
        if(!ServiceIsStart(mServiceList,"com.example.yp.androidtrafficmonitor.service.TrafficSpeedService")){
            startService(new Intent(MainActivity.this,TrafficMonitorService.class));
        }

    }

    void initView(){
        showBtn=(Button) findViewById(R.id.trafficMonitorBtn);
        queryTrafficBtn = (Button) findViewById(R.id.queryTraffic);
        aboutUsBtn = (Button) findViewById(R.id.aboutUs);
        settingBtn = (Button) findViewById(R.id.setting);
        interConnMonitorBtn = (Button) findViewById(R.id.interConnMonitorBtn);
        lineChartBtn = (Button) findViewById(R.id.lineChartBtn);
        floatStBtn = (Switch) findViewById(R.id.FloatSwitch);
        mobileStBtn = (Switch) findViewById(R.id.mobileSwitch);
        wifiStBtn = (Switch) findViewById(R.id.wifiSwitch);

        //allTrafficTV = (TextView) findViewById(R.id.allTraffic);
        trafficUseTV = (TextView) findViewById(R.id.trafficUseTV);
        mCircleView = (CircleView) findViewById(R.id.wave_view);
        mCircleView.setmWaterLevel(0.1F);
        // 开始执行
        mCircleView.startWave();

        aboutUsBtn.setOnClickListener(this);
        showBtn.setOnClickListener(this);
        queryTrafficBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
        interConnMonitorBtn.setOnClickListener(this);
        lineChartBtn.setOnClickListener(this);
        floatStBtn.setOnCheckedChangeListener(this);
        mobileStBtn.setOnCheckedChangeListener(this);
        wifiStBtn.setOnCheckedChangeListener(this);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.trafficMonitorBtn: {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TrafficMonitorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.interConnMonitorBtn:{
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, InterConnMonitorActivity.class);
                startActivity(intent);
                /*Log.v("start","开始");
                Intent intent = new Intent(MainActivity.this, TrafficMonitorService.class);
                intent.setAction("com.yp.test");
                startService(intent);
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
                MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
                registerReceiver(myBroadcastReceiver,intentFilter);
                Log.v("end","结束");*/
                break;
            }

            case R.id.queryTraffic:sendMessage();
                break;

            case R.id.aboutUs : {
                Dialog dialog = new Dialog(this);
                //设置它的ContentView
                dialog.setContentView(R.layout.about_us_dialog);
                dialog.show();
                break;
            }
            case R.id.setting:{
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SettingActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.lineChartBtn:{
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,GraphChartActivity.class);
                startActivity(intent);
                break;
            }

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v("Switch","Switch");
        switch (buttonView.getId()){
            case R.id.FloatSwitch:{
                if (isChecked) {
                    startService(new Intent(MainActivity.this, TrafficSpeedService.class));
                } else {
                    stopService(new Intent(MainActivity.this, TrafficSpeedService.class));
                }
                break;
            }
            case R.id.mobileSwitch:{
                /*ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(!isChecked){
                        ConnectivityManager connectivityManager = null;
                        Class connectivityManagerClz = null;
                        try {
                            connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                            connectivityManagerClz = connectivityManager.getClass();
                            Method method = connectivityManagerClz.getMethod(
                                    "setMobileDataEnabled", new Class[] { boolean.class });
                            method.invoke(connectivityManager, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                }*/
                break;
            }
            case R.id.wifiSwitch:{
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if(!isChecked){

                    wifiManager.setWifiEnabled(false);
                }
                else {
                    wifiManager.setWifiEnabled(true);
                }

                break;
            }
        }
    }


    void sendMessage(){
    String phone_number = "10086";
    String sms_content = "cxll";
    SmsManager smsManager = SmsManager.getDefault();
    int i = sms_content.length();
    smsManager.sendTextMessage(phone_number, null, sms_content, null, null);
    Toast.makeText(MainActivity.this, "发送完毕", Toast.LENGTH_SHORT).show();
    MainActivity.this.getContentResolver().registerContentObserver(
                Uri.parse("content://sms"), true, new SmsObserver(new Handler()));
        Log.v("Observer","StartObserver");
}


    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
            Log.v("Observer","StartObserver1");
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.v("Observer","StartObserver2");
            StringBuilder sb = new StringBuilder();
            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            cursor.moveToNext();
            sb.append("body=" + cursor.getString(cursor.getColumnIndex("body")));
            Log.i("Observer", sb.toString());
            cursor.close();
            Pattern pat = Pattern.compile("已用(.*?)M");//解析出"已使用...M"样式的语句
            Matcher mat = pat.matcher(sb);


            if(mat.find()){
                String st = mat.group();
                Log.v("ObserverLast1",st);
                mobileTraffic = Long.valueOf(st.substring(2,st.length()-4))*1024*1024;
                SharedPreferences.Editor editor = trafficSP.edit();
                editor.putLong("mobile",mobileTraffic);
                editor.commit();
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
            /*Pattern pats = Pattern.compile("[\\d]+\\.[\\d]+");//解析出"已使用...M"语句中的数字
            Matcher mats = pat.matcher(mat.group());
            if(mats.find()){
                Log.v("ObserverLast2",mat.group());
            }*/
            Log.v("Observer","StartObserver3");
            super.onChange(selfChange);
        }
    }



    /*public LinearLayout mFloatLayout;
    public WindowManager.LayoutParams wmParams;
    public WindowManager mWindowManager;
    public TextView mFloatView;

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 设置window
        // type为TYPE_SYSTEM_ALERT
        wmParams.format = PixelFormat.RGBA_8888;// 设置图片格式，效果为背景透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;// 默认位置：左上角
        wmParams.width = 100;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x = 100;// 设置x、y初始值，相对于gravity
        wmParams.y = 10;
        // 获取浮动窗口视图所在布局
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.traffic_float_view, null);
        mWindowManager.addView(mFloatLayout, wmParams);// 添加mFloatLayout
        mFloatView = (TextView) mFloatLayout.findViewById(R.id.floatTv);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                Log.i("TAG", "RawX" + event.getRawX());
                Log.i("TAG", "X" + event.getX());
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;// 减25为状态栏的高度
                Log.i("TAG", "RawY" + event.getRawY());
                Log.i("TAG", "Y" + event.getY());
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);// 刷新
                return false; // 此处必须返回false，否则OnClickListener获取不到监听
            }
        });
        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // do something... 跳转到应用
            }
        });
    }*/

    //通过Service的类名来判断是否启动某个服务　
    private boolean ServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList,String className){
       for(int i = 0; i < mServiceList.size(); i ++){
           Log.v("ServiceStart",mServiceList.get(i).service.getClassName());
             if(className.equals(mServiceList.get(i).service.getClassName())){
                return true;
             }
       }
        return false;
    }

}


