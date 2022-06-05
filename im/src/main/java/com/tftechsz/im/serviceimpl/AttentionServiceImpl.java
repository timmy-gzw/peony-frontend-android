package com.tftechsz.im.serviceimpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.uikit.PartyChatActivity;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.utils.RxUtil;

@Route(path = ARouterApi.ATTENTION_SERVICE, name = "关注服务")
public class AttentionServiceImpl implements AttentionService {

    ChatApiService service;

    @Override
    public void init(Context context) {
        service = new RetrofitManager().createUserApi(ChatApiService.class);
    }

    @Override
    public void getIsAttention(int uid, ResponseObserver<BaseResponse<Boolean>> observer) {
        service.getIsAttention(uid)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void attentionUser(int uid, ResponseObserver<BaseResponse<Boolean>> observer) {
        service.attentionUser(uid)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void blackUser(int uid, ResponseObserver<BaseResponse<Boolean>> observer) {
        service.black(uid)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

    @Override
    public void finishPartyActivity() {
        if( AppManager.getAppManager().getActivity(PartyChatActivity.class) != null)
            AppManager.getAppManager().finishActivity(PartyChatActivity.class);

        LogUtil.e("============",(AppManager.getAppManager().getActivity(PartyChatActivity.class) != null) + "===");
    }


}
