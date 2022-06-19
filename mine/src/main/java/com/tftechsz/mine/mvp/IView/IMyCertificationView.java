package com.tftechsz.mine.mvp.IView;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.RealStatusInfoDto;

public interface IMyCertificationView extends MvpView {

    void getUserInfoSuccess(UserInfo userInfo);

    void getRealInfoSuccess(RealStatusInfoDto data);

    void getSelfInfoSuccess(RealStatusInfoDto data);

}
