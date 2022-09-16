package com.tftechsz.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.BuildConfig;
import com.blankj.utilcode.util.AppUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.PermissionPopWindow;

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
        showPermissionPop(activity, BaseApplication.getInstance().getString(R.string.chat_open_storage_camera_permission));
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

    public interface OnShowPermissionPopListener {
        void onShowPermissionPop(boolean agreeToRequest);
    }

    /**
     * 去授权访问所有文件的权限管理页面
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void toAllFilePermissionSetting(Activity activity, int reqCode) {
        if (activity == null || activity.isFinishing()) return;
        PermissionPopWindow permissionPopWindow = new PermissionPopWindow(activity);
        permissionPopWindow.setContentText(BaseApplication.getInstance().getString(R.string.permission_manager_all_file));
        permissionPopWindow.addOnClickListener(new PermissionPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, reqCode);
            }
        });
        permissionPopWindow.showPopupWindow();
    }

    public static void beforeCheckPermission(Activity activity, MotionEvent event, String[] permissions, OnShowPermissionPopListener permissionPopListener) {
        if (permissionPopListener == null) return;
        if (activity == null || activity.isFinishing()) {
            permissionPopListener.onShowPermissionPop(true);
            return;
        }
        if (permissions == null || permissions.length == 0) {
            permissionPopListener.onShowPermissionPop(true);
            return;
        }
        StringBuilder sb = null;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                String str = permissionTipMap(permission);
                if (sb.indexOf(str) == -1) {
                    if (!TextUtils.isEmpty(sb)) {
                        sb.append("\n\n");
                    }
                    sb.append(str);
                }
            }
        }
        if (sb == null || TextUtils.isEmpty(sb)) {
            permissionPopListener.onShowPermissionPop(true);
        } else {
            if(event != null && event.getAction() != MotionEvent.ACTION_DOWN){
                return;
            }
            PermissionPopWindow permissionPopWindow = new PermissionPopWindow(BaseApplication.getInstance());
            permissionPopWindow.setContentText(sb);
            permissionPopWindow.addOnClickListener(new PermissionPopWindow.OnSelectListener() {
                @Override
                public void onCancel() {
                    permissionPopListener.onShowPermissionPop(false);
                }

                @Override
                public void onSure() {
                    permissionPopListener.onShowPermissionPop(true);
                }
            });
            permissionPopWindow.showPopupWindow();
        }
    }
    /**
     * 请求权限前的说明弹窗
     */
    public static void beforeCheckPermission(Activity activity, String[] permissions, OnShowPermissionPopListener permissionPopListener) {
        beforeCheckPermission(activity,null,permissions,permissionPopListener);
    }

    public static String permissionTipMap(String permission) {
        switch (permission) {
            case Manifest.permission.RECORD_AUDIO:
                return BaseApplication.getInstance().getString(R.string.permission_record);
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return BaseApplication.getInstance().getString(R.string.permission_storage);
            case Manifest.permission.CAMERA:
                return BaseApplication.getInstance().getString(R.string.permission_camera);
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return BaseApplication.getInstance().getString(R.string.permission_location);
            default:
                return "";
        }
    }
}
