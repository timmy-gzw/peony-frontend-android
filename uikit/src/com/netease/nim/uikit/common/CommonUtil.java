package com.netease.nim.uikit.common;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Collection;

public class CommonUtil {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 设置徽章
     *
     * @param img   img
     * @param badge 徽章路径
     */
    public static void setBadge(ImageView img, String badge) {
        //badge = "http://peony-public.oss-cn-shenzhen.aliyuncs.com/dress/badge/noble_label_1.png";
        if (img == null || TextUtils.isEmpty(badge)) {
            return;
        }
        img.setVisibility(View.VISIBLE);
        Glide.with(img.getContext())
                .asBitmap()
                .load(badge)
                .into(img);
    }
}
