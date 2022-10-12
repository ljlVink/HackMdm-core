package com.ljlVink.Util;

import android.annotation.SuppressLint;
import android.app.csdk.CSDKManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Sysutils_1 {
    @SuppressLint("MissingPermission")
    public static ArrayList<String> FindLspDemoPkgName(Context context, String meta) {
        ArrayList<String> lst = new ArrayList<String>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            String pkgname=packageInfo.packageName;
            if (!pkgname.equals(context.getPackageName())) {
                if (getMetaDataValue(context, "HackMdm", pkgname).equals(meta)) {
                    lst.add(packageInfo.packageName);
                }
            }
        }
        return lst;
    }
    public static String getDevice() {
        String manufacturer = Character.toUpperCase(Build.MANUFACTURER.charAt(0)) + Build.MANUFACTURER.substring(1);
        if (!Build.BRAND.equals(Build.MANUFACTURER)) {
            manufacturer += " " + Character.toUpperCase(Build.BRAND.charAt(0)) + Build.BRAND.substring(1);
        }
        manufacturer += " " + Build.MODEL + " ";
        return manufacturer;
    }

    public static boolean isSystemApplication(Context context){
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

    public static String getMetaDataValue(Context context, String meatName, String pkgname) {
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
}
