package com.tftechsz.mine.mvp.IView;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.MvpView;

import java.util.List;

public interface IMineInfoView extends MvpView {


    void getChooseBirthday(String data,String constellation);

    void getChooseAddress(String data);

    void getChooseHeight(String data);

    void getChooseWeight(String data);

    void getChooseIncome(String data);

    void updateUserInfoSuccess(String data);

    void uploadAvatarSuccess(String data);

    void uploadAvatarFail(String data);

    void getUserInfoSuccess(UserInfo data);

    void getPhotoSuccess(List<String> data);
}
