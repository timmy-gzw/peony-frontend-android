package com.tftechsz.common.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 避免造成内存泄漏的 handler
 */
public class SafeHandle extends Handler {
    private final WeakReference<Activity> mActivity;

    public SafeHandle(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    protected void mHandleMessage(Message msg) {

    }

    @Override
    public void handleMessage(Message msg) {
        try {
            if (mActivity.get() == null) {
                return;
            }
            mHandleMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
