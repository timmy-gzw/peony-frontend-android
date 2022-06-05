package com.tftechsz.home.mvp.presenter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.home.mvp.iview.ISearchView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;

import java.util.List;

public class SearchPresenter extends BasePresenter<ISearchView> {

    public HomeApiService service;
    AccostService accostService;
    MineService mineService;
    private final UserProviderService userService;

    public SearchPresenter() {
        service = RetrofitManager.getInstance().createUserApi(HomeApiService.class);
        accostService = ARouter.getInstance().navigation(AccostService.class);
        mineService  = ARouter.getInstance().navigation(MineService.class);
        userService = ARouter.getInstance().navigation(UserProviderService.class);
    }

    /**
     * 搜索用户
     */
    public void searchUser(int page, String keyword) {
        addNet(service.searchUser(page, keyword).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<UserInfo>>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<List<UserInfo>> response) {
                        if (null == getView()) return;
                        getView().getSearchSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getSearchFail(msg);
                    }
                }));
    }


    /**
     * 搭讪用户
     */
    public void accostUser(int position, String userId) {
        accostService.accostUser(userId, CommonUtil.isBtnTextHome(userService) ? 1 : 2, 6, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                if (null == getView()) return;
                getView().accostUserSuccess(position, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                if (null == getView()) return;
                getView().getSearchFail(msg);
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
