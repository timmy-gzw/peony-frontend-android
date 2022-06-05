package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.WithdrawReq;

public interface IWithdrawView extends MvpView {

    void withdrawSuccess(String msg);

    void withdrawWaySuccess(WithdrawReq.Withdraw msg);

}
