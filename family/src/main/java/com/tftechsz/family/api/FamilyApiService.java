package com.tftechsz.family.api;


import com.tftechsz.family.entity.dto.ChangeMasterDto;
import com.tftechsz.family.entity.dto.FamilyApplyDto;
import com.tftechsz.family.entity.dto.FamilyBlackListDto;
import com.tftechsz.family.entity.dto.FamilyConfigDto;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.entity.dto.FamilyMemberGroupDto;
import com.tftechsz.family.entity.dto.FamilyRankDto;
import com.tftechsz.family.entity.dto.FamilyRecruitDto;
import com.tftechsz.family.entity.req.JoinRuleReq;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.entity.FamilyInviteBean;
import com.tftechsz.common.entity.FamilyRoleDto;
import com.tftechsz.common.entity.MasterChangeStatus;
import com.tftechsz.common.event.RecruitBaseDto;
import com.tftechsz.common.http.BaseResponse;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FamilyApiService {


    /**
     * 创建家族
     */
    @FormUrlEncoded
    @POST("create")
    Flowable<BaseResponse<FamilyInfoDto>> createFamily(@Field("tname") String name, @Field("icon") String icon);


    /**
     * 申请加入家族
     */
    @FormUrlEncoded
    @POST("apply/join")
    Flowable<BaseResponse<String>> apply(@Field("family_id") int family_id, @Field("invite_user_id") String invite_user_id);


    /**
     * 离开家族
     */
    @FormUrlEncoded
    @POST("apply/leave")
    Flowable<BaseResponse<Boolean>> leave(@Field("message") String message);

    /**
     * 家族详情
     *
     * @return
     */
    @GET("home")
    Flowable<BaseResponse<FamilyInfoDto>> familyDetail(@Query("family_id") int family_id);

    /**
     * 我的家族
     */
    @GET("family_list_base")
    Flowable<BaseResponse<FamilyInfoDto>> mineFamily();


    /**
     * 获取成员列表
     *
     * @return
     */
    @GET("member_all_list")
    Flowable<BaseResponse<List<FamilyMemberDto>>> getFamilyMember();

    /**
     * 获取成员列表
     *
     * @param page
     * @param family_id
     * @return
     */
    @GET("member_list")
    Flowable<BaseResponse<List<FamilyMemberGroupDto>>> getFamilyMember(@Query("page") int page, @Query("family_id") int family_id, @Query("order_by") int orderBy);

    /**
     * 获取可以转让的成员列表
     *
     * @return
     */
    @GET("change_master")
    Flowable<BaseResponse<List<FamilyMemberGroupDto>>> getChangeMaster();

    /**
     * 获取聊天广场成员列表
     *
     * @param page
     * @return
     */
    @GET("member_list")
    Flowable<BaseResponse<List<FamilyMemberDto>>> getRoomMember(@Query("page") int page, @Query("rid") String rid);

    /**
     * 获取排行列表
     *
     * @param cate
     * @return
     */
    @GET("list")
    Flowable<BaseResponse<FamilyRankDto>> rankList(@Query("page") int page, @Query("cate") String cate);

    /**
     * 获取申请列表
     *
     * @param type // 对应的申请操作 join = 加入，leave = 离开
     * @return
     */
    @GET("apply/list")
    Flowable<BaseResponse<List<FamilyApplyDto>>> applyList(@Query("page") int page, @Query("family_id") int family_id, @Query("type") String type);


    /**
     * 同意加入家族
     *
     * @return
     */
    @FormUrlEncoded
    @POST("join")
    Flowable<BaseResponse<Boolean>> acceptJoin(@Field("apply_id") int apply_id);

    /**
     * 同意离开家族
     *
     * @return
     */
    @FormUrlEncoded
    @POST("leave")
    Flowable<BaseResponse<Boolean>> acceptLeave(@Field("apply_id") int apply_id);


    /**
     * 忽略申请和离开操作
     *
     * @param type // 对应的申请操作 join = 加入，leave = 离开
     * @return
     */
    @FormUrlEncoded
    @POST("apply/ignore")
    Flowable<BaseResponse<Boolean>> ignoreApply(@Field("family_id") int family_id, @Field("type") String type);


    /**
     * 获取可以设置的用户角色
     *
     * @return
     */
    @GET("config/role")
    Flowable<BaseResponse<List<FamilyRoleDto>>> getRole(@Query("user_id") int user_id);

    /**
     * 转让族长
     *
     * @return
     */
    @FormUrlEncoded
    @POST("change_master")
    Flowable<BaseResponse<ChangeMasterDto>> changeMaster(@Field("user_id") int user_id);

    /**
     * 转让族长通知
     *
     * @return
     */
    @FormUrlEncoded
    @POST("change_master_notice")
    Flowable<BaseResponse<Boolean>> changeMasterNotice(@Field("user_id") int user_id);

    /**
     * 设置用户的角色
     */
    @FormUrlEncoded
    @POST("user/role")
    Flowable<BaseResponse<Boolean>> setUserRole(@Field("user_id") int user_id, @Field("role_id") int role_id);

    /**
     * 踢出用户
     */
    @FormUrlEncoded
    @POST("user/out")
    Flowable<BaseResponse<Boolean>> removeUser(@Field("user_id") int user_id);


    /**
     * 编辑家族信息-名称|宣言|图章
     *
     * @param field tname=家族名称 ，intro=家族宣言，icon=家族徽章
     * @return
     */
    @FormUrlEncoded
    @POST("edit")
    Flowable<BaseResponse<Boolean>> editFamilyInfo(@Field("field") String field, @Field("value") String value);

    /**
     * 编辑加入条件
     *
     * @return
     */
    @POST("join/rule")
    Flowable<BaseResponse<Boolean>> joinRule(@Body JoinRuleReq body);

    /**
     * 获取加入条件
     *
     * @return
     */
    @GET("join/rule")
    Flowable<BaseResponse<JoinRuleReq>> getJoinRule();

    /**
     * 获取家族配置
     *
     * @return
     */
    @GET("family")
    Flowable<BaseResponse<FamilyConfigDto>> getFamilyConfig();

    /**
     * 获取家族ID
     *
     * @return
     */
    @GET("get_id_by_tid")
    Flowable<BaseResponse<FamilyIdDto>> getFamilyId(@Query("tid") String tid);

    /**
     * 获取当前角色
     *
     * @return
     */
    @GET("my_role")
    Flowable<BaseResponse<FamilyIdDto>> getFamilyRole();

    /**
     * 解散家族
     *
     * @return
     */
    @FormUrlEncoded
    @POST("remove")
    Flowable<BaseResponse<Boolean>> dissolutionFamily(@Field("message") String message);

    /**
     * 查询是否存在family
     */
    @GET("family_is_exist")
    Flowable<BaseResponse<FamilyIdDto>> searchFamily(@Query("family_id") String family_id, @Query("tid") String tid);

    /**
     * 是否可以创建家族
     *
     * @return
     */
    @GET("condition")
    Flowable<BaseResponse<FamilyIdDto>> getCondition();

    /**
     * 获取家族邀请
     *
     * @return
     */
    @GET("square_recommend")
    Flowable<BaseResponse<FamilyInviteBean>> getFamilyInvite();

    /**
     * 获取不活跃用户
     *
     * @return
     */
    @GET("clean_not_active_user_map")
    Flowable<BaseResponse<List<FamilyInactiveDto>>> getInactiveUser();

    /**
     * 清理不活跃用户
     *
     * @return
     */
    @FormUrlEncoded
    @POST("clean_not_active_user")
    Flowable<BaseResponse<Boolean>> clearInactiveUser(@Field("type") int type);

    /**
     * 紧言全部
     *
     * @param family_id
     * @param operation
     * @return
     */
    @FormUrlEncoded
    @POST("mute_all")
    Flowable<BaseResponse<Boolean>> muteAll(@Field("family_id") String family_id, @Field("operation") int operation);

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
     * 获取家族黑名单列表
     *
     * @return
     */
    @GET("black/list")
    Flowable<BaseResponse<List<FamilyBlackListDto>>> getBlackList(@Query("family_id") String family_id, @Query("page") int page);

    /**
     * 获取家族招募红包
     *
     * @return
     */
    @GET("recruit/base")
    Flowable<BaseResponse<RecruitBaseDto>> getRecruitBase();

    /**
     * 获取家族招募红包列表
     *
     * @return
     */
    @GET("recruit/list")
    Flowable<BaseResponse<List<FamilyRecruitDto>>> getRecruitList(@Query("page") int page);

    /**
     * 打开家族招募红包
     *
     * @return
     */
    @FormUrlEncoded
    @POST("recruit/red_packet_open")
    Flowable<BaseResponse<Boolean>> openRedPacket(@Field("red_packet_id") int red_id);

    /**
     * 家族签到
     * "from_type": "family-home", // 家族首页=family-home,家族聊天室=family-im
     */
    @FormUrlEncoded
    @POST("sign")
    Flowable<BaseResponse<Boolean>> familySign(@Field("from_type") String from_type);

    /**
     * 获取ait消息
     *
     * @return
     */
    @GET("ait/list")
    Flowable<BaseResponse<List<FamilyMemberDto>>> getAitList(@Query("page") int page);

    /**
     * 读ait消息
     */
    @FormUrlEncoded
    @POST("ait/read")
    Flowable<BaseResponse<Boolean>> aitRead(@Field("id") int id);

    /**
     * 获取组情侣成员列表
     * @return
     */
    @GET("sex/list")
    Flowable<BaseResponse<List<FamilyMemberDto>>> getGroupCouple();

    /**
     * 编辑姓名和公告
     */
    @FormUrlEncoded
    @POST("room/edit")
    Flowable<BaseResponse<Boolean>> editFile(@Field("field") String field,@Field("value") String value);

    /**
     * 获取家族ID
     *
     * @return
     */
    @GET("member_list_search")
    Flowable<BaseResponse<List<FamilyMemberDto>>> member_list_search(@Query("family_id") int family_id , @Query("nickname") String nickname);

    /**
     * 族长转让状态
     *
     * @return
     */
    @GET("master_change_status")
    Flowable<BaseResponse<MasterChangeStatus>> master_change_status();

    /**
     * 家族批量踢出用户
     */
    @FormUrlEncoded
    @POST("user/batch_out")
    Flowable<BaseResponse<Boolean>> batchOut(@Field("family_id") int family_id,@Field("user_ids") String user_ids);
}
