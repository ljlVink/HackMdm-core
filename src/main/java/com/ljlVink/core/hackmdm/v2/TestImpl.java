package com.ljlVink.core.hackmdm.v2;

import android.content.Context;
import android.widget.Toast;

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
    public void initHack(int opt){
        Toast.makeText(mContext, "inithack now is TestImpl,opt="+opt, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getMDMName() {
        return "MUMUtest";
    }
    @Override
    public void disableStatusBar(){
        Toast.makeText(mContext, "disableStatusBar(", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void clear_whitelist_app_lenovo(){
        Toast.makeText(mContext, "mumuimpl-clear_whitelist_app_lenovo(", Toast.LENGTH_SHORT).show();

    }
}
