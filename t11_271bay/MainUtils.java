package com.ljlVink.core.core.t11_271bay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ljlVink.utils.Toast;

public class MainUtils {
    private Context context;
    public MainUtils(Context context){
        this.context=context;
    }
    public String getversion(){
        return "20220909";
    }
    public void InitHack(){
        //开网
        RootCommand("sh /system/tshook/network.sh");
        RootCommand("iptables -X");

        //控制妞
        statusbar_mode("none");
        //adb mtp
        RootCommand("setprop persist.sys.usb.config adb,mtp");

        sdcardmode(false);
    }
    /**
     * mode分三种 all back none
     * */
    public void statusbar_mode(String mode){
        Intent intent=new Intent("com.tensafe.statusbar.setmode");
        intent.setPackage("com.tensafe.app.onerun");
        intent.putExtra("mode",mode);
        context.sendBroadcast(intent);
    }
    public void sdcardmode(boolean disable){
        Intent intent=new Intent("com.tensafe.sdcard");
        intent.setPackage("com.tensafe.app.onerun");
        intent.putExtra("disable",disable);
        context.sendBroadcast(intent);

    }
    public void disable_safectrlmn(){
        RootCommand("setprop persist.tensafe.device tensafe-dbg");
    }public void enable_safectrlmn(){
        RootCommand("setprop persist.tensafe.device lspdemo");
    }
    public void iceapp(String pkgname,boolean isice){
        if(isice){
            RootCommand("pm disable "+pkgname);
        }else {
            RootCommand("pm enable "+pkgname);
        }
    }
    public void InstallApp(String abspath){
        Intent intent = new Intent("com.tensafe.app.onerun.fun.slient_installpackage_pm");
        intent.setPackage("com.topjohnwu.magisk");
        intent.putExtra("pkgpath", abspath);
        context.sendBroadcast(intent);
    }
    public void RootCommand(String str){
        Intent intent = new Intent("com.tensafe.app.onerun.fun.sucmd");
        intent.setPackage("com.topjohnwu.magisk");
        intent.putExtra("argv", str);
        intent.putExtra("magic", "!@#$@ss2$");
        context.sendBroadcast(intent);
    }
    public void killapp(String pkgname){
        RootCommand("am force-stop "+pkgname);
    }
    public void FirstHack(){
        InitHack();

    }

}
