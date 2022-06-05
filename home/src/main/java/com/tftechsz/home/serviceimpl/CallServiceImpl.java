package com.tftechsz.home.serviceimpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.CallService;
import com.tftechsz.common.utils.RxUtil;

@Route(path = ARouterApi.CALL_SERVICE, name = "语音拨打权限")
public class CallServiceImpl implements CallService {

    HomeApiService service;

    @Override
    public void init(Context context) {
        service = new RetrofitManager().createIMApi(HomeApiService.class);
    }


    @Override
    public void callCheck(String userId, String type, ResponseObserver<BaseResponse<CallCheckDto>> observer) {
        service.callCheck(userId, type).compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }
}
