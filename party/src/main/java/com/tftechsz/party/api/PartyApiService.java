package com.tftechsz.party.api;


import com.tftechsz.common.entity.AudioBean;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.PartyPermission;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.party.entity.AudioIds;
import com.tftechsz.party.entity.CoinBean;
import com.tftechsz.party.entity.CostBean;
import com.tftechsz.party.entity.MusicCountBean;
import com.tftechsz.party.entity.MusicListDto;
import com.tftechsz.party.entity.PartyEditBean;
import com.tftechsz.party.entity.PartyLuckWheelBean;
import com.tftechsz.party.entity.PartyManagerBgBean;
import com.tftechsz.party.entity.PartyManagerListBean;
import com.tftechsz.party.entity.PartyRankBean;
import com.tftechsz.party.entity.PartySetListConfigBean;
import com.tftechsz.party.entity.PartyTurntableBean;
import com.tftechsz.party.entity.QueuePartyListBean;
import com.tftechsz.party.entity.SpecialEffectsBean;
import com.tftechsz.party.entity.TurntableCheckBean;
import com.tftechsz.party.entity.TurntableStartBeforeBean;
import com.tftechsz.party.entity.dto.JoinPartyDto;
import com.tftechsz.party.entity.dto.MusicActionDto;
import com.tftechsz.party.entity.dto.PartyDto;
import com.tftechsz.party.entity.dto.PartyPkSaveDto;
import com.tftechsz.party.entity.dto.PkTimeDto;
import com.tftechsz.party.entity.req.SavePkReq;
import com.tftechsz.party.entity.req.StartPkReq;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PartyApiService {
    /**
     * 幸运转盘-获取配置信息
     */
    @GET("/turntable/config")
    Flowable<BaseResponse<PartyLuckWheelBean>> getLuckConfig();

    /**
     * 幸运转盘-开始
     */
    @FormUrlEncoded
    @POST("/turntable/start")
    Flowable<BaseResponse<Boolean>> startTurntable(@Field("party_room_id") String party_room_id, @Field("type") int type, @Field("number") String number);

    /**
     * 获取party首页数据
     */
    @GET("list")
    Flowable<BaseResponse<PartyDto>> getPartyList(@Query("page") int page);

    /**
     * 进入party
     *
     * @param type in/out 进/出
     * @return
     */
    @FormUrlEncoded
    @POST("join_leave_room")
    Flowable<BaseResponse<JoinPartyDto>> joinParty(@Field("roomid") int id, @Field("type") String type);

    /**
     * 加入离开party
     *
     * @param from_type open / close
     * @return
     */
    @FormUrlEncoded
    @POST("join_leave_room/party")
    Flowable<BaseResponse<PartyDto>> joinLeaveParty(@Field("from_type") String from_type, @Field("party_room_id") int id, @Field("is_window") int isWindow);

    /**
     * 获取派对音乐列表
     */
    @GET("music/list")
    Flowable<BaseResponse<MusicListDto>> getMusicList();

    /**
     * 更新派对音乐列表  排序
     */
    @POST("music/weight")
    Flowable<BaseResponse<MusicActionDto>> sortMusicList(@Body AudioIds audioIds);

    /**
     * 更新派对音乐列表  删除
     */
    @POST("music/delete")
    Flowable<BaseResponse<MusicActionDto>> deleteMusicList(@Body AudioIds audioIds);

    /**
     * 获取用户可上传条目 以及 最大上传音乐条数 已上传x条
     */
    @GET("music/count")
    Flowable<BaseResponse<MusicCountBean>> getUploadMusicCount();

    /**
     * 上传音乐
     */
    @POST("music/save")
    Flowable<BaseResponse<MusicActionDto>> musicSave(@Body List<AudioBean> pram);

    /**
     * 个人转盘记录
     * "page": 1,   // 默认值
     * "size": 20   // 默认值
     */
    @GET("turntable/flow")
    Flowable<BaseResponse<List<PartyTurntableBean>>> partyFlow(@Query("page") String page, @Query("size") String size);

    /**
     * 转盘排行榜
     */
    @GET("turntable/rank")
    Flowable<BaseResponse<PartyRankBean>> partyRank();

    /**
     * 获取派对信息
     */
    @FormUrlEncoded
    @POST("room/layer/info")
    Flowable<BaseResponse<JoinPartyDto>> getPartyInfo(@Field("room_id") int id);


    /**
     * 申请上麦
     */
    @FormUrlEncoded
    @POST("microphone/apply")
    Flowable<BaseResponse<Boolean>> applySeat(@Field("room_id") int id, @Field("index") int index, @Field("pk_info_id") int pk_info_id);

    /**
     * 转盘开关
     */
    @GET("turntable/check")
    Flowable<BaseResponse<TurntableCheckBean>> wheelSwitch(@Query("party_room_id") int party_room_id);

    /**
     * 特效开关
     */
    @POST("room/dress/switch")
    Flowable<BaseResponse<Integer>> specialEffectsSwitch(@Body SpecialEffectsBean bean);

    /**
     * 下麦
     *
     * @param status 1: 下麦 2：被踢
     */
    @FormUrlEncoded
    @POST("microphone/leave")
    Flowable<BaseResponse<Boolean>> leaveSeat(@Field("room_id") int id, @Field("index") int index, @Field("status") int status);


    /**
     * 派对管理列表
     */
    @GET("room_manage/list")
    Flowable<BaseResponse<PartyManagerListBean>> partyManager(@Query("party_room_id") String party_room_id);

    /**
     * 派对管理拉黑踢出禁言
     */
    @FormUrlEncoded
    @POST("room_manage/cancel")
    Flowable<BaseResponse<String>> setAct(@Field("party_room_id") String party_room_id, @Field("action_type") int action_type, @Field("user_id") String id);


    /**
     * 获取pk配置
     */
    @GET("pk/config")
    Flowable<BaseResponse<PkTimeDto>> getPkTime();

    /**
     * 开始PK
     */
    @POST("pk/start")
    Flowable<BaseResponse<Boolean>> startPk(@Body StartPkReq startPkReq);


    /**
     * 结束PK
     */
    @POST("pk/stop")
    Flowable<BaseResponse<Boolean>> stopPk(@Body StartPkReq startPkReq);

    /**
     * 开始惩罚
     */
    @POST("pk/start_punish")
    Flowable<BaseResponse<Boolean>> startPunish(@Body StartPkReq startPkReq);

    /**
     * 结束惩罚
     */
    @POST("pk/end_punish")
    Flowable<BaseResponse<Boolean>> stopPunish(@Body StartPkReq startPkReq);

    /**
     * 关闭PK模式
     */
    @POST("pk/end")
    Flowable<BaseResponse<Boolean>> endPK(@Body StartPkReq startPkReq);

    /**
     * 保存数据
     */
    @POST("pk/save")
    Flowable<BaseResponse<PartyPkSaveDto>> startSave(@Body SavePkReq startPkReq);


    /**
     * 再次PK
     */
    @POST("pk/pk_again")
    Flowable<BaseResponse<PartyPkSaveDto>> startPkAgain(@Body SavePkReq startPkReq);


    /**
     * 付费金币 或者 免费金币
     */
    @GET("my/property")
    Flowable<BaseResponse<CoinBean>> getCoinConfig(@Query("type") String type);

    /**
     * 派对装扮查询
     */
    @GET("get_edit")
    Flowable<BaseResponse<PartyEditBean>> getPartyEdit(@Query("party_room_id") int party_room_id);

    /**
     * 派对装扮提交
     */
    @FormUrlEncoded
    @POST("edit")
    Flowable<BaseResponse<String>> editParty(/*@Field("icon_value") String icon_value, @Field("room_name") String room_name, */@Field("fight_pattern") int fight_pattern,
                                                                                                                               @Field("is_mute") int is_mute, @Field("microphone_pattern") int microphone_pattern, /*@Field("announcement") String announcement
            , @Field("title") String title,*/ @Field("bg_icon_value") String bg_icon_value, @Field("party_room_id") int party_room_id);

    /**
     * 派对关闭
     */
    @FormUrlEncoded
    @POST("close")
    Flowable<BaseResponse<Boolean>> closeParty(@Field("party_room_id") int party_room_id);

    /**
     * 麦序-管理员同意用户上麦
     * "room_id": "1",         // 房间ID
     * "id": "1",              // 申请ID
     * "to": "1",              // 申请用户
     */
    @FormUrlEncoded
    @POST("microphone/apply/agree")
    Flowable<BaseResponse<Boolean>> agreeUserSequence(@Field("room_id") int room_id, @Field("id") int id, @Field("to") int to);

    /**
     * 派对管理背景图
     */
    @GET("room_manage/bg_icons")
    Flowable<BaseResponse<List<PartyManagerBgBean>>> getPartyEditBg();

    /**
     * 派对功能列表
     */
    @FormUrlEncoded
    @POST("room/nodes")
    Flowable<BaseResponse<List<PartyPermission>>> partyListFunc(@Field("room_id") int room_id);

    /**
     * 派对管理拉黑踢出禁言
     * party_room_id	int	是	房间ID
     * user_id	int	是	会员ID
     * action_type	int	是	选项类型： 0房管，1 拉黑，2踢出，3禁言
     * opt_type	int	否	选项类型type值，见接口派对管理踢出和禁言选项
     */
    @FormUrlEncoded
    @POST("room_manage/set_act")
    Flowable<BaseResponse<String>> roomManagerSetAct(@Field("party_room_id") String party_room_id, @Field("user_id") String user_id, @Field("action_type") int action_type, @Field("opt_value") int opt_value);

    /**
     * 派对管理踢出和禁言选项
     */
    @GET("room_manage/option_map")
    Flowable<BaseResponse<PartySetListConfigBean>> getSetOptionList();

    /**
     * 抱上麦序
     */
    @FormUrlEncoded
    @POST("microphone/invite")
    Flowable<BaseResponse<String>> inviteMac(@Field("room_id") int room_id, @Field("to") String to);

    /**
     * 抱下麦序
     */
    @FormUrlEncoded
    @POST("microphone/kick")
    Flowable<BaseResponse<String>> inviteMacKick(@Field("room_id") int room_id, @Field("user_id") String user_id);


    /**
     * 爱意值开关
     *
     * @param status // 1/0 开/关
     */
    @FormUrlEncoded
    @POST("room/love/switch")
    Flowable<BaseResponse<Boolean>> loveSwitch(@Field("room_id") int room_id, @Field("status") int status);

    /**
     * 同意上麦
     *
     * @return
     */
    @FormUrlEncoded
    @POST("microphone/invite/agree")
    Flowable<BaseResponse<String>> agreeSeat(@Field("room_id") int room_id, @Field("user_id") int user_id, @Field("inviter") int inviter);


    /**
     * 麦位排队列表
     */
    @FormUrlEncoded
    @POST("microphone/queue/users")
    Flowable<BaseResponse<QueuePartyListBean>> queueList(@Field("room_id") int room_id, @Field("page") int page);

    /**
     * 清空麦序位置
     */
    @FormUrlEncoded
    @POST("microphone/queue/empty")
    Flowable<BaseResponse<Boolean>> clearQueue(@Field("room_id") int room_id, @Field("user_id") int user_id);

    /**
     * 麦位排队用户清空-本人
     */
    @FormUrlEncoded
    @POST("microphone/own/empty")
    Flowable<BaseResponse<Boolean>> emptyMicrophone(@Field("room_id") int room_id);


    /**
     * 锁定麦位
     * lock：1锁定 0：解锁
     */
    @FormUrlEncoded
    @POST("microphone/lock")
    Flowable<BaseResponse<Boolean>> lockMicrophone(@Field("room_id") int room_id, @Field("index") int index, @Field("lock") int lock);

    /**
     * 禁言麦位
     * status： 0/1 禁言/取消
     */
    @FormUrlEncoded
    @POST("microphone/user/banned")
    Flowable<BaseResponse<Boolean>> muteMicrophone(@Field("room_id") int room_id, @Field("to") int to, @Field("index") int index, @Field("status") int status);


    /**
     * 用户拒绝上麦
     */
    @FormUrlEncoded
    @POST("microphone/invite/refuse")
    Flowable<BaseResponse<Boolean>> refuseOnSeat(@Field("room_id") int room_id, @Field("inviter") int inviter);

    /**
     * 更新语音房 名称和公告
     */
    @FormUrlEncoded
    @POST("edit_audit_field")
    Flowable<BaseResponse<String>> editAuditField(@Field("value") String value, @Field("party_room_id") int party_room_id);

    /**
     * 派对审核信息检查
     * party_room_id	int	是	派对房间ID
     * field	string	是	审核的字段名称，公告就传递announcement
     */
    @GET("check_auditing")
    Flowable<BaseResponse<Boolean>> checkAuditing(@Query("party_room_id") int party_room_id, @Query("field") String field);


    /**
     * 转盘-点击开始前
     */
    @GET("turntable/start_before")
    Flowable<BaseResponse<TurntableStartBeforeBean>> startBefore(@Query("type") int type, @Query("number") String number);

    /**
     * 购买幸运石
     */
    @FormUrlEncoded
    @POST("turntable/buy")
    Flowable<BaseResponse<Boolean>> buyTurntable(@Field("type") int type, @Field("party_room_id") String party_room_id, @Field("number") int number);


    /**
     * 购买幸运石-点击确认操作
     * isauto:0-不勾选 1-勾选
     */
    @FormUrlEncoded
    @POST("turntable/set_auto")
    Flowable<BaseResponse<Boolean>> setAuto(@Field("type") int type, @Field("is_auto") int is_auto);

    /**
     * 查询幸运石
     */
    @GET("turntable/cost")
    Flowable<BaseResponse<CostBean>> selectCostLuckyStone(@Query("type") int type);

    /**
     * 获取派对大表情
     * @return
     */
    @GET("room/picture_list")
    Flowable<BaseResponse<List<GiftDto>>> getPictureList();

    /**
     * 选择图片
     * @return
     */
    @FormUrlEncoded
    @POST("room/choose_picture")
    Flowable<BaseResponse<Boolean>> choosePicture(@Field("id") int id, @Field("party_room_id") int party_room_id);

}
