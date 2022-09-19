package com.tftechsz.common.privacy;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.Keep;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.privacy_proxy.PrivacyProxyCall;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.PrivacySentryBuilder;
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil;

/**
 * 自定义隐私合规hook对象
 */
@Keep
@PrivacyClassProxy
public class PrivacyProxyCustom extends PrivacyProxyCall {

    @PrivacyMethodProxy(
            originalClass = PackageManager.class,
            originalMethod = "getPackageInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static PackageInfo getPackageInfo(PackageManager manager, String packageName, int flags) throws PackageManager.NameNotFoundException {
        PrivacyProxyUtil.Util.INSTANCE.doFilePrinter("getPackageInfo", "getPackageInfo", packageName + ", " + flags, false, false);
        PrivacySentryBuilder builder = PrivacySentry.Privacy.INSTANCE.getBuilder();
        if (builder != null && builder.isVisitorModel()) {
            return null;
        }
        return manager.getPackageInfo(packageName, flags);
    }
}
