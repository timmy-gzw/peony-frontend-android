package com.tftechsz.common.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    private static Toast mToast;

    /**
     * 文本显示
     */
    public static void showToast(Context context, String text) {
        Utils.toast(text, true);
        /*if (TextUtils.isEmpty(text)) return;
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();*/
    }


    /**
     * 文本显示
     */
    public static void showLong(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    /**
     * 传入资源文件
     */
    public static void showToast(Context context, int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }
}
