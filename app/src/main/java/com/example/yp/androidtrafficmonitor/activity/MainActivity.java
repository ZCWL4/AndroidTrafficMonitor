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
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
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
            if(msg.what == 2){
                Log.v("changColor","Red");
                mCircleView.changeWaveColor(Color.RED);
            }
            if(msg.what == -2){
                Log.v("changColor","White");
                mCircleView.changeWaveColor(Color.WHITE);
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
                    //Log.v("流量警戒值",settingSP.getString("流量警戒值","2048"));
                    if (mobileTraffic / 1024 / 1024 > settingSP.getInt("流量警戒值", 2048)) {
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    } else if  (mobileTraffic / 1024 / 1024 < settingSP.getInt("流量警戒值", 2048)) {
                        Message msg = new Message();
                        msg.what = -2;
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

    //初始化网速的service
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

        //水纹球的调用代码
        trafficUseTV = (TextView) findViewById(R.id.trafficUseTV);
        mCircleView = (CircleView) findViewById(R.id.wave_view);
        mCircleView.setmWaterLevel(0.1F);
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
                //setDataConnectionState(this,false);

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

    //发送短信
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

        //解析短信
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

    /*public void setDataConnectionState(Context cxt, boolean state) {

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);;
        Class telephonyManagerClass = null;
        try {
            telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
        Method getITelephonyMethod = null;

            getITelephonyMethod = telephonyManagerClass
                    .getDeclaredMethod("getITelephony");
        getITelephonyMethod.setAccessible(false);
        Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
        Class ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
        }
        catch(Exception e){

            }

    }*/


}


