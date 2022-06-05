package com.tftechsz.im.api;


import com.netease.nim.uikit.OnLineListBean;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.im.model.BgSetBean;
import com.tftechsz.im.model.CallStatusInfo;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.model.CoupleRelieveInfo;
import com.tftechsz.im.model.FateInfo;
import com.tftechsz.im.model.dto.AirdropBagDto;
import com.tftechsz.im.model.dto.AirdropDetailDto;
import com.tftechsz.im.model.dto.AirdropDto;
import com.tftechsz.im.model.dto.AppInitDto;
import com.tftechsz.im.model.dto.ButtonConfigDto;
import com.tftechsz.im.model.dto.CallLogDto;
import com.tftechsz.im.model.dto.CoupleBagDetailDto;
import com.tftechsz.im.model.dto.CoupleBagDto;
import com.tftechsz.im.model.dto.CoupleLetterDto;
import com.tftechsz.im.model.dto.GroupCoupleDto;
import com.tftechsz.im.model.dto.JoinLeaveRoom;
import com.tftechsz.im.model.dto.LiveHomeDto;
import com.tftechsz.im.model.dto.MessageIntimacyDto;
import com.tftechsz.im.model.dto.PullWiresDto;
import com.tftechsz.im.model.dto.RoomApplyDto;
import com.tftechsz.im.model.dto.VoiceChatDto;
import com.tftechsz.im.model.dto.VoiceRoleCheckDto;
import com.tftechsz.im.model.dto.VoiceRoleCheckNewDto;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.IntimacyDto;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.entity.RechargeQuickDto;
import com.tftechsz.common.http.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApiService {

    /**
     * 获取IM用户的信息
     *
     * @return
     */
    @GET("info/ims")
    Flowable<BaseResponse<List<UserInfo>>> getImUserInfo(@Query("user_ids") String user_ids);

    /**
     * 关注用户
     */
    @FormUrlEncoded
    @POST("behavior/follow")
    Flowable<BaseResponse<Boolean>> attentionUser(@Field("user_id") int id);

    /**
     * 用户是否关注过
     */
    @GET("behavior/follow")
    Flowable<BaseResponse<Boolean>> getIsAttention(@Query("user_id") int id);

    /**
     * 拉黑用户
     */
    @FormUrlEncoded
    @POST("behavior/black")
    Flowable<BaseResponse<Boolean>> black(@Field("user_id") int id);

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
     * 获取亲密度信息
     *
     * @param id
     * @return
     */
    @GET("info/intimacy")
    Flowable<BaseResponse<IntimacyDto>> getIntimacy(@Query("user_id") String id);

    /**
     * 获取是否有权限去通话
     *
     * @param call_id
     * @return
     */
    @GET("call_check_coin")
    Flowable<BaseResponse<CallStatusInfo>> checkCallStatus(@Query("call_id") String call_id);

    /**
     * 点击接通请求接口
     *
     * @param call_id
     * @return
     */
    @FormUrlEncoded
    @POST("behavior/before_call_accept_check")
    Flowable<BaseResponse<CallStatusInfo>> checkAcceptCheck(@Field("call_id") String call_id);


    /**
     * 获取家族ID
     *
     * @return
     */
    @GET("get_id_by_tid")
    Flowable<BaseResponse<FamilyIdDto>> getFamilyId(@Query("tid") String tid);

    /**
     * 进入聊天页面
     *
     * @param to_user_id
     * @return
     */
    @FormUrlEncoded
    @POST("join_leave_room/p2p")
    Flowable<BaseResponse<JoinLeaveRoom>> joinP2PRoom(@Field("to_user_id") String to_user_id);

    /**
     * 进入聊天页面
     *
     * @param to_user_id
     * @return
     */
    @FormUrlEncoded
    @POST("join_leave_room/family")
    Flowable<BaseResponse<FamilyIdDto>> joinFamilyRoom(@Field("to_user_id") String to_user_id, @Field("fromTpe") String fromTpe);

    /**
     * 获取聊天中的历史消息记录
     *
     * @return
     */
    @GET("chat_history")
    Flowable<BaseResponse<ChatHistoryDto>> getChatHistory();

    /**
     * app进入获取密友信息
     *
     * @return
     */
    @GET("app_init")
    Flowable<BaseResponse<AppInitDto>> appInit();

    /**
     * 更新密友列表信息
     *
     * @return
     */
    @GET("intimacy/list")
    Flowable<BaseResponse<List<MessageIntimacyDto>>> updateIntimacy(@Query("ids") String ids);

    /**
     * 获取快捷支付金额
     *
     * @return
     */
    @GET("payment/quick")
    Flowable<BaseResponse<RechargeQuickDto>> quickRecharge();


    /**
     * 语音匹配
     *
     * @param from_user_id 来源用户ID
     * @param to_user_id   发送用户ID
     * @param action       yes : 同意，no :拒绝, 默认：no
     * @return
     */
    @FormUrlEncoded
    @POST("matching/video/action")
    Flowable<BaseResponse<Boolean>> videoAction(@Field("from_user_id") String from_user_id, @Field("to_user_id") String to_user_id, @Field("action") String action);

    /**
     * 语音匹配
     *
     * @param from_user_id 来源用户ID
     * @param to_user_id   发送用户ID
     * @param action       yes : 同意，no :拒绝, 默认：no
     * @return
     */
    @FormUrlEncoded
    @POST("matching/voice/action")
    Flowable<BaseResponse<Boolean>> voiceAction(@Field("from_user_id") String from_user_id, @Field("to_user_id") String to_user_id, @Field("action") String action);


    /**
     * 获取系统牵线列表
     *
     * @param from_type 1 2 3 4  现在是传4后续其他场景
     * @return
     */
    @GET("msg/list")
    Flowable<BaseResponse<List<PullWiresDto>>> getMsgList(@Query("from_type") int from_type, @Query("page") int page);


    /**
     * 缘分牵线信息内容
     *
     * @param from_type  来源类型 为4
     * @param to_user_id 发送用户ID
     * @return
     */
    @GET("msg/detail")
    Flowable<BaseResponse<List<FateInfo>>> getDetailFateMsg(@Query("from_type") String from_type, @Query("to_user_id") long to_user_id);


    @GET("call/list")
    Flowable<BaseResponse<List<CallLogDto>>> getCallList(@Query("page") int page);

    /**
     * 获取禁用提示
     *
     * @return
     */
    @GET("tips")
    Flowable<BaseResponse> getTips();


    /**
     * 获取空投信息
     *
     * @return
     */
    @GET("gift/bag")
    Flowable<BaseResponse<List<AirdropDto>>> getAirdrop();

    /**
     * 发送空投
     *
     * @param rule      0-不限 1-男士 2-女士
     * @param time_type 0-立即可抢 5-5分钟后可抢
     * @return
     */
    @FormUrlEncoded
    @POST("gift/bag/send")
    Flowable<BaseResponse<Boolean>> sendAirdrop(@Field("gift_bag_id") int gift_bag_id, @Field("rule") int rule, @Field("time_type") int time_type);

    /**
     * 打开空投
     *
     * @return
     */
    @FormUrlEncoded
    @POST("gift/bag/open")
    Flowable<BaseResponse<Boolean>> openAirdrop(@Field("gift_bag_id") int gift_bag_id);


    /**
     * 获取im空投信息
     *
     * @return
     */
    @GET("gift/bag/lately")
    Flowable<BaseResponse<List<AirdropBagDto>>> getAirdropBag(@Query("gift_bag_id") int gift_bag_id);


    /**
     * 获取密友接口
     *
     * @return
     */
    @GET("intimacy/list_new")
    Flowable<BaseResponse<List<ContactInfo>>> getIntimacyList(@Query("page") int page);


    /**
     * 空投详情
     *
     * @return
     */
    @GET("gift/bag/receive")
    Flowable<BaseResponse<AirdropDetailDto>> getAirdropDetail(@Query("gift_bag_id") int gift_bag_id);

    /**
     * 进行游戏
     *
     * @param type 1 = 骰子 2 = 剪刀石头布
     */
    @FormUrlEncoded
    @POST("dice_game")
    Flowable<BaseResponse<Boolean>> doGame(@Field("type") int type);

    /**
     * 红包雨点击
     */
    @FormUrlEncoded
    @POST("red_packet_rain/click")
    Flowable<BaseResponse<Boolean>> clickRain(@Field("red_packet_rain_id") int red_packet_rain_id, @Field("click_id") int click_id);

    /**
     * 红包雨结束
     */
    @FormUrlEncoded
    @POST("red_packet_rain/end")
    Flowable<BaseResponse<Boolean>> rainEnd(@Field("red_packet_rain_id") int red_packet_rain_id);

    /**
     * 获取直播房间
     *
     * @return
     */
    @GET("live/blind_date/list")
    Flowable<BaseResponse<LiveHomeDto>> getLiveHome();

    /**
     * 获取直播房间
     *
     * @return
     */
    @GET("live/blind_date/get_token")
    Flowable<BaseResponse<LiveTokenDto>> getLiveHomeToken(@Query("channel_name") String channel_name);

    /**
     * 查看对方组情侣详情
     *
     * @param
     * @return
     */
    @GET("couple/info")
    Flowable<BaseResponse<GroupCoupleDto>> getCoupleInfo(@Query("user_id") String user_id);

    /**
     * 查看是否组了情侣
     *
     * @param
     * @return
     */
    @GET("couple/check")
    Flowable<BaseResponse<GroupCoupleDto>> getCoupleCheck();

    /**
     * 发送表白信
     */
    @FormUrlEncoded
    @POST("couple_letter/send")
    Flowable<BaseResponse<Boolean>> sendCoupleLetter(@Field("user_id") int user_id);

    /**
     * 解除情侣
     */
    @FormUrlEncoded
    @POST("couple/relieve")
    Flowable<BaseResponse<Boolean>> relieveCouple(@Field("user_id") int user_id);


    /**
     * 表白信列表
     */
    @GET("couple_letter/list")
    Flowable<BaseResponse<List<CoupleLetterDto>>> getCoupleLetter();

    /**
     * 接受拒绝表白信
     *
     * @return letter_id, type 1-接受 2-拒绝
     */
    @FormUrlEncoded
    @POST("couple_letter/confirm")
    Flowable<BaseResponse<Boolean>> operateLetter(@Field("letter_id") int letter_id, @Field("type") int type);

    /**
     * 获取聊天背景
     */
    @FormUrlEncoded
    @POST("chat/bg/config")
    Flowable<BaseResponse<List<BgSetBean>>> getBgConfig(@Field("to_user_id") String to_user_id);

    /**
     * 获取聊天背景
     */
    @FormUrlEncoded
    @POST("chat/bg/set")
    Flowable<BaseResponse<Boolean>> setChatBg(@Field("to_user_id") String to_user_id, @Field("id") int id, @Field("used") int used);

    /**
     * 创建房间
     */
    @POST("room/create")
    Flowable<BaseResponse<Boolean>> createRoom();

    /**
     * 退出语音聊天房间
     */
    @POST("room/delete")
    Flowable<BaseResponse<Boolean>> deleteRoom();

    /**
     * 获取招募红包按钮
     *
     * @return
     */
    @GET("button/config")
    Flowable<BaseResponse<ButtonConfigDto>> getRecruitRedPacket();

    /**
     * 获取语音房信息
     *
     * @return
     */
    @GET("room/info")
    Flowable<BaseResponse<VoiceChatDto>> getRoomInfo();

    /**
     * 加入房间
     */
    @FormUrlEncoded
    @POST("room/join_check")
    Flowable<BaseResponse<Boolean>> joinRoom(@Field("index") int index);

    /**
     * 获取申请列表
     *
     * @return
     */
    @GET("room/apply_list")
    Flowable<BaseResponse<List<RoomApplyDto>>> getApplyList();

    /**
     * 申请操作
     *
     * @param type 1-通过 2-拒绝 3-个人取消
     * @return
     */
    @FormUrlEncoded
    @POST("room/apply")
    Flowable<BaseResponse<Boolean>> apply(@Field("apply_id") int apply_id, @Field("user_id") int user_id, @Field("type") int type);

    /**
     * 下麦
     *
     * @return
     */
    @POST("room/leave")
    Flowable<BaseResponse<Boolean>> down();


    /**
     * 声音操作
     *
     * @param type    操作对象 1-自己 2-他人
     * @param user_id 操作对象ID,自己可不传或0
     * @param status  2 5 6 7
     */
    @FormUrlEncoded
    @POST("room/microphone")
    Flowable<BaseResponse<Boolean>> microphone(@Field("type") int type, @Field("user_id") int user_id, @Field("status") int status);


    /**
     * 踢掉用户
     */
    @FormUrlEncoded
    @POST("room/kickout")
    Flowable<BaseResponse<Boolean>> kickOut(@Field("user_id") int user_id);

    /**
     * 编辑姓名和公告
     */
    @FormUrlEncoded
    @POST("room/edit")
    Flowable<BaseResponse<Boolean>> editFile(@Field("field") String field, @Field("value") String value);


    /**
     * 查看权限
     *
     * @param user_id 传0后is_operate默认为false
     */
    @GET("room/role_check")
    Flowable<BaseResponse<VoiceRoleCheckDto>> roleCheck(@Query("user_id") int user_id);


    /**
     * 编辑
     */
    @GET("room/audit")
    Flowable<BaseResponse<VoiceRoleCheckNewDto>> editComCheck(@Query("type") int type);

    /**
     * 派对在线列表
     */
    @GET("userlist")
    Flowable<BaseResponse<OnLineListBean>> userlist(@Query("party_room_id") String party_room_id, @Query("page") int page);

    /**
     * 领取任务
     */
    @FormUrlEncoded
    @POST("couple/task/receive")
    Flowable<BaseResponse<Boolean>> getTask(@Field("family_id") String family_id, @Field("task_id") String task_id);


    /**
     * 获取礼包数据
     */
    @POST("couple/gift/bag")
    Flowable<BaseResponse<List<CoupleBagDto>>> getGiftBag();

    /**
     * 获取礼包数据
     */
    @FormUrlEncoded
    @POST("couple/gift/bag/detail")
    Flowable<BaseResponse<CoupleBagDetailDto>> getGiftBagDetail(@Field("family_id") int family_id, @Field("gift_bag_id") int gift_bag_id);

    /**
     * 购买情侣礼包礼物
     */
    @FormUrlEncoded
    @POST("couple/gift/bag/buy")
    Flowable<BaseResponse<Boolean>> buyGiftBag(@Field("family_id") int family_id, @Field("gift_bag_id") int gift_bag_id);


    /**
     * 获取解除情侣信息
     */
    @GET("couple_relieve/info")
    Flowable<BaseResponse<CoupleRelieveInfo>> coupleRelieveInfo();

    /**
     * 解除情侣
     * "type": 1   // 1-申请解除 2-强制解除
     */
    @FormUrlEncoded
    @POST("couple_relieve/apply")
    Flowable<BaseResponse<Boolean>> coupleRelieveApply(@Field("user_id") String user_id, @Field("type") int type);


    /**
     * 解除情侣-（同意、拒绝）-弹窗
     * "type": 3   // 1-接受解除申请 2-拒绝解除申请 9-取消申请
     */
    @FormUrlEncoded
    @POST("couple_relieve/handle")
    Flowable<BaseResponse<Boolean>> relieveCoupleHandler(@Field("apply_id") int apply_id, @Field("type") int type);


}
