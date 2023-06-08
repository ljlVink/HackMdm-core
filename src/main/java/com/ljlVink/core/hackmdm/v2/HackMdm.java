package com.ljlVink.core.hackmdm.v2;

import android.app.csdk.CSDKManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.ljlVink.Util.Sysutils_1;
import com.ljlVink.core.hackmdm.v2.Lenovo.CSDKMDM;
import com.ljlVink.core.hackmdm.v2.Lenovo.MiaMDM;

public class HackMdm{
    public static MDMInterface DeviceMDM;
    static  boolean LENOVO_MIAMDM=true;
    static boolean LENOVO_CSDK=true;
    public Context mContext;
    public HackMdm(Context context){
        this.mContext=context;
    }
    public void initMDM() {
        try{
            Class.forName("android.app.mia.MiaMdmPolicyManager");
        }
        catch (ClassNotFoundException e){
            LENOVO_MIAMDM=false;
        }
        try{
            Class.forName("android.app.csdk.CSDKManager");
        }
        catch (ClassNotFoundException e){
            LENOVO_CSDK=false;
        }
        if(LENOVO_CSDK){
            try{
                if(new CSDKManager(mContext).getDeviceInfo(1).equals("")){
                    LENOVO_CSDK=false;
                }
            }catch (Throwable e){
                LENOVO_CSDK=false;
            }
        }
        DeviceMDM=GenericMDM.getInstance(mContext);

        if(LENOVO_CSDK && !LENOVO_MIAMDM){
            DeviceMDM=CSDKMDM.getInstance(mContext);
        }
        else if(!LENOVO_CSDK && LENOVO_MIAMDM){
            DeviceMDM=MiaMDM.getInstance(mContext);
        }
        else if(LENOVO_CSDK && LENOVO_MIAMDM){
            DeviceMDM=MiaMDM.getInstance(mContext);
        }else if(Build.BRAND.equals("T11")){
            DeviceMDM=Supi.getInstance(mContext);
        }
        else if(Sysutils_1.getDevice().contains("MuMu")||!Istablet()){
            Toast.makeText(mContext, "non-pad-device", Toast.LENGTH_SHORT).show();
            DeviceMDM=TestImpl.getInstance(mContext);
        }
        else {
            DeviceMDM=GenericMDM.getInstance(mContext);
        }
    }
    private boolean Istablet(){
        boolean isPad = (mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y); // 屏幕尺寸
        return (isPad || screenInches >= 7.0);
    }
}
