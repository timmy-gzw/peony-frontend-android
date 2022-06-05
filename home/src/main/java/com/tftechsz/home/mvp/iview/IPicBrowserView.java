package com.tftechsz.home.mvp.iview;

import com.tftechsz.home.entity.InfoPictureBean;
import com.tftechsz.common.base.MvpView;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.entity.MsgCheckDto;

/**
 * 包 名 : com.tftechsz.home.mvp.iview
 * 描 述 : TODO
 */
public interface IPicBrowserView extends MvpView {
    void getInfoPictureSucess(InfoPictureBean infoPictureBean);

    void picLikeSucess(Boolean data);

    void accostUserSuccess(AccostDto data);

    void getCheckMsgSuccess(String userId, MsgCheckDto data);
}
