package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.req.GetBindData;

/**
 * 包 名 : com.tftechsz.mine.mvp.IView
 * 描 述 : TODO
 */
public interface IAccountBindingView extends MvpView {
    void getBindDataSuccess(GetBindData data);

    void bindPhoneSuccess(String data);
}
