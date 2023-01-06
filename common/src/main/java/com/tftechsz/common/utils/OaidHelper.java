package com.tftechsz.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用MSA的SDK来实现获取OAID的通用方案
 * assets中的supplierconfig.json是官方demo中提供的，不能删除，否则获取oaid会失败
 */
public final class OaidHelper {
    private static final String TAG = OaidHelper.class.getSimpleName();
    /**
     * 部分支持获取到的机型会偶现很慢，部分不支持获取的机型会长时间无回调，综合测试情况确定的超时等待阈值
     */
    private static final long WAIT_TIME = 2000L;
    /**
     * 在内存中持久化的OAID
     */
    private static String mOAID;

    private static final Object object = new Object();
    private static final OaidListener mListener = new OaidListener();

    /**
     * 是否初始化，只能初始化一次
     */
    private static final AtomicBoolean IS_INIT = new AtomicBoolean(false);

    private OaidHelper() {
    }

    public static String getOAID(Context context) {
        synchronized (object) {
            if (!TextUtils.isEmpty(mOAID)) {
                return mOAID;
            }
            try {
                int initState = 0;
                if (!IS_INIT.get()) {
                    IS_INIT.set(true);
                    initState = MdidSdkHelper.InitSdk(context, true, mListener);
                }

                if (initState != ErrorCode.INIT_ERROR_RESULT_DELAY) {
                    return mOAID;
                }
                object.wait(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return mOAID;
        }
    }

    private static class OaidListener implements IIdentifierListener {

        @Override
        public void OnSupport(boolean b, IdSupplier idSupplier) {
            try {
                if (idSupplier == null) {
                    return;
                }
                mOAID = idSupplier.getOAID();
            } finally {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }
    }

}
