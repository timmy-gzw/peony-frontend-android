package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.entity.dto.VipConfigBean;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.DressUpBean;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.VipPrivilegeBean;
import com.tftechsz.mine.mvp.IView.IVipView;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class IVipPresenter extends BasePresenter<IVipView> {
    public MineApiService configService;
    public MineApiService userService;

    public IVipPresenter() {
        configService = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
        userService = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    //vip价格
    public void getVipPrice() {
        addNet(configService.getVipPrice().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<VipPriceBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<VipPriceBean>> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getVipPriceSuccess(response.getData());
                    }
                }));
    }

    //vip会员特权
    public void getVipPrivilege() {
        addNet(configService.getVipPrivilege().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<VipPrivilegeBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<VipPrivilegeBean>> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getVipPrivilegeSuccess(response.getData());
                    }
                }));
    }

    //vip装扮
    public void getVipConfig() {
        addNet(userService.getVipConfig().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<VipConfigBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<VipConfigBean> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getVipConfigSuccess(response.getData());
                    }
                }));
    }

    //获取vip素材
    public void getMaterialAll(int type) {
        addNet(configService.getMaterialAll(type).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<DressUpBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<DressUpBean>> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getDressUpSuccess(response.getData());
                    }
                }));
    }

    //设置特权
    public void postVipConfig(int type, int material_id) {
        addNet(userService.postVipConfig(type, material_id).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().postVipConfigSuccess();
                    }
                }));
    }
}
