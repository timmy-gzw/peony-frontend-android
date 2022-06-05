package com.tftechsz.common.pagestate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import com.tftechsz.common.R;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;

/**
 * time:2020/4/25
 * author:hss
 * desription:
 */
public class NoNetworkHelper {

    public static void setShowDialogImpl(IShowDialog showDialog) {
        NoNetworkHelper.showDialog = showDialog;
    }

    static IShowDialog showDialog;

    public interface IShowDialog {
        void showNoNetWorkDlg(final Context context);
    }


    static void showNoNetWorkDlg(final Context context) {
        if (showDialog != null) {
            showDialog.showNoNetWorkDlg(context);
            return;
        }
        try {
            new CustomPopWindow(context, 1).setContent(Utils.getString(R.string.pagestate_no_network_msg))
                    .setLeftButton(Utils.getString(R.string.pagestate_cancel))
                    .setRightButton(Utils.getString(R.string.pagestate_go_setting))
                    .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onSure() {
                            // 跳转到系统的网络设置界面
                            Intent intent = null;
                            // 先判断当前系统版本
                            if (Build.VERSION.SDK_INT > 10) {  // 3.0以上
                                //intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                intent = new Intent(Settings.ACTION_SETTINGS);
                            } else {
                                intent = new Intent();
                                intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                            }
                            context.startActivity(intent);
                        }
                    }).showPopupWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                if (info.isAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }
}
