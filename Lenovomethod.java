package com.ljlVink.core.core;


import android.app.admin.DevicePolicyManager;
import android.app.csdk.CSDKManager;
import android.app.mia.MiaMdmPolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.huosoft.wisdomclass.linspirerdemo.AR;
import com.ljlVink.Activity.AppManageActivity;
import com.ljlVink.MDM;
import com.ljlVink.utils.Sysutils;
import com.ljlVink.utils.Toast;
import com.ljlVink.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class Lenovomethod {
    Context context;
    private int MMDM=-1;
    int Lenovo_Mia=3;
    int Lenovo_Csdk=2;
    private CSDKManager csdkManager;
    private MiaMdmPolicyManager miaMdmPolicyManager;
    ComponentName testDeviceAdmin;
    DevicePolicyManager dpm;

    private final static String launcher="com.android.launcher3";
    private final static String execmdsvc="com.drupe.swd.launcher.huoshan.mdm.service.ExecuteCmdService";
    public boolean isSystemApplication(){
        PackageManager mPackageManager = context.getPackageManager();
        try {
            final PackageInfo packageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)!=0){
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Lenovomethod(Context context){
        this.context=context;
        MMDM=new MDM(context).MDM();
        if (MMDM==Lenovo_Csdk){
            csdkManager=new CSDKManager(context);
        }
        else if (MMDM==Lenovo_Mia){
            miaMdmPolicyManager=new MiaMdmPolicyManager(context);
        }
        testDeviceAdmin = new ComponentName(context, AR.class);
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    }
    public boolean isDeviceAdminActive(){ return dpm.isAdminActive(testDeviceAdmin); }
    public boolean isDeviceOwnerActive(){ return dpm.isDeviceOwnerApp(context.getPackageName()); }
    public boolean isDeviceOwnerActive(String packagename) { return dpm.isDeviceOwnerApp(packagename); }
    public void disablenotify(boolean isdisable){
        if(MMDM==Lenovo_Csdk){
            try{
                csdkManager.disableStatusBarNotification(isdisable);
            }catch (Throwable ignore){}
        }
    }
    public String getLenovo_version(){
        return "20220810patch03";
    }
    public void initSecondHack(){
        if(MMDM==Lenovo_Csdk){
            try{csdkManager.disableBluetooth(false);}catch(Exception e){}
            try{csdkManager.disableStatusBarPanel(false);}catch(Exception e){}
            try{csdkManager.disallowSetNotificationVolume_v3(false);}catch(Exception e){}
            try{csdkManager.disableBluetoothShare(false);}catch(Exception e){}
            try{csdkManager.setSafeModeDisabled(false);}catch(Exception e){}
            try{csdkManager.disableHiddenGame(false);}catch(Exception e){}
            try{csdkManager.disableWifiDirect(false);}catch(Exception e){}
            try{csdkManager.enableDevMode(true);}catch(Exception e){}
            try{csdkManager.disableWifi(false);}catch(Exception e){}
            try{csdkManager.disableMultiUser(false);}catch(Exception e){}
            try{csdkManager.enableUsbDebugging(true);}catch(Exception e){}
            try{csdkManager.setPackageEnabled(launcher, false);}catch(Exception e) {}
            try{csdkManager.hideBackSoftKey(false);}catch (Exception e){}
            try{thread_control_firewall(false);}catch(Exception e){}
            try{csdkManager.setCustomOTG(true);}catch(Exception e){}
            try{csdkManager.enableMassStorage(true);}catch(Exception e){ }
            try{csdkManager.setCurrentUsbMode(1);}catch (Exception e){}
            try{csdkManager.allowTFcard(true);}catch(Throwable e){}
            disablenotify(false);
            fix_csdk_component();

        }
        if(MMDM==Lenovo_Mia){
            try{
                miaMdmPolicyManager.setUsbOnlyCharging(false);
                miaMdmPolicyManager.allowBluetoothDataTransfer(true);
                miaMdmPolicyManager.allowBluetooth(true);
                miaMdmPolicyManager.setHomeKey(false);
                miaMdmPolicyManager.setStatusBar(false);
                try{miaMdmPolicyManager.controlApp(launcher,true); }
                catch (Exception e){ }
                thread_control_firewall(false);
                try{miaMdmPolicyManager.setTFcard(false);}catch (Exception e){ }
            }catch (Exception e){ }
        }

    }
    public void Lenovo_clear_whitelist_app(){
        if(MMDM==Lenovo_Mia){
            miaMdmPolicyManager.setInstallPackages(null);
        }else if(MMDM==Lenovo_Csdk){
            csdkManager.removeInstallPackageWhiteList(csdkManager.getInstallPackageWhiteList());
        }
    }
    public void thread_control_firewall(boolean enable){
        if(DataUtils.readint(context,"no_firewall_ctrl",0)==1){
            return;
        }
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("lspdemo","--------------------------");
                try{
                    if(MMDM==Lenovo_Csdk){
                        csdkManager.SetEnable(enable);
                    }else if(MMDM==Lenovo_Mia){
                        miaMdmPolicyManager.urlSetEnable(enable);
                    }
                }catch (Throwable th){

                }
                Log.e("lspdemo","-------finish--------------");
            }
        });
        th.run();
    }
    public  void iceapp(String icename,boolean isice){
        if (MMDM == Lenovo_Csdk) {
            try {
                csdkManager.setPackageEnabled(icename, !isice);
            } catch (Exception e) {
            }

        } else if (MMDM == Lenovo_Mia) {
            try {
                miaMdmPolicyManager.controlApp(icename, isice);
            } catch (Exception e) {
            }
        }
    }
    public void mia_mdm_addDisallowedUninstallPackages(ArrayList<String> list){
        miaMdmPolicyManager.addDisallowedUninstallPackages(list);
    }
    public void mia_mdm_removeDisallowedUninstallPackages(ArrayList<String> list){
        miaMdmPolicyManager.removeDisallowedUninstallPackages(list);
    }
    public void killApplicationProcess(String pkgname){
        if(context.getPackageName().equals(pkgname)){
            return;
        }
        if(MMDM==Lenovo_Csdk){
            csdkManager.killApplicationProcess(pkgname);
        }
        if (MMDM==Lenovo_Mia){
            miaMdmPolicyManager.killApplicationProcess(pkgname);
        }
    }
    public void installapp(String path) {
        if (MMDM == Lenovo_Csdk) {
            csdkManager.installPackage(path);
        } else if (MMDM == Lenovo_Mia) {
            miaMdmPolicyManager.silentInstall(path);
        }
    }
    public void addInstallPackageWhiteList(String pkg){
        if(MMDM==Lenovo_Csdk){
            ArrayList list2;
            list2 = (ArrayList) csdkManager.getInstallPackageWhiteList();
            list2.add(pkg+miahash());
            csdkManager.addInstallPackageWhiteList(list2);

        }
        else if (MMDM==Lenovo_Mia){
            ArrayList list2;
            list2 = (ArrayList) miaMdmPolicyManager.getInstallPackageWhiteList();
            list2.add(pkg+miahash());
            miaMdmPolicyManager.addInstallPackages(list2);

        }
    }
    public String Get_Lenovo_app_whitelist(){
        if(MMDM==Lenovo_Mia){
            return miaMdmPolicyManager.getInstallPackageWhiteList().toString();
        }
        else if(MMDM==Lenovo_Csdk){
            return csdkManager.getInstallPackageWhiteList().toString();
        }
        return "";
    }
    public String getCSDK_sn_code(){
        if (MMDM== Lenovo_Csdk){
            return csdkManager.getDeviceInfo(2);
        }
        return "null";
    }
    public void  mia_setpasswd(String str){
        if(MMDM==Lenovo_Mia){
            try {
                miaMdmPolicyManager.setLockPassword(str);
            }catch (Exception e ){
            }
        }

    }
    public void backtolsp(){
        int enablelspForBJSZ=DataUtils.readint(context,"bjsz_mode",0);
        if(Sysutils.FindLspDemoPkgName(context,"assistlauncher").contains(DataUtils.readStringValue(context,"desktop_pkg",""))){
            enablelspForBJSZ=1;
        }else enablelspForBJSZ=0;
        if(MMDM==Lenovo_Csdk){
            try{csdkManager.setHomeKey(false);}catch (Throwable e){}
            try{thread_control_firewall(true);}catch (Throwable e){}
            if(enablelspForBJSZ==0)
            try{csdkManager.setPackageEnabled(launcher, true); } catch (Throwable e) { }
            try{csdkManager.hideHomeSoftKey(false);}catch (Throwable e){}
            try{csdkManager.disableBluetooth(true);}catch (Throwable e){}
            try{csdkManager.disableBluetoothShare(true);}catch (Throwable e){}
            if(enablelspForBJSZ==1){
                try{
                    csdkManager.setCustomLauncher(DataUtils.readStringValue(context,"desktop_pkg",""),"com.lspdemo.assistlauncher.MainActivity");
                }catch ( Exception e){}
            }
        }
        if(MMDM==Lenovo_Mia){
            try{miaMdmPolicyManager.setHomeKey(true);}catch (Throwable e){}
            if(enablelspForBJSZ==0)
            try{miaMdmPolicyManager.controlApp(launcher,false); } catch (Throwable e){ }
            try{thread_control_firewall(true);}catch (Throwable e){}
            try{miaMdmPolicyManager.setHomeKey(false);}catch (Throwable e){}
            try{miaMdmPolicyManager.allowBluetooth(false);}catch (Throwable e){}
            try{miaMdmPolicyManager.allowBluetoothDataTransfer(false);}catch (Throwable e){}
            if(enablelspForBJSZ==1){
                try{
                    miaMdmPolicyManager.setCustomLauncher(DataUtils.readStringValue(context,"desktop_pkg",""));
                }catch ( Exception e){}
            }

        }

    }
    public void csdk_setlauncher(String pkgname,String compname){
        try{
            csdkManager.setCustomLauncher(pkgname, compname);
        }
        catch (Exception E){

        }
    }
    public void fix_csdk_component(){
        if (MMDM==Lenovo_Csdk){
            try{
                csdkManager.setComponentEnabled(new ComponentName("com.android.settings","com.android.settings.password.ChooseLockPassword"),0,0);
            }catch (Throwable ignore){
            }
        }

    }
    public void hack_deviceowner(){
        if(!dpm.isDeviceOwnerApp(context.getPackageName())){
            if(MMDM==Lenovo_Csdk){
                try {
                    csdkManager.setDeviceOwner(context.getPackageName() + "/com.huosoft.wisdomclass.linspirerdemo.AR");
                }catch (Exception e){
                }
                if(!dpm.isDeviceOwnerApp(context.getPackageName())){
                    ArrayList<String>arr=Sysutils.FindLspDemoPkgName(context,"Hackmdm");
                    for(int i=0;i<arr.size();i++){
                        if(dpm.isDeviceOwnerApp(arr.get(i))){
                            return;
                        }
                    }
                    PackageManager pm = context.getPackageManager();
                    List<PackageInfo> packages = pm.getInstalledPackages(0);
                    for (PackageInfo packageInfo : packages) {
                        if(dpm.isDeviceOwnerApp(packageInfo.packageName)){
                            Toast.ShowInfo(context,"抓到了:"+packageInfo.packageName+",准备解除owner");
                            try {
                                csdkManager.removeDeviceOwner(packageInfo.packageName);
                            }catch (Exception e){}
                        }
                    }
                    try {
                        csdkManager.setDeviceOwner(context.getPackageName() + "/com.huosoft.wisdomclass.linspirerdemo.AR");
                        if(!isDeviceOwnerActive()){
                            Toast.ShowErr(context,"自动激活失败 请检查");
                        }
                    }catch (Exception e){
                    }
                    restartApplication();
                }
                else {
                    Toast.ShowSuccess(context,"自动注入成功");
                }
            }

        }
    }
    public void restartApplication() {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void wash_whitelist(){
        if(MMDM==Lenovo_Csdk){
            csdkManager=new CSDKManager(context);
            ArrayList list4 =new ArrayList<>();//架空白名单
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    if(launcher.equals(packageInfo.packageName)||"com.ndwill.swd.appstore".equals(packageInfo.packageName)){
                        continue;
                    }
                    list4.add(packageInfo.packageName+miahash());
                }
            }//获取包名
            if(isSystemApplication()){
                list4.add(context.getPackageName()+miahash());
            }
            csdkManager.removeInstallPackageWhiteList(csdkManager.getInstallPackageWhiteList());
            csdkManager.addInstallPackageWhiteList(list4);
        }
        else if(MMDM==Lenovo_Mia){
            miaMdmPolicyManager=new MiaMdmPolicyManager(context);
            ArrayList list4=new ArrayList<>();//架空白名单
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    if(launcher.equals(packageInfo.packageName)||"com.ndwill.swd.appstore".equals(packageInfo.packageName)){
                        continue;
                    }
                    list4.add(packageInfo.packageName+miahash());
                }
            }
            if(isSystemApplication()){
                list4.add(context.getPackageName()+miahash());
            }
            miaMdmPolicyManager.removeInstallPackages(miaMdmPolicyManager.getInstallPackageWhiteList());
            miaMdmPolicyManager.addInstallPackages(list4);
        }
    }
    public String miahash(){
        if(DataUtils.readint(context,"miahash_add",0)==2){
            return "";
        }
         if(DataUtils.readint(context,"miahash_add",0)==1){
            return ";miahash";
        }
         if(DataUtils.readint(context,"miahash_add",0)==0){
            if(MMDM==Lenovo_Csdk){
                if(Build.DISPLAY.contains("2108")){
                    return ";miahash";
                }
            }
            else if(MMDM==Lenovo_Mia){
                return ";miahash";
            }
        }
        return ";miahash";
    }
}
