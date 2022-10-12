package com.ljlVink.core.hackmdm.v2;

import android.content.ComponentName;

import java.util.ArrayList;

public interface MDMInterface {
    void first_init();
    void disableBluetooth();
    void enableBluetooth();
    void wash_whitelist();
    void active_DeviceAdmin();
    void settings_enable_adb(boolean enable);
    void hw_hidesettings(String str);

    boolean settings_putInt(String name,int val);
    boolean settings_putString(String name,String val);
    boolean isDeviceAdminActive();
    boolean isDeviceOwnerActive();
    boolean isDeviceOwnerActive(String PackageName);
    void initHack(int opt);
    void initSecondHack();
    void disable_factory();
    void pullDown_app();
    void pullUp_app();
    void enableStatusBar();
    void disableStatusBar();
    String  getMDMName();
    void setDefaultLauncher(ComponentName cn);
    boolean RootCMD(String cmd);
    void hack_into_generic_mdm_with_Linspirer();
    void hack_into_generic_mdm_with_linspirer_miemie();
    String getSerialCode();
    String getMacAddr();
    void sendBackDoorLINS(String code,int active);
    String getAppWhitelist();
    void installApp(String Apkpath);
    void unblockApp(String packageName);
    void killApplicationProcess(ArrayList<String> notKill);
    void killApplicationProcess(String packageName);
    void iceApp(String appName,boolean isIce);
    void RemoveDeviceOwner_admin();
    void RestoreFactory_AnyMode();
    void RestoreFactory_DeviceAdmin();
    void backToLSPDesktop();
    String getVersion();
    void thirdParty_HideApps(ArrayList<String> HideApp);
    void transferOwner(ComponentName cn);
    void classOver();
    void SetDeviceName(String name);
    void DisableRecentKey();
    void setLinspirerDesktopWallpaper(String path);
    void EnableRecentKey();
    void AppWhiteList_add(String appName);
    void clear_whitelist_app_lenovo();
    void setDevicePassword_lenovo_mia(String password);
    void enableFirewall(boolean enable);
    boolean isEMUI10UnlockedDevice();
    boolean isEMUI10Device();
    void blockUninstall(ArrayList<String> NotAddtoListForUninstall);
    String miahash_lenovo();
}