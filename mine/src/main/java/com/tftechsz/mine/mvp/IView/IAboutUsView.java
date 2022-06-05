package com.tftechsz.mine.mvp.IView;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.MvpView;

public interface IAboutUsView extends MvpView {

    void getUpdateCheckSuccess(ConfigInfo.MineInfo data);

}
