package com.example.yp.androidtrafficmonitor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.IBinder;
import android.util.Log;

import com.example.yp.androidtrafficmonitor.ui.FloatView;
import com.example.yp.androidtrafficmonitor.utils.TrafficUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class TrafficMonitorService extends Service {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean flag = false;
    private long mobileTemp;
    private long mobileTraffic;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("trafficInfor", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("TrafficService","startCommand");
        startMonitor(this);
        //UidTrafficdDB.query(getApplicationContext());

        /*FloatView view = new FloatView(this,getNetSpeed());
        view.show();*/
        return START_STICKY;
    }

//    @Override
//    public void onDestroy() {
//        Intent intent = new Intent(this,TrafficMonitorService.class);
//        startService(intent);
//    }



    public synchronized void startMonitor(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!flag) {
                    TrafficUtil.startMonitor(context);
                    mobileTraffic = TrafficStats.getMobileRxBytes()+TrafficStats.getMobileTxBytes();
                    long mobile = mobileTraffic - mobileTemp;
                    Log.v("Mobile",mobileTraffic+" "+mobileTemp);
                    mobileTemp = mobileTraffic;
                    int nowDay = getCurrentDay();
                    editor.putLong("mobile", mobile + sp.getLong("mobile",0));
                    /*for(long i=1; i<25; i++){
                        editor.putLong(String.valueOf(i), new Random().nextInt(25) *1024*1024);
                    }*/

                    editor.putLong(String.valueOf(nowDay),mobile+sp.getLong(String.valueOf(nowDay),0));
                    editor.commit();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.v("flag", "标记");

                }
            }
        }).start();
    }

    public int getCurrentDay(){
        SimpleDateFormat sf = new SimpleDateFormat("dd");
        Calendar c = Calendar.getInstance();
        return Integer.valueOf(sf.format(c.getTime()));
    }

}
