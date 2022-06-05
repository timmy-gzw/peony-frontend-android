package com.tftechsz.im.mvp.presenter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.mvp.iview.ITeamCallView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;

public class TeamCallPresenter extends BasePresenter<ITeamCallView> {

    MineService mineService;
    ChatApiService service;
    UserProviderService userProviderService;

    public TeamCallPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        userProviderService = ARouter.getInstance().navigation(UserProviderService.class);
    }




}
