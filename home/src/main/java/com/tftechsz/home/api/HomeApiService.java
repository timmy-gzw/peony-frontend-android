package com.tftechsz.home.api;


import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.home.entity.InfoPictureBean;
import com.tftechsz.home.entity.req.RecommendReq;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.http.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HomeApiService {

    /**
     * 获取config
     */
    @GET("launch")
    Flowable<BaseResponse<String>> getConfig();

    /**
     * 获取个人用户信息
     */
    @GET("info/my")
    Flowable<BaseResponse<UserInfo>> getUserInfo();

    /**
     * 获取推荐人
     */
    @GET("city/list")
    Flowable<BaseResponse<RecommendReq>> getRecommendUser(@Query("page") int page);

    /**
     * 搜索
     */
    @GET("search/list")
    Flowable<BaseResponse<List<UserInfo>>> searchUser(@Query("page") int page, @Query("keywords") String keywords);

    /**
     * 搭讪用户
     *
     * @return accost_type  1 喜欢 2 搭讪
     * accost_from  const TYPE_SYSTEM = 1; // 系统 月老逻辑
     * const TYPE_HOME = 2; // 首页搭讪
     * const TYPE_DETAIL = 3; // 个人资料页搭讪
     * const TYPE_BLOG = 4; // 动态搭讪
     * const TYPE_PHOTO = 5; // 相册搭讪
     * const TYPE_SEARCH = 6; // 搜索搭讪
     * const TYPE_TIPS = 7; // 私聊TIPS搭讪
     * const TYPE_ALERT = 8; // 首页日今弹窗搭讪
     * const TYPE_PARTY_ACCOST = 9 //派对在线搭讪
     */
    @FormUrlEncoded
    @POST("behavior/accost")
    Flowable<BaseResponse<AccostDto>> accostUser(@Field("user_id") String user_id, @Field("accost_type") int accost_type, @Field("accost_from") int accost_from);

    /**
     * 获取附近人
     *
     * @param page 第几页默认1
     * @return
     */
    @GET("near/list")
    Flowable<BaseResponse<RecommendReq>> getNearUser(@Query("page") int page, @Query("limit") int limit, @Query("longitude") double longitude, @Query("latitude") double latitude);

    /**
     * 视频匹配
     */
    @POST("matching/video/add")
    Flowable<BaseResponse<Boolean>> videoMatch();

    /**
     * 退出视频匹配
     *
     * @return
     */
    @POST("matching/video/quit")
    Flowable<BaseResponse<Boolean>> quitVideoMatch();

    /**
     * 音频匹配
     */
    @POST("matching/voice/add")
    Flowable<BaseResponse<Boolean>> voiceMatch();

    /**
     * 退出音频匹配
     *
     * @return
     */
    @POST("matching/voice/quit")
    Flowable<BaseResponse<Boolean>> quitVoiceMatch();

    /**
     * 视频心跳
     *
     * @return
     */
    @POST("matching/video/beat")
    Flowable<BaseResponse<Boolean>> videoBeat();

    /**
     * 语音心跳
     *
     * @return
     */
    @POST("matching/voice/beat")
    Flowable<BaseResponse<Boolean>> voiceBeat();

    /**
     * 获取推荐人
     */
    @GET("call_check")
    Flowable<BaseResponse<CallCheckDto>> callCheck(@Query("to_user_id") String to_user_id, @Query("call_type") String call_type);

    //查看-别人相册-所有
    @GET("info/picture/they/all")
    Flowable<BaseResponse<InfoPictureBean>> getInfoPicture(@Query("user_id") int uid);

    @FormUrlEncoded
    @POST("behavior/picture/praise")
    Flowable<BaseResponse<Boolean>> picLike(@Field("user_id") int user_id);

}
