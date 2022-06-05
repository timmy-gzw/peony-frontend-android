package com.tftechsz.moment.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.entity.MsgCheckDto;

import java.util.List;

public interface IDynamicView extends MvpView {

    void accostUserSuccess(int userId, AccostDto data);

    void praiseSuccess(int position,Boolean data);

    void deleteTrendSuccess(int position,Boolean data);

    void getTrendSuccess(List<CircleBean> data);

    void getTrendError();

    void getInfoSucess(CircleBean data);

    void getTrendFail(int code,String message);

    void commentSuccess(boolean data);

    void attentionSuccess(boolean data);

    void getInfoFial(String msg);

    void getCheckMsgSuccess(String userId, MsgCheckDto data);
}
