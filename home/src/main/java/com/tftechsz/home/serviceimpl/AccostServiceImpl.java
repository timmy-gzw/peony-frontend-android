package com.tftechsz.home.serviceimpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.RxUtil;

@Route(path = ARouterApi.ACCOST_SERVICE, name = "搭讪服务")
public class AccostServiceImpl implements AccostService {

    HomeApiService service;

    @Override
    public void init(Context context) {
        service = new RetrofitManager().createUserApi(HomeApiService.class);
    }

    @Override
    public void accostUser(String userId, int accost_type, int accost_from, ResponseObserver<BaseResponse<AccostDto>> observer) {
        CommonUtil.getToken(() -> service.accostUser(userId,accost_type,accost_from)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer));
    }



}
