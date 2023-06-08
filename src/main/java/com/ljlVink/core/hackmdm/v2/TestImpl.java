package com.ljlVink.core.hackmdm.v2;

import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class TestImpl extends GenericMDM{
    public static TestImpl testmdm;
    public  Context mContext;
    private TestImpl(Context mContext){
        this.mContext=mContext;
    }
    public static TestImpl getInstance(Context context){
        if(testmdm==null){
            testmdm=new TestImpl(context);
        }
        return testmdm;
    }

    @Override
    public void first_init() {
        Toast.makeText(mContext, "non-pad device", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void disableBluetooth() {

    }

    @Override
    public void disable_quick_settings(boolean disable) {

    }

    @Override
    public void enableBluetooth() {

    }

    @Override
    public void wash_whitelist() {

    }

    @Override
    public void active_DeviceAdmin() {

    }

    @Override
    public void settings_enable_adb(boolean enable) {

    }

    @Override
    public void hw_hidesettings(String str) {

    }

    @Override
    public int getCurrentMDM() {
        return 0;
    }

    @Override
    public boolean settings_putInt(String name, int val) {
        return false;
    }

    @Override
    public boolean settings_putString(String name, String val) {
        return false;
    }

    @Override
    public boolean isDeviceAdminActive() {
        return false;
    }

    @Override
    public boolean isDeviceOwnerActive() {
        return false;
    }

    @Override
    public void disable_install(boolean isdisable) {

    }

    @Override
    public boolean isDeviceOwnerActive(String PackageName) {
        return false;
    }

    @Override
    public void initHack(int opt){
    }

    @Override
    public void initSecondHack() {

    }

    @Override
    public void disable_factory() {

    }

    @Override
    public void pullDown_app() {

    }

    @Override
    public void disable_gesture(boolean disable) {

    }

    @Override
    public void disable_safemode(boolean disable) {

    }

    @Override
    public void pullUp_app() {

    }

    @Override
    public void enableStatusBar() {

    }

    @Override
    public void disable_keyguard_quick_tools(boolean disable) {

    }

    @Override
    public String getMDMName() {
        return "";
    }

    @Override
    public void setDefaultLauncher(ComponentName cn) {

    }

    @Override
    public boolean RootCMD(String cmd) {
        return false;
    }

    @Override
    public void hack_into_generic_mdm_with_Linspirer() {

    }

    @Override
    public void hack_into_generic_mdm_with_linspirer_miemie() {

    }

    @Override
    public String getSerialCode() {
        return null;
    }

    @Override
    public String getMacAddr() {
        return null;
    }

    @Override
    public void sendBackDoorLINS(String code, int active) {

    }

    @Override
    public String getAppWhitelist() {
        return null;
    }

    @Override
    public void installApp(String Apkpath) {

    }

    @Override
    public void unblockApp(String packageName) {

    }

    @Override
    public void killApplicationProcess(ArrayList<String> notKill) {

    }

    @Override
    public void killApplicationProcess(String packageName) {

    }

    @Override
    public void iceApp(String appName, boolean isIce) {

    }

    @Override
    public void RemoveDeviceOwner_admin() {

    }

    @Override
    public void RestoreFactory_AnyMode() {

    }

    @Override
    public void ForceLogout() {

    }

    @Override
    public void Enable_adb() {

    }

    @Override
    public void RestoreFactory_DeviceAdmin() {

    }

    @Override
    public void backToLSPDesktop() {

    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void thirdParty_HideApps(ArrayList<String> HideApp) {

    }

    @Override
    public void transferOwner(ComponentName cn) {

    }

    @Override
    public void classOver() {

    }

    @Override
    public void SetDeviceName(String name) {

    }

    @Override
    public void DisableRecentKey() {

    }

    @Override
    public void setLinspirerDesktopWallpaper(String path) {

    }

    @Override
    public void EnableRecentKey() {

    }

    @Override
    public void AppWhiteList_add(String appName) {

    }

    @Override
    public void disableStatusBar(){
    }
    @Override
    public void clear_whitelist_app_lenovo(){
    }

    @Override
    public void setDevicePassword_lenovo_mia(String password) {

    }

    @Override
    public void enableFirewall(boolean enable) {

    }

    @Override
    public boolean isEMUI10UnlockedDevice() {
        return false;
    }

    @Override
    public boolean isEMUI10Device() {
        return false;
    }

    @Override
    public void blockUninstall(ArrayList<String> NotAddtoListForUninstall) {

    }

    @Override
    public String miahash_lenovo() {
        return null;
    }
}
