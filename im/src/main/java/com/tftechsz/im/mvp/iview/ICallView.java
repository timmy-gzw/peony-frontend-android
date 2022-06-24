package com.tftechsz.im.mvp.iview;

import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.tftechsz.im.model.CallStatusInfo;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.GiftDto;

import java.util.List;

public interface ICallView extends MvpView {
    void getChatUserInfo(List<UserInfo> userInfo);

    void getCallUserInfoSuccess(CallStatusInfo data);

    void checkAcceptCheckSuccess(InviteParamBuilder paramBuilder, String account, ChatMsg data);

    void getCallUserInfoNoNet();

    void hungUp();

    void getCallUserInfoNoNetAgain();

    void hungUpAgain();


    void showGiftAnimation(GiftDto data);

    void cancel();

    void getUserInfoSuccess(UserInfo userInfo);
}
