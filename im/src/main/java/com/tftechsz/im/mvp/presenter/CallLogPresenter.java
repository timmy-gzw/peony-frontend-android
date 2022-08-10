package com.tftechsz.im.mvp.presenter;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.CallLogDto;
import com.tftechsz.im.mvp.iview.ICallLogView;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.RechargeBeforePop;

import java.util.List;

/**
 * 包 名 : com.tftechsz.im.mvp.presenter
 * 描 述 : TODO
 */
public class CallLogPresenter extends BasePresenter<ICallLogView> {

    MineService mineService;
    ChatApiService service;
    UserProviderService userProviderService;
    private RechargePopWindow rechargePopWindow;
    private RechargeBeforePop beforePop;

    public CallLogPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        userProviderService = ARouter.getInstance().navigation(UserProviderService.class);
    }


    public void getCallList(int page) {
        addNet(service
                .getCallList(page)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<CallLogDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<CallLogDto>> response) {
                        if (getView() == null) return;
                        getView().getCallListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().getCallListError();
                    }
                }));
    }


    public void checkCallMsg(String sessionId, int type) {
        mineService.getCallCheck(sessionId, 3, new ResponseObserver<BaseResponse<CallCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<CallCheckDto> response) {
                CallCheckDto data = response.getData();
                if (null == data || !com.tftechsz.common.utils.CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, userProviderService.getUserInfo())) {
                    if (data != null && data.list != null && null != data.list.video && type == 2) {
                        if (data.list.video.is_lock) {
                            if (null != data.error && null != data.error.intimacy) {
                                showCustomPop(data.error.intimacy.msg);
                            } else if (null != data.error && null != data.error.video) {
                                if (TextUtils.equals(data.error.video.cmd_type, Constants.DIRECT_RECHARGE)) {
                                    if (userProviderService.getConfigInfo() != null && userProviderService.getConfigInfo().share_config != null && userProviderService.getConfigInfo().share_config.is_limit_from_channel == 1) {
                                        if (beforePop == null)
                                            beforePop = new RechargeBeforePop(BaseApplication.getInstance());
                                        beforePop.addOnClickListener(() -> showRechargePop(1, sessionId));
                                        beforePop.showPopupWindow();
                                    } else {
                                        showRechargePop(1, sessionId);
                                    }
                                } else {
                                    showCustomPop(data.error.video.msg);
                                }
                            }
                        } else {
                            userProviderService.setMatchType("");
                            ChatMsgUtil.callMessage(type, String.valueOf(userProviderService.getUserId()), sessionId, "", false);
                        }
                    } else if (data != null && data.list != null && null != data.list.voice && type == 1) {
                        if (data.list.voice.is_lock) {
                            if (null != data.error && null != data.error.intimacy) {
                                showCustomPop(data.error.intimacy.msg);
                            } else if (null != data.error && null != data.error.voice) {
                                if (TextUtils.equals(data.error.voice.cmd_type, Constants.DIRECT_RECHARGE)) {
                                    if (userProviderService.getConfigInfo() != null && userProviderService.getConfigInfo().share_config != null && userProviderService.getConfigInfo().share_config.is_limit_from_channel == 1) {
                                        if (beforePop == null)
                                            beforePop = new RechargeBeforePop(BaseApplication.getInstance());
                                        beforePop.addOnClickListener(() -> showRechargePop(1, sessionId));
                                        beforePop.showPopupWindow();
                                    } else {
                                        showRechargePop(3, sessionId);
                                    }
                                } else {
                                    showCustomPop(data.error.voice.msg);
                                }
                            }
                        } else {
                            userProviderService.setMatchType("");
                            ChatMsgUtil.callMessage(type, String.valueOf(userProviderService.getUserId()), sessionId, "", false);
                        }
                    }
                }
            }
        });

    }

    /**
     * 显示充值列表
     */
    private void showRechargePop(int scene, String userId) {
        if (rechargePopWindow == null)
            rechargePopWindow = new RechargePopWindow(BaseApplication.getInstance(), 3, scene, 0, 0, userId);
        rechargePopWindow.getCoin();
        rechargePopWindow.requestData();
        rechargePopWindow.showPopupWindow();
    }


    private void showCustomPop(String content) {
        CustomPopWindow customPopWindow = new CustomPopWindow(BaseApplication.getInstance());
        customPopWindow.setContent(content);
        customPopWindow.setRightGone();
        customPopWindow.showPopupWindow();
    }


}
