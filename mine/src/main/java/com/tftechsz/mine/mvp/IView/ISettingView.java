package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.SignNumBean;

public interface ISettingView extends MvpView {

    void exitSuccess(boolean data);

    void num(SignNumBean bean);

}
