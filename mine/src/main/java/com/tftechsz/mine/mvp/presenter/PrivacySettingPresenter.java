package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.PrivacyDto;
import com.tftechsz.mine.mvp.IView.IPrivacySettingView;

public class PrivacySettingPresenter extends BasePresenter<IPrivacySettingView> {

    public MineApiService service;

    public PrivacySettingPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    public  void showPop(){
        CustomPopWindow customPopWindow = new CustomPopWindow(BaseApplication.getInstance());
        customPopWindow.setTitle("权限设置");
        customPopWindow.setContent("未获取此设备地理位置权限,此无法正常使用。打开应用设置页以修改应用权限");
        customPopWindow.setLeftButton("知道了");
        customPopWindow.setRightButton("去设置");
        customPopWindow.setOutSideDismiss(false);
        customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                PermissionUtil.gotoPermission(BaseApplication.getInstance());
            }
        });
        customPopWindow.showPopupWindow();
    }

    /**
     * 获取会员设置特权
     */
    public void getPrivilege() {
        addNet(service.getPrivilege().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PrivacyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PrivacyDto> response) {
                        if (null == getView()) return;
                        getView().getPrivilegeSuccess(response.getData());
                    }
                }));
    }


    /**
     * 设置会员特权
     */
    public void setPrivilege(int type, int value) {
        addNet(service.setPrivilege(type, value).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().setPrivilegeSuccess(type, value, response.getData());
                    }
                }));
    }


}
