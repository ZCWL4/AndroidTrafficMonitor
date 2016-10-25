package com.example.yp.androidtrafficmonitor.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yp.androidtrafficmonitor.service.ServiceForBroadcast;
import com.example.yp.androidtrafficmonitor.service.TrafficMonitorService;
import com.example.yp.androidtrafficmonitor.service.TrafficSpeedService;

public class BootBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent mintent) {

        Log.v("Broadcast","BootBroadcast");
        Intent intent = new Intent(context , ServiceForBroadcast.class);
        context.startService(intent);
        SharedPreferences sp = context.getSharedPreferences("test1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("test",9);
        editor.commit();
        context.startService(new Intent(context,TrafficMonitorService.class));
        context.startService(new Intent(context, TrafficSpeedService.class));

        /*Intent intent1 = new Intent(context, TestActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);*/
    }

}