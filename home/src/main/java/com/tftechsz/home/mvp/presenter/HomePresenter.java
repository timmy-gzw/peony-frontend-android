package com.tftechsz.home.mvp.presenter;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.util.MD5Util;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tftechsz.common.ApiConstants;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AesUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.home.BuildConfig;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.home.entity.req.RecommendReq;
import com.tftechsz.home.mvp.iview.IHomeView;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePresenter extends BasePresenter<IHomeView> {
    private final HomeApiService service;
    private final AccostService accostService;
    private final MineService mineService;
    private final UserProviderService userService;

    public HomePresenter() {
        service = RetrofitManager.getInstance().createUserApi(HomeApiService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        accostService = ARouter.getInstance().navigation(AccostService.class);
        userService = ARouter.getInstance().navigation(UserProviderService.class);
    }

    /**
     * 获取推荐用户
     */
    public void getRecommendUser(int page, boolean uploadTop) {
        if (service == null) {
            return;
        }
        addNet(service.getRecommendUser(page).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RecommendReq>>() {
                    @Override
                    public void onSuccess(BaseResponse<RecommendReq> response) {
                        if (null == getView()) return;
                        if (null == response.getData())
                            getView().getRecommendFail("");
                        else
                            getView().getRecommendSuccess(response.getData(), uploadTop);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getRecommendFail(msg);
                        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance()))
                            return;
                        getConfig(0, "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null == getView()) return;
                        getView().getRecommendFail(e.getMessage());
                        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance()))
                            return;
                        getConfig(0, "");
                    }
                }));
    }


    /**
     * 获取附近用户
     */
    public void getNearUser(int page, boolean uploadTop) {
        String lat = MMKVUtils.getInstance().decodeString(userService.getUserId() + Constants.LOCATION_LATITUDE);
        String lon = MMKVUtils.getInstance().decodeString(userService.getUserId() + Constants.LOCATION_LONGITUDE);
        double curLat = TextUtils.isEmpty(lat) ? 0 : Double.parseDouble(Objects.requireNonNull(lat));
        double curLon = TextUtils.isEmpty(lon) ? 0 : Double.parseDouble(Objects.requireNonNull(lon));
        if (curLat == 0 || curLon == 0)
            return;
        addNet(service.getNearUser(page, 20, curLon, curLat).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RecommendReq>>() {
                    @Override
                    public void onSuccess(BaseResponse<RecommendReq> response) {
                        if (null == getView()) return;
                        if (null == response.getData())
                            getView().getRecommendFail("");
                        else
                            getView().getRecommendSuccess(response.getData(), uploadTop);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getRecommendFail(msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null == getView()) return;
                        getView().getRecommendFail(e.getMessage());
                    }
                }));
    }

    /**
     * 搭讪用户
     */
    public void accostUser(int position, String userId) {
        accostService.accostUser(userId, CommonUtil.isBtnTextHome(userService) ? 1 : 2, 2, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                if (null == getView()) return;
                getView().accostUserSuccess(position, response.getData());
            }

        });

    }


    /**
     * 检查私信次数
     */
    public void getMsgCheck(String userId) {
        mineService.getMsgCheck(userId, new ResponseObserver<BaseResponse<MsgCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<MsgCheckDto> response) {
                if (null == getView()) return;
                getView().getCheckMsgSuccess(userId, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });

    }


    /**
     * 获取配置
     */
    public void getConfig(int flag, String url) {
        HomeApiService configService;
        if (flag == 0) {
            String newUrl = SPUtils.getString(Constants.CURRENT_HOST);
            configService = RetrofitManager.getInstance().createApi(HomeApiService.class, BuildConfig.DEBUG ? TextUtils.isEmpty(newUrl) ? ApiConstants.HOST_TEST : newUrl : ApiConstants.HOST);
        } else if (flag == 2) {
            configService = RetrofitManager.getInstance().createApi(HomeApiService.class, url);
        } else {
            configService = RetrofitManager.getInstance().createApi(HomeApiService.class, BuildConfig.DEBUG ? ApiConstants.HOST_TEST : ApiConstants.HOST_RESERVE);
        }
        addNet(configService.getConfig().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (!TextUtils.isEmpty(response.getData())) {
                            try {
                                String key = response.getData().substring(0, 6);
                                byte[] data = Base64.decodeBase64(response.getData().substring(6).getBytes());
                                String iv = MD5Util.toMD532(key).substring(0, 16);
                                byte[] jsonData = AesUtil.AES_cbc_decrypt(data, MD5Util.toMD532(key).getBytes(), iv.getBytes());
                                LogUtil.e("------------", new String(jsonData));
                                userService.setConfigInfo(new String(jsonData));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (flag == 0) {
                            downLunch();
                        } else if (flag == 2) {
                            getConfig(3, "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (flag == 0) {
                            downLunch();
                        } else if (flag == 2) {
                            getConfig(3, "");
                        }
                    }

                }));
    }

    /**
     * 下载配置信息
     */
    public void downLunch() {
        OkHttpClient ClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(BuildConfig.DEBUG ? ApiConstants.HOST_TEST_DOWN : ApiConstants.HOST_DOWN).build();
        Call call = ClientBuilder.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getConfig(3, "");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String url = Objects.requireNonNull(response.body()).string();
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    getConfig(2, url + "/");
                }
            }
        });
    }


}
