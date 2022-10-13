package com.ljlVink.core.hackmdm.v2;

import android.app.csdk.CSDKManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import com.ljlVink.core.hackmdm.v2.Lenovo.CSDKMDM;

import java.util.ArrayList;
import java.util.List;

public class Supi extends GenericMDM {
    public static Supi SupiMDM;
    public Context mContext;

    private Supi(Context context){
        this.mContext=context;
    }
    @Override
    public void killApplicationProcess(String name){
        RootCMD("am force-stop "+name);
    }
    @Override
    public void installApp(String path) {
        Intent intent = new Intent("com.tensafe.app.onerun.fun.slient_installpackage_pm");
        intent.setPackage("com.topjohnwu.magisk");
        intent.putExtra("pkgpath", path);
        mContext.sendBroadcast(intent);

    }
    @Override
    public void killApplicationProcess(ArrayList<String> notkill) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                PackageManager pm1 = mContext.getPackageManager();
                List<PackageInfo> packages1 = pm1.getInstalledPackages(0);
                for (PackageInfo packageInfo : packages1) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                        if(!mContext.getPackageName().equals(packageInfo.packageName)&&!notkill.contains(packageInfo.packageName)){
                            killApplicationProcess(packageInfo.packageName);
                        }
                    }
                }
                Looper.loop();
            }
        });
        thread.start();
    }

    @Override
    public String getAppWhitelist() {
        ArrayList<String> list1=new ArrayList<>();
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfox : packages) {
            if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                list1.add(packageInfox.packageName);
            }
        }
        return list1.toString();
    }
    public static Supi getInstance(Context context){
        if(SupiMDM==null){
            SupiMDM=new Supi(context);
        }
        return SupiMDM;
    }

    @Override
    public void iceApp(String appName,boolean isIce){
        if(isIce){
            RootCMD("pm disable "+appName);
        }else {
            RootCMD("pm enable "+appName);
        }

    }
    @Override
    public void initHack(int opt){
        RootCMD("sh /system/tshook/network.sh");
        RootCMD("iptables -X");
        RootCMD("setprop persist.sys.usb.config adb,mtp");
        Intent intent=new Intent("com.tensafe.sdcard");
        intent.setPackage("com.tensafe.app.onerun");
        intent.putExtra("disable",false);
        mContext.sendBroadcast(intent);
        Intent intent2=new Intent("com.tensafe.statusbar.setmode");
        intent2.setPackage("com.tensafe.app.onerun");
        intent2.putExtra("mode","none");
        mContext.sendBroadcast(intent2);
    }
    @Override
    public boolean RootCMD(String cmd){
        Intent intent = new Intent("com.tensafe.app.onerun.fun.sucmd");
        intent.setPackage("com.topjohnwu.magisk");
        intent.putExtra("argv", cmd);
        intent.putExtra("magic", "!@#$@ss2$");
        mContext.sendBroadcast(intent);
        super.RootCMD(cmd);
        return true;
    }
    @Override
    public int getCurrentMDM(){
        return 4;
    }

    @Override
    public String getMDMName() {
        return "supi_T11";
    }

}
