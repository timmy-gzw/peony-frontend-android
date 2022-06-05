package com.tftechsz.mine.mvp.presenter;

import android.text.TextUtils;

import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IMinePhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MinePhotoPresenter extends BasePresenter<IMinePhotoView> {


    public MineApiService service;

    public MinePhotoPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 上传图片
     *
     * @param images
     * @param mPhotoList 使用上传成功后的路径，否者显示不了
     * @param items
     * @param index
     */
    public void uploadFile(StringBuilder images, List<LocalMedia> mPhotoList, List<LocalMedia> items, final int index) {
        if (getView() != null)
            getView().showLoadingDialog();
        if (index == 0) {
            if (mPhotoList.size() > 1) {
                for (int i = 0; i < mPhotoList.size(); i++) {
                    if (!TextUtils.equals(mPhotoList.get(i).getPath(), "ADD")) {
                        String path = mPhotoList.get(i).getPath();
                        images.append(path);
                        images.append(",");
                    }
                }
            }
        }
        if (items.size() <= index) {
            uploadPhoto(items, images.substring(0, images.length() - 1));
        } else {
            LocalMedia localMedia = items.get(index);
            String path = !TextUtils.isEmpty(localMedia.getCutPath()) ? localMedia.getCutPath()
                    : (!TextUtils.isEmpty(localMedia.getCompressPath()) ? localMedia.getCompressPath() : localMedia.getRealPath());
            File file = new File(path);
            UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_PHOTO_TYPE, UploadHelper.PATH_USER_PHOTO, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onLoading(long cur, long total) {
                    int p = (int) (1f * cur / total * 100);
                }

                @Override
                public void onSuccess(String url) {
                    images.append(url);
                    images.append(",");
                    uploadFile(images, mPhotoList, items, index + 1);
                    LogUtil.e("==========", url);
                }

                @Override
                public void onError() {
                    if (getView() == null) return;
                    getView().hideLoadingDialog();
                }
            });
        }
    }

    public void uploadFile(StringBuffer images, List<LocalMedia> mPhotoList) {
        if (getView() != null)
            getView().showLoadingDialog();
        String s = images.toString();
        if (mPhotoList.size() == 0) {
            Utils.logE("开始上传: " + s);
            uploadPhoto(new ArrayList<>(), s);
            return;
        }
        LocalMedia media = mPhotoList.get(0);
        if (TextUtils.equals(media.getPath(), "ADD") || (!TextUtils.isEmpty(media.getPath()) && (media.getPath().startsWith("/user/photo/") || media.getPath().startsWith("http")))) { //已经上传了的网络图
            if (!TextUtils.equals(media.getPath(), "ADD")) {
                images.append(media.getPath()).append(",");
            }
            mPhotoList.remove(media);
            uploadFile(images, mPhotoList);
        } else {
            String path = !TextUtils.isEmpty(media.getCutPath()) ? media.getCutPath()
                    : (!TextUtils.isEmpty(media.getCompressPath()) ? media.getCompressPath() : media.getRealPath());
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
                    images.append(url).append(",");
                    mPhotoList.remove(media);
                    Utils.logE("图片地址....: " + url);
                    uploadFile(images, mPhotoList);
                }

                @Override
                public void onError() {
                    if (getView() == null) return;
                    Utils.toast("图片上传出错, 请重试");
                    getView().hideLoadingDialog();
                }
            });
        }
    }


    /**
     * 更新顺序
     */
    public void updatePosition(List<LocalMedia> mPhotoList) {
        StringBuilder sb = new StringBuilder();
        LogUtil.e("===================", mPhotoList.size() + "");
        for (int i = 0; i < mPhotoList.size(); i++) {
            if (!TextUtils.equals("ADD", mPhotoList.get(i).getPath())) {
                sb.append(mPhotoList.get(i).getPath());
                sb.append(",");
                LogUtil.e("===================", mPhotoList.get(i).getPath());
            }
        }
        if (TextUtils.isEmpty(sb)) {
            uploadPhoto(mPhotoList, "");
        } else {
            uploadPhoto(mPhotoList, sb.substring(0, sb.length() - 1));
        }
    }

    /**
     * 重新加载
     */
    public void reLoad(List<LocalMedia> mPhotoList, List<LocalMedia> data) {
        mPhotoList.clear();
        LocalMedia localMedia = new LocalMedia();
        localMedia.setPath("ADD");
        mPhotoList.add(localMedia);
        mPhotoList.addAll(data);
    }


    /**
     * 获取自己相册
     */
    public void getSelfPhoto(int pageSize) {
        addNet(service.getSelfPhoto(pageSize).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> response) {
                        if (getView() == null) return;
                        getView().getPhotoSuccess(response.getData());
                    }
                }));
    }


    /**
     * 上传照片
     */
    public void uploadPhoto(List<LocalMedia> items, String images) {
        addNet(service.uploadPhoto(images).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                        List<LocalMedia> mPhotoList = new ArrayList<>();
                        String[] image = images.split(",");
                        for (String s : image) {
                            LocalMedia localMedia = new LocalMedia();
                            localMedia.setPath(s);
                            mPhotoList.add(localMedia);
                        }
                        getView().uploadPhotoSuccess(items, mPhotoList, response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().hideLoadingDialog();
                    }
                }));
    }

    public void pictureSort(List<LocalMedia> data) {

    }
}
