package com.tftechsz.common.http;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.MMKVUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ParmaInterceptor implements Interceptor {

    private UserProviderService service;

    public ParmaInterceptor() {
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @NotNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        int inter = MMKVUtils.getInstance().decodeInt(Constants.UPDATE_CONFIG_LAUNCH);
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        HttpUrl oldHttpUrl = request.url();
        MMKVUtils.getInstance().encode(Constants.CURRENT_REQUEST_URL, oldHttpUrl.toString());
        if (inter == 0) {
            return chain.proceed(builder.url(oldHttpUrl).build());
        }
        String newHost = oldHttpUrl.host();
        String first = newHost.substring(0, newHost.indexOf("."));
        String replace = newHost.substring(first.length() + 1);
        String domain = "";
        if (null == service)
            service = ARouter.getInstance().navigation(UserProviderService.class);
        ConfigInfo configInfo = service.getConfigInfo();
        if (null != configInfo && configInfo.sys != null) {
            domain = newHost.replace(replace, configInfo.sys.common_domain);
        }
        HttpUrl newBaseUrl = oldHttpUrl
                .newBuilder()
                .scheme(oldHttpUrl.scheme())
                .host(domain.replace(":" + oldHttpUrl.port(), ""))
                .port(oldHttpUrl.port())
                .build();
        MMKVUtils.getInstance().encode(Constants.CURRENT_REQUEST_URL, newBaseUrl.url().toString());
        if (TextUtils.isEmpty(newBaseUrl.url().toString())) {
            return chain.proceed(builder.url(oldHttpUrl).build());
        }
        return chain.proceed(builder.url(newBaseUrl).build());
    }
}

