package com.tftechsz.mine.mvp.presenter;

import android.content.Context;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.LogoutStatusDto;
import com.tftechsz.mine.mvp.IView.ILogoutView;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class ILogoutPresenter extends BasePresenter<ILogoutView> {
    public MineApiService configService;
    private CustomPopWindow mCustomPopWindow;

    public ILogoutPresenter() {
        configService = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
    }

    public void getLogoutStatus() {
        addNet(configService.getLogoutStatus().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<LogoutStatusDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<LogoutStatusDto> response) {
                        if (getView() == null) return;
                        getView().getLogoutStatusSuccess(response.getData());
                    }
                }));
    }

    public void destroyAccount() {
        addNet(RetrofitManager.getInstance().createUserApi(MineApiService.class)
                .destroyAccount()
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<LogoutStatusDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<LogoutStatusDto> response) {
                        if (getView() == null) return;
                        getView().destroyAccountSuccess(response.getData());
                    }
                }));
    }

    public void unDestroyAccount() {
        addNet(RetrofitManager.getInstance().createUserApi(MineApiService.class)
                .unDestroyAccount()
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (getView() == null) return;
                        getView().undestroyAccountSuccess(response.getData());
                    }
                }));
    }


    public void showPop(Context context, String repeat_msg) {
        if (mCustomPopWindow == null)
            mCustomPopWindow = new CustomPopWindow(context).setContent(repeat_msg)
                    .setLeftButton(context.getString(R.string.cancel))
                    .setRightButton(context.getString(R.string.confirm))
                    .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onSure() {
                            destroyAccount();
                        }
                    });
        mCustomPopWindow.setOutSideDismiss(false);
        mCustomPopWindow.setBackPressEnable(false);
        mCustomPopWindow.showPopupWindow();
    }
}
