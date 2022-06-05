package com.tftechsz.home.mvp.iview;

import com.tftechsz.common.base.MvpView;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.netease.nim.uikit.common.UserInfo;

import java.util.List;

public interface ISearchView extends MvpView {


    void getSearchSuccess(List<UserInfo> data);

    void getSearchFail(String msg);

    void accostUserSuccess(int userId, AccostDto data);

    void getCheckMsgSuccess(String userId, MsgCheckDto data);

}
