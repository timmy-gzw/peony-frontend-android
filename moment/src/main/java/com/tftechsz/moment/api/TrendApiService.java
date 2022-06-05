package com.tftechsz.moment.api;


import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.moment.entity.CommentListItem;
import com.tftechsz.moment.entity.LikeListItem;
import com.tftechsz.moment.entity.req.PublishReq;
import com.tftechsz.moment.mvp.entity.NoticeBean;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrendApiService {
    /**
     * 获取推荐 附近 关注动态
     *
     * @param type
     * @param pageSize
     * @param page
     * @return
     */
    @GET("get_blog_list")
    Flowable<BaseResponse<List<CircleBean>>> getDynamicRecommend(@Query("type") String type, @Query("page") int page);

    /**
     * 获取个人动态
     *
     * @return
     */
    @GET("get_they_info")
    Flowable<BaseResponse<List<CircleBean>>> getUserTrend(@Query("page") int page, @Query("user_id") String user_id);

    /**
     * 查看指定动态
     *
     * @return
     */
    @GET("get_info")
    Flowable<BaseResponse<CircleBean>> get_info(@Query("blog_id") int blog_id);

    /**
     * 获取自己的动态
     *
     * @return
     */
    @GET("get_my_info")
    Flowable<BaseResponse<List<CircleBean>>> getSelfTrend(@Query("page") int page);

    //点赞
    @POST("blog_praise")
    Flowable<BaseResponse<Boolean>> praise(@Query("blog_id") int blogId);


    /**
     * 发布动态
     *
     * @param mPublishReq
     * @return
     */
    @POST("add")
    Flowable<BaseResponse<Boolean>> publish(@Body PublishReq mPublishReq);

    /**
     * @return
     */
    @GET("before_check")
    Flowable<BaseResponse<Boolean>> beforeCheck();

    /**
     * 删除动态
     *
     * @param blog_id
     * @return
     */
    @DELETE("blog_delete/{blog_id}")
    Flowable<BaseResponse<Boolean>> delTrend(@Path("blog_id") int blog_id);


    /**
     * 浏览次数添加
     *
     * @return
     */
    @FormUrlEncoded
    @POST("views_count")
    Flowable<BaseResponse<Boolean>> trendViewCount(@Field("blog_ids") String blog_ids);

    /**
     * 获取动态评论
     *
     * @param page
     * @param blogId
     * @return
     */
    @GET("get_blog_comment")
    Flowable<BaseResponse<List<CommentListItem>>> getTrendComment(@Query("page") int page, @Query("blog_id") int blogId);


    /**
     * 动态评论
     *
     * @return
     */
    @FormUrlEncoded
    @POST("blog_comment")
    Flowable<BaseResponse<Boolean>> trendComment(@Field("blog_id") int blogId, @Field("content") String content);

    /**
     * 动态评论-回复
     *
     * @return
     */
    @FormUrlEncoded
    @POST("comment/reply")
    Flowable<BaseResponse<Boolean>> trendCommentReply(@Field("comment_id") int comment_id, @Field("content") String content);

    /**
     * 删除评论
     *
     * @return
     */
    @FormUrlEncoded
    @POST("blog_comment_del")
    Flowable<BaseResponse<Boolean>> delTrendComment(@Field("comment_id") int comment_id);


    /**
     * 获取动态点赞
     *
     * @param page
     * @param blogId
     * @return
     */
    @GET("get_blog_praise")
    Flowable<BaseResponse<List<LikeListItem>>> getTrendPraise(@Query("page") int page, @Query("blog_id") int blogId);

    /**
     * 动态举报
     *
     * @param type_id
     * @param blog_id
     * @param message
     * @param resource
     * @return
     */
    @FormUrlEncoded
    @POST("blog_report")
    Flowable<BaseResponse<Boolean>> report(@Field("type_id") int type_id, @Field("blog_id") int blog_id, @Field("message") String message, @Field("resource") String resource);

    /**
     * 用户举报
     *
     * @param type_id  类型
     * @param user_id  用户id
     * @param message  消息
     * @param resource 资源
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/complain/user")
    Flowable<BaseResponse<Boolean>> reportUser(@Field("type_id") int type_id, @Field("user_id") int user_id, @Field("message") String message, @Field("resource") String resource);


    /**
     * 用户反馈
     *
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/complain/system")
    Flowable<BaseResponse<Boolean>> feedback(@Field("message") String message, @Field("resource") String resource);

    /**
     * 动态_通知列表
     *
     * @return
     */
    @GET("notice/list")
    Flowable<BaseResponse<List<NoticeBean>>> getNotice(@Query("page") int page);
}

