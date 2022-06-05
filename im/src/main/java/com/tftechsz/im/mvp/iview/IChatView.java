package com.tftechsz.im.mvp.iview;

import com.netease.nim.uikit.OnLineListBean;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.entity.MsgCheckDto;

import java.util.List;

public interface IChatView extends MvpView {


    void getUserInfoSuccess(UserInfo userInfo);

    void checkUserInfoSuccess(String userId, UserInfo userInfo);

    void getChatUserInfo(List<ContactInfo> contact);

    void getContactInfoSuccess();

    void notifyDataSetChanged();


    void getChatHistorySuccess(ChatHistoryDto data);

    void onSuccessUserList(OnLineListBean onLineListBean);

    void getCheckMsgSuccess(String userId, MsgCheckDto data);

    void accostUserSuccess(int position, AccostDto data);
}
