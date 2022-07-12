package com.tftechsz.mine.mvp.IView;

import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.entity.dto.TrendDto;

import java.util.List;

public interface IMineDetailView extends MvpView {


    void getUserInfoSuccess(UserInfo userInfo);

    void getGiftSuccess(List<GiftDto> data);

    void getTrendSuccess(List<TrendDto> data);

    void getPhotoSuccess(List<String> data);

    void accostUserSuccess(AccostDto data);

    void attentionSuccess(boolean isAttention);

    void blackSuccess(boolean isBlack);

    void cancelBlackSuccess(boolean isBlack);

    void getRealInfoSuccess(RealStatusInfoDto data);

    void getSelfInfoSuccess(RealStatusInfoDto data);

    void getCallCheckSuccess(CallCheckDto data);

    void getCheckMsgSuccess(String userId, MsgCheckDto data, boolean isAutoShowGiftPanel);

    void getCheckCallSuccess(String userId, CallCheckDto data);
}
