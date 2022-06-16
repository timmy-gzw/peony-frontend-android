package com.tftechsz.moment.mvp.presenter;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.EventBusConstant;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.videocompressor.VideoCompress;
import com.tftechsz.moment.api.TrendApiService;
import com.tftechsz.moment.entity.req.PublishReq;
import com.tftechsz.moment.mvp.IView.ITrendView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TrendPresenter extends BasePresenter<ITrendView> {
    public TrendApiService service;
    private final Object lock = new Object();
    //保存获取到的阿里云oss图片Url
    private final List<String> uploadServerUrlList = new ArrayList<>();

    public TrendPresenter() {
        service = RetrofitManager.getInstance().createBlogApi(TrendApiService.class);
    }

    /**
     * 发布
     * "content": "你好",    //动态内容文字
     * "media": "http://www.baidu.com",  //音频、视频url
     * "type": 1,  //1-视频 2-音频 3-图片
     */
    public void publish(String content, List<LocalMedia> selectList, boolean isSelVideo) {
        PublishReq mPublishReq = new PublishReq();
        mPublishReq.setContent(content);
        //BaseURL    https://peony-blob.oss-cn-shenzhen.aliyuncs.com
        //拼接后变成  https://peony-user.oss-cn-shenzhen.aliyuncs.com/upload/images/watch1-1140x713.png
        // https://peony-user.oss-cn-shenzhen.aliyuncs.com/upload/images/af901004a52bdc367a54c19d2f3c17811de63002f8e07276ed13ab8e0ce601b4.0.JPG
        JSONArray medias = new JSONArray();
        for (String url : uploadServerUrlList) {
            medias.put(url);
        }
        mPublishReq.setMedia(medias.toString());
        mPublishReq.setType(isSelVideo ? "1" : "3");//图片类型是3
        if (isSelVideo) {
            ArrayList<Integer> size = new ArrayList<>();
            int[] videoWidthHeight = Utils.getVideoWidthHeight(selectList.get(0).getRealPath());
            size.add(videoWidthHeight[0]);
            size.add(videoWidthHeight[1]);
            mPublishReq.setVideoSize(size);
        }
        CommonUtil.getToken(new com.tftechsz.common.utils.CommonUtil.OnSelectListener() {
            @Override
            public void onSure() {
                //GlobalDialogManager.getInstance().dismiss();
                addNet(service.publish(mPublishReq).compose(applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                            @Override
                            public void onSuccess(BaseResponse<Boolean> response) {
                                if (null == getView()) return;
                                getView().sendTrendSuccess(response.getData());
                                GlobalDialogManager.getInstance().dismiss();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                super.onFail(code, msg);
                                if (null == getView()) return;
                                GlobalDialogManager.getInstance().dismiss();
                            }
                        }));
            }
        });
    }

    public void beforeCheck(List<LocalMedia> selectList, boolean isSelVideo) {
        addNet(service.beforeCheck().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().beforeCheckSuccess();
                        beforeCheckSuccess(selectList, isSelVideo);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));
    }

    private void beforeCheckSuccess(List<LocalMedia> selectList, boolean isSelVideo) {
        String realPath = selectList.get(0).getRealPath();
        int videoFilter = SPUtils.getInstance().getInt(Interfaces.SP_VIDEO_COMPRESS_FILTER);
        if (isSelVideo && SPUtils.getInstance().getBoolean(Interfaces.SP_IS_VIDEO_COMPRESS, true) && FileUtils.getLength(realPath) > 1024L * 1024 * (videoFilter == 0 ? 5 : videoFilter)) {
            String compressPath = BaseApplication.getInstance().getCacheDir() + File.separator + "video-compress/" + FileUtils.getFileName(realPath);
            if (FileUtils.isFileExists(compressPath)) {
                Utils.logE("压缩文件存在, 大小: " + FileUtils.getSize(compressPath) + "  路径:" + compressPath);
                LocalMedia localMedia = new LocalMedia();
                if (FileUtils.getLength(compressPath) < 10240) { //如果小于10k就属于不正常视频
                    localMedia.setRealPath(realPath);
                } else {
                    localMedia.setRealPath(compressPath);
                }
                setList(selectList, localMedia);
                uploadToOss(selectList, true);
                return;
            }
            FileUtils.createOrExistsFile(compressPath);
            VideoCompress.compressVideoLow(realPath, compressPath, new VideoCompress.CompressListener() {
                long time;

                @Override
                public void onStart() {
                    time = System.currentTimeMillis();
                    Utils.logE("onStart");
                }

                @Override
                public void onSuccess() {
                    long SuccessTime = System.currentTimeMillis();
                    Utils.logE("原大小: " + FileUtils.getSize(realPath)
                            + " ,压缩耗时: " + (SuccessTime - time) / 1000 + "秒" + (SuccessTime - time) % 1000
                            + " ,压缩后大小: " + FileUtils.getSize(compressPath));
                    if (FileUtils.getLength(compressPath) < 10240) { //如果小于10k就属于不正常视频
                        uploadToOss(selectList, true);
                        return;
                    }
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setRealPath(compressPath);
                    setList(selectList, localMedia);
                    uploadToOss(selectList, true);
                }

                @Override
                public void onFail() {
                    uploadToOss(selectList, true);
                    Utils.logE("onFail");

                }

                @Override
                public void onProgress(float percent) {
                    Utils.logE("onProgress: " + percent);

                }
            });
        } else {
            uploadToOss(selectList, isSelVideo);
        }
    }

    private void setList(List<LocalMedia> selectList, LocalMedia localMedia) {
        if (selectList.size() > 0) {
            selectList.set(0, localMedia);
        } else {
            selectList.add(localMedia);
        }
    }

    private void uploadToOss(List<LocalMedia> list, boolean isSelVideo) {
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final Handler handler = new Handler(Looper.myLooper());

        class Task implements Runnable {
            final String path;

            Task(String path) {
                this.path = path;
            }

            @Override
            public void run() {
                UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_TYPE, UploadHelper.PATH_MOMENT, isSelVideo ? UploadHelper.TYPE_M_VIDEOS : UploadHelper.TYPE_IMAGE, new File(path), new UploadHelper.OnUploadListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long cur, long total) {

                    }

                    @Override
                    public void onSuccess(String url) {
                        Utils.logE("url:  https://peony-blog.oss-cn-shenzhen.aliyuncs.com" + url);
                        synchronized (lock) {
                            uploadServerUrlList.add(url);
                            if (!taskList.isEmpty()) {
                                Runnable runnable = taskList.pop();
                                handler.post(runnable);
                            } else {
                                //完成之后的个人操作
                                EventBus.getDefault().post(EventBusConstant.UPLOADPUBLISHDATA);
                            }
                        }
                    }


                    @Override
                    public void onError() {
                        GlobalDialogManager.getInstance().dismiss();
                    }
                });
            }
        }
        //循环遍历原始路径 添加至linklist中
        for (LocalMedia localMedia : list) {
            taskList.add(new Task(!TextUtils.isEmpty(localMedia.getRealPath()) ? localMedia.getRealPath() : localMedia.getPath()));
        }
        handler.post(taskList.pop());
    }
}
