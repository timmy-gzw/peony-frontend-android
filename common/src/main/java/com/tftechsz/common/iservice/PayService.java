package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * 支付
 */
public interface PayService extends IProvider {
    /**
     * 支付宝
     */
    void alipay(int type_id, String from_type, ResponseObserver<BaseResponse<String>> responseObserver);

    /**
     * 微信
     */
    void wechatPay(int type_id, String from_type, ResponseObserver<BaseResponse<WxPayResultInfo>> responseObserver);


    void SXYalipay(int type_id, String from_type, ResponseObserver<BaseResponse<String>> responseObserver);

    void SXYWeChatPay(int type_id, String from_type, ResponseObserver<BaseResponse<SXYWxPayResultInfo>> responseObserver);
}
