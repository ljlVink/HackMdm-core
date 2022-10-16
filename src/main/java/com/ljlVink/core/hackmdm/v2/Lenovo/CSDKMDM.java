package com.ljlVink.core.hackmdm.v2.Lenovo;

import android.app.csdk.CSDKManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.ljlVink.core.hackmdm.v2.DataUtils;
import com.ljlVink.core.hackmdm.v2.GenericMDM;
import com.ljlVink.Util.Sysutils_1;
import com.lzf.easyfloat.EasyFloat;

import java.util.ArrayList;
import java.util.List;

public class CSDKMDM extends GenericMDM {
    public static CSDKMDM LenovoCSDK;
    private CSDKManager csdk;
    public Context mContext;

    private CSDKMDM(Context context){
        this.mContext=context;
        csdk=new CSDKManager(context);
    }
    @Override
    public void initSecondHack(){
        try{csdk.disableBluetooth(false);}catch(Throwable ignore){}
        try{csdk.disableStatusBarPanel(false);}catch(Throwable ignore){}
        try{csdk.disallowSetNotificationVolume_v3(false);}catch(Throwable ignore){}
        try{csdk.disableBluetoothShare(false);}catch(Throwable ignore){}
        try{csdk.setSafeModeDisabled(false);}catch(Throwable ignore){}
        try{csdk.disableHiddenGame(false);}catch(Throwable ignore){}
        try{csdk.disableWifiDirect(false);}catch(Throwable ignore){}
        try{csdk.enableDevMode(true);}catch(Throwable ignore){}
        try{csdk.disableWifi(false);}catch(Throwable ignore){}
        try{csdk.disableMultiUser(false);}catch(Throwable ignore){}
        try{csdk.enableUsbDebugging(true);}catch(Throwable ignore){}
        try{csdk.setPackageEnabled("com.android.launcher3", false);}catch(Throwable ignore) {}
        try{csdk.hideBackSoftKey(false);}catch (Throwable ignore){}
        try{enableFirewall(false);}catch(Throwable ignore){}
        try{csdk.setCustomOTG(true);}catch(Throwable ignore){}
        try{csdk.enableMassStorage(true);}catch(Throwable ignore){ }
        try{csdk.setCurrentUsbMode(1);}catch (Throwable ignore){}
        try{csdk.allowTFcard(true);}catch(Throwable ignore){}
        try{csdk.disableStatusBarNotification(false);}catch (Throwable ignore){}
        try{csdk.setComponentEnabled(new ComponentName("com.android.settings","com.android.settings.password.ChooseLockPassword"),0,0);}catch (Throwable ignore){}
        try{csdk.setComponentEnabled(new ComponentName("com.android.settings","com.android.settings.password.ChooseLockGeneric"),0,0);}catch (Throwable ignore){}

    }
    @Override
    public int getCurrentMDM(){
        return 2;
    }
    @Override
    public String getMDMName() {
        return "CSDK";
    }
    @Override
    public String getMacAddr(){
        return csdk.getDeviceInfo(1);
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
        try{csdk.setHomeKey(false);}catch (Throwable e){}
        enableFirewall(true);
        if(enablelspForBJSZ==0) try{iceApp("com.android.launcher3",false); } catch (Throwable e) { }
        try{csdk.hideHomeSoftKey(false);}catch (Throwable e){}
        try{csdk.disableBluetooth(true);}catch (Throwable e){}
        try{csdk.disableBluetoothShare(true);}catch (Throwable e){}
        if(enablelspForBJSZ==1){
            try{
                csdk.setCustomLauncher(DataUtils.readStringValue(mContext,"desktop_pkg",""),"com.lspdemo.assistlauncher.MainActivity");
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
    public void initHack(int opt){
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                initSecondHack();
                classOver();
                if(opt==0){
                    wash_whitelist();
                    forceActiveDeviceOwner();
                    disable_factory();
                }
                pullDown_app();
                blockUninstall(new ArrayList<>());
                Looper.loop();
            }
        });
        th.start();
    }

    public static CSDKMDM getInstance(Context context){
        if(LenovoCSDK==null){
            LenovoCSDK=new CSDKMDM(context);
        }
        return LenovoCSDK;
    }
    @Override
    public void AppWhiteList_add(String appName){
        ArrayList<String> list2;
        list2 = (ArrayList<String>) csdk.getInstallPackageWhiteList();
        list2.add(appName+miahash_lenovo());
        csdk.addInstallPackageWhiteList(list2);
    }
    @Override
    public void setDefaultLauncher(ComponentName cn){
        try{
            csdk.setCustomLauncher(cn.getPackageName(), cn.getClassName());
        }
        catch (Exception ignore){}
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
                            csdk.killApplicationProcess(packageInfo.packageName);
                        }
                    }
                }
                Looper.loop();
            }
        });
        thread.start();
    }
    @Override
    public void DisableRecentKey(){
        try{
            csdk.hideMenuSoftKey(true);
        }catch (Throwable ignore){
        }
    }
    @Override
    public void EnableRecentKey(){
        try{
            csdk.hideMenuSoftKey(false);
        }catch (Throwable ignore){
        }
    }
    @Override
    public void classOver(){
        try{
            csdk.hideHomeSoftKey(false);
            csdk.releaseKeyControl();
            csdk.hideMenuSoftKey(false);
            csdk.hideNavigationBar(false);
            csdk.hideStatusBar(false);
            csdk.fullScreenForever(false);
        }catch (Throwable ignore){}
    }
    @Override
    public void clear_whitelist_app_lenovo(){
        try{
            csdk.removeInstallPackageWhiteList(csdk.getInstallPackageWhiteList());
        }catch (Throwable ignore){}
    }
    public void forceActiveDeviceOwner(){
        try {
            csdk.setDeviceOwner(mContext.getPackageName() + "/com.huosoft.wisdomclass.linspirerdemo.AR");
        }catch (Exception e){
        }
        if(!dPm.isDeviceOwnerApp(mContext.getPackageName())){
            ArrayList<String> arr= Sysutils_1.FindLspDemoPkgName(mContext,"linspirerdemo");
            for(int i=0;i<arr.size();i++){
                if(dPm.isDeviceOwnerApp(arr.get(i))){
                    return;
                }
            }
            PackageManager pm = mContext.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                if(dPm.isDeviceOwnerApp(packageInfo.packageName)){
                    try {
                        csdk.removeDeviceOwner(packageInfo.packageName);
                    }catch (Exception e){}
                }
            }
            try {
                csdk.setDeviceOwner(mContext.getPackageName() + "/com.huosoft.wisdomclass.linspirerdemo.AR");
                if(!isDeviceOwnerActive()){
                }
            }catch (Exception e){
            }
        }
    }
    @Override
    public void wash_whitelist(){
        ArrayList<String> list4 =new ArrayList<>();
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
        csdk.removeInstallPackageWhiteList(csdk.getInstallPackageWhiteList());
        csdk.addInstallPackageWhiteList(list4);
    }
    @Override
    public String getAppWhitelist() {
        wash_whitelist();
        return csdk.getInstallPackageWhiteList().toString();
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
            if(Build.DISPLAY.contains("2108")){
                return ";miahash";
            }
        }
        return ";miahash";
    }

    @Override
    public void enableFirewall(boolean enable){
        if(DataUtils.readint(mContext,"no_firewall_ctrl",0)==1){
            return;
        }
        if(enable){
            try{
                if(!csdk.isEnable()){
                    csdk.SetEnable(enable);
                }
            }catch (Throwable ignore){
            }
        }
        else {
            try{
                if(csdk.isEnable()){
                    csdk.SetEnable(enable);
                }
            }
            catch (Throwable ignore){
            }
        }

    }
    @Override
    public void killApplicationProcess(String pkg){
        try{
            csdk.killApplicationProcess(pkg);
        }catch (Exception e){
        }
    }
    @Override
    public void iceApp(String app,boolean isIce){
        try {
            csdk.setPackageEnabled(app, !isIce);
        } catch (Exception e) {
            try{
                dPm.setApplicationHidden(admin,app,false);
            }catch (Throwable ignore){}
        }
    }
    @Override
    public String getSerialCode(){
        if(csdk.getDeviceInfo(2)!=null){
            return csdk.getDeviceInfo(2);
        }
        return "null";
    }
    @Override
    public void installApp(String apk){
        try{
            csdk.installPackage(apk);
        }catch (Exception e){

        }
    }
}
