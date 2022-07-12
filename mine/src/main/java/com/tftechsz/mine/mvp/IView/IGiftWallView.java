package com.tftechsz.mine.mvp.IView;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.GiftDto;

import java.util.List;

public interface IGiftWallView extends MvpView {

    void onGetUserInfoSuccess(UserInfo userInfo);

    void onGetGiftSuccess(List<GiftDto> data);

}
