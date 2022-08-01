package com.tftechsz.common.http;


import com.tftechsz.common.Constants;
import com.tftechsz.common.entity.AccostModeDto;
import com.tftechsz.common.entity.AccostPopupDto;
import com.tftechsz.common.entity.ActivityGiftInfoDto;
import com.tftechsz.common.entity.CallbackExt;
import com.tftechsz.common.entity.CoupleRelieveApplyInfo;
import com.tftechsz.common.entity.FamilyBoxDto;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.entity.FamilyRoleDto;
import com.tftechsz.common.entity.GifTitleDto;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.entity.PartyInfoDto;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.entity.RechargeQuickDto;
import com.tftechsz.common.entity.RedDetailInfo;
import com.tftechsz.common.entity.RedOpenDetails;
import com.tftechsz.common.entity.RedPacketDto;
import com.tftechsz.common.entity.ReviewBean;
import com.tftechsz.common.entity.ShareDto;
import com.tftechsz.common.entity.UserIDs;
import com.tftechsz.common.entity.UserOnline;
import com.tftechsz.common.entity.UserShareDto;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.common.utils.UploadHelper;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PublicService {
    @GET("aliyun_oss_blog_sts")
    Observable<BaseResponse<UploadHelper.OssTokenDto>> getOssToken();

    @GET("aliyun_oss_photo_sts")
    Observable<BaseResponse<UploadHelper.OssTokenDto>> getPhotoOssToken();

    @GET("aliyun_oss_user_sts")
    Observable<BaseResponse<UploadHelper.OssTokenDto>> getUserOssToken();

    /**
     * 红包接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST("red_packet/open")
    Flowable<BaseResponse<RedPacketDto>> openRedPacket(@Field("red_packet_id") int id);

    /**
     * 获取家族ID
     *
     * @return
     */
    @GET("get_id_by_tid")
    Flowable<BaseResponse<FamilyIdDto>> getFamilyId(@Query("tid") String tid);

    /**
     * 进入聊天室
     *
     * @return
     */
    @FormUrlEncoded
    @POST("join")
    Flowable<BaseResponse> joinRoom(@Field("rid") String rid);

    /**
     * 离开
     *
     * @param rid
     * @return
     */
    @FormUrlEncoded
    @POST("leave")
    Flowable<BaseResponse> leaveRoom(@Field("rid") String rid);


    /**
     * 红包-发送-家族招募
     *
     * @param coin   // 多少个金币
     * @param number // 红包个数
     * @param des    // 红包描述
     * @return
     */
    @FormUrlEncoded
    @POST("recruit/red_packet")
    Flowable<BaseResponse<Boolean>> sendGoldRedEnvelopeFamilyRecruit(@Field("coin") int coin, @Field("number") int number, @Field("des") String des);

    /**
     * 红包-发送-家族
     *
     * @param coin            // 多少个金币
     * @param number          // 红包个数
     * @param des             // 红包描述
     * @param receiveUserType // 0不限（默认） 1男 2女
     * @return
     */
    @FormUrlEncoded
    @POST("red/send/family")
    Flowable<BaseResponse<Boolean>> sendGoldRedEnvelopeFamily(@Field("coin") int coin, @Field("number") int number, @Field("receive_user_type") int receiveUserType, @Field("des") String des);

    /**
     * 红包-发送-聊天室
     *
     * @param rid    // 聊天室id
     * @param coin   // 多少个金币
     * @param number // 红包个数
     * @param des    // 红包描述
     * @return
     */
    @FormUrlEncoded
    @POST("red/send/room")
    Flowable<BaseResponse<Boolean>> sendGoldRedEnvelopeRoom(@Field("rid") String rid, @Field("coin") int coin, @Field("number") int number, @Field("des") String des);


    @GET("red/detail")
    Flowable<BaseResponse<RedDetailInfo>> getRedDetail(@Query("red_packet_id") int red_packet_id);

    /**
     * 红包-详细信息
     * http://git.loveccc.cn/backend/markdown/-/blob/master/exch/%E7%BA%A2%E5%8C%85-%E9%A2%86%E5%8F%96%E8%AF%A6%E6%83%85%E5%88%97%E8%A1%A8.md
     */
    @GET("red/open/list")
    Flowable<BaseResponse<RedOpenDetails>> openList(@Query("red_packet_id") int red_packet_id);

    /**
     * 获取快捷支付金额
     *
     * @return
     */
    @GET("payment/quick")
    Flowable<BaseResponse<RechargeQuickDto>> quickRecharge();

    /**
     * 上传错误日志
     *
     * @return
     */
    @FormUrlEncoded
    @POST("debug_app")
    Observable<BaseResponse> uploadLog(@Field("content") String content);

    /**
     * 上头条
     *
     * @param text
     * @return
     */
    @FormUrlEncoded
    @POST("headlines/send")
    Flowable<BaseResponse<Boolean>> sendStories(@Field("text") String text);

    /**
     * 获取分享配置
     *
     * @return
     */
    @GET("invite")
    Flowable<BaseResponse<ShareDto>> getShareConfig(@Query("type") String type);

    /**
     * 获取分享配置
     *
     * @return
     */
    @GET("invite")
    Flowable<BaseResponse<ShareDto>> getShareConfigToPartyParam(@Query("type") String type, @Query("data[party_room_id]") String party_room_id, @Query("data[room_name]") String room_name);

    /**
     * 用户列表-分享
     *
     * @return
     */
    @GET("share/list")
    Flowable<BaseResponse<UserShareDto>> getUserShareList(@Query("type") String type, @Query("page") int page);

    /**
     * 上头条
     *
     * @param user_id
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/accost_popup")
    Flowable<BaseResponse<List<AccostPopupDto>>> accostPopup(@Field("user_id") String user_id);


    /**
     * 获取搭讪类型
     *
     * @return
     */
    @GET("accost/mode")
    Flowable<BaseResponse<AccostModeDto>> getAccostModel();

    /**
     * 微信支付自查
     *
     * @return
     */
    @FormUrlEncoded
    @POST("wechat/result")
    Flowable<BaseResponse<Boolean>> wechatResult(@Field("status") int status, @Field("trade_no") String trade_no);

    /**
     * 身份识别检测
     *
     * @return
     */
    @GET("check/mode")
    Flowable<BaseResponse<Boolean>> checkModel(@Query("phone_model") String phone_model);

    /**
     * 获取可以设置的用户角色
     *
     * @return
     */
    @GET("config/role")
    Flowable<BaseResponse<List<FamilyRoleDto>>> getRole(@Query("user_id") int user_id);

    /**
     * 礼物横幅
     * 客户端做请求限制：每个礼物 间隔5分钟
     */
    @POST("gift/activity/info")
    Flowable<BaseResponse<ActivityGiftInfoDto>> giftInfoActivity(@Body RequestBody params);

    /**
     * 设置用户的角色
     */
    @FormUrlEncoded
    @POST("user/role")
    Flowable<BaseResponse<Boolean>> setUserRole(@Field("user_id") int user_id, @Field("role_id") int role_id);

    /**
     * 获取禁言列表
     *
     * @return
     */
    @GET("mute_map")
    Flowable<BaseResponse<List<FamilyInactiveDto>>> getMuteMap();

    /**
     * 禁言
     *
     * @param operation 1：禁言 0 取消禁言
     */
    @FormUrlEncoded
    @POST("mute")
    Flowable<BaseResponse<Boolean>> muteUser(@Field("family_id") int family_id, @Field("user_id") int user_id, @Field("type") int type, @Field("operation") int operation);


    /**
     * 拉黑家族
     *
     * @param operation 1：拉黑 0 取消拉黑
     */
    @FormUrlEncoded
    @POST("black/user")
    Flowable<BaseResponse<Boolean>> blackUser(@Field("family_id") int family_id, @Field("user_id") int user_id, @Field("operation") int operation);

    /**
     * 踢出用户
     */
    @FormUrlEncoded
    @POST("user/out")
    Flowable<BaseResponse<Boolean>> removeUser(@Field("user_id") int user_id);

    /**
     * 获取当前角色
     *
     * @return
     */
    @GET("my_role")
    Flowable<BaseResponse<FamilyIdDto>> getFamilyRole(@Query("to_user_id") String to_user_id);


    /**
     * 获取真人认证的信息
     *
     * @return
     */
    @GET("check/real")
    Flowable<BaseResponse<RealStatusInfoDto>> getRealInfo();


    /**
     * 获取实名认证的信息
     *
     * @return
     */
    @GET("check/self")
    Flowable<BaseResponse<RealStatusInfoDto>> getSelfInfo();

    /**
     * 抢家族红包
     *
     * @return
     */
    @GET("rob/coins")
    Flowable<BaseResponse<FamilyBoxDto>> rushFamilyBox();

    /**
     * 获取礼物背包
     */
    @GET("item/all")
    Flowable<BaseResponse<String>> getGiftPack();


    /**
     * 获取家族的成员信息
     *
     * @return
     */
    @GET("get_user_info")
    Flowable<BaseResponse<FamilyIdDto>> getFamilyInfoById(@Query("user_id") String user_id, @Query("family_id") int family_id);


    /**
     * 获取派对的成员信息
     *
     * @return
     */
    @FormUrlEncoded
    @POST("room/userinfo")
    Flowable<BaseResponse<PartyInfoDto>> getPartyUserInfo(@Field("user_id") String user_id, @Field("room_id") String room_id);

    /**
     * 判断是否有收礼权限
     *
     * @return
     */
    @FormUrlEncoded
    @POST("room/user/receive/gift")
    Flowable<BaseResponse<List<Integer>>> hasReceiveGift(@Field("uids") String params);


    /**
     * 派对送礼
     *
     * @param gift_id       礼物id
     * @param gift_num      礼物数量
     * @param to            送给谁
     * @param party_room_id 派对id
     * @param from_type     1-默认场景 2-PK场景
     * @return
     */
    @FormUrlEncoded
    @POST("gift/callback")
    Flowable<BaseResponse<CallbackExt>> sendPartyGift(@Field("gift_id") int gift_id, @Field("gift_num") int gift_num, @Field("to") String to, @Field("party_room_id") String party_room_id, @Field("from_type") int from_type);

    /**
     * 身份证实名校验
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/partyself")
    Flowable<BaseResponse<RealCheckDto>> partySelf(@Field("identity_id") String identity_id, @Field("identity_name") String identity_name);

    /**
     * 消息检测反作弊
     *
     * @return
     */
    @FormUrlEncoded
    @POST("anti_cheat")
    Flowable<BaseResponse<Boolean>> uploadCheat(@Field("scene") String scene, @Field("content") String content);

    /**
     * 获取礼物类别
     *
     * @param scene 场景 1 家族 2派对 3 私人聊天
     */
    @GET("gift/child_cate")
    Flowable<BaseResponse<List<GifTitleDto>>> getGiftChildCate(@Query("scene") int scene, @Query("selected_cate") int selected_cate);

    /**
     * 获取礼物列表
     *
     * @param scene      场景 1 家族 2派对 3 私人聊天
     * @param child_cate 1人气;2豪华;3新奇;4贵族; 5 背包
     */
    @GET("gift/list_by")
    Flowable<BaseResponse<String>> getGiftList(@Query("scene") int scene, @Query("child_cate") int child_cate);

    /**
     * 背包礼物检测
     *
     * @return
     */
    @FormUrlEncoded
    @POST("item/check")
    Flowable<BaseResponse<Boolean>> packGiftCHeck(@Field("item_id") int item_id, @Field("num") int num);

    /**
     * 效验手机号
     *
     * @return
     */
    @FormUrlEncoded
    @POST("phone/check")
    Flowable<BaseResponse<Boolean>> checkPhone(@Field("phone") String phone);

    /**
     * 效验验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("phone/code/check")
    Flowable<BaseResponse<Boolean>> checkCode(@Field("phone") String phone, @Field("code") String code);

    /**
     * 发送验证码
     *
     * @param pram
     * @return
     */
    @POST("send/note")
    Flowable<BaseResponse<String>> sendCode(@Body LoginReq pram);


    /**
     * 提现到支付宝
     *
     * @return
     */
    @POST("convert/rmb")
    Flowable<BaseResponse<String>> withdraw(@Body WithdrawReq withdraw);

    /**
     * 背包礼物检测
     *
     * @return
     */
    @GET("item/num")
    Flowable<BaseResponse<GiftDto>> packGiftNum(@Query("item_id") int item_id);

    /**
     * 获取密友在线状态
     *
     * @return
     */
    @POST("uids/is_online")
    Flowable<BaseResponse<List<UserOnline>>> is_online(@Body UserIDs uids);


    /**
     * 解除情侣-（同意、拒绝）-弹窗
     * "type": 3   // 1-接受解除申请 2-拒绝解除申请 9-取消申请
     */
    @FormUrlEncoded
    @POST("couple_relieve/handle")
    Flowable<BaseResponse<Boolean>> relieveCoupleHandler(@Field("apply_id") int apply_id, @Field("type") int type);

    /**
     * 获取解除情侣信息(处理申请弹窗)
     */
    @GET("couple_relieve/apply_info")
    Flowable<BaseResponse<CoupleRelieveApplyInfo>> coupleRelieveApply(@Query("apply_id") int apply_id);

    /**
     * 获取审核相关配置
     */
    @GET(Constants.HOST_REVIEW_CONFIG)
    Flowable<ReviewBean> getReviewConfig(@Path("packageName") String packageName,
                                         @Path("channel") String channel,
                                         @Path("versionName") String versionName);
}
