/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.tftechsz.common.location;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tftechsz.common.base.BaseApplication;

/**
 * @author by liuhongjian01 on 16/8/17.
 */
public class BDLocationManager {
    private final BDAbstractLocationListener mSysLocListener = new MLocationListener();
    private LocationListener listener;
    private LocationClient mLocClient;

    private static class Holder {
        private static final BDLocationManager INSTANCE = new BDLocationManager();
    }


    public static BDLocationManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addListener(LocationListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    public void initGPS() {
        try {
            if (mLocClient == null)
                mLocClient = new LocationClient(BaseApplication.getInstance());
            LocationClientOption option = new LocationClientOption();
            // 打开gps
            option.setScanSpan(0);
            option.setIsNeedAddress(true);
            option.setNeedNewVersionRgc(true);
            option.setLocationNotify(true);
            // 设置坐标类型
            option.setCoorType("bd09ll");
            mLocClient.setLocOption(option);
            mLocClient.start();
        } catch (Exception e) {
        }
    }

    public void startLoc() {
        if (mLocClient != null)
            mLocClient.registerLocationListener(mSysLocListener);
    }

    public void stopLoc() {
        if (mLocClient != null)
            mLocClient.unRegisterLocationListener(mSysLocListener);
    }


    public class MLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            if (null != listener) {
                listener.onLocationChanged(location);
            }


        }


    }
}
