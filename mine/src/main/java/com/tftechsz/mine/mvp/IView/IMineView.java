package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.netease.nim.uikit.common.UserInfo;

public interface IMineView extends  MvpView {

    void getUserInfoSuccess(UserInfo userInfo);
}
