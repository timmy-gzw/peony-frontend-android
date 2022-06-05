package com.tftechsz.home.mvp.iview;

import com.tftechsz.home.entity.req.RecommendReq;
import com.tftechsz.common.base.MvpView;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.entity.MsgCheckDto;

public interface IHomeView extends  MvpView {


    void getRecommendSuccess(RecommendReq data,boolean uploadTop);

    void getRecommendFail(String msg);

    void accostUserSuccess(int userId,AccostDto data);

    void getCheckMsgSuccess(String userId,MsgCheckDto data);
}
