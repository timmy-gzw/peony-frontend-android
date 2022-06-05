package com.tftechsz.home.mvp.presenter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.home.entity.InfoPictureBean;
import com.tftechsz.home.mvp.iview.IPicBrowserView;
import com.tftechsz.common.base.BasePresenter;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.MineService;

/**
 * 包 名 : com.tftechsz.home.mvp.presenter
 * 描 述 : TODO
 */
public class PicBrowserPresenter extends BasePresenter<IPicBrowserView> {
    public HomeApiService service;
    AccostService accostService;
    MineService mineService;


    public PicBrowserPresenter() {
        service = RetrofitManager.getInstance().createUserApi(HomeApiService.class);
        accostService = ARouter.getInstance().navigation(AccostService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
    }

    public void getInfoPicture(int uid) {
        addNet(service.getInfoPicture(uid).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<InfoPictureBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<InfoPictureBean> response) {
                        if (null == getView()) return;
                        getView().getInfoPictureSucess(response.getData());
                    }
                }));
    }

    public void picLike(int uid) {
        addNet(service.picLike(uid).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().picLikeSucess(response.getData());
                    }
                }));
    }

    public void accostUser(String userId,int accost_type) {
        accostService.accostUser(userId, accost_type,5, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                if (null == getView()) return;
                getView().accostUserSuccess(response.getData());
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

}
