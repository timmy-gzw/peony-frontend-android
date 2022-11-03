package com.tftechsz.common.http;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.snail.antifake.deviceid.AndroidDeviceIMEIUtil;
import com.snail.antifake.jni.EmulatorDetectUtil;
import com.tftechsz.common.ApiConstants;
import com.tftechsz.common.BuildConfig;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitManager {
    private static Retrofit retrofit;
    public static RetrofitManager retrofitManager;
    private UserProviderService service;

    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (retrofitManager == null) {
                    retrofitManager = new RetrofitManager();
                }
            }
        }
        return retrofitManager;
    }

    public synchronized <T> T createApi(Class<T> clazz, String url) {
        if (null == service)
            service = ARouter.getInstance().navigation(UserProviderService.class);
        buildBaseRetrofit(url, 2);
        return retrofit.create(clazz);
    }


    public synchronized <T> T createUserApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_USER);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createFamilyApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_FAMILY);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createChatRoomApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_CHAT_ROOM);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createIMApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_IM);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createIMApi2(Class<T> clazz) {
        setUrl2(ApiConstants.HOST_IM);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createPaymentApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_PAYMENT);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createUploadApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_UPLOAD);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createUploadCheatApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_USER, 2);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createBlogApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_BLOG);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createConfigApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_CONFIG);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createExchApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_EXCH);
        return retrofit.create(clazz);
    }

    public synchronized <T> T createPartyApi(Class<T> clazz) {
        setUrl(ApiConstants.HOST_PARTY);
        return retrofit.create(clazz);
    }


    /**
     * 设置url
     *
     * @param host
     */
    private void setUrl(String host) {
        setUrl(host, 15);
    }

    /**
     * 设置url
     *
     * @param host
     */
    private void setUrl(String host, int time) {
        if (null == service)
            service = ARouter.getInstance().navigation(UserProviderService.class);
        ConfigInfo configInfo = service.getConfigInfo();
        if (null != configInfo) {
            String url = configInfo.sys.common_api_scheme + host + configInfo.sys.common_domain + "/";
            buildBaseRetrofit(url, time);
        }
    }

    /**
     * 设置url
     *
     * @param host
     */
    private void setUrl2(String host) {
        if (null == service)
            service = ARouter.getInstance().navigation(UserProviderService.class);
        ConfigInfo configInfo = service.getConfigInfo();
        if (null != configInfo) {
            String url = configInfo.sys.common_api_scheme_2 + host + configInfo.sys.common_domain_2 + "/";
            if (TextUtils.isEmpty(configInfo.sys.common_api_scheme_2)) {
                url = configInfo.sys.common_api_scheme + host + configInfo.sys.common_domain + "/";
            }
            buildBaseRetrofit(url, 15);
        }
    }

    /**
     * okhttp
     */
    private OkHttpClient provideOkHttpClient(int time) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(time * 1000, TimeUnit.MILLISECONDS).readTimeout(time * 1000, TimeUnit.MILLISECONDS);

        builder.addInterceptor(chain -> {
            //手机版本，厂商，uuid imsi appName,appVersion
            String apiUa = "";
            //芍药号
            String userCode = service.getUserInfo() != null ? service.getUserInfo().getUser_code() : "";

            String yundunToken = "";
            //对应的接口添加反作弊
            if (!TextUtils.isEmpty(chain.request().url().toString())) {
                String url = chain.request().url().toString();
                if (url.contains("/login") || url.contains("/add") || url.contains("/blog_comment")
                        || url.contains("/info/complete") || url.contains("/anti_cheat") || url.contains("/behavior/accost")) {
                    yundunToken = MMKVUtils.getInstance().decodeString(Constants.YUNDUN_TOKEN);
                }
                if (url.contains("/jsonConfig/")) {
                    apiUa = "";
                } else {
                    apiUa = AppUtils.getApiUa();
                }
            } else {
                apiUa = AppUtils.getApiUa();
            }
            boolean isEmulator =  EmulatorDetectUtil.isEmulator(BaseApplication.getInstance());
            String deviceIdType = Utils.getDeviceIdType();
            Request request = chain.request().newBuilder()
                    .addHeader("x-auth-token", service.getToken())
                    .addHeader("api-ua", apiUa)
                    .addHeader("x-user-code", userCode)
                    .addHeader("x-anti-cheat-token", yundunToken)
                    .addHeader("dit", deviceIdType)
                    .addHeader("is-emulator", String.valueOf(isEmulator ? 1 : 0))
                    .build();
            return chain.proceed(request);
        });
        builder.addInterceptor(new ParmaInterceptor());
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logInterceptor());
            builder.sslSocketFactory(createSSLSocketFactory(), mMyTrustManager);//屏蔽ssl整数验证
        }
        builder.addInterceptor(new EncryptionInterceptor());
        return builder.build();
    }

    private final int logSubLenth = 2000;//每行log长度

    private HttpLoggingInterceptor logInterceptor() {
        //新建log拦截器
        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor(message -> {
                    if (message.length() > logSubLenth) {
                        logSplit(message, 1);
                    } else {
                        try {
                            LogUtil.e("OkHttpClient", "OkHttpMessage:数据：     " + URLDecoder.decode(convertUnicodeToCh(message), "UTF-8"));
//                            LogUtil.e("OkHttpClient", "OkHttpMessage:数据：     " + convertUnicodeToCh(message));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    /**
     * 将unicode字符串转为正常字符串
     *
     * @param str unicode字符串（比如"\u67e5\u8be2\u6210\u529f"）
     * @return 转换后的字符串（比如"查询成功"）
     */
    private static String convertUnicodeToCh(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
        Matcher matcher = pattern.matcher(str);

        // 迭代，将str中的所有unicode转换为正常字符
        while (matcher.find()) {
            String unicodeFull = matcher.group(1); // 匹配出的每个字的unicode，比如\u67e5
            String unicodeNum = matcher.group(2); // 匹配出每个字的数字，比如\u67e5，会匹配出67e5

            // 将匹配出的数字按照16进制转换为10进制，转换为char类型，就是对应的正常字符了
            char singleChar = (char) Integer.parseInt(unicodeNum, 16);

            // 替换原始字符串中的unicode码
            str = str.replace(unicodeFull, singleChar + "");
        }
        return str;
    }

    private void logSplit(String message, int i) {
        if (message.length() <= logSubLenth) {
            try {
                LogUtil.e("RetrofitManager", "OkHttpMessage: 数据" + i + "：     " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        } else if (message.length() > logSubLenth * 4) {
            return;
        }
        String msg1 = message.substring(0, logSubLenth);
        try {
            LogUtil.e("RetrofitManager", "OkHttpMessage: 数据" + i + "：     " + msg1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msg2 = message.substring(logSubLenth);
        logSplit(msg2, ++i);
    }

    private Retrofit buildBaseRetrofit(String url, int time) {
        retrofit = new Retrofit.Builder().client(provideOkHttpClient(time)).baseUrl(url).
                addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).addConverterFactory(new FileRequestBodyConverterFactory()).build();//文件上传
        return retrofit;
    }


    public static class FileRequestBodyConverterFactory extends Converter.Factory {
        @Override
        public Converter<File, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            return new FileRequestBodyConverter();
        }
    }

    static class FileRequestBodyConverter implements Converter<File, RequestBody> {
        @Override
        public RequestBody convert(File file) throws IOException {
            return RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        }
    }


    MyTrustManager mMyTrustManager;

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            mMyTrustManager = new MyTrustManager();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{mMyTrustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    //实现X509TrustManager接口
    public static class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
