package com.tftechsz.mine.mvp.presenter;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.iservice.CallService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.entity.dto.TrendDto;
import com.tftechsz.mine.mvp.IView.IMineDetailView;

import java.util.List;

public class MineDetailPresenter extends BasePresenter<IMineDetailView> {

    public MineApiService service;
    public MineApiService exchService;
    public MineApiService blogService;
    AccostService accostService;
    AttentionService attentionService;
    CallService callService;
    MineService mineService;
    private final UserProviderService userService;
    private PartyService partyService;


    public MineDetailPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
        blogService = RetrofitManager.getInstance().createBlogApi(MineApiService.class);
        accostService = ARouter.getInstance().navigation(AccostService.class);
        attentionService = ARouter.getInstance().navigation(AttentionService.class);
        callService = ARouter.getInstance().navigation(CallService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        partyService = ARouter.getInstance().navigation(PartyService.class);
        userService = ARouter.getInstance().navigation(UserProviderService.class);

    }


    /**
     * 获取他人用户信息
     */
    public void getUserInfoById(String userId) {
        addNet(service.getUserInfoById(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (getView() == null) return;
                        getView().getUserInfoSuccess(response.getData());
                    }
                }));
    }


    /**
     * 获取用户信息
     */
    public void getUserInfoDetail() {
        addNet(service.getUserInfoDetail().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (getView() == null) return;
                        getView().getUserInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取他人用户收到礼物
     */
    public void getUserGift(int pageSize, String userId) {
        addNet(exchService.getUserGift(pageSize, userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<GiftDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<GiftDto>> response) {
                        if (getView() == null) return;
                        getView().getGiftSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取自己收到礼物
     */
    public void getSelfGift(int pageSize) {
        addNet(exchService.getSelfGift(pageSize).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<GiftDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<GiftDto>> response) {
                        if (getView() == null) return;
                        getView().getGiftSuccess(response.getData());
                    }
                }));
    }


    /**
     * 获取自己动态
     */
    public void getSelfTrend(int pageSize) {
        addNet(blogService.getSelfTrend(pageSize).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<TrendDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<TrendDto>> response) {
                        if (getView() == null) return;
                        getView().getTrendSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取他人动态
     */
    public void getUserTrend(int pageSize, String userId) {
        addNet(blogService.getUserTrend(pageSize, userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<TrendDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<TrendDto>> response) {
                        if (getView() == null) return;
                        getView().getTrendSuccess(response.getData());
                    }
                }));
    }


    /**
     * 获取自己相册
     */
    public void getSelfPhoto(int pageSize) {
        addNet(service.getSelfPhoto(pageSize).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> response) {
                        if (getView() == null) return;
                        getView().getPhotoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取他人相册
     */
    public void getUserPhoto(int pageSize, String userId) {
        addNet(service.getUserPhoto(pageSize, userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> response) {
                        if (getView() == null) return;
                        getView().getPhotoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 关注用户
     */
    public void attentionUser(int id) {
        attentionService = ARouter.getInstance().navigation(AttentionService.class);
        attentionService.attentionUser(id, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;
                getView().attentionSuccess(response.getData());
            }
        });
    }

    /**
     * 获取真人认证信息
     */
    public void getRealInfo() {
        addNet(service.getRealInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                        if (getView() == null) return;
                        getView().getRealInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取实名认证信息
     */
    public void getSelfInfo() {
        addNet(service.getSelfInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                        if (getView() == null) return;
                        getView().getSelfInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 搭讪用户
     */
    public void accostUser(String userId, int accost_type) {
        accostService.accostUser(userId, accost_type, 3, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                if (getView() == null) return;
                getView().accostUserSuccess(response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });
    }

    /**
     * 拉黑用户
     */
    public void blackUser(int userId) {
        attentionService.blackUser(userId, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;
                getView().blackSuccess(response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });
    }

    public void cancelBlack(int userId) {
        addNet(service.cancelBlack(userId).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (getView() == null) return;
                getView().cancelBlackSuccess(response.getData());
            }
        }));
    }


    public void showBlackPop(Context context, int userId) {
        CustomPopWindow pop = new CustomPopWindow(context);
        pop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {
                blackUser(userId);
            }
        });
        pop.setTitle("确定要拉黑TA吗");
        pop.setContent("拉黑后你将收不到对方的消息和呼叫，且在好友列表互相看不到对方");
        pop.showPopupWindow();
    }

    /**
     * 是否有权限
     */
    public void callCheck(String userId) {
        callService.callCheck(userId, "", new ResponseObserver<BaseResponse<CallCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<CallCheckDto> response) {
                if (getView() == null) return;
                getView().getCallCheckSuccess(response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });
    }

    /**
     * 检查私信次数
     */
    public void getMsgCheck(String userId) {
        mineService.getMsgCheck(userId, new ResponseObserver<BaseResponse<MsgCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<MsgCheckDto> response) {
                if (null == getView()) return;
                getView().getCheckMsgSuccess(userId, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                if (null == getView()) return;
            }
        });

    }


    /**
     * 检查是否可以拨打电话
     */
    public void getCallCheck(String userId) {
        if (CommonUtil.showCallTip(partyService)) {
            return;
        }
        if (CommonUtil.showCallTip2(partyService, new CommonUtil.OnSelectListener() {
            @Override
            public void onSure() {
                call(userId);
            }
        }))
            return;
        call(userId);

    }


    public void call(String userId) {
        mineService.getCallCheck(userId, 1, new ResponseObserver<BaseResponse<CallCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<CallCheckDto> response) {
                if (null == getView()) return;
                getView().getCheckCallSuccess(userId, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                if (null == getView()) return;
            }
        });
    }


}
