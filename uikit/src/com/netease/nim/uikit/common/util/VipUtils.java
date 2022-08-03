package com.netease.nim.uikit.common.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.netease.nim.uikit.R;

import java.io.File;

/**
 * 包 名 : com.netease.nim.uikit.common.util
 * 描 述 : TODO
 */
public class VipUtils {
    private static final String CHAT_BUBBLE_DIR = "chat_bubble";
    private static final String PIC_FRAME_DIR = "pic_frame";

    public static int getPictureFrameBackground(int id) {
        return getPictureFrameBackground(id, false, true);
    }

    public static int getPictureFrameBackground(int id, boolean isEnd, boolean isRound) { //头像框
        switch (id) {
            case 1:
                if (isEnd) {
                    return R.drawable.vip_style_picture_frame_s01_end;
                }
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s01_round;
                }
                return R.drawable.vip_style_picture_frame_s01_start;
            default:
//                return R.drawable.vip_style_picture_frame_s49_start;
                return R.color.transparent;
        }
    }

    public static int getChatBubbleBackground(int id, boolean isEnd, boolean isParty) {
        switch (id) {
            case 3:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s01_end;
                }
                return R.drawable.vip_style_chat_bubble_s01_start;

            case 4:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s02_end;
                }
                return R.drawable.vip_style_chat_bubble_s02_start;


            default:
                if (isParty) {
                    return R.drawable.bg_black_tran10_radius12;
                }
                if (isEnd) {
//                    return R.drawable.vip_style_chat_bubble_s103_end;
                    return R.drawable.nim_message_item_right_selector;
                }
//                return R.drawable.vip_style_chat_bubble_s103_start;
                return R.drawable.nim_message_item_left_selector;
        }
    }

    public static void setPersonalise(View view, int id, boolean isEnd) {
        setPersonalise(view, id, isEnd, false, false);
    }

    public static void setPersonalise(View view, int id, boolean isEnd, boolean isPicFrame) {
        setPersonalise(view, id, isEnd, isPicFrame, false);
    }

    /**
     * @param view       view
     * @param id         头像/气泡 id
     * @param isEnd      是否在右边
     * @param isPicFrame 是否头像框
     * @param isParty    是否派对聊天气泡
     */
    public static void setPersonalise(View view, int id, boolean isEnd, boolean isPicFrame, boolean isParty) {
        //FIXME 暂不支持vip气泡 根据需求放开
        id = 0;
        if (!isPicFrame) {
            // view.setBackgroundResource(getChatBubbleBackground(id, isEnd));
            setPadding(id, view, isParty);
            if (view instanceof TextView) { //气泡文字颜色设置
                TextView tv = (TextView) view;
                setTextColor(id, tv, null, isEnd);
            } else if (view instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) view;
                for (int i = 0; i < frameLayout.getChildCount(); i++) {
                    View childAt = frameLayout.getChildAt(i);
                    if (childAt instanceof TextView) {
                        TextView tv = (TextView) childAt;
                        setTextColor(id, tv, null, isEnd);
                    }
                    if (childAt instanceof ImageView) {
                        ImageView img = (ImageView) childAt;
                        setTextColor(id, null, img, isEnd);
                    }
                }
            }
        } else {
            // view.setBackgroundResource(getPictureFrameBackground(id, isEnd, false));
        }
        if (id == 0) {
            if (!isPicFrame) {
                view.setBackgroundResource(getChatBubbleBackground(id, isEnd, isParty));
            } else {
                view.setBackgroundResource(getPictureFrameBackground(id));
            }
            return;
        }

        String fileName = getFileName(id, isEnd, isPicFrame, isParty);
        String localFilePath = getLocalFilePath(fileName, isPicFrame);
        if (!TextUtils.isEmpty(localFilePath) && FileUtils.isFileExists(localFilePath) && ImageUtils.getBitmap(new File(localFilePath)) != null) { //如果缓存了直接加载, 否则进行下载
            //UIUtils.logE("本地存在: " + localFilePath);
            view.setBackground(getNinePatchDrawable(ImageUtils.getBitmap(new File(localFilePath)), Utils.getApp()));
            return;
        }

        //  <--------------------开始下载.9图, 给个默认背景-------------------->
        if (isEnd) {
            view.setBackgroundResource(isPicFrame ? R.drawable.bg_trans : R.drawable.nim_message_item_right_selector);
        } else {
            view.setBackgroundResource(isPicFrame ? R.drawable.bg_trans : R.drawable.nim_message_item_left_selector);
        }
        if (id == -1) {
            return;
        }
        //UIUtils.logE("本地不存在, 开始下载: " + getNetPicUrl(fileName, isPicFrame && id > 17));
        int finalId = id;
        Glide.with(Utils.getApp())
                .downloadOnly()
                .load(getNetPicUrl(fileName, isPicFrame))
                .addListener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        //setPersonalise(view, 0, isEnd, isPicFrame); //失败时使用默认
                        if (!isPicFrame) { //失败时使用本地
                            view.setBackgroundResource(getChatBubbleBackground(finalId, isEnd, isParty));
                        } else {
                            view.setBackgroundResource(getPictureFrameBackground(finalId, isEnd, false));
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File file, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        File localFile = new File(localFilePath);
                        FileUtils.createOrExistsFile(localFile);//创建文件
                        FileUtils.copy(file, localFile);//下载的文件移动到指定目录
                        if (!TextUtils.isEmpty(localFilePath) && ImageUtils.getBitmap(localFile) != null) {
                            view.setBackground(getNinePatchDrawable(ImageUtils.getBitmap(localFile), Utils.getApp()));
                        } else {
                            if (!isPicFrame) { //失败时使用本地
                                view.setBackgroundResource(getChatBubbleBackground(finalId, isEnd, isParty));
                            } else {
                                view.setBackgroundResource(getPictureFrameBackground(finalId));
                            }
                        }
                        //UIUtils.logE("下载成功: " + localFile.getPath());
                        return false;
                    }
                }).submit();
    }

    /**
     * @param isParty 派对内聊天气泡
     */
    private static void setPadding(int id, View view, boolean isParty) {
        int paddingHorizontal = isParty ? 12 : 15;
        int paddingVertical = isParty ? 5 : 12;
        switch (id) {
            case 10:
            case 16:
            case 17:
            case 29:
                paddingHorizontal = 20;
                paddingVertical = 15;
                break;
            case 100:
            case 101:
            case 102:
            case 103:
                if (!isParty) {
                    paddingHorizontal = 20;
                    paddingVertical = 15;
                }

                break;
        }
        //派对内左右上下的间距不一样
        view.setPadding(ConvertUtils.dp2px(isParty ? paddingHorizontal - 3 : paddingHorizontal),
                ConvertUtils.dp2px(paddingVertical),
                ConvertUtils.dp2px(paddingHorizontal),
                ConvertUtils.dp2px(isParty ? paddingVertical + 3 : paddingVertical));
    }

    private static void setTextColor(int id, TextView tv, ImageView img, boolean isEnd) {
        int color = R.color.color_333333;
        if (isEnd) {
            color = R.color.white;
        }
        switch (id) {
            case 8:
                color = R.color.color_4D92DA;
                break;
            case 9:
                color = R.color.color_EF6094;
                break;
            case 11:
                color = R.color.color_FA9F36;
                break;
            case 16:
                color = R.color.color_F45933;
                break;
            case 17:
                color = R.color.color_FFEDAE;
                break;
            case 29:
                color = R.color.color_3D5B94;
                break;
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 2001:
            case 2002:
            case 2003:
            case 2004:
            case 2005:
            case 2006:
            case 2007:
                color = R.color.white;
                break;
        }
        if (tv != null) {
            tv.setTextColor(Utils.getApp().getResources().getColor(color));
        }
        if (img != null) {
            img.setColorFilter(Utils.getApp().getResources().getColor(color));
        }
    }

    private static String getFileName(int id, boolean isEnd, boolean isPicFrame, boolean isParty) { //peony_3_start.9.png
        StringBuilder sb = new StringBuilder();
        switch (AppUtils.getAppPackageName()) {
            case "com.tftechsz.peony":
                sb.append("peony_");
                break;

            case "com.tftechsz.hyacinth":
                sb.append("hyacinth_");
                break;
        }
        sb.append(id);
        if (isParty) {
            return sb.append("_party.9.png").toString();
        }
        if (isPicFrame) {
            return sb.append("_round.png").toString();
        }
        if (isEnd) {
            return sb.append("_end.9.png").toString();
        }
        return sb.append("_start.9.png").toString();
    }

    private static String getLocalFilePath(String fileName, boolean isReplaceNine) {  //cache/tfpeony/peony_3_start.9.png
        StringBuilder sb = new StringBuilder();
        if (Utils.getApp() == null || Utils.getApp().getExternalCacheDir() == null)
            return "";
        return sb.append(Utils.getApp().getExternalCacheDir().getAbsoluteFile())
                .append(File.separator)
                .append("tfpeony")
                .append(File.separator)
                .append(isReplaceNine ? fileName.replace(".9", "") : fileName).toString();
    }

    /**
     * @return 拼接全路径
     */
    private static String getNetPicUrl(String fileName, boolean isReplaceNine) { //  .../peony_3_start.9.png
        String cdn = SPUtils.getInstance().getString("sp_vip_pic_host", "http://peony-public.oss-cn-shenzhen.aliyuncs.com/vip/");
        return cdn + (isReplaceNine ? fileName.replace(".9", "") : fileName);
    }

    //获取.9图片
    private static Drawable getNinePatchDrawable(Bitmap bitmap, Context context) {
        byte[] chunk = bitmap.getNinePatchChunk();
        NinePatchDrawable ninePatchDrawable;
        if (chunk != null && NinePatch.isNinePatchChunk(chunk)) {
            ninePatchDrawable = new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
        } else {
            return new BitmapDrawable(context.getResources(), bitmap);
        }
        return ninePatchDrawable;
    }
}
