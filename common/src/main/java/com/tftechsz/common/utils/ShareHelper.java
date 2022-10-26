package com.tftechsz.common.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tftechsz.common.iservice.UserProviderService;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ShareHelper {

    private static ShareHelper INSTANCE;

    private ShareHelper() {
    }

    public static synchronized ShareHelper get() {
        if (INSTANCE == null) {
            INSTANCE = new ShareHelper();
        }
        return INSTANCE;
    }


    /**
     * 分享动态
     */
    public void openShare(Activity activity, String url, String title, String content, int type) {
        if (type == 3 || type == 4) {
            shareQQ(activity, url, title, content, null, type, null);
        } else {
            shareWx(activity, url, title, content, null, type);
        }
    }

    /**
     * 分享微信
     */
    public void shareWx(Activity activity, String url, String title, String content, String img, int type) {
        if (!AppUtils.isWeChatAppInstalled(activity)) {
            Utils.toast("手机未安装微信");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (type == 1) {
            IWXAPI api;
            UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
            ConfigInfo configInfo = service.getConfigInfo();
            String weChatAppId = CommonUtil.getWeChatAppId(configInfo);
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            api = WXAPIFactory.createWXAPI(BaseApplication.getInstance(), weChatAppId);
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = TextUtils.isEmpty(title) ? com.blankj.utilcode.util.Utils.getApp().getString(R.string.app_name) : title;
            msg.description = TextUtils.isEmpty(content) ? com.blankj.utilcode.util.Utils.getApp().getString(R.string.app_name) : content;
            Bitmap thumbBmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher_share);
            msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
            return;
        }
        UMWeb web = new UMWeb(Utils.performUrl(url));
        web.setTitle(title);//标题
        UMImage umImage;
        if (!TextUtils.isEmpty(img)) {
            umImage = new UMImage(activity, img);
        } else {
            umImage = new UMImage(activity, R.mipmap.ic_launcher_share);
        }
        umImage.compressFormat = Bitmap.CompressFormat.PNG;
        web.setThumb(umImage);
        web.setDescription(content);//描述
        new ShareAction(activity)
                .withMedia(web)
                .setPlatform(perFormType(type))
                .setCallback(shareListener).share();
    }

    public SHARE_MEDIA perFormType(int type) {
        switch (type) {
            case 2: //朋友圈
                return SHARE_MEDIA.WEIXIN_CIRCLE;
            case 3: //qq
                return SHARE_MEDIA.QQ;
            case 4: //qq空间
                return SHARE_MEDIA.QZONE;
            default: //默认微信好友
                return SHARE_MEDIA.WEIXIN;
        }
    }


    public void ShareImgToWX(Activity activity, String url, int type) {
        Utils.logE("触发分享");
        IWXAPI wxapi = WXAPIFactory.createWXAPI(activity, Constants.WX_APP_ID);
        if (!wxapi.isWXAppInstalled()) {
            Utils.toast("手机未安装微信");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String[] split = url.split(",");
        if (split.length == 2) {
            Bitmap bitmap = Utils.base64ToBitmap(split[1]);
            if (bitmap != null) {
                int rowBytes = bitmap.getRowBytes();
                Utils.logE("bitmap=" + rowBytes);
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = new WXImageObject(bitmap);
                msg.thumbData = bitmap2Bytes(bitmap, 32);  //设置缩略图
                bitmap.recycle();
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = type == 1 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                wxapi.sendReq(req);

            }
        }
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxKb
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxKb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxKb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * qq/qZone 分享
     *
     * @param activity    activity
     * @param url         url
     * @param title       title
     * @param content     content
     * @param img         img
     * @param type        3:qq 4:qq空间
     * @param iUiListener 回调
     */
    public void shareQQ(Activity activity, String url, String title, String content, String img, int type, IUiListener iUiListener) {
        if (!(com.blankj.utilcode.util.AppUtils.isAppInstalled("com.tencent.tim")
                || com.blankj.utilcode.util.AppUtils.isAppInstalled("com.tencent.mobileqq"))) {
            ToastUtil.showToast(activity, "手机未安装qq");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Bundle params = new Bundle();

        switch (type) {
            case 3: //qq
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Utils.performUrl(url));
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, img);
                BaseApplication.getInstance().getTencentInstance().shareToQQ(activity, params, iUiListener);
                break;

            case 4:  //qq空间
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);//选填
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Utils.performUrl(url));//必填
                ArrayList<String> list = new ArrayList<>();
                list.add(img);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
                BaseApplication.getInstance().getTencentInstance().shareToQzone(activity, params, iUiListener);
                break;
        }
    }


    /**
     * 分享返回监听
     */
    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
        }
    };
}
