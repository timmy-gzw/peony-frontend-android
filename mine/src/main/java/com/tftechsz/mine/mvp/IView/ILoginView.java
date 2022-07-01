package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.LoginDto;

public interface ILoginView extends MvpView {

    void getOpenLoginAuthStatusSuccess();

    void getOpenLoginAuthStatusFail();

    void onFail(int flag);

    void cancelLoginAuth();

    /**
     * 获取验证码成功
     *
     * @param data
     */
    void getCodeSuccess(String data);

    //去完善个人资料
    void loginSuccess(LoginDto data);

    void getConfigSuccess(String msg, int flag);

    void agreementSuccess();

    void onGetReviewConfig(boolean r);

}
