package com.tftechsz.mine.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.PayService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.api.MineApiService;

@Route(path = ARouterApi.MINE_PAY_SERVICE, name = "支付")
public class PayServiceImpl implements PayService {

    MineApiService service;


    @Override
    public void init(Context context) {
        service = new RetrofitManager().createPaymentApi(MineApiService.class);
    }

    @Override
    public void alipay(int type_id, String from_type, ResponseObserver<BaseResponse<String>> observer) {
        service.alipay(type_id, from_type)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void wechatPay(int type_id, String from_type, ResponseObserver<BaseResponse<WxPayResultInfo>> observer) {
        service.wechatPay(type_id, from_type)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void SXYalipay(int type_id, String from_type, ResponseObserver<BaseResponse<String>> observer) {
        service.SXYalipay(type_id, from_type)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void SXYWeChatPay(int type_id, String from_type, ResponseObserver<BaseResponse<SXYWxPayResultInfo>> observer) {
        service.SXYwechatPay(type_id, from_type)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }
}
