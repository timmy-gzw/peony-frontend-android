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

            case 2:
                if (isEnd) {
                    return R.drawable.vip_style_picture_frame_s02_end;
                }
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s02_round;
                }
                return R.drawable.vip_style_picture_frame_s02_start;

            case 6:
                if (isEnd) {
                    return R.drawable.vip_style_picture_frame_s06_end;
                }
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s06_round;
                }
                return R.drawable.vip_style_picture_frame_s06_start;

            case 7:
                if (isEnd) {
                    return R.drawable.vip_style_picture_frame_s07_end;
                }
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s07_round;
                }
                return R.drawable.vip_style_picture_frame_s07_start;

            case 18:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s18_round;
                }
                return R.drawable.vip_style_picture_frame_s18_start;
            case 19:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s19_round;
                }
                return R.drawable.vip_style_picture_frame_s19_start;
            case 20:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s20_round;
                }
                return R.drawable.vip_style_picture_frame_s20_start;
            case 21:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s21_round;
                }
                return R.drawable.vip_style_picture_frame_s21_start;
            /*case 26:
                return R.drawable.vip_style_picture_frame_s26_start;
            case 27:
                return R.drawable.vip_style_picture_frame_s27_start;
            case 28:
                return R.drawable.vip_style_picture_frame_s28_start;
            case 30:
                return R.drawable.vip_style_picture_frame_s30_start;
            case 31:
                return R.drawable.vip_style_picture_frame_s31_start;
            case 32:
                return R.drawable.vip_style_picture_frame_s32_start;*/
            case 33:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s33_round;
                }
                return R.drawable.vip_style_picture_frame_s33_start;
            case 42:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s42_round;
                }
            case 43:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s43_round;
                }
            case 44:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s44_round;
                }
            case 45:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s45_round;
                }
            case 46:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s46_round;
                }
            case 47:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s47_round;
                }
            case 48:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s48_round;
                }
                return R.drawable.vip_style_picture_frame_s48_start;
            case 49:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s49_round;
                }
                return R.drawable.vip_style_picture_frame_s49_start;
            case 50:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s50_round;
                }

            case 1000:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1000_round;
                }
            case 1001:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1001_round;
                }
            case 1002:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1002_round;
                }
            case 1003:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1003_round;
                }
            case 1004:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1004_round;
                }
            case 1005:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1005_round;
                }
            case 1006:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1006_round;
                }
            case 1007:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1007_round;
                }
            case 1008:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1008_round;
                }
            case 1009:
                if (isRound) {
                    return R.drawable.vip_style_picture_frame_s1009_round;
                }

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
            case 8:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s08_end;
                }
                return R.drawable.vip_style_chat_bubble_s08_start;

            case 9:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s09_end;
                }
                return R.drawable.vip_style_chat_bubble_s09_start;
            case 10:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s10_end;
                }
                return R.drawable.vip_style_chat_bubble_s10_start;
            case 11:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s11_end;
                }
                return R.drawable.vip_style_chat_bubble_s11_start;
            case 16:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s16_end;
                }
                return R.drawable.vip_style_chat_bubble_s16_start;
            case 17:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s17_end;
                }
                return R.drawable.vip_style_chat_bubble_s17_start;

            case 29:
                if (isEnd) {
                    return R.drawable.vip_style_chat_bubble_s29_end;
                }
                return R.drawable.vip_style_chat_bubble_s29_start;

            case 100:
                if (isParty) return R.drawable.vip_style_chat_bubble_s100_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s100_end;
                return R.drawable.vip_style_chat_bubble_s100_start;

            case 101:
                if (isParty) return R.drawable.vip_style_chat_bubble_s101_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s101_end;
                return R.drawable.vip_style_chat_bubble_s101_start;

            case 102:
                if (isParty) return R.drawable.vip_style_chat_bubble_s102_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s102_end;
                return R.drawable.vip_style_chat_bubble_s102_start;

            case 103:
                if (isParty) return R.drawable.vip_style_chat_bubble_s103_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s103_end;
                return R.drawable.vip_style_chat_bubble_s103_start;

            case 104:
                if (isParty) return R.drawable.vip_style_chat_bubble_s104_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s104_end;
                return R.drawable.vip_style_chat_bubble_s104_start;

            case 105:
                if (isParty) return R.drawable.vip_style_chat_bubble_s105_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s105_end;
                return R.drawable.vip_style_chat_bubble_s105_start;

            case 106:
                if (isParty) return R.drawable.vip_style_chat_bubble_s106_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s106_end;
                return R.drawable.vip_style_chat_bubble_s106_start;

            case 107:
                if (isParty) return R.drawable.vip_style_chat_bubble_s107_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s107_end;
                return R.drawable.vip_style_chat_bubble_s107_start;

            case 108:
                if (isParty) return R.drawable.vip_style_chat_bubble_s108_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s108_end;
                return R.drawable.vip_style_chat_bubble_s108_start;

            case 2001:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2001_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2001_end;
                return R.drawable.vip_style_chat_bubble_s2001_start;

            case 2002:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2002_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2002_end;
                return R.drawable.vip_style_chat_bubble_s2002_start;

            case 2003:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2003_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2003_end;
                return R.drawable.vip_style_chat_bubble_s2003_start;

            case 2004:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2004_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2004_end;
                return R.drawable.vip_style_chat_bubble_s2004_start;

            case 2005:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2005_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2005_end;
                return R.drawable.vip_style_chat_bubble_s2005_start;

            case 2006:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2006_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2006_end;
                return R.drawable.vip_style_chat_bubble_s2006_start;

            case 2007:
                if (isParty) return R.drawable.vip_style_chat_bubble_s2007_party;
                if (isEnd) return R.drawable.vip_style_chat_bubble_s2007_end;
                return R.drawable.vip_style_chat_bubble_s2007_start;

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
