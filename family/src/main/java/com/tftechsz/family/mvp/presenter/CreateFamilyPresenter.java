package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.mvp.IView.ICreateFamilyView;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;

import java.io.File;

public class CreateFamilyPresenter extends BasePresenter<ICreateFamilyView> {

    public FamilyApiService service;

    public CreateFamilyPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 创建家族
     *
     * @param url
     */
    public void createFamily(String name, String url) {
        addNet(service.createFamily(name, url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyInfoDto> response) {
                        if (null == getView()) return;
                        getView().createFamilySuccess(response.getData());
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


    public void uploadAvatar(String name, String path) {
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
                createFamily(name, url);
//                getView().uploadAvatarSuccess(url);
//                getView().hideLoadingDialog();
            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });

    }

}
