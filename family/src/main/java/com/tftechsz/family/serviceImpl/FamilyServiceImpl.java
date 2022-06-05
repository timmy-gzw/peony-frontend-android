package com.tftechsz.family.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.FamilyService;
import com.tftechsz.common.utils.RxUtil;

@Route(path = ARouterApi.FAMILY_SERVICE, name = "家族服务")
public class FamilyServiceImpl implements FamilyService {

    FamilyApiService service;

    @Override
    public void init(Context context) {
        service = new RetrofitManager().createFamilyApi(FamilyApiService.class);
    }


    @Override
    public void searchFamily(String tid, ResponseObserver<BaseResponse<FamilyIdDto>> observer) {
        service.searchFamily("",tid).compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void familySign(String fromType,ResponseObserver<BaseResponse<Boolean>> responseObserver) {
        service.familySign(fromType).compose(RxUtil.applySchedulers())
                .subscribe(responseObserver);
    }
}
