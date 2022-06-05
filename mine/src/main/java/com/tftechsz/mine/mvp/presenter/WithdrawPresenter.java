package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.mine.mvp.IView.IWithdrawView;

public class WithdrawPresenter extends BasePresenter<IWithdrawView> {

    public MineApiService service;
    public MineApiService userService;

    public WithdrawPresenter() {
        service = RetrofitManager.getInstance().createExchApi(MineApiService.class);
        userService = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 提现方式获取
     */
    public void withdrawWay() {
        addNet(userService.withdrawWay().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<WithdrawReq.Withdraw>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<WithdrawReq.Withdraw> response) {
                        if (null != getView())
                            getView().withdrawWaySuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 支付宝申请提现
     */
    public void withdraw(int typeId, String name, String account, String card, String phone) {
        WithdrawReq withdrawReq = new WithdrawReq();
        withdrawReq.type_id = typeId;
        WithdrawReq.Withdraw withdraw = new WithdrawReq.Withdraw();
        withdraw.account = account;
        withdraw.name = name;
        withdraw.identity = card;
        withdraw.phone = phone;
        withdrawReq.withdraw = withdraw;
        addNet(service.withdraw(withdrawReq).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null != getView())
                            getView().withdrawSuccess(response.getMessage());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


}
