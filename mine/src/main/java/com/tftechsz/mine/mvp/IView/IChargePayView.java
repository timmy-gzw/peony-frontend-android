package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.PaymentDto;

public interface IChargePayView extends MvpView {

    void onGetCoin(IntegralDto bean);

    void onGetRechargeInfo(PaymentDto bean);

    void paySuccess();

    void payFail();

}
