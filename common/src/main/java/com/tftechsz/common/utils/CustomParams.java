package com.tftechsz.common.utils;

import android.content.Context;
import com.sdk.engine.IDParams;

/**
 * IDParams的实现类，将oaid参数传递给SDK
 */
public class CustomParams implements IDParams {

    private final Context mContext;

    public CustomParams(Context context) {
        this.mContext = context;
    }

    @Override
    public String getOaid() {
        //获取oaid
        return OaidHelper.getOAID(mContext);
    }

}
