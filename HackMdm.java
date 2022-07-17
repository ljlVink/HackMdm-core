package com.ljlVink.core.core;
import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
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

import androidx.preference.PreferenceManager;

import com.huosoft.wisdomclass.linspirerdemo.AR;
import com.ljlVink.Activity.NewUI;
import com.ljlVink.MDM;
import com.ljlVink.ToastUtils.Toast;
import com.ljlVink.core.DataUtils;
import com.ljlVink.core.t11_271bay.MainUtils;
import com.ljlVink.xposed.Main;
import com.lzf.easyfloat.EasyFloat;


import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
public class HackMdm extends Object {
    private int MMDM;
    int Lenovo_Mia=3;
    int Lenovo_Csdk=2;
    int Generic_mdm=-1;
    int T11=4;
    private Postutil postutil;
    private Lenovomethod lenovo;
    Context context;
    ComponentName testDeviceAdmin;
    MainUtils SupiImpl;
    DevicePolicyManager dpm;
    ActivityManager am;
    private final static String launcher="com.android.launcher3";
    private final static String execmdsvc="com.drupe.swd.launcher.huoshan.mdm.service.ExecuteCmdService";
    public HackMdm(Context context){
        this.context=context;
        MMDM=new MDM(context).MDM();
        lenovo=new Lenovomethod(context);
        postutil=new Postutil(context);
        testDeviceAdmin = new ComponentName(context, AR.class);
        SupiImpl=new MainUtils(context);
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
    }

    public String getHackmdm_version(){
        return "202207015"+"(Lenovo:"+lenovo.getLenovo_version()+")"+"(Supi:"+SupiImpl.getversion()+")";
    }
    public int getMMDM(){
        return MMDM;
    }
    public boolean isEMUI10Device(){
        //华为管控下,考虑到owner会被抢占
        if(NewUI.getDevice().contains("HUAWEI")&&Build.VERSION.SDK_INT==29){
            if(MMDM==Generic_mdm&&!isDeviceOwnerActive()&&dpm.isDeviceOwnerApp("com.android.launcher3")){
                return true;
            }
        }
        return false;
    }
    public boolean isEMUI10UnlockedDevice(){
        //TODO 临时解法
        boolean autoselect=false;
        if(NewUI.getDevice().contains("HUAWEI")&&Build.VERSION.SDK_INT==29){
            if(MMDM==Generic_mdm&&!dpm.isDeviceOwnerApp("com.android.launcher3")){
                autoselect= true;
            }
        }
        else {autoselect= false;}
        if(DataUtils.readint(context,"emui_control",0)==2){
            return true;
        }
        else if(DataUtils.readint(context,"emui_control",0)==1){
            return false;
        }
        return autoselect;
    }
    public void wash_whitelist(){
        lenovo.wash_whitelist();
    }
    public void ActivateDeviceAdmin(){
        try{
            if(!dpm.isAdminActive(testDeviceAdmin)){
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra (DevicePolicyManager.EXTRA_DEVICE_ADMIN,testDeviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活deviceadmin防被卸载");
                context.startActivity(intent);
            }
        }catch (Exception E){
            E.printStackTrace();
        }
    }
    public void dpm_enable_adb(){
        Toast.ShowWarn(context,"enable");
        if(isDeviceOwnerActive()){
            dpm.clearUserRestriction(testDeviceAdmin, UserManager.DISALLOW_DEBUGGING_FEATURES);
            dpm.setGlobalSetting(testDeviceAdmin,"adb_enabled","true");
        }
    }
    public void dpm_disable_adb(){
        if(isDeviceOwnerActive()){
            dpm.setGlobalSetting(testDeviceAdmin,"adb_enabled","false");
        }
    }

    public void hw_hidesettings(String str){
        try {
              Settings.Global.putString(context.getContentResolver(),"settings_menus_remove",str);
        }catch (Exception e){
            Toast.ShowInfo(context, "请先赋予app权限(参照说明)");
        }
        //Log.e("LTKLog",Settings.Global.getString(context.getContentResolver(),"settings_menus_remove"));
    }
    public void findowner(){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if(isDeviceOwnerActive(packageInfo.packageName)){
                Log.e("tag",packageInfo.packageName);
            }
        }

    }
    public boolean isDeviceAdminActive(){ return dpm.isAdminActive(testDeviceAdmin); }
    public boolean isDeviceOwnerActive(){ return dpm.isDeviceOwnerApp(context.getPackageName()); }
    public boolean isDeviceOwnerActive(String packagename) { return dpm.isDeviceOwnerApp(packagename); }
    public void hack_DeviceOwner(){
        lenovo.hack_deviceowner();
    }
    public static ArrayList<String> FindLspDemoPkgName(Context context){
        ArrayList<String> lst=new ArrayList<String>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0&&!packageInfo.packageName.equals(context.getPackageName())){
                if(getMetaDataValue(context,"HackMdm",packageInfo.packageName).equals("linspirerdemo")){
                    lst.add(packageInfo.packageName) ;
                }
            }
        }
        return lst;
    }
    public static String getMetaDataValue(Context context, String meatName,String pkgname) {
        String value = "null";
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(pkgname, PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                Object object = applicationInfo.metaData.get(meatName);
                if (object != null) {
                    value = object.toString();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return value;
    }
    public void fix_csdk_compoment(){
        lenovo.fix_csdk_component();
    }
    public void disable_notify(){
        lenovo.disablenotify(true);
    }
    public void enable_notify(){
        lenovo.disablenotify(false);

    }
    public void set_default_launcher(String pkgname,String compname){
        if(MMDM==Lenovo_Csdk){
            lenovo.csdk_setlauncher(pkgname,compname);
        }else if(isDeviceOwnerActive()){
            setdefaultlauncher(new ComponentName(pkgname,compname));
        }
    }

    public void initSecondHack(){
        lenovo.initSecondHack();
        if(MMDM==T11){
            new MainUtils(context).FirstHack();
        }
    }
    private void Disallow_Factory(){
        if(isDeviceOwnerActive()){
            dpm.addUserRestriction(testDeviceAdmin, UserManager.DISALLOW_FACTORY_RESET);
        }
    }
    public void pulldownApp(){
        ArrayList<String>li1test=DataUtils.ReadStringArraylist(context,"superapp");
        if (isDeviceOwnerActive()){
            for (int i = 0; i < li1test.size(); i++) {
                dpm.setApplicationHidden(testDeviceAdmin,li1test.get(i),false);
            }
        }
    }
    public void EnableStatusBar(){
        if(isDeviceOwnerActive()){
            dpm.setStatusBarDisabled(testDeviceAdmin,false);
        }
    }

    public void DisableStatusBar(){
        if(isDeviceOwnerActive()){
            dpm.setStatusBarDisabled(testDeviceAdmin,true);

        }
    }
    public void setdefaultlauncher(ComponentName componentName){
        if(!isDeviceOwnerActive()){
            return;
        }try{
            IntentFilter filter = new IntentFilter("android.intent.action.MAIN");
            filter.addCategory("android.intent.category.HOME");
            filter.addCategory("android.intent.category.DEFAULT");
            dpm.clearPackagePersistentPreferredActivities(testDeviceAdmin,"com.huawei.android.launcher");
            dpm.addPersistentPreferredActivity(testDeviceAdmin,filter,componentName);
        }catch (Exception e){

        }

    }
    public void T11Cmd(String cmd){
        SupiImpl.RootCommand(cmd);

    }
    public boolean RootCommand(String command) {

        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
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
    public void BlockUnInstall( ArrayList<String> NotAddtoListForUninstall){
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            ArrayList<String>pkg=new ArrayList<>();
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    if(launcher.equals(packageInfo.packageName)||"com.ndwill.swd.appstore".equals(packageInfo.packageName)||(NotAddtoListForUninstall.contains(packageInfo.packageName))){
                        continue;
                    }
                    if(isDeviceOwnerActive()){
                        if(!dpm.isUninstallBlocked(testDeviceAdmin,packageInfo.packageName)){
                            dpm.setUninstallBlocked(testDeviceAdmin,packageInfo.packageName,true);
                        }
                    }
                    pkg.add(packageInfo.packageName);
                }

            }
            if(!isDeviceOwnerActive()&&MMDM==Lenovo_Mia){
                lenovo.mia_mdm_addDisallowedUninstallPackages(pkg);
            }
    }
    public  String getCSDK_sn_code(){
        return lenovo.getCSDK_sn_code();
    }
    public void sendbdoorlinspirer(String command,int active){
        Intent huawei =new Intent();
        huawei.setAction(execmdsvc);
        huawei.putExtra("cmd",command);
        huawei.putExtra("active",1);//optional
        huawei.setPackage(launcher);
        context.startService(huawei);

    }
    public void huawei_MDM_Unlock(){
        sendbdoorlinspirer("command_release_control",1);
    }
    public void hack_into_generic_mdm_with_Linspirer(){
        sendbdoorlinspirer("command_camera",1);
        sendbdoorlinspirer("command_connect_usb",1);
        sendbdoorlinspirer("command_bluetooth",1);
        sendbdoorlinspirer("command_otg",1);
        try{
            Intent intent2 = new Intent();
            intent2.setPackage(launcher);
            intent2.setAction("com.linspirer.edu.disablefirewall");
            context.sendBroadcast(intent2);
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
            context.sendBroadcast(intent4);
        }
    }
    public void hack_into_generic_mdm_with_linspirer_miemie(){
        Intent intent=new Intent();
        intent.setClassName("com.linspirer.sheepmie","com.linspirer.receiver.SheepMieReceiver");
        intent.setAction("com.linspirer.sheepmie_enableusb");
        context.sendBroadcast(intent);

    }
    public void uninstall_linspirer_silent(String pkgname){
        Intent intent =new Intent();
        intent.setPackage(launcher);
        intent.setAction("com.linspirer.edu.silentuninstall");
        intent.putExtra("packageName",pkgname);
        context.sendBroadcast(intent);

    }
    public String getappwhitelist(){
        try{
            if (MMDM==Lenovo_Mia){
                wash_whitelist();
                return lenovo.Get_Lenovo_app_whitelist();
            }
            else if (MMDM==Lenovo_Csdk) {
                wash_whitelist();
                return  lenovo.Get_Lenovo_app_whitelist();
            }
            else if(MMDM==Generic_mdm){
                ArrayList<String>list1=new ArrayList<>();
                List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
                for (PackageInfo packageInfox : packages) {
                    if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                        if(launcher.equals(packageInfox.packageName)||"com.ndwill.swd.appstore".equals(packageInfox.packageName)){
                            continue;
                        }
                        list1.add(packageInfox.packageName+"[white]");
                    }else if(DataUtils.readint(context,"allow_system_internet")==1){
                        list1.add(packageInfox.packageName+"[net]");
                    }
                }
                return list1.toString();
            }else if(MMDM==T11){
                return "T11设备不支持app白名单";
            }
            return "?";
        }
        catch (Exception e){
            return "白名单获取异常";
        }
    }
    public void appwhitelist_add(String appname){
        if (MMDM==Generic_mdm&&!isEMUI10UnlockedDevice()){
            Intent intent4 = new Intent();
            intent4.setPackage(launcher);
            intent4.setAction("com.linspirer.edu.setappwhitelist");
            ArrayList<String>applist=new ArrayList<>();
            ArrayList<String>network_applist=new ArrayList<>();
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo packageInfox : packages) {
                if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    if(launcher.equals(packageInfox.packageName)||"com.ndwill.swd.appstore".equals(packageInfox.packageName)){
                        continue;
                    }
                    applist.add(packageInfox.packageName);
                    network_applist.add(packageInfox.packageName);
                }else if(DataUtils.readint(context,"allow_system_internet")==1){
                    network_applist.add(packageInfox.packageName);
                }
            }
            applist.add(appname);
            intent4.putExtra("appwhitelist",applist);
            context.sendBroadcast(intent4);
            //网络白名单
            Intent intent5 = new Intent();
            intent5.setPackage("com.android.launcher3");
            intent5.setAction("com.linspirer.edu.setappnetwhitelist");
            applist.add(launcher);
            applist.add("com.ndwill.swd.appstore");
            intent5.putExtra("appnetwhitelist",applist);
            context.sendBroadcast(intent5);
        }
        else if (MMDM==Lenovo_Mia){
            wash_whitelist();
            lenovo.addInstallPackageWhiteList(appname);
        }
        else if (MMDM==Lenovo_Csdk) {
            wash_whitelist();
            lenovo.addInstallPackageWhiteList(appname);
        }
    }
    public void savesuperapps(ArrayList<String> dataList){
        DataUtils.saveStringArrayList(context,"superapp",dataList);

    }
    public void pullupApp(){
        ArrayList<String>li1test=DataUtils.ReadStringArraylist(context,"superapp");
        if (dpm.isDeviceOwnerApp(context.getPackageName())){
            for (int i = 0; i < li1test.size(); i++) {
                dpm.setApplicationHidden(testDeviceAdmin,li1test.get(i),true);
            }
        }
        else{
            if(MMDM==Lenovo_Mia){
                for (int i = 0; i < li1test.size(); i++) {
                    iceApp(li1test.get(i),true);
                }
            }
        }
        if(MMDM==Generic_mdm&&!isEMUI10UnlockedDevice()){
            //emui10管控
            Intent intent4 = new Intent();
            intent4.setPackage("com.android.launcher3");
            intent4.setAction("com.linspirer.edu.disableapp");
            intent4.putExtra("appwhitelist",li1test);
            context.sendBroadcast(intent4);
        }
    }
    public void uninstallApp(String pkgname){
        if (dpm.isDeviceOwnerApp(context.getPackageName())){
            dpm.setUninstallBlocked(testDeviceAdmin,pkgname,false);
        }else if (!dpm.isDeviceOwnerApp(context.getPackageName())&&MMDM==Lenovo_Mia){
            ArrayList<String>list=new ArrayList<>();
            list.add(pkgname);
            lenovo.mia_mdm_removeDisallowedUninstallPackages(list);
        }
    }
    public void installapp(String path){
        if(MMDM==Lenovo_Csdk){
            lenovo.installapp(path);
        }
        else if (MMDM==Lenovo_Mia){
            lenovo.installapp(path);
        }
        else if(MMDM==Generic_mdm){
            Intent intent4 = new Intent();
            intent4.setPackage("com.android.launcher3");
            intent4.setAction("com.linspirer.edu.silentinstall");
            intent4.putExtra("path",path);
            context.sendBroadcast(intent4);
        }else if(MMDM==T11){
            SupiImpl.InstallApp(path);
        }
    }
    public void killApplicationProcess(ArrayList<String> notkill){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                PackageManager pm1 = context.getPackageManager();
                List<PackageInfo> packages1 = pm1.getInstalledPackages(0);
                for (PackageInfo packageInfo : packages1) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                        if(MMDM==Lenovo_Csdk&&!context.getPackageName().equals(packageInfo.packageName)&&!notkill.contains(packageInfo.packageName)){
                            lenovo.killApplicationProcess(packageInfo.packageName);
                        }
                        else if(MMDM==Lenovo_Mia&&!context.getPackageName().equals(packageInfo.packageName)&&!notkill.contains(packageInfo.packageName)){
                            lenovo.killApplicationProcess(packageInfo.packageName);

                        }else if(MMDM==T11&&!context.getPackageName().equals(packageInfo.packageName)&&!notkill.contains(packageInfo.packageName)){
                            SupiImpl.killapp(packageInfo.packageName);
                        }
                    }
                }
                Looper.loop();
            }
        });
        thread.start();
    }
    public void killApplicationProcess(String str){
        lenovo.killApplicationProcess(str);
    }
    public void iceApp(String icename ,boolean isice){
        if(icename.equals(context.getPackageName())){
            return;
        }
        if(!TextUtils.isEmpty(icename)) {
            if (MMDM == Lenovo_Csdk) {
                lenovo.iceapp(icename,isice);
            } else if (MMDM == Lenovo_Mia) {
                lenovo.iceapp(icename,isice);
            }
            else if(MMDM==Generic_mdm){
                if(isDeviceOwnerActive()){
                    dpm.setApplicationHidden(testDeviceAdmin,icename,isice);
                }
                else{
                    if(icename.equals("com.android.launcher3")){
                        return;
                    }
                    //调用领创
                    ArrayList<String > arr=new ArrayList<>();
                    arr.add(icename);
                    if(isice){
                        Intent intent4 = new Intent();
                        intent4.setPackage("com.android.launcher3");
                        intent4.setAction("com.linspirer.edu.disableapp");
                        intent4.putExtra("appwhitelist",arr);
                        context.sendBroadcast(intent4);
                    }
                    else{
                        Intent intent4 = new Intent();
                        intent4.setPackage("com.android.launcher3");
                        intent4.setAction("com.linspirer.edu.enableapp");
                        intent4.putExtra("appwhitelist",arr);
                        context.sendBroadcast(intent4);
                    }
                }
            }else if(MMDM==T11){
                SupiImpl.iceapp(icename,isice);
            }

        }
    }
    public void RemoveOwner_admin(){
        if(MMDM==Lenovo_Mia){
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> packages = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo : packages) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                        if(isDeviceOwnerActive()){
                            if(dpm.isUninstallBlocked(testDeviceAdmin,packageInfo.packageName)){
                                dpm.setUninstallBlocked(testDeviceAdmin,packageInfo.packageName,false);
                            }
                        }
                        else{
                            uninstallApp(packageInfo.packageName);
                        }
                    }
            }
            try{
                if(isDeviceOwnerActive()){
                    dpm.clearUserRestriction(testDeviceAdmin,UserManager.DISALLOW_FACTORY_RESET);
                    dpm.clearDeviceOwnerApp(context.getPackageName());
                }
            }
            catch (Exception e){}
            try{
                if(isDeviceAdminActive()){
                    dpm.removeActiveAdmin(testDeviceAdmin);
                }
            }catch (Exception e){}
        }
        else if(MMDM==Lenovo_Csdk){
            if(isDeviceOwnerActive()){
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> packages = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo : packages) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                        if(dpm.isUninstallBlocked(testDeviceAdmin,packageInfo.packageName)){
                            dpm.setUninstallBlocked(testDeviceAdmin,packageInfo.packageName,false);
                        }
                    }
                }
                dpm.clearUserRestriction(testDeviceAdmin,UserManager.DISALLOW_FACTORY_RESET);
                dpm.clearDeviceOwnerApp(context.getPackageName());
            }
            try{
                dpm.removeActiveAdmin(testDeviceAdmin);
            }
            catch (Exception e){
            }
        }
        else{
            try{
                if(isDeviceOwnerActive()){
                    dpm.clearUserRestriction(testDeviceAdmin,UserManager.DISALLOW_FACTORY_RESET);
                    dpm.clearDeviceOwnerApp(context.getPackageName());
                }
            }
            catch (Exception e){}
            try{
                if(isDeviceAdminActive()){
                    dpm.removeActiveAdmin(testDeviceAdmin);
                }
            }catch (Exception e){}
        }
    }

    public void backToLSP(){
        wash_whitelist();
        BlockUnInstall(new ArrayList<>());
        pullupApp();
        lenovo.backtolsp();
        try{ EasyFloat.dismiss();}catch (Exception e){}
        try{
            String desktop_pkgname=DataUtils.readStringValue(context,"desktop_pkg","");
            if(desktop_pkgname.equals("")||desktop_pkgname.equals("com.android.launcher3"))
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage("com.android.launcher3"));
            else{
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(desktop_pkgname));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(MMDM==Generic_mdm){
            if(!DataUtils.readStringValue(context,"wallpaper","").equals("")){
                setwallpaper(DataUtils.readStringValue(context,"wallpaper",""));
            }
        }
    }
    public void thirdpartylauncher_hideapps(ArrayList list){
        Intent intent = new Intent("com.android.launcher3.mdm.hide_show_apps");
        intent.putStringArrayListExtra("hide_show_apps", list);
        context.sendBroadcast(intent);
    }
    public void easteregg(){
        Intent intent=new Intent();
        intent.setPackage(launcher);
        intent.putExtra("packageName",context.getPackageName());
        intent.setAction("com.linspirer.edu.obtain.userinfo");
        context.sendBroadcast(intent);
    }
    public void transfer(ComponentName cn){
        if(dpm.isDeviceOwnerApp(context.getPackageName())&& Build.VERSION.SDK_INT>=28){
            postutil.sendPost("trying to transfer owner to "+cn.getPackageName()+",original:"+testDeviceAdmin.getPackageName());
            try{
                dpm.transferOwnership(testDeviceAdmin,cn,null);
            }
            catch (Exception e){
                postutil.sendPost("trying to transfer owner to "+cn.getPackageName()+",original:"+testDeviceAdmin.getPackageName()+"FAILED WITH EXCEPTION "+e.toString());
            }
        }
        else{
            Toast.ShowInfo(context,"Requires android 9+ and Device owner");
        }
        return ;
    }

    public void Lenovo_clear_whitelist_app(){
        lenovo.Lenovo_clear_whitelist_app();
    }
    public void mia_setpasswd(String str){
        lenovo.mia_setpasswd(str);
    }
    public void setwallpaper(String path){
        if(MMDM==Generic_mdm){
            Intent intent4 = new Intent();
            intent4.setPackage(launcher);
            intent4.setAction("com.linspirer.edu.setwallpaper");
            intent4.putExtra("path",path);
            context.sendBroadcast(intent4);
        }
    }
    public void SetDeviceName(String name){
        if(isEMUI10Device()){
            Intent intent4 = new Intent();
            intent4.setPackage(launcher);
            intent4.setAction("com.linspirer.edu.setdevicename");
            intent4.putExtra("device_name",name);
            context.sendBroadcast(intent4);
        }
        else {
            if(!isDeviceOwnerActive())return;
            dpm.setProfileName(testDeviceAdmin,name);
        }
    }
    public void RestoreFactory_DeviceAdmin(){
        if(!isDeviceOwnerActive()&&isDeviceAdminActive()){
            dpm.wipeData(0);
        }

    }
    public void RestoreFactory_anymode(){
        sendbdoorlinspirer("command_reset_factory",1);
        try{
            dpm.wipeData(0);
        }catch (Exception e){
        }
    }
    public void Generic_mdm_Second_hack(){
        if(isEMUI10UnlockedDevice()){
            return;
        }
        Intent intent4 = new Intent();
        intent4.setPackage(launcher);
        intent4.setAction("com.linspirer.edu.setappwhitelist");
        ArrayList<String>applist=new ArrayList<>();
        ArrayList<String>network_applist=new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfox : packages) {
            if ((packageInfox.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                if(launcher.equals(packageInfox.packageName)||"com.ndwill.swd.appstore".equals(packageInfox.packageName)){
                    continue;
                }
                applist.add(packageInfox.packageName);
                network_applist.add(packageInfox.packageName);
            }
            else if(DataUtils.readint(context,"allow_system_internet")==1){
                network_applist.add(packageInfox.packageName);
            }
        }
        intent4.putExtra("appwhitelist",applist);
        context.sendBroadcast(intent4);
        //网络白名单
        Intent intent5 = new Intent();
        intent5.setPackage("com.android.launcher3");
        intent5.setAction("com.linspirer.edu.setappnetwhitelist");
        applist.add(launcher);
        applist.add("com.ndwill.swd.appstore");
        intent5.putExtra("appnetwhitelist",network_applist);
        context.sendBroadcast(intent5);
        //放导航，截屏
        Intent intent6 = new Intent();
        intent6.setPackage("com.android.launcher3");
        intent6.setAction("com.linspirer.edu.enablenavigationbar");
        context.sendBroadcast(intent6);
        intent6.setAction("com.linspirer.edu.enable.screenshot");
        context.sendBroadcast(intent6);
        intent6.setAction("com.linspirer.edu.enable.changewallpaper");
        context.sendBroadcast(intent6);
        intent6.setAction("com.linspirer.edu.enableotg");
        context.sendBroadcast(intent6);
    }
    public void initHack(int flag){
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                initSecondHack();
                if(flag==0){
                    wash_whitelist();
                    ActivateDeviceAdmin();
                    hack_DeviceOwner();
                    Disallow_Factory();
                    Generic_mdm_Second_hack();
                }
                pulldownApp();
                BlockUnInstall(new ArrayList<>());
                if (MMDM==Generic_mdm){
                    hack_into_generic_mdm_with_Linspirer();
                    hack_into_generic_mdm_with_linspirer_miemie();
                }
                Looper.loop();

            }
        });
        th.start();
    }
    public String genauth(){
        return Postutil.getWifiMacAddress(context).toLowerCase();
    }
}