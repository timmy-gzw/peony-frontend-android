package com.tftechsz.common.base;

/**
 * mvp activity实现接口父类
 */
public interface MvpView {

    void showLoadingDialog();

    boolean isLoadingDialogShow();

    void toastTip(String msg);

    void hideLoadingDialog();

}
