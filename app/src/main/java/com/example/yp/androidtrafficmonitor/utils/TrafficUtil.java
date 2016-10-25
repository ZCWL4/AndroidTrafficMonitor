package com.example.yp.androidtrafficmonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.util.Log;

import com.example.yp.androidtrafficmonitor.Dao.UidTrafficdDB;
import com.example.yp.androidtrafficmonitor.beans.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yp on 2016/10/11.
 */
public class TrafficUtil {

    public static int appAccount;
    public static ArrayList<AppInfo> appInfoArrayList;


    public synchronized static void startMonitor(Context context){
        Log.v("Flg---------------","startMonitor");
        AppInfo appInfo;
        appInfoArrayList = new ArrayList<>();
        //获取所有的安装在手机上的应用软件的信息，并且获取这些软件里面的权限信息
        PackageManager pm=context.getPackageManager();//获取系统应用包管理
        //获取每个包内的androidmanifest.xml信息，它的权限等等
        List<PackageInfo> pinfos=pm.getInstalledPackages
                ( PackageManager.GET_PERMISSIONS);
        //遍历每个应用包信息
        for(PackageInfo info:pinfos){
            //请求每个程序包对应的androidManifest.xml里面的权限
            String[] premissions=info.requestedPermissions;
            if(premissions!=null && premissions.length>0){
                //找出需要网络服务的应用程序
                for(String premission : premissions){
                    if("android.permission.INTERNET".equals(premission)){

                        appInfo = new AppInfo();
                        //获取每个应用程序在操作系统内的进程id
                        int uid = info.applicationInfo.uid;
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long rx= TrafficStats.getUidRxBytes(uid);
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long tx= TrafficStats.getUidTxBytes(uid);

                        String packeageName = info.applicationInfo.packageName;

                        appInfo.uid = uid;
                        appInfo.appName = ""+info.applicationInfo.loadLabel(pm);
                        appInfo.packageName = packeageName;
                        appInfo.traffic= rx+tx;

                       appInfoArrayList.add(appInfo);

                        /*if(UidTrafficdDB.isExistUid(context,appInfo)){
                            UidTrafficdDB.update(context,appInfo,"ViceTraffic");
                        }
                        else {


                            UidTrafficdDB.insert(context,appInfo,"ViceTraffic");
                        }*/

                        //Drawable appIcon = info.applicationInfo.loadIcon(context.getPackageManager());

//                        if(rx<0 || tx<0){
//                            continue;
//                        }else{
//                            //String result = result + info.applicationInfo.loadLabel(pm)+ Formatter.formatFileSize(this, rx+tx) + "\n";
//                        }
                    }
                }
            }
        }
        UidTrafficdDB.saveToMainDB(context,appInfoArrayList,"MainTraffic");
        //uidTraffic.setText(result);
    }

}
