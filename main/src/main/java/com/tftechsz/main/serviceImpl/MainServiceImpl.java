package com.tftechsz.main.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.entity.SignInSuccessBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MainService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.main.api.MainApiService;

@Route(path = ARouterApi.SERVICE_MAIN, name = "Main服务")
public class MainServiceImpl implements MainService {

    MainApiService service;

    @Override
    public void init(Context context) {
        service = new RetrofitManager().createUserApi(MainApiService.class);
    }

    @Override
    public void signIn(ResponseObserver<BaseResponse<SignInSuccessBean>> observer) {
        service.sign()
                .compose(RxUtil.<BaseResponse<SignInSuccessBean>>applySchedulers())
                .subscribe(observer);
    }
}
