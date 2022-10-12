package com.ljlVink.core.hackmdm.v2;

import android.content.Context;

public class TestImpl extends GenericMDM{
    public static TestImpl testmdm;
    public Context mContext;

    private TestImpl(Context context){
        GenericMDM.getInstance(context);
        this.mContext=context;
    }
    public static TestImpl getInstance(Context context){
        if(testmdm==null){
            testmdm=new TestImpl(context);
        }
        return testmdm;
    }

}
