package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.RealStatusInfoDto;

public interface IExchangeDetailView extends MvpView {

    void getRealInfoSuccess(RealStatusInfoDto data);

    void getSelfInfoSuccess(RealStatusInfoDto data);

    void exchange(Boolean data);

}
