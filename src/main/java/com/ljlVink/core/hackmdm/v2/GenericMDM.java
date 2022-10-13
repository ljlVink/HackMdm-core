package com.ljlVink.core.hackmdm.v2;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huosoft.wisdomclass.linspirerdemo.AR;
import com.ljlVink.Util.Sysutils_1;
import com.lzf.easyfloat.EasyFloat;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GenericMDM implements MDMInterface{
    private final static String launcher="com.android.launcher3";
    private final static String execmdsvc="com.drupe.swd.launcher.huoshan.mdm.service.ExecuteCmdService";
    public static Context mContext;
    public static DevicePolicyManager dPm;
    public static GenericMDM genericMDM;
    public static ComponentName admin;

    public static GenericMDM getInstance(Context context){
        if(genericMDM==null){
            genericMDM=new GenericMDM();
            mContext=context;
            dPm=(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            admin=new ComponentName(context, AR.class);
        }
        return genericMDM;
    }
    @Override
    public void first_init(){

    }
    @Override
    public void disableBluetooth() {
        if(isDeviceOwnerActive()){
            try{
                dPm.addUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH);
                dPm.addUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH_SHARING);
                dPm.addUserRestriction(admin, UserManager.DISALLOW_CONFIG_BLUETOOTH);
            }catch (Exception e){

            }
        }
    }

    @Override
    public void enableBluetooth() {
        if(isDeviceOwnerActive()){
            try{
                dPm.clearUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH);
                dPm.clearUserRestriction(admin, UserManager.DISALLOW_BLUETOOTH_SHARING);
                dPm.clearUserRestriction(admin, UserManager.DISALLOW_CONFIG_BLUETOOTH);
            }catch (Exception e){

            }
        }
    }

    @Override
    public void wash_whitelist() {
        //only Lenovo
    }

    @Override
    public void active_DeviceAdmin() {
        try{
            if(!dPm.isAdminActive(admin)){

                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra (DevicePolicyManager.EXTRA_DEVICE_ADMIN,admin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活deviceadmin防被卸载");
                mContext.startActivity(intent);
            }
        }catch (Exception E){
            E.printStackTrace();
        }
    }
    @Override
    public void hw_hidesettings(String str){
        settings_putString("settings_menus_remove",str);
    }

    @Override
    public int getCurrentMDM() {
        return -1;
    }

    @Override
    public void settings_enable_adb(boolean enable){
        settings_putInt("adb_enabled",enable?1:0);
        settings_putInt("development_settings_enabled",enable?1:0);
    }

    @Override
    public boolean settings_putInt(String name,int val) {
        try{
            Settings.Global.putInt(mContext.getContentResolver(),name,val);
        }catch (Exception r){
            return false;
        }
        return true;

    }

    @Override
    public boolean settings_putString(String name,String val) {
        try{

        }catch (Exception r){
            return false;
        }
        return true;
    }

    @Override
    public boolean isDeviceAdminActive() {
        return dPm.isAdminActive(admin);
    }

    @Override
    public boolean isDeviceOwnerActive() {
        return dPm.isDeviceOwnerApp(mContext.getPackageName());
    }

    @Override
    public boolean isDeviceOwnerActive(String PackageName) {
        return dPm.isDeviceOwnerApp(PackageName);
    }

    @Override
    public void initHack(int opt) {
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                classOver();
                if(opt==0){
                    active_DeviceAdmin();
                    disable_factory();
                }
                pullDown_app();
                blockUninstall(new ArrayList<>());
                hack_into_generic_mdm_with_Linspirer();
                hack_into_generic_mdm_with_linspirer_miemie();
                Looper.loop();
            }
        });
        th.start();
    }


    @Override
    public void initSecondHack() {

    }

    @Override
    public void disable_factory() {
        if(isDeviceOwnerActive()){
            dPm.addUserRestriction(admin, UserManager.DISALLOW_FACTORY_RESET);
        }

    }

    @Override
    public void pullDown_app() {
        ArrayList<String>li1test=DataUtils.ReadStringArraylist(mContext,"superapp");
        if (isDeviceOwnerActive()){
            for (int i = 0; i < li1test.size(); i++) {
                dPm.setApplicationHidden(admin,li1test.get(i),false);
            }
        }
        else {
            Intent intent4 = new Intent();
            intent4.setPackage("com.android.launcher3");
            intent4.setAction("com.linspirer.edu.enableapp");
            intent4.putExtra("appwhitelist",li1test);
            mContext.sendBroadcast(intent4);

        }
    }

    @Override
    public void pullUp_app() {
        ArrayList<String>Applist=DataUtils.ReadStringArraylist(mContext,"superapp");
        if (dPm.isDeviceOwnerApp(mContext.getPackageName())){
            for (int i = 0; i < Applist.size(); i++) {
                dPm.setApplicationHidden(admin,Applist.get(i),true);
            }
        }else {
            if(!isEMUI10UnlockedDevice()){
                Intent intent4 = new Intent();
                intent4.setPackage("com.android.launcher3");
                intent4.setAction("com.linspirer.edu.disableapp");
                intent4.putExtra("appwhitelist",Applist);
                mContext.sendBroadcast(intent4);
            }
        }
    }

    @Override
    public void enableStatusBar() {
        if(isDeviceOwnerActive()){
            dPm.setStatusBarDisabled(admin,false);
        }

    }

    @Override
    public void disableStatusBar() {
        if(isDeviceOwnerActive()){
            dPm.setStatusBarDisabled(admin,true);
        }
    }

    @Override
    public String getMDMName() {
        return "DevicePolicyManager_GenericMDM";
    }

    @Override
    public void setDefaultLauncher(ComponentName cn) {
        if(!isDeviceOwnerActive()){
            return;
        }try{
            IntentFilter filter = new IntentFilter("android.intent.action.MAIN");
            filter.addCategory("android.intent.category.HOME");
            filter.addCategory("android.intent.category.DEFAULT");
            dPm.clearPackagePersistentPreferredActivities(admin,"com.huawei.android.launcher");
            dPm.addPersistentPreferredActivity(admin,filter,cn);
        }catch (Exception e){

        }

    }

    @Override
    public boolean RootCMD(String cmd) {

        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) { }
        }
        return true;
    }

    @Override
    public void hack_into_generic_mdm_with_Linspirer() {
        sendBackDoorLINS("command_camera",1);
        sendBackDoorLINS("command_connect_usb",1);
        sendBackDoorLINS("command_bluetooth",1);
        sendBackDoorLINS("command_otg",1);
        try{
            Intent intent2 = new Intent();
            intent2.setPackage(launcher);
            intent2.setAction("com.linspirer.edu.disablefirewall");
            mContext.sendBroadcast(intent2);
        }catch (Exception e){}
        if(!isEMUI10UnlockedDevice()){
            ArrayList<String> apps=new ArrayList<>();
            apps.add("com.android.email");
            apps.add("com.huawei.hidisk");
            apps.add("com.huawei.android.launcher");
            apps.add("com.huawei.vassistant");
            apps.add("com.google.android.marvin.talkback");
            apps.add("com.huawei.android.thememanager");
            apps.add("com.android.browser");
            apps.add("com.huawei.screenrecorder");
            apps.add("com.huawei.hiview");
            apps.add("com.huawei.systemmanager");
            apps.add("com.huawei.hwmwlauncher");
            apps.add("com.huawei.hnreader");
            apps.add("com.huawei.hwread.al");
            apps.add("com.huawei.hitouch");
            apps.add("com.huawei.android.tips");
            apps.add("com.huawei.hiai");
            apps.add("com.huawei.hwid");
            apps.add("com.huawei.android.findmyphone");
            apps.add("com.huawei.appmarket");
            apps.add("com.huawei.wallet");
            apps.add("com.huawei.fans");
            apps.add("com.myscript.calculator.huawei");
            apps.add("com.huawei.email");
            apps.add("com.hicloud.android.clone");
            apps.add("com.huawei.fastapp");
            apps.add("com.bjbyhd.screenreader_huawei");
            apps.add("com.huawei.kidsmode");
            apps.add("com.huawei.educenter");
            apps.add("com.vipkid.studypad");
            apps.add("com.youban.xblertongpbhw");
            apps.add("com.sinyee.babybus.talk2kiki");
            apps.add("com.huawei.search");
            apps.add("com.huawei.gamebox");
            apps.add("com.huawei.meetime");
            apps.add("com.huawei.android.instantshare");
            apps.add("com.huawei.android.airsharing");
            apps.add("com.huawei.phoneservice");
            apps.add("com.hihonor.phoneservice");
            apps.add("com.hihonor.hidisk");
            apps.add("com.hihonor.vassistant");
            apps.add("com.android.quicksearchbox");
            apps.add("com.hihonor.wallet");
            apps.add("com.hihonor.email");
            apps.add("com.hihonor.kidsmode");
            apps.add("com.hihonor.gamebox");
            apps.add("com.sec.android.app.camera");
            apps.add("com.sec.android.app.myfiles");
            apps.add("com.sec.android.app.launcher");
            apps.add("com.sec.android.app.samsungapps");
            apps.add("com.samsung.android.email.provider");
            apps.add("com.sec.android.app.sbrowser");
            apps.add("com.samsung.android.onlinevideo");
            apps.add("com.sec.android.app.music");
            apps.add("com.samsung.android.sm");
            apps.add("com.sec.android.app.popupcalculator");
            Intent intent4 = new Intent();
            intent4.setPackage("com.android.launcher3");
            intent4.setAction("com.linspirer.edu.enableapp");
            intent4.putExtra("appwhitelist",apps);
            mContext.sendBroadcast(intent4);
        }
        if(isEMUI10UnlockedDevice()){
            return;
        }
        Intent intent4 = new Intent();
        intent4.setPackage(launcher);
        intent4.setAction("com.linspirer.edu.setappwhitelist");
        ArrayList<String>applist=new ArrayList<>();
        ArrayList<String>network_applist=new ArrayList<>();
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfox : packages) {
            if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                if(launcher.equals(packageInfox.packageName)||"com.ndwill.swd.appstore".equals(packageInfox.packageName)){
                    continue;
                }
                applist.add(packageInfox.packageName);
                network_applist.add(packageInfox.packageName);
            }
            else if(DataUtils.readint(mContext,"allow_system_internet")==1){
                network_applist.add(packageInfox.packageName);
            }
        }
        intent4.putExtra("appwhitelist",applist);
        mContext.sendBroadcast(intent4);
        //网络白名单
        Intent intent5 = new Intent();
        intent5.setPackage("com.android.launcher3");
        intent5.setAction("com.linspirer.edu.setappnetwhitelist");
        applist.add(launcher);
        applist.add("com.ndwill.swd.appstore");
        intent5.putExtra("appnetwhitelist",network_applist);
        mContext.sendBroadcast(intent5);
        //放导航，截屏
        Intent intent6 = new Intent();
        intent6.setPackage("com.android.launcher3");
        intent6.setAction("com.linspirer.edu.enablenavigationbar");
        mContext.sendBroadcast(intent6);
        intent6.setAction("com.linspirer.edu.enable.screenshot");
        mContext.sendBroadcast(intent6);
        intent6.setAction("com.linspirer.edu.enable.changewallpaper");
        mContext.sendBroadcast(intent6);
        intent6.setAction("com.linspirer.edu.enableotg");
        mContext.sendBroadcast(intent6);
    }
    @Override
    public void hack_into_generic_mdm_with_linspirer_miemie() {
        Intent intent=new Intent();
        intent.setClassName("com.linspirer.sheepmie","com.linspirer.receiver.SheepMieReceiver");
        intent.setAction("com.linspirer.sheepmie_enableusb");
        mContext.sendBroadcast(intent);
    }
    @Override
    public String getSerialCode() {
        return  "null";
    }

    @Override
    public String getMacAddr() {
        return "null";
    }

    @Override
    public void sendBackDoorLINS(String code, int active) {
        Intent huawei =new Intent();
        huawei.setAction(execmdsvc);
        huawei.putExtra("cmd",code);
        huawei.putExtra("active",active);//optional
        huawei.setPackage(launcher);
        mContext.startService(huawei);
    }

    @Override
    public String getAppWhitelist() {
        ArrayList<String>list1=new ArrayList<>();
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfox : packages) {
            if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                if(launcher.equals(packageInfox.packageName)||"com.ndwill.swd.appstore".equals(packageInfox.packageName)){
                    continue;
                }
                list1.add(packageInfox.packageName+"[white]");
            }else if(DataUtils.readint(mContext,"allow_system_internet")==1){
                list1.add(packageInfox.packageName+"[net]");
            }
        }
        return list1.toString();
    }

    @Override
    public void installApp(String apkpath) {
        Intent intent4 = new Intent();
        intent4.setPackage("com.android.launcher3");
        intent4.setAction("com.linspirer.edu.silentinstall");
        intent4.putExtra("path",apkpath);
        mContext.sendBroadcast(intent4);
    }

    @Override
    public void unblockApp(String packageName) {
        if (dPm.isDeviceOwnerApp(mContext.getPackageName())){
            dPm.setUninstallBlocked(admin,packageName,false);
        }
    }

    @Override
    public void killApplicationProcess(ArrayList<String> notKill) {

    }

    @Override
    public void killApplicationProcess(String packageName) {

    }
    @Override
    public String getVersion(){
        return "v2.20221013.Release";
    }

    @Override
    public void iceApp(String appName, boolean isIce) {
        if(appName.equals(mContext.getPackageName())){
            return;
        }
        if(!TextUtils.isEmpty(appName)) {
            if(isDeviceOwnerActive()){
                dPm.setApplicationHidden(admin,appName,isIce);
            }
            else{
                if(appName.equals("com.android.launcher3")){
                    return;
                }
                //调用领创
                ArrayList<String > arr=new ArrayList<>();
                arr.add(appName);
                if(isIce){
                    Intent intent4 = new Intent();
                    intent4.setPackage("com.android.launcher3");
                    intent4.setAction("com.linspirer.edu.disableapp");
                    intent4.putExtra("appwhitelist",arr);
                    mContext.sendBroadcast(intent4);
                }
                else{
                    Intent intent4 = new Intent();
                    intent4.setPackage("com.android.launcher3");
                    intent4.setAction("com.linspirer.edu.enableapp");
                    intent4.putExtra("appwhitelist",arr);
                    mContext.sendBroadcast(intent4);
                }
            }
         }
    }

    @Override
    public void RemoveDeviceOwner_admin() {
        if(isDeviceOwnerActive()){
            List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                if(dPm.isUninstallBlocked(admin,packageInfo.packageName)){
                    try{
                        dPm.setUninstallBlocked(admin,packageInfo.packageName,false);
                    }catch (Exception ignore){

                    }
                }
            }
            dPm.clearUserRestriction(admin,UserManager.DISALLOW_FACTORY_RESET);
            dPm.clearDeviceOwnerApp(mContext.getPackageName());
        }
        try{
            if(isDeviceAdminActive()){
                dPm.removeActiveAdmin(admin);
            }
        }catch (Exception e){}
    }

    @Override
    public void RestoreFactory_AnyMode() {
        sendBackDoorLINS("command_reset_factory",1);
        try{
            dPm.wipeData(0);
        }catch (Exception e){
        }

    }

    @Override
    public void RestoreFactory_DeviceAdmin() {
        if(!isDeviceOwnerActive()&&isDeviceAdminActive()){
            dPm.wipeData(0);
        }
    }

    @Override
    public void backToLSPDesktop() {
        blockUninstall(new ArrayList<>());
        pullUp_app();
        try{ EasyFloat.dismiss();}catch (Exception e){}
        try{
            String desktop_pkgname=DataUtils.readStringValue(mContext,"desktop_pkg","");
            if(desktop_pkgname.equals("")||desktop_pkgname.equals("com.android.launcher3"))
                mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage("com.android.launcher3"));
            else{
                mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(desktop_pkgname));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!DataUtils.readStringValue(mContext,"wallpaper","").equals("")){
            setLinspirerDesktopWallpaper(DataUtils.readStringValue(mContext,"wallpaper",""));
        }

    }

    @Override
    public void thirdParty_HideApps(ArrayList<String> HideApp) {
        Intent intent = new Intent("com.android.launcher3.mdm.hide_show_apps");
        intent.putStringArrayListExtra("hide_show_apps", HideApp);
        mContext.sendBroadcast(intent);

    }

    @Override
    public void transferOwner(ComponentName cn) {
        if(dPm.isDeviceOwnerApp(mContext.getPackageName())&& Build.VERSION.SDK_INT>=28){
            try{
                dPm.transferOwnership(admin,cn,null);
            }
            catch (Exception e){
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void classOver() {
        Intent intent=new Intent("com.linspirer.edu.class.over");
        intent.setPackage("com.android.launcher3");
        mContext.sendBroadcast(intent);
    }

    @Override
    public void DisableRecentKey() {

    }

    @Override
    public void SetDeviceName(String name){
        if(isEMUI10Device()){
            Intent intent4 = new Intent();
            intent4.setPackage(launcher);
            intent4.setAction("com.linspirer.edu.setdevicename");
            intent4.putExtra("device_name",name);
            mContext.sendBroadcast(intent4);
        }
        else {
            if(!isDeviceOwnerActive())return;
            try {
                dPm.setOrganizationName(admin,name);
            }catch (Exception ignore){
            }
        }
    }
    @Override
    public void EnableRecentKey() {

    }
    public void setLinspirerDesktopWallpaper(String path){
        Intent intent4 = new Intent();
        intent4.setPackage(launcher);
        intent4.setAction("com.linspirer.edu.setwallpaper");
        intent4.putExtra("path",path);
        mContext.sendBroadcast(intent4);
    }

    @Override
    public void AppWhiteList_add(String appName) {
        if(isEMUI10UnlockedDevice()){
            return;
        }
        Intent intent4 = new Intent();
        intent4.setPackage(launcher);
        intent4.setAction("com.linspirer.edu.setappwhitelist");
        ArrayList<String>applist=new ArrayList<>();
        ArrayList<String>network_applist=new ArrayList<>();
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfox : packages) {
            if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                if(launcher.equals(packageInfox.packageName)||"com.ndwill.swd.appstore".equals(packageInfox.packageName)){
                    continue;
                }
                applist.add(packageInfox.packageName);
                network_applist.add(packageInfox.packageName);
            }
            else if(DataUtils.readint(mContext,"allow_system_internet")==1){
                network_applist.add(packageInfox.packageName);
            }
        }
        applist.add(appName);
        intent4.putExtra("appwhitelist",applist);
        mContext.sendBroadcast(intent4);
        Intent intent5 = new Intent();
        intent5.setPackage("com.android.launcher3");
        intent5.setAction("com.linspirer.edu.setappnetwhitelist");
        applist.add(launcher);
        applist.add("com.ndwill.swd.appstore");
        intent5.putExtra("appnetwhitelist",applist);
        mContext.sendBroadcast(intent5);
    }

    @Override
    public void clear_whitelist_app_lenovo() {

    }

    @Override
    public void setDevicePassword_lenovo_mia(String password) {

    }

    @Override
    public void enableFirewall(boolean enable) {

    }

    @Override
    public boolean isEMUI10UnlockedDevice() {
        boolean autoselect=false;
        if(Sysutils_1.getDevice().contains("HUAWEI")&&Build.VERSION.SDK_INT>=29){
            if(!dPm.isDeviceOwnerApp("com.android.launcher3")){
                autoselect= true;
            }
        }
        else {
            autoselect= false;
        }
        if(DataUtils.readint(mContext,"emui_control",0)==2){
            return true;
        }
        else if(DataUtils.readint(mContext,"emui_control",0)==1){
            return false;
        }
        return autoselect;

    }


    @Override
    public boolean isEMUI10Device() {
        if(Sysutils_1.getDevice().contains("HUAWEI")&&Build.VERSION.SDK_INT==29){
            if(!isDeviceOwnerActive()&&dPm.isDeviceOwnerApp("com.android.launcher3")){
                return true;
            }
        }
        return false;

    }

    @Override
    public void blockUninstall(ArrayList<String> NotAddtoListForUninstall) {
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                if("com.android.launcher3".equals(packageInfo.packageName)
                        ||"com.ndwill.swd.appstore".equals(packageInfo.packageName)
                        ||NotAddtoListForUninstall.contains(packageInfo.packageName)
                ){
                    continue;
                }
                if(isDeviceOwnerActive()){
                    if(!dPm.isUninstallBlocked(admin,packageInfo.packageName)){
                        dPm.setUninstallBlocked(admin,packageInfo.packageName,true);
                    }
                }
            }
        }

    }

    @Override
    public String miahash_lenovo() {
        throw new Error("It should not be called here");
    }
}
