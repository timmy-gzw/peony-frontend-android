package com.tftechsz.common.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.BuildConfig;
import com.blankj.utilcode.util.AppUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.widget.pop.CustomPopWindow;

public class PermissionUtil {

    public static void gotoPermission(Context context) {
//        if (RomUtils.isXiaomi()) {
//            PermissionUtil.gotoMiuiPermission(context);//小米
//        } else if (RomUtils.isMeizu()) {
//            PermissionUtil.gotoMeizuPermission(context);
//        } else if (RomUtils.isOppo()) {
//            PermissionUtil.goOppoManager(context);
//        } else if (RomUtils.isHuawei()) {
//            PermissionUtil.gotoHuaweiPermission(context);
//        } else {
//            context.startActivity(PermissionUtil.getAppDetailSettingIntent(context));
//        }
        AppUtils.launchAppDetailsSettings();
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private static void gotoMiuiPermission(Context context) {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                context.startActivity(getAppDetailSettingIntent(context));
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private static void gotoMeizuPermission(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 华为的权限管理页面
     */
    private static void gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    private static void goOppoManager(Context context) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.coloros.securitypermission", "com.coloros.securitypermission.permission.PermissionAppAllPermissionActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }


    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     */
    private static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }


    public static void showPermissionPop(Activity activity) {
        showPermissionPop(activity, BaseApplication.getInstance().getString(R.string.chat_open_camera_permission));
    }

    public static void showPermissionPopWebview(Activity activity) {
        showPermissionPop(activity, "未获取存储和拍照权限,视频或相册功能无法正常使用。打开应用设置页以修改应用权限");
    }


    public static void showPermissionPop(Activity activity, String content) {
        CustomPopWindow customPopWindow = new CustomPopWindow(activity);
        customPopWindow.setTitle("权限设置");
        customPopWindow.setContent(content);
        customPopWindow.setLeftButton("知道了");
        customPopWindow.setRightButton("去设置");
        customPopWindow.setOutSideDismiss(false);
        customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                PermissionUtil.gotoPermission(BaseApplication.getInstance());
            }
        });
        customPopWindow.showPopupWindow();
    }

    /**
     * tip消息点击事件回调
     */
    public interface OnSelectListener {
        void onCancel();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
