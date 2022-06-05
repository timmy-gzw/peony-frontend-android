package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.mine.entity.req.CompleteReq;

public interface IImproveInfoView extends MvpView {

    void chooseBirthday(String day);

    void completeInfoSuccess(String msg);

    void getCompleteInfoSuccess(CompleteReq data);

    void uploadAvatarSucceeded(String url);

    void getRandomNicknameSucceeded(String nikename);
}
