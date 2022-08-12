package com.tftechsz.mine.api;


import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.PaymentDto;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.mine.entity.AccostSettingBean;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.DressListDto;
import com.tftechsz.mine.entity.DressUpBean;
import com.tftechsz.mine.entity.NobleBean;
import com.tftechsz.mine.entity.NoteValueConfirmDto;
import com.tftechsz.mine.entity.SignNumBean;
import com.tftechsz.mine.entity.UpdateAccostSettingBean;
import com.tftechsz.mine.entity.UserCertifyBean;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.VipPrivilegeBean;
import com.tftechsz.mine.entity.VisitorDto;
import com.tftechsz.mine.entity.dto.ChargeInfoDto;
import com.tftechsz.mine.entity.dto.ChargeItemDto;
import com.tftechsz.mine.entity.dto.CheckExchangeDto;
import com.tftechsz.mine.entity.dto.ExchangeRecord;
import com.tftechsz.mine.entity.dto.FriendDto;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.entity.dto.GradeLevelDto;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.mine.entity.dto.LogoutStatusDto;
import com.tftechsz.mine.entity.dto.MinePhotoDto;
import com.tftechsz.mine.entity.dto.NearUserDto;
import com.tftechsz.mine.entity.dto.OcrCheckDto;
import com.tftechsz.mine.entity.dto.PrivacyDto;
import com.tftechsz.mine.entity.dto.ShopInfoDto;
import com.tftechsz.mine.entity.dto.TrendDto;
import com.tftechsz.mine.entity.dto.VipConfigBean;
import com.tftechsz.mine.entity.req.BindData;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.mine.entity.req.DelAccostSettingBean;
import com.tftechsz.mine.entity.req.GetBindData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MineApiService {

    /**
     * 获取config
     */
    @GET("launch")
    Flowable<BaseResponse<String>> getConfig();


    /**
     * 获取首页配置
     */
    @GET("common/init")
    Flowable<BaseResponse<NearUserDto>> initNearUser();

    /**
     * 上传设备信息
     *
     * @param umeng_id  友盟的唯一ID
     * @param imei      imei
     * @param oaid      oaid"
     * @param is_first  是否是第一次激活，获取设备号之后的激活写 0
     * @param from_type 来源类型 default = 其他模式，h5 =落地页
     * @param key       h5 模式下的唯一标识
     * @return
     */
    @FormUrlEncoded
    @POST("user/active")
    Flowable<BaseResponse<Boolean>> uploadDeviceInfo(@Field("umeng_id") String umeng_id, @Field("imei") String imei, @Field("oaid") String oaid
            , @Field("android_id") String android_id, @Field("mac") String mac, @Field("from_channel") String from_channel, @Field("from_type") String from_type, @Field("key") String key, @Field("is_first") int is_first);

    /**
     * 登录
     */
    @POST("login")
    Flowable<BaseResponse<LoginDto>> userLogin(@Body LoginReq pram);

    /**
     * 获取充值金额
     *
     * @return
     */
    @GET("payment")
    Flowable<BaseResponse<List<RechargeDto>>> getRechargeList();

    /**
     * 获取充值金额
     *
     * @return
     */
    @GET("payment/new")
    Flowable<BaseResponse<PaymentDto>> getRechargeNewList();

    /**
     * 绑定数据
     *
     * @param
     * @return
     */
    @POST("info/bind")
    Flowable<BaseResponse<String>> bindData(@Body BindData pram);

    /**
     * 绑定数据-微信
     *
     * @param
     * @return
     */
    @FormUrlEncoded
    @POST("info/bind/wechat_repair")
    Flowable<BaseResponse<String>> bindWechatData(@Field("code") String code);

    /**
     * 获取绑定数据
     *
     * @return
     */
    @GET("info/bind")
    Flowable<BaseResponse<GetBindData>> getBindData();

    /**
     * 第三方解绑
     *  有效值：wechat、qq
     * @return
     */
    @FormUrlEncoded
    @POST("unbind/third")
    Flowable<BaseResponse> unBindThird(@Field("type")String type);

    /**
     * 用户信息完善
     */
    @POST("info/complete")
    Flowable<BaseResponse<String>> completeUserInfo(@Body CompleteReq pram);

    /**
     * 用户信息完善
     */
    @GET("info/complete")
    Flowable<BaseResponse<CompleteReq>> getCompleteUserInfo();

    /**
     * 随机名称
     */
    @GET("rand/nickname")
    Flowable<BaseResponse<String>> getRandomNikeName();


    /**
     * 获取用户信息  第三登录获取用户信息用
     */
    @GET("base/info")
    Flowable<BaseResponse<UserInfo>> getThirdUserInfo();

    /**
     * 获取个人用户信息
     */
    @GET("info/my")
    Flowable<BaseResponse<UserInfo>> getUserInfo();

    /**
     * 检查女性可以私信次数
     *
     * @return
     */
    @FormUrlEncoded
    @POST("before_send_msg_check")
    Flowable<BaseResponse<MsgCheckDto>> getMsgCheck(@Field("user_id") String user_id, @Field("type") int type);

    /**
     * 检查打电话前是否真人认证
     *
     * @param user_id
     * @param from    1: 个人资料  2：私聊
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/before_call_check")
    Flowable<BaseResponse<CallCheckDto>> getCallCheck(@Field("user_id") String user_id, @Field("from") int from);

    /**
     * 获取个人用户信息详情
     */
    @GET("info/detail")
    Flowable<BaseResponse<UserInfo>> getUserInfoDetail();

    /**
     * 根据用户ID获取信息
     *
     * @param userId
     * @return
     */
    @GET("info/they")
    Flowable<BaseResponse<UserInfo>> getUserInfoById(@Query("user_id") String userId);

    /**
     * 获取我的积分
     */
    @GET("info/integral")
    Flowable<BaseResponse<IntegralDto>> getIntegral();

    /**
     * 获取我的音符
     */
    @GET("info/note_value")
    Flowable<BaseResponse<IntegralDto>> getNoteValue();

    /**
     * 获取好友 ， 关注 ， 粉丝数量
     *
     * @param type //好友列表类型：watch = 关注列表，fans = 粉丝列表，friend = 好友列表 ； 默认 friend
     */
    @GET("watch/list")
    Flowable<BaseResponse<List<FriendDto>>> getFriend(@Query("page") int page, @Query("type") String type);

    /**
     * 获取用户黑名单
     */
    @GET("black/list")
    Flowable<BaseResponse<List<FriendDto>>> getBlackList();

    /**
     * 音符兑换金币
     *
     * @param value 兑换的金币
     * @return 1
     */
    @FormUrlEncoded
    @POST("convert/musical_note_coin")
    Flowable<BaseResponse<Boolean>> exchangeNoteValue(@Field("value") int value);


    /**
     * 用户音符兑换金币
     * 兑换操作
     */
    @GET("convert/confirm")
    Flowable<BaseResponse<NoteValueConfirmDto>> convertConfirm(@Query("type") String type, @Query("value") int value);

    /**
     * 取消拉黑
     *
     * @param user_id
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/cancel/black")
    Flowable<BaseResponse<Boolean>> cancelBlack(@Field("user_id") int user_id);


    /**
     * 获取用户收费设置信息
     */
    @GET("info/charge")
    Flowable<BaseResponse<ChargeInfoDto>> getChargeInfo();

    /**
     * 修改收费设置
     */
    @FormUrlEncoded
    @POST("info/charge")
    Flowable<BaseResponse<Boolean>> updateChargeInfo(@Field("field") String field, @Field("value") int value);

    /**
     * 获取收费价格配置
     */
    @GET("charge")
    Flowable<BaseResponse<ChargeItemDto>> getChargeItem();

    /**
     * 获取积分商城
     */
    @GET("shopping")
    Flowable<BaseResponse<List<ShopInfoDto>>> getShop();

    /**
     * 获取积分兑换记录
     */
    @GET("convert/record")
    Flowable<BaseResponse<List<ExchangeRecord>>> getExchangeRecord(@Query("page") int page);

    /**
     * 获取音符兑换记录
     */
    @GET("convert/musical_note/record")
    Flowable<BaseResponse<List<ExchangeRecord>>> getNoteValueExchangeRecord(@Query("page") int page);

    /**
     * 积分兑换
     */
    @FormUrlEncoded
    @POST("convert")
    Flowable<BaseResponse<Boolean>> exchange(@Field("type_id") int type_id);

    /**
     * 获取消费清单
     */
    @GET("flow/list")
    Flowable<BaseResponse<List<ExchangeRecord>>> getIntegralDetailed(@Query("page") int page);

    /**
     * 获取金币清单
     *
     * @param page
     * @return
     */
    @GET("flow/coin/list")
    Flowable<BaseResponse<List<ExchangeRecord>>> getCoinDetailed(@Query("page") int page);

    /**
     * 聊天卡消耗记录
     */
    @GET("/chat_card/flow")
    Flowable<BaseResponse<List<ExchangeRecord>>> getRecordChat(@Query("page") int page);

    /**
     * 音符消耗记录
     */
    @GET("flow/musical_note/list")
    Flowable<BaseResponse<List<ExchangeRecord>>> getNoteValueDetailed(@Query("page") int page);

    /**
     * 获取自己礼物
     */
    @GET("gift/list/my")
    Flowable<BaseResponse<List<GiftDto>>> getSelfGift(@Query("limit") int pageSize);

    /**
     * 获取他人礼物
     */
    @GET("gift/list/they")
    Flowable<BaseResponse<List<GiftDto>>> getUserGift(@Query("limit") int pageSize, @Query("user_id") String user_id);

    /**
     * 获取所有礼物 自己或他人
     *
     * @param userId userId必传
     */
    @GET("gift/list/v2")
    Flowable<BaseResponse<ArrayList<GiftDto>>> getGiftList(@Query("user_id") String userId);

    /**
     * 搭讪用户
     *
     * @return
     */
    @POST("behavior/accost")
    Flowable<BaseResponse<AccostDto>> accostUser(@Body RequestBody params);

    /**
     * 获取他人动态
     */
    @GET("get_they_list_simple")
    Flowable<BaseResponse<List<TrendDto>>> getUserTrend(@Query("limit") int pageSize, @Query("user_id") String user_id);

    /**
     * 获取自己动态
     */
    @GET("get_my_list_simple")
    Flowable<BaseResponse<List<TrendDto>>> getSelfTrend(@Query("limit") int pageSize);

    /**
     * 获取自己的相册
     *
     * @return
     */
    @GET("info/picture/my")
    Flowable<BaseResponse<List<String>>> getSelfPhoto(@Query("limit") int pageSize);

    /**
     * 获取自己的相册
     *
     * @return
     */
    @GET("info/picture/edit")
    Flowable<BaseResponse<List<MinePhotoDto>>> getPhoto();

    /**
     * 获取他人的相册
     *
     * @return
     */
    @GET("info/picture/they")
    Flowable<BaseResponse<List<String>>> getUserPhoto(@Query("limit") int pageSize, @Query("user_id") String user_id);

    /**
     *
     */
    @FormUrlEncoded
    @POST("info/picture/once")
    Flowable<BaseResponse<Boolean>> uploadPic(@Field("image") String image);

    /**
     *
     */
    @FormUrlEncoded
    @POST("info/picture/delete")
    Flowable<BaseResponse<Boolean>> removePic(@Field("image") String image);

    /**
     * 更改用户信息
     */
    @POST("info/data")
    Flowable<BaseResponse<String>> updateUserInfo(@Body RequestBody params);

    /**
     * 上传个人头像
     *
     * @param icon
     * @return
     */
    @FormUrlEncoded
    @POST("info/icon")
    Flowable<BaseResponse<String>> uploadAvatar(@Field("icon") String icon);

    /**
     * 校验个人头像
     *
     * @param
     * @return
     */
    @GET("facedetect/check")
    Flowable<BaseResponse<RealCheckDto>> facedetectCheck();

    /**
     * 检查活体检测
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("liveperson/recheck")
    Flowable<BaseResponse<RealCheckDto>> recheck(@Field("token") String token);

    /**
     * 检查活体检测-派对
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("liveperson/party_recheck")
    Flowable<BaseResponse<RealCheckDto>> partyRecheck(@Field("token") String token);

    /**
     * 真人认证头像上传
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/real")
    Flowable<BaseResponse<Boolean>> uploadRealAvatar(@Field("icon") String icon, @Field("image") String image);

    /**
     * 获取真人认证的状态
     *
     * @return
     */
    @GET("check/real/status")
    Flowable<BaseResponse<String>> getRealStatus();


    /**
     * 照片上传
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/picture")
    Flowable<BaseResponse<Boolean>> uploadPhoto(@Field("images") String image);

    /**
     * 实名认证信息上传
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/self")
    Flowable<BaseResponse<RealCheckDto>> uploadRealNameInfoNew(@Field("realname") String name, @Field("identity") String phone, @Field("image_identity_up") String image_identity_up, @Field("image_identity_down") String image_identity_down);

    /**
     * 实名认证信息上传
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/self")
    Flowable<BaseResponse<Boolean>> uploadRealNameInfo(@Field("realname") String name, @Field("identity") String phone, @Field("image_identity_up") String image_identity_up, @Field("image_identity_down") String image_identity_down);

    /**
     * ocr
     *
     * @return
     */
    @FormUrlEncoded
    @POST("ocr/check")
    Flowable<BaseResponse<OcrCheckDto>> ocrCheck(@Field("image_identity_up") String image_identity_up, @Field("image_identity_down") String image_identity_down);

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
     * 获取个人土豪和魅力等级
     *
     * @return
     */
    @GET("info/level")
    Flowable<BaseResponse<GradeLevelDto>> getAccostList(@Query("type") String type);

    /**
     * 支付宝支付
     *
     * @param type_id
     * @return
     */
    @FormUrlEncoded
    @POST("alipay/create")
    Flowable<BaseResponse<String>> alipay(@Field("type_id") int type_id, @Field("from_type") String from_type);

    /**
     * 支付宝支付-首信易
     *
     * @param type_id
     * @return
     */
    @FormUrlEncoded
    @POST("shouxinyi/alipay/create")
    Flowable<BaseResponse<String>> SXYalipay(@Field("type_id") int type_id, @Field("from_type") String from_type);


    /**
     * 提现到支付宝
     *
     * @return
     */
    @POST("convert/rmb")
    Flowable<BaseResponse<String>> withdraw(@Body WithdrawReq withdraw);


    /**
     * 用户提现方式获取
     */
    @GET("info/withdraw")
    Flowable<BaseResponse<WithdrawReq.Withdraw>> withdrawWay();


    /**
     * 微信支付
     *
     * @param type_id
     * @return
     */
    @FormUrlEncoded
    @POST("wechat/create")
    Flowable<BaseResponse<WxPayResultInfo>> wechatPay(@Field("type_id") int type_id, @Field("from_type") String from_type);

    /**
     * 微信支付-首信易
     *
     * @param type_id
     * @return
     */
    @FormUrlEncoded
    @POST("/shouxinyi/wechat/create")
    Flowable<BaseResponse<SXYWxPayResultInfo>> SXYwechatPay(@Field("type_id") int type_id, @Field("from_type") String from_type);

    /**
     * 退出登录
     *
     * @return
     */
    @POST("login_out")
    Flowable<BaseResponse<Boolean>> loginOut();

    /**
     * 聊天卡 数量
     *
     * @return
     */
    @GET("chat_card/info")
    Flowable<BaseResponse<SignNumBean>> getSignChatNum();

    /**
     * 上传录音
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/voice")
    Flowable<BaseResponse<Boolean>> uploadVoice(@Field("voice") String voice, @Field("voice_time") int voice_time);

    /**
     * 基本信息动态获取
     *
     * @return
     */
    @GET("info/fields")
    Flowable<BaseResponse<IntegralDto>> getField(@Query("type") String type, @Query("fields") String field);


    /**
     * 获取是否需要更新
     *
     * @return
     */
    @GET("app_update_check")
    Flowable<BaseResponse<ConfigInfo.MineInfo>> getUpdateCheck();


    /**
     * 招呼设置-获取列表
     *
     * @return
     */
    @GET("user/quick")
    Flowable<BaseResponse<AccostSettingBean>> getAccostSettingAdapter();

    /**
     * 招呼设置-获取列表
     *
     * @return
     */
    @GET("quick/message/list")
    Flowable<BaseResponse<List<AccostSettingListBean>>> getAccostSettingList(@Query("type") String type);


    /**
     * 招呼设置-添加
     *
     * @return
     */
    @POST("quick/message/add")
    Flowable<BaseResponse<Boolean>> addAccostSetting(@Body UpdateAccostSettingBean settingBean);

    /**
     * 招呼设置-更新
     *
     * @return
     */
    @POST("quick/message/update")
    Flowable<BaseResponse<Boolean>> updateAccostSetting(@Body UpdateAccostSettingBean settingBean);

    /**
     * 招呼设置-删除
     *
     * @return
     */
    @POST("quick/message/delete")
    Flowable<BaseResponse<Boolean>> delAccostSetting(@Body DelAccostSettingBean delIds);

    /**
     * 用户事件埋点
     *
     * @param scene
     * @param event
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/aliyun/log")
    Flowable<BaseResponse<Boolean>> trackEvent(@Field("scene") String scene, @Field("event") String event, @Field("index") String index, @Field("extend") String extend);

    /**
     * 获取会员设置特权
     */
    @GET("vip/privilege")
    Flowable<BaseResponse<PrivacyDto>> getPrivilege();

    /**
     * 获取vip价格
     * @return
     */
    @GET("vip/new")
    Flowable<BaseResponse<VipConfigBean>> getVipInfo();
    /**
     * 设置会员特权
     *
     * @param type  1-土豪值、魅力值 2-礼物墙  3：上电视上头条  ，4 排行榜 7 搭讪开关 6:家族
     * @param value 1-开启 0-关闭
     * @return
     */
    @FormUrlEncoded
    @POST("vip/privilege")
    Flowable<BaseResponse<Boolean>> setPrivilege(@Field("type") int type, @Field("value") int value);

    /**
     * 获取vip价格
     *
     * @return
     */
    @GET("vip")
    Flowable<BaseResponse<List<VipPriceBean>>> getVipPrice();

    /**
     * 获取vip特权
     *
     * @return
     */
    @GET("vip_privilege")
    Flowable<BaseResponse<List<VipPrivilegeBean>>> getVipPrivilege();

    /**
     * 获取vip设置的素材
     *
     * @return
     */
    @GET("vip/config")
    Flowable<BaseResponse<VipConfigBean>> getVipConfig();

    /**
     * 设置素材
     *
     * @param type        : // 类型：1-头像框 2-聊天气泡 3-背景图片
     * @param material_id : // 素材ID
     * @return
     */
    @FormUrlEncoded
    @POST("vip/config")
    Flowable<BaseResponse<Boolean>> postVipConfig(@Field("type") int type, @Field("material_id") int material_id);


    /**
     * 获取VIP素材
     *
     * @return
     */
    @GET("vip_material_all")
    Flowable<BaseResponse<List<DressUpBean>>> getMaterialAll(@Query("type") int type);


    /**
     * 获取VIP素材
     *
     * @return
     */
    @GET("visitors/list")
    Flowable<BaseResponse<VisitorDto>> getVisitor(@Query("page") int page);

    /**
     * 支付宝实名认证
     *
     * @return
     */
    @FormUrlEncoded
    @POST("alipay/user_certify")
    Flowable<BaseResponse<UserCertifyBean>> userCertify(@Field("realname") String realname, @Field("identity") String identity, @Field("account") String account);

    /**
     * 查询是否成功
     *
     * @return
     */
    @GET("alipay/user_certify_callback")
    Flowable<BaseResponse<Boolean>> checkCertify(@Query("certify_id") String certify_id);


    /**
     * 是否有权限兑换  被封禁不能兑换
     *
     * @param type_id
     * @return
     */
    @GET("convert/check")
    Flowable<BaseResponse<CheckExchangeDto>> checkExchange(@Query("type_id") int type_id);


    /**
     * 支付宝实名认证
     *
     * @return
     */
    @FormUrlEncoded
    @POST("info/picture/sort")
    Flowable<BaseResponse<Boolean>> pictureSort(@Field("images") String images);

    /**
     * 创建直播房间
     *
     * @return
     */
    @FormUrlEncoded
    @POST("live/blind_date/create")
    Flowable<BaseResponse<LiveTokenDto>> createLiveRoom(@Field("room_name") String room_name);

    /**
     * 账号状态
     *
     * @return
     */
    @GET("account/status")
    Flowable<BaseResponse<LogoutStatusDto>> getLogoutStatus();

    /**
     * 账号注销
     *
     * @return
     */
    @GET("behavior/destroy/account")
    Flowable<BaseResponse<LogoutStatusDto>> destroyAccount();

    /**
     * 账号取消注销
     *
     * @return
     */
    @GET("behavior/destroy/account/rollback")
    Flowable<BaseResponse<String>> unDestroyAccount();

    /**
     * 获取贵族列表
     *
     * @return
     */
    @GET("nobility/home")
    Flowable<BaseResponse<NobleBean>> getNobilityList();

    /**
     * 获取装扮配置项
     *
     * @return
     */
    @GET("dress/category/list")
    Flowable<BaseResponse<List<DressListDto>>> getCategoryList();

    /**
     * 我的->填写邀请码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("h5/invite/bind")
    Flowable<BaseResponse> setInviteCode(@Field("invite_code") String inviteCode);
}
