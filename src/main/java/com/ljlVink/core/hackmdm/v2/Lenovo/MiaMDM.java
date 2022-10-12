package com.ljlVink.core.hackmdm.v2.Lenovo;


import android.app.mia.MiaMdmPolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import com.ljlVink.core.hackmdm.v2.DataUtils;
import com.ljlVink.core.hackmdm.v2.GenericMDM;
import com.ljlVink.Util.Sysutils_1;
import com.lzf.easyfloat.EasyFloat;

import java.util.ArrayList;
import java.util.List;

public class MiaMDM extends GenericMDM {
    public static MiaMDM LenovoMia;
    public Context mContext;
    private MiaMdmPolicyManager mia;
    private MiaMDM (Context context){
        GenericMDM.getInstance(context);
        this.mContext=context;
        this.mia=new MiaMdmPolicyManager(context);
    }
    public static MiaMDM getInstance(Context context){
        if(LenovoMia==null){
            LenovoMia=new MiaMDM(context);
        }
        return LenovoMia;
    }
    @Override
    public String getMDMName() {
        return "Mia";
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
                            mia.killApplicationProcess(packageInfo.packageName);
                        }
                    }
                }
                Looper.loop();
            }
        });
        thread.start();
    }
    @Override
    public void AppWhiteList_add(String appName){
        ArrayList<String> list2;
        list2 = (ArrayList<String>) mia.getInstallPackageWhiteList();
        list2.add(appName+miahash_lenovo());
        mia.addInstallPackages(list2);
    }

    @Override
    public String miahash_lenovo(){
        if(DataUtils.readint(mContext,"miahash_add",0)==2){
            return "";
        }
        if(DataUtils.readint(mContext,"miahash_add",0)==1){
            return ";miahash";
        }
        if(DataUtils.readint(mContext,"miahash_add",0)==0){
            return ";miahash";
        }
        return ";miahash";
    }
    @Override
    public void initSecondHack() {
        try{
            mia.setUsbOnlyCharging(false);
            mia.allowBluetoothDataTransfer(true);
            mia.allowBluetooth(true);
            mia.setHomeKey(false);
            mia.setStatusBar(false);
            try{mia.controlApp("com.android.launcher3",true); } catch (Throwable ignore){ }
            enableFirewall(false);
            try{mia.setTFcard(false);}catch (Throwable ignore){ }
        }catch (Throwable ignore){}

    }

    @Override
    public void initHack(int opt){
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                initSecondHack();
                classOver();
                if(opt==0){
                    wash_whitelist();
                    disable_factory();
                }
                pullDown_app();
                blockUninstall(new ArrayList<>());
                Looper.loop();
            }
        });
        th.start();
    }

    @Override
    public void iceApp(String app,boolean isIce){
        try {
            mia.controlApp(app, isIce);
        } catch (Exception e) {
        }
    }
    @Override
    public void backToLSPDesktop() {
        wash_whitelist();
        blockUninstall(new ArrayList<>());
        pullUp_app();
        try{ EasyFloat.dismiss();}catch (Exception e){}
        int enablelspForBJSZ;
        if(Sysutils_1.FindLspDemoPkgName(mContext,"assistlauncher").contains(DataUtils.readStringValue(mContext,"desktop_pkg",""))){
            enablelspForBJSZ=1;
        }
        else enablelspForBJSZ=0;
        try{mia.setHomeKey(true);}catch (Throwable e){}
        if(enablelspForBJSZ==0) try{mia.controlApp("com.android.launcher3",false); } catch (Throwable e){ }
        try{enableFirewall(true);}catch (Throwable e){}
        try{mia.setHomeKey(false);}catch (Throwable e){}
        try{mia.allowBluetooth(false);}catch (Throwable e){}
        try{mia.allowBluetoothDataTransfer(false);}catch (Throwable e){}
        if(enablelspForBJSZ==1){
            try{
                mia.setCustomLauncher(DataUtils.readStringValue(mContext,"desktop_pkg",""));
            }catch ( Exception e){}
        }
        try{
            String desktop_pkgname=DataUtils.readStringValue(mContext,"desktop_pkg","");
            if(desktop_pkgname.equals("")||desktop_pkgname.equals("com.android.launcher3"))
                mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage("com.android.launcher3"));
            else{
                mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(desktop_pkgname));
            }
            if(DataUtils.readint(mContext,"bjsz_mode_disable_recent",0)==1){
                DisableRecentKey();
            }
        }catch (Exception ignore){}
    }

    @Override
    public void first_init(){
    }
    @Override
    public void pullUp_app(){
        ArrayList<String>li1test= DataUtils.ReadStringArraylist(mContext,"superapp");
        if(isDeviceOwnerActive()){
            return;
        }
        else{
            for (int i = 0; i < li1test.size(); i++) {
                iceApp(li1test.get(i),true);
            }
        }
    }
    @Override
    public void classOver() {
        try {
            mia.setNavigaBar(false);
        } catch (Throwable ignore) {
        }
        try{
            mia.setRecentKey(false);
            mia.setHomeKey(false);
            mia.setBackKey(false);
            mia.setVolumeupKey(false);
            mia.setVolumedownKey(false);
            mia.setPowerSingleClickKey(false);
        }catch (Throwable ignore){

        }

    }
    @Override
    public void DisableRecentKey() {
        try{
            mia.setRecentKey(true);
        }catch (Throwable ignore){

        }

    }
    @Override
    public void wash_whitelist(){
        ArrayList<String> list4=new ArrayList<>();//架空白名单
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                if("com.android.launcher3".equals(packageInfo.packageName)||"com.ndwill.swd.appstore".equals(packageInfo.packageName)){
                    continue;
                }
                list4.add(packageInfo.packageName+miahash_lenovo());
            }
        }
        if(Sysutils_1.isSystemApplication(mContext)){
            list4.add(mContext.getPackageName()+miahash_lenovo());
        }
        mia.removeInstallPackages(mia.getInstallPackageWhiteList());
        mia.addInstallPackages(list4);
    }
    @Override
    public String getAppWhitelist() {
        wash_whitelist();
        return mia.getInstallPackageWhiteList().toString();
    }
    @Override
    public void clear_whitelist_app_lenovo() {
        mia.setInstallPackages(null);
    }

    @Override
    public void killApplicationProcess(String pkg){
        try{
            mia.killApplicationProcess(pkg);
        }catch (Exception e){
        }
    }
    @Override
    public void enableFirewall(boolean enable) {
        try{
            mia.urlSetEnable(enable);
        }catch (Exception e){

        }
    }
    @Override
    public void installApp(String apk){
        try{
            mia.silentInstall(apk);
        }catch (Exception e){
        }
    }
    @Override
    public void setDevicePassword_lenovo_mia(String password){
        try {
            mia.setLockPassword(password);
        }catch (Exception e ){
        }
    }
}
