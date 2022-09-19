package com.tftechsz.common.utils;

import android.app.Activity;

import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.netease.nim.uikit.common.media.imagepicker.loader.GlideEngine;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.VideoInfo;

import java.util.List;

public class ChoosePicUtils {

    public static void picSingle(Activity activity, OnResultCallbackListener listener) {
        picSingle(activity, 0, false, listener);
    }

    public static void picSingle(Activity activity, boolean isEnableCrop, OnResultCallbackListener listener) {
        picSingle(activity, 0, isEnableCrop, listener);
    }

    public static void picSingle(Activity activity, int requestCode) {
        picSingle(activity, requestCode, false, null);
    }


    private static void picSingle(Activity activity, int requestCode, boolean isEnableCrop, OnResultCallbackListener listener) {
        //单选相册
        PictureSelectionModel pictureSelectionModel = PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频
                .imageEngine(GlideEngine.createGlideEngine()) //
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                //.isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .isSingleDirectReturn(isEnableCrop)//PictureConfig.SINGLE模式下是否直接返回
                //.previewImage(false)//是否预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .minimumCompressSize(1000)// 小于100kb的图片不压缩
                .isCompress(!isEnableCrop)// 是否压缩
                .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)//列表动画效果,AnimationType.ALPHA_IN_ANIMATION、SLIDE_IN_BOTTOM_ANIMATION
                ;

        if (isEnableCrop) {
            pictureSelectionModel
                    .isEnableCrop(true)//是否开启裁剪
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .rotateEnabled(false) // 裁剪是否可旋转图片
                    .withAspectRatio(1, 1)//裁剪比例
                    .setCircleStrokeWidth(5)//设置圆形裁剪边框粗细
                    .setCircleDimmedColor(Utils.getColor(R.color.half_transparent))//设置圆形裁剪背景色值
                    .freeStyleCropEnabled(false)//裁剪框是否可拖拽
                    .cropImageWideHigh(1000, 1000)
//                .circleDimmedLayer(true)// 是否开启圆形裁剪
//                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)//是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .scaleEnabled(true);// 裁剪是否可放大缩小图片
        }

        if (listener != null) {
            pictureSelectionModel.forResult(listener);//结果回调onActivityResult code
        } else {
            pictureSelectionModel.forResult(requestCode);//结果回调onActivityResult code
        }
    }


    public static void takePhoto(Activity activity) {
        PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) //
                .isCompress(true)
                .isEnableCrop(true)
                .forResult(PictureConfig.REQUEST_CAMERA);
    }

    public static void picMultiple(Activity activity, int size, int requestCode) {
        picMultiple(activity, size, requestCode, null);
    }

    public static void picMultiple(Activity activity, int size, int requestCode, List<LocalMedia> selectList) {
        picMultiple(activity, size, requestCode, selectList, false);
    }

    public static void picMultiple(Activity activity, int size, int requestCode, List<LocalMedia> selectList
            , boolean isSelVideo) {
        picMultiple(activity, size, requestCode, selectList, isSelVideo,true);
    }

    public static void picMultiple(Activity activity, int size, int requestCode, List<LocalMedia> selectList
            , boolean isSelVideo,boolean isCamera) {
        PictureSelector.create(activity)
                .openGallery(isSelVideo ? /*PictureMimeType.ofImage() &*/ PictureMimeType.ofAll() : PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                //.isWeChatStyle(true)//开启R.style.picture_WeChat_style样式
                // .ofVideo()、音频.ofAudio()
//                    .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(isSelVideo)// 是否可预览视频
                //.isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .maxSelectNum(size) // 最大图片选择数量
                //.isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .maxVideoSelectNum(1)
                .selectionData(selectList)
                .recordVideoSecond(15)//录制视频秒数 默认60s
                .videoMaxSecond(Interfaces.MAX_SELCTED_VIDOE_TIME + 1)// 查询多少秒以内的视频
                .videoMinSecond(Interfaces.MIN_SELCTED_VIDOE_TIME)
                .isMaxSelectEnabledMask(true)//选择条件达到阀时列表是否启用蒙层效果
                .bindCustomPlayVideoCallback(data -> {//自定义视频播放拦截
                    LocalMedia localMedia = (LocalMedia) data;
                    VideoInfo videoInfo = new VideoInfo(localMedia.getRealPath(), true);
                    Utils.startTrendVideoViewActivity(activity, null, videoInfo);
                    //Utils.playVideo(activity, localMedia.getRealPath());
                })
                .isEnablePreviewAudio(false) // 是否可播放音频
                .isCamera(isCamera)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.isCompress(true)// 是否压缩
                //.compressFocusAlpha(true)//压缩后是否保持图片的透明通道
                //.minimumCompressSize(800)// 小于100kb的图片不压缩
                //.compressQuality(50)//图片压缩后输出质量
//                .synOrAsy(false)//同步true或异步false 压缩 默认同步
//                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                //.isEnableCrop(false)// 是否裁剪
                //.showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .cutOutQuality(90)// 裁剪压缩质量 默认100
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                //.withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
//                    .isGif(false)// 是否显示gif图片
//                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
//                    .circleDimmedLayer(false)// 是否圆形裁剪
//                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                .isOpenClickSound(false)// 是否开启点击声音
                // .selectionMedia(selectList)// 是否传入已选图片
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
//                .rotateEnabled(true) // 裁剪是否可旋转图片
//                .scaleEnabled(true)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频or音频也可适用
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(requestCode);//结果回调onActivityResult code
    }

    public static void picMultiple(Activity activity, int maxSize, List<LocalMedia> selectList, OnResultCallbackListener<LocalMedia> listener) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频
                .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(false)// 是否可预览视频
                .maxSelectNum(maxSize) // 最大图片选择数量
                .selectionData(selectList)
                .isMaxSelectEnabledMask(true)//选择条件达到阀时列表是否启用蒙层效果
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .forResult(listener);//结果回调onActivityResult code
    }

}
