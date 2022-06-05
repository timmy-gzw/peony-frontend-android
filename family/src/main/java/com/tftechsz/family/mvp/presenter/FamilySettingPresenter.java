package com.tftechsz.family.mvp.presenter;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.mvp.IView.IFamilySettingView;
import com.tftechsz.family.widget.pop.FamilyExitPopWindow;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.MasterChangeStatus;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import java.io.File;

public class FamilySettingPresenter extends BasePresenter<IFamilySettingView> {

    public FamilyApiService service;

    public FamilySettingPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 退出家族
     */
    public void leave(String message) {
        addNet(service.leave(message).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || null == response.getData()) return;
                        getView().applyLeaveSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 是否禁言
     */
    public void muteAll(String familyId, int operation) {
        addNet(service.muteAll(familyId, operation).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        if (response.getData() != null) {
                            getView().muteAllSuccess(response.getData(), operation);
                        }
                    }
                }));
    }

    /**
     * 显示退出家族弹窗
     *
     * @param context
     */
    public void showExitPop(Context context) {
        FamilyExitPopWindow popWindow = new FamilyExitPopWindow(context);
        popWindow.addOnClickListener(() -> {
            showExitPopSecond(context);

        });
        popWindow.showPopupWindow();
    }

    /**
     * 显示退出家族弹窗第二步
     */
    public void showExitPopSecond(Context context) {
        CustomPopWindow customPopWindow = new CustomPopWindow(context);
        customPopWindow.setContent(context.getString(R.string.exit_family_tip));
        customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                leave("");
            }
        });
        customPopWindow.showPopupWindow();
    }

    /**
     * 更新家族信息
     */
    public void editFamilyInfo(String url) {
        addNet(service.editFamilyInfo("icon", url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().updateFamilyIconSuccess(response.getData());
                        getView().hideLoadingDialog();

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        ToastUtils.showShort(msg);
                        if (null == getView()) return;
                        getView().hideLoadingDialog();
                    }
                }));
    }

    /**
     * 上传头像
     */
    public void uploadAvatar(String path) {
        getView().showLoadingDialog();
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_FAMILY, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                editFamilyInfo(url);

            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });

    }

    /**
     * 更新家族信息
     */
    public void master_change_status() {
        addNet(service.master_change_status().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<MasterChangeStatus>>() {
                    @Override
                    public void onSuccess(BaseResponse<MasterChangeStatus> response) {
                        if (null == getView() || response == null || response.getData() == null)
                            return;
                        getView().masterChangeStatusSuccess(response.getData());
                    }
                }));
    }

}
