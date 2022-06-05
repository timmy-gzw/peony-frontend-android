package com.tftechsz.family.mvp.presenter;

import android.content.Context;

import com.tftechsz.family.R;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.common.event.RecruitBaseDto;
import com.tftechsz.family.mvp.IView.IFamilyDetailView;
import com.tftechsz.family.widget.pop.FamilyExitPopWindow;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInviteBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import java.io.File;

public class FamilyDetailPresenter extends BasePresenter<IFamilyDetailView> {

    public FamilyApiService service;

    public FamilyDetailPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 家族详情
     */
    public void getFamilyDetail(int family_id) {
        if (getView() != null)
            getView().showLoadingDialog();
        addNet(service.familyDetail(family_id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyInfoDto> response) {
                        if (null == getView() || null == response.getData()) return;
                        getView().hideLoadingDialog();
                        getView().getFamilyDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() != null)
                            getView().hideLoadingDialog();
                    }
                }));
    }

    /**
     * 申请加入家族
     */
    public void apply(int family_id, String invite_user_id) {
        addNet(service.apply(family_id, invite_user_id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView() || null == response.getData()) return;
                        getView().applySuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
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
     * 个人家族详情
     */
    public void getMineFamily() {
        addNet(service.mineFamily().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyInfoDto> response) {
                        if (null == getView()) return;
                        getView().getFamilyDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 是否可以创建家族
     */
    public void getCondition() {
        addNet(service.getCondition().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null == getView() || null == response.getData()) return;
                        getView().getConditionSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
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
                        if (null == getView()) return;
                        getView().hideLoadingDialog();
                    }
                }));
    }

    /**
     * 获取家族邀请
     */
    public void getFamilyInvite() {
        addNet(service.getFamilyInvite().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyInviteBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyInviteBean> response) {
                        if (null == getView()) return;
                        if (response.getData() != null) {
                            getView().getFamilyInviteSuccess(response.getData());
                        }
                    }
                }));
    }


    /**
     * 获取家族邀请
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
     * 家族签到
     */
    public void familySign(String fromType) {
        addNet(service.familySign(fromType).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView() || null == response.getData()) return;
                        getView().familySignSuccess(response.getData());
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

    public void getRecruitBase() {
        addNet(service.getRecruitBase().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RecruitBaseDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RecruitBaseDto> response) {
                        if (null == getView()) return;
                        getView().getRecruitBaseSuccess(response.getData());
                    }
                }));
    }
}
