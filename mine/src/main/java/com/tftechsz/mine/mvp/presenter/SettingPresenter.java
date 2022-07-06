package com.tftechsz.mine.mvp.presenter;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.SignNumBean;
import com.tftechsz.mine.mvp.IView.ISettingView;

public class SettingPresenter extends BasePresenter<ISettingView> {
    public MineApiService service;

    public SettingPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    public void loginOutPop(Context context) {
        CustomPopWindow popWindow = new CustomPopWindow(context);
        popWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                loginOut();
            }
        });
        popWindow.setTitle("提示");
        popWindow.setContent("确定退出登录?");
        popWindow.showPopupWindow();
    }


    /**
     * 退出登录
     */
    public void loginOut() {
        PartyService partyService = ARouter.getInstance().navigation(PartyService.class);
        addNet(service.loginOut().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData()) {
                            if (partyService.isRunFloatService()) {
                                partyService.stopFloatService();
                            }
                            SPUtils.put(Constants.IS_COMPLETE_INFO, 0);
                            NIMClient.getService(AuthService.class).logout();
                            UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
                            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.my != null){
                                ConfigInfo configInfo = service.getConfigInfo();
                                configInfo.share_config.my.clear();
                                service.setConfigInfo(new Gson().toJson(configInfo));
                            }
                            AppManager.getAppManager().finishAllActivity();
                            ARouterUtils.toLoginActivity(ARouterApi.MINE_LOGIN);
                            //重置用户协议勾选状态
                            MMKVUtils.getInstance().encode(Constants.AGREED_TO_TOS, false);
                        }
                    }
                }));
    }


    /**
     * 聊天卡数量
     */
    public void getSignChatNum() {
        addNet(service.getSignChatNum().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<SignNumBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<SignNumBean> response) {
                        if (response.getData() != null) {
                            getView().num(response.getData());
                        }
                    }
                }));
    }


}
