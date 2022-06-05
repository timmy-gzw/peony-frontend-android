package com.tftechsz.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.netease.nim.uikit.common.util.MD5Util;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UploadHelper {
    public static UploadHelper helper;
    private Context mContext;
    private OssTokenDto.Credentials preOssToken;
    private OssTokenDto ossTokenDto;
    private String preType;
    private OSS oss;

    public static final int OSS_TYPE = 1;   //动态OSS
    public static final int OSS_USER_TYPE = 2;   //用户0SS
    public static final int OSS_PHOTO_TYPE = 3;   //相册OSS

    public static final String PATH_USER_AVATAR = "user/avatar/";   //头像
    public static final String PATH_USER_PHOTO = "user/photo/";   //相册
    public static final String PATH_USER_FAMILY = "user/family/";   //群头像

    public static final String PATH_USER_PARTY = "party/cover/";   //派对封面

    public static final String PATH_USER_REAL_AUTH = "user/auth/";   //真人认证
    public static final String PATH_USER_REAL_NAME = "user/real_name/";   //实名认证
    public static final String PATH_USER_ACCOST = "user/accost/";   //打招呼相册
    public static final String PATH_BLOG = "blog/blog/";  //动态
    public static final String PATH_MUSIC = "music/";  //音乐

    public static final String TYPE_IMAGE = "images";
    public static final String TYPE_S_VIDEOS = "svideos";//小视频
    public static final String TYPE_M_VIDEOS = "mvideos";//短片
    public static final String TYPE_AUDIOS = "audios";//
    private OSSAsyncTask task;

    public static UploadHelper getInstance(Context context) {
        if (null == helper) {
            synchronized (UploadHelper.class) {
                if (helper == null) {
                    helper = new UploadHelper(context);
                }
            }
        }
        return helper;
    }


    private InitOssSuccessListener l;

    private UploadHelper(Context c) {
        mContext = c;
    }

    OnUploadListener listener;

    public interface OnUploadListener {
        void onStart();

        void onLoading(long cur, long total);

        void onSuccess(String url);

        void onError();
    }


    /**
     * 图片上传
     *
     * @param ossType 1：动态  2：用户 3：相册
     * @param type
     * @param file
     * @param l
     */
    public synchronized void doUpload(int ossType, String type, File file, OnUploadListener l) {
        listener = l;
        if (ossType == OSS_TYPE) {
            getOssInfo(() -> {
                upload(PATH_BLOG, type, file);
            });

        } else if (ossType == OSS_USER_TYPE) {
            getUserOssInfo(() -> {
                upload(PATH_BLOG, type, file);
            });

        } else if (ossType == OSS_PHOTO_TYPE) {
            getPhotoOssInfo(() -> {
                upload(PATH_BLOG, type, file);
            });

        }
    }

    /**
     * @param ossType  oss 请求地址
     * @param pathType // 图片存放地址
     * @param type     图片类型
     * @param file     文件地址
     * @param l
     */
    public synchronized void doUpload(int ossType, String pathType, String type, File file, OnUploadListener l) {
        listener = l;
        if (ossType == OSS_TYPE) {
            getOssInfo(() -> {
                compressBeforeUpload(pathType, type, file);
            });
        } else if (ossType == OSS_USER_TYPE) {
            getUserOssInfo(() -> {
                compressBeforeUpload(pathType, type, file);
            });
        } else if (ossType == OSS_PHOTO_TYPE) {
            getPhotoOssInfo(() -> {
                compressBeforeUpload(pathType, type, file);
            });
        }
    }


    /**
     * 上传前 压缩图片
     *
     * @param type
     * @param file
     */
    private void compressBeforeUpload(String pathType, String type, File file) {
        LuBanUtils.compress(mContext, file, new LuBanUtils.OnCompress() {
            @Override
            public void success(File f) {
                upload(pathType, type, f);
            }

            @Override
            public void error(String msg) {
//                ToastUtils.showShort(mContext, "上传失败，请重试！");
            }
        });
    }


    private void upload(String pathType, String type, File file) {
        if (TextUtils.equals(TYPE_IMAGE, type)) {
            String name = pathType + MD5Util.toMD532(TimeUtils.getCurrentTimeInLong() + file.getName()) + lastImageName(file);
            LogUtil.e("------------------", name);
            upload(name, file.getAbsolutePath());
        } else if (TextUtils.equals(TYPE_M_VIDEOS, type)) {
            //String name = pathType + MD5Util.toMD532(TimeUtils.getCurrentTimeInLong() + file.getName()) + lastVideoName(file);
            String md5 = pathType + FileUtils.getFileMD5ToString(file).toLowerCase() + lastVoiceName(file);
            upload(md5, file.getAbsolutePath());
        } else if (TextUtils.equals(TYPE_AUDIOS, type)) {
            //String name = pathType + MD5Util.toMD532(TimeUtils.getCurrentTimeInLong() + file.getName()) + lastVoiceName(file);
            String md5 = pathType + FileUtils.getFileMD5ToString(file).toLowerCase() + lastVoiceName(file);
            upload(md5, file.getAbsolutePath());
        } else {
            String name = pathType + MD5Util.toMD532(TimeUtils.getCurrentTimeInLong() + file.getName()) + lastImageName(file);
            LogUtil.e("------------------", name);
            upload(name, name);
        }
    }


    public String lastImageName(File file) {
        if (file == null) return null;
        String filename = file.getName();
        if (filename.lastIndexOf(".") == -1) {
            return ".png";//文件没有后缀名的情况
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public String lastVideoName(File file) {
        if (file == null) return null;
        String filename = file.getName();
        if (filename.lastIndexOf(".") == -1) {
            return ".mp4";//文件没有后缀名的情况
        }
        return filename.substring(filename.lastIndexOf("."));

    }

    public String lastVoiceName(File file) {
        if (file == null) return null;
        String filename = file.getName();
        if (filename.lastIndexOf(".") == -1) {
            return ".aac";//文件没有后缀名的情况
        }
        return filename.substring(filename.lastIndexOf("."));

    }

    /**
     * 阿里云文件上传
     */
    private void upload(String md5Name, String filePath) {
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(ossTokenDto.bucket, md5Name, filePath);
        try {
            boolean b = oss.doesObjectExist(ossTokenDto.bucket, md5Name);
            Utils.logE("md5: " + md5Name + " , 阿里云" + (b ? "存在文件" : "不存在文件") + " :" + filePath);
            if (b) {
                listener.onSuccess("/" + md5Name);
                return;
            }
        } catch (Exception e) {
            Utils.logE(e.getMessage());
            e.printStackTrace();
        }

        // 异步上传时可以设置进度回调。
        put.setProgressCallback((request, currentSize, totalSize) -> {
            Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            if (null != listener) {
                listener.onLoading(currentSize, totalSize);
            }
        });
        listener.onStart();
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                //upload/images/watch1-1140x713.png
                //https://peony-user.oss-cn-shenzhen.aliyuncs.com/upload/images/watch1-1140x713.png
                LogUtils.dTag("图片文件名是", md5Name);

                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                //String fileUrl = preOssToken.host + "/" + fileName;
                // Log.d("file Url", preOssToken.host + "/" + fileName);
                if (null != listener) {
                    listener.onSuccess("/" + md5Name);
                }
                cancelUpLoad();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                preOssToken = null;
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                    uploadLog(clientExcepion.getMessage() + clientExcepion.getCause());
                }
                if (serviceException != null) {
                    // 服务异常。
                    LogUtil.e("ErrorCode", serviceException.getErrorCode());
                    LogUtil.e("RequestId", serviceException.getRequestId());
                    LogUtil.e("HostId", serviceException.getHostId());
                    LogUtil.e("RawMessage", serviceException.getRawMessage());
                    uploadLog(serviceException.getErrorCode() + serviceException.getRawMessage());
                }
                if (null != listener) {
                    listener.onError();
                    ToastUtil.showToast(BaseApplication.getInstance(), "上传失败,请重新上传");
                }

                cancelUpLoad();

                //  ToastUtils.showShort(mContext, "上传失败，请重试！");
            }
        });

        task.waitUntilFinished();

    }

    /**
     * 获取阿里云oss 必要信息
     */
    private void uploadLog(String content) {
        ARouter.getInstance().navigation(MineService.class)
                .trackEvent("图片上传失败", "upload", "uploadPic",
                        content, null);
    }


    /**
     * 取消上传
     */
    public void cancelUpLoad() {
        if (task != null) {
            task.cancel();
        }
        helper = null;
    }

    /**
     * 获取阿里云oss 必要信息
     */
    private void getOssInfo(InitOssSuccessListener l) {
        RetrofitManager.getInstance().createUploadApi(PublicService.class)
                .getOssToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<OssTokenDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<OssTokenDto> response) {
                        LogUtils.dTag("返回的OSS数据", response.getData().toString());
                        setSuccessData(response, l);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取阿里云oss 必要信息
     */
    private void getUserOssInfo(InitOssSuccessListener l) {
        RetrofitManager.getInstance().createUploadApi(PublicService.class)
                .getUserOssToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<OssTokenDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseResponse<OssTokenDto> response) {
                        LogUtils.dTag("返回的OSS数据", response.getData().toString());
                        setSuccessData(response, l);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取阿里云oss 必要信息
     */
    private void getPhotoOssInfo(InitOssSuccessListener l) {
        RetrofitManager.getInstance().createUploadApi(PublicService.class)
                .getPhotoOssToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<OssTokenDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseResponse<OssTokenDto> response) {
                        LogUtils.dTag("返回的OSS数据", response.getData().toString());
                        setSuccessData(response, l);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 成功获取
     *
     * @param response
     */
    private void setSuccessData(BaseResponse<OssTokenDto> response, InitOssSuccessListener l) {
        if (response.getCode() == 0) {
            ossTokenDto = response.getData();
            preOssToken = ossTokenDto.credentials;
            String endpoint = ossTokenDto.end_point;
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
            conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
            conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
            conf.setMaxErrorRetry(2); // retry，default 2
            OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(preOssToken.AccessKeyId, preOssToken.AccessKeySecret, preOssToken.SecurityToken);
            oss = new OSSClient(mContext, endpoint, credentialProvider, conf);
            l.initSuccess();
        }
    }


    private void setError() {
        if (listener != null) {
            listener.onError();
        }
    }


    interface InitOssSuccessListener {
        void initSuccess();
    }

    public static class OssTokenDto {

        public static class Credentials {
            public String AccessKeyId;
            public String SecurityToken;
            public String AccessKeySecret;
            public String Expiration;//超时时间，未过超时时间 不需要重新请求该接口

            public String toString() {
                return "Credentials{" +
                        "AccessKeyId='" + AccessKeyId + '\'' +
                        ", SecurityToken='" + SecurityToken + '\'' +
                        ", AccessKeySecret='" + AccessKeySecret + '\'' +
                        ", Expiration='" + Expiration + '\'' +
                        '}';
            }
        }

        public Credentials credentials;
        public String region_id;
        public String bucket;
        public String end_point;
        public String errorCode;
        public String errorMsg;
        public String expiration;//超时时间，未过超时时间 不需要重新请求该接口
        public String host;
        public String securityToken;
        public String useDir;

        @Override
        public String toString() {
            return "OssTokenDto{" +
                    "credentials=" + credentials +
                    ", region_id='" + region_id + '\'' +
                    ", bucket='" + bucket + '\'' +
                    ", end_point='" + end_point + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    ", expiration='" + expiration + '\'' +
                    ", host='" + host + '\'' +
                    ", securityToken='" + securityToken + '\'' +
                    ", useDir='" + useDir + '\'' +
                    '}';
        }
    }
}
