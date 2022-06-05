package com.tftechsz.mine.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.PaymentDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.api.MineApiService;

@Route(path = ARouterApi.MINE_SERVICE, name = "个人服务")
public class MineServiceImpl implements MineService {

    MineApiService service;


    @Override
    public void init(Context context) {
        service = new RetrofitManager().createUserApi(MineApiService.class);
    }

    @Override
    public void getRealInfo(ResponseObserver<BaseResponse<RealStatusInfoDto>> observer) {
        service.getRealInfo()
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void getSelfInfo(ResponseObserver<BaseResponse<RealStatusInfoDto>> observer) {
        service.getSelfInfo()
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void getUserInfo(ResponseObserver<BaseResponse<UserInfo>> observer) {
        service.getUserInfo()
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void getMsgCheck(String user_id, ResponseObserver<BaseResponse<MsgCheckDto>> observer) {
        RetrofitManager.getInstance().createUserApi(MineApiService.class).getMsgCheck(user_id, 2)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void getCallCheck(String user_id, int from, ResponseObserver<BaseResponse<CallCheckDto>> observer) {
        service.getCallCheck(user_id, from)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void getRechargeNewList(ResponseObserver<BaseResponse<PaymentDto>> observer) {
        new RetrofitManager().createConfigApi(MineApiService.class).getRechargeNewList()
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void trackEvent(String scene, String event, String index, String extend, ResponseObserver<BaseResponse<Boolean>> observer) {

        if (observer != null) {
            service.trackEvent(scene, event, index, extend)
                    .compose(RxUtil.applySchedulers())
                    .subscribe(observer);
        } else {
            service.trackEvent(scene, event, index, extend)
                    .compose(RxUtil.applySchedulers())
                    .subscribe(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> booleanBaseResponse) {

                        }
                    });
        }
    }

    @Override
    public void getUserInfoById(String user_id, ResponseObserver<BaseResponse<UserInfo>> observer) {
        service.getUserInfoById(user_id)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);

    }


}
