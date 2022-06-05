package com.tftechsz.common.other;

import android.app.FragmentManager;

import com.tftechsz.common.widget.pop.AndroidLoadingDialogNew;

public class GlobalDialogManager {

    private AndroidLoadingDialogNew mLoadingDialog;
    private boolean mIsShow;//是否显示

    private static GlobalDialogManager INSTANCE;
    private FragmentManager manager;

    private GlobalDialogManager() {
        init();
    }

    public synchronized static GlobalDialogManager getInstance() {
        if (INSTANCE == null) {
            synchronized (GlobalDialogManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new GlobalDialogManager();
            }
        }
        return INSTANCE;
    }

//    private static class SingletonHolder {
//        private static GlobalDialogManager INSTANCE = new GlobalDialogManager();
//    }

    public void init() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AndroidLoadingDialogNew();
        }
    }

    /**
     * 展示加载框
     */
    public synchronized void show(FragmentManager manager, String hintMessage) {
        this.manager = manager;
        if (manager == null || mIsShow) {
            return;
        }
        init();
        mLoadingDialog.showAllowingStateLoss(manager, "loadingDialog");
        mLoadingDialog.setHintMsg(hintMessage);
        mIsShow = true;
    }

    /**
     * 展示加载框
     */
    public synchronized boolean isShow() {
        mIsShow = mLoadingDialog != null && mIsShow;
        return mIsShow;
    }


    /**
     * 隐藏加载框
     */
    public synchronized void dismiss() {
        try {
            if (mLoadingDialog != null && mIsShow && manager != null) {
                mLoadingDialog.dismissAllowingStateLoss();
                mIsShow = false;
                //mLoadingDialog = null;
                //INSTANCE = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
