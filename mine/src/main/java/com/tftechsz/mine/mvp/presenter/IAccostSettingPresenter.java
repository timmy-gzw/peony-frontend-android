package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.AccostType;
import com.tftechsz.mine.entity.UpdateAccostSettingBean;
import com.tftechsz.mine.entity.req.DelAccostSettingBean;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class IAccostSettingPresenter extends BasePresenter<IAccostSettingView> {

    public MineApiService service;

    public IAccostSettingPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    /* 获取**/
    public void getAccostSettingList(AccostType type) {
        addNet(service.getAccostSettingList(filterType(type)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<AccostSettingListBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<AccostSettingListBean>> response) {
                        if (getView() == null) return;
                        getView().getAccostListSuccess(response.getData());
                    }
                }));
    }

    /* 添加**/
    public void addAccostSetting(AccostType type, AccostSettingListBean bean) {
        addNet(service.addAccostSetting(new UpdateAccostSettingBean(filterType(type), bean)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().addAccostSettingSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().addAccostSettingError();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getView() == null) return;
                        getView().addAccostSettingError();
                    }
                }));
    }

    /* 更新**/
    public void updateAccostSetting(int id, int position, AccostType type, AccostSettingListBean bean) {
        addNet(service.updateAccostSetting(new UpdateAccostSettingBean(id, filterType(type), bean)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().updateAccostSettingSuccess(position);
                    }
                }));
    }

    /* 删除**/
    public void delAccostSetting(int position, DelAccostSettingBean delIds) {
        addNet(service.delAccostSetting(delIds).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().delAccostSettingSuccess(position);
                    }
                }));
    }

    private String filterType(AccostType accostType) {
        switch (accostType) {
            case VOICE:
                return "voice";
            case PICTURE:
                return "picture";
        }
        return "text";
    }

}
