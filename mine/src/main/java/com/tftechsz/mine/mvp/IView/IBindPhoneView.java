package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : TODO
 */
public interface IBindPhoneView extends MvpView {
    void getCodeSuccess(String data);

    void bindPhoneSuccess(String data);

    void completeInfoSuccess(String data);
}
