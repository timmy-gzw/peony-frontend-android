package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.tftechsz.common.entity.SignInSuccessBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * main
 */
public interface MainService extends IProvider {
    /**
     * 签到
     */
    void signIn(ResponseObserver<BaseResponse<SignInSuccessBean>> responseObserver);

}
