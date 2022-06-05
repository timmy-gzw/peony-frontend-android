package com.netease.nim.uikit.common;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.netease.nim.uikit.R;


/**
 * 包 名 : com.netease.nim.uikit.common
 * 描 述 : TODO
 */
public class UIUtils {
    /**
     * 设置家族成员头衔
     * id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
     */
    public static void setFamilyTitle(TextView tv, int id) {
        tv.setVisibility(View.VISIBLE);
        if (id == 64) {//族长
            tv.setBackgroundResource(R.drawable.peony_jzxqy_zzhz_icon);
        } else if (id == 32) {//副族长
            tv.setBackgroundResource(R.drawable.peony_jzxqy_fzzhz_icon);
        } else if (id == 4) {//长老
            tv.setBackgroundResource(R.drawable.peony_jzxqy_zlhz_icon);
        } else {
            tv.setVisibility(View.GONE);
            tv.setBackgroundResource(R.color.transparent);
        }
    }

    public static void logE(Object s) {
        if (AppUtils.isAppDebug()) {
            Log.e("UIUtils.logE", String.valueOf(s));
        }
    }
}
