package com.tftechsz.mine.mvp.presenter;

import android.text.TextUtils;

import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.MinePhotoDto;
import com.tftechsz.mine.mvp.IView.IMinePhotoViewNew;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MinePhotoPresenterNew extends BasePresenter<IMinePhotoViewNew> {

    public MineApiService service;

    public MinePhotoPresenterNew() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    public void getPhoto() {
        addNet(service.getPhoto().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<MinePhotoDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<MinePhotoDto>> response) {
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                        getView().getPhotoSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                    }
                }));
    }

    public void pictureSort(List<MinePhotoDto> data) {
        ArrayList<String> list = new ArrayList<>();
        for (MinePhotoDto media : data) {
            if (!media.getUrl().equals("ADD")) {
                list.add(media.getUrl());
            }
        }
        if (list.size() == 0) {
            return;
        }
        addNet(service.pictureSort(ArrayList2String(list)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                        getView().sortPhotoSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                    }
                }));
    }

    // ArrayList类型转成String类型
    private String ArrayList2String(ArrayList<String> arrayList) {
        StringBuilder result = new StringBuilder("[");
        StringBuilder sb = new StringBuilder();
        if (arrayList != null && arrayList.size() > 0) {
            for (String item : arrayList) {
                // 把列表中的每条数据用逗号分割开来，然后拼接成字符串
                result.append("\"").append(item).append("\",");
            }
            // 去掉最后一个逗号
            sb.append(result.substring(0, result.length() - 1)).append("]");
        }
        return sb.toString();
    }

    public void updatePhoto(LocalMedia media) {
        if (getView() != null)
            getView().showLoadingDialog();
        String path = null;
        try {
            path = !TextUtils.isEmpty(media.getCutPath()) ? media.getCutPath()
                    : (!TextUtils.isEmpty(media.getCompressPath()) ? media.getCompressPath() :
                    (!TextUtils.isEmpty(media.getRealPath()) ? media.getRealPath() :
                            (!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getOriginalPath())));
        } catch (Exception e) {
            return;
        }
        Utils.logE("上传图片....: " + path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_PHOTO_TYPE, UploadHelper.PATH_USER_PHOTO, UploadHelper.TYPE_IMAGE, new File(path), new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long cur, long total) {
            }

            @Override
            public void onSuccess(String url) {
                Utils.logE("图片地址....: " + url);
                upload(url);
            }

            @Override
            public void onError() {
                if (getView() == null) return;
                Utils.toast("图片上传出错, 请重试");
                getView().hideLoadingDialog();
            }
        });
    }

    private void upload(String url) {
        addNet(service.uploadPic(url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                        getView().uploadPhotoSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                    }
                }));
    }

    public void remove(String url) {
        addNet(service.removePic(url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                        getView().uploadPhotoSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                    }
                }));

    }
}
