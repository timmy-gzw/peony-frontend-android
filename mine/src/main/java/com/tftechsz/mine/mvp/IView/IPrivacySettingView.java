package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.dto.PrivacyDto;

public interface IPrivacySettingView extends MvpView {


    void getPrivilegeSuccess(PrivacyDto data);

    void setPrivilegeSuccess(int type, int value, Boolean data);

    void setImgCover(String data);

}
