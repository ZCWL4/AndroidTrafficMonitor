package com.example.yp.androidtrafficmonitor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.example.yp.androidtrafficmonitor.application.AppApplication;
import com.example.yp.androidtrafficmonitor.ui.FloatView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.TimerTask;

public class TrafficSpeedService extends Service {
    private long preRxBytes = 0;
    private long preTime = 0;
    private FloatView view;
    private Handler handler;
    //private boolean isSwitchFloat;

    public TrafficSpeedService() {
    }

    @Override
    public void onCreate() {
        Log.v("onCreate","onCreate");
        super.onCreate();
        view = new FloatView(TrafficSpeedService.this);
        view.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //isSwitchFloat = intent.getBooleanExtra("SwitchFloat",false);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("TrafficSpeedService","start");


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    String result = getNetSpeed();
                    Log.v("getNetSpeed",result);
                    if(view!=null){
                        view.setSpeedView(result);
                    }

                }

            }
        };
        startRrefreshSpeed();
        return START_STICKY;
    }

    public void startRrefreshSpeed(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //preRxBytes = TrafficStats.getTotalRxBytes();;
                    //preTime = System.currentTimeMillis();


                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);

                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(view!=null){
            //WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            //windowManager.removeView(view);
            view.destroy();
        }

    }

    public String getNetSpeed() {
        long curRxBytes =  TrafficStats.getTotalRxBytes();
        long nowTime = System.currentTimeMillis();
        float speed = 0;
        if (preRxBytes == 0)
            preRxBytes = curRxBytes;
        if(preTime == 0){
            preTime = nowTime;
        }
        long bytes = curRxBytes - preRxBytes;
        if(nowTime - preTime!=0){
            speed = (float)bytes*1000/1024/(nowTime - preTime);
            Log.v("SPEED",""+speed);
            Log.v("bytes",""+bytes);
            Log.v("time",""+""+(nowTime - preTime));

        }
        //int kb = (int) Math.floor(bytes / 1024 + 0.5);
        //double kb = (double)bytes / (double)1024;
        //BigDecimal bd = new BigDecimal(kb);
        //Log.v("SPEED",String.valueOf(kb)+System.);
        //return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        preRxBytes = curRxBytes;
        preTime = nowTime;
        DecimalFormat df = new DecimalFormat("0.00");
        String result = df.format(speed);
        Log.v("Result",result);
        return result+"k/s";
    }

}
