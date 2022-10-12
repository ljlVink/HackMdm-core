package com.ljlVink.core.hackmdm.v2;

import android.app.csdk.CSDKManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

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
        Log.e("hackmdm","init start");
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
        if(LENOVO_CSDK==true&&LENOVO_MIAMDM==false){
            DeviceMDM=CSDKMDM.getInstance(mContext);
        }
        if(LENOVO_CSDK==false&&LENOVO_MIAMDM==true){
            DeviceMDM=MiaMDM.getInstance(mContext);
        }
        if(LENOVO_CSDK==true&&LENOVO_MIAMDM==true){
            DeviceMDM=MiaMDM.getInstance(mContext);

        }if(Build.BRAND.equals("T11")){
            DeviceMDM=Supi.getInstance(mContext);
        }/*
        if(Build.BRAND.equals("MuMu")){
            DeviceMDM=TestImpl.getInstance(mContext);
        }*/else {Log.e("hackmdm","init generic");

            DeviceMDM=GenericMDM.getInstance(mContext);
        }
        Log.e("hackmdm","init end");
    }
}
