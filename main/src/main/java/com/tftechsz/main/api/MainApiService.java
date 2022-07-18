package com.tftechsz.main.api;


import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.home.entity.SignInSuccessBean;
import com.tftechsz.main.entity.UpdateLocationReq;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MainApiService {


    /**
     * 用户提交经纬度
     */
    @POST("search/start")
    Flowable<BaseResponse<String>> setAddress(@Body UpdateLocationReq data);

    //用户签到-列表
    @GET("sign/list")
    Flowable<BaseResponse<SignInBean>> signList();

    /**
     * 用户签到-列表
     *
     * @param scene home：首页
     *              my：我的
     */
    @GET("sign/list_new")
    Flowable<BaseResponse<SignInBean>> signListNew(@Query("scene") String scene);

    //用户签到-
    @POST("sign/add")
    Flowable<BaseResponse<SignInSuccessBean>> sign();
}
