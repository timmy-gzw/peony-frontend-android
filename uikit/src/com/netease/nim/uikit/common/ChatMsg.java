
package com.netease.nim.uikit.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.netease.nim.uikit.bean.AccostDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息列表
 */
public class ChatMsg implements Serializable {
    public static final int EVENT_DEFAULT = 0;  //
    public static final int EVENT_BUSY = 5;  //忙碌
    public static final int EVENT_REJECT = 6;  //拒绝接听
    public static final int EVENT_ACCEPT = 9;    //同意接通
    public static final int EVENT_END = 10;  //挂断
    public static final int EVENT_NOT_ACCEPT = 11;  //对方未接听
    public static final int EVENT_OFF_LINE = 12;  //挂断  对方不在线
    public static final int EVENT_ERROR = 13;  //挂断   网络异常


    public static final String INTIMACY_TYPE = "intimacy_red_heart";  //亲密度通知
    public static final String RED_PACKET_TYPE = "red_packet";  //红包通知
    public static final String RED_PACKET_TYPE_IM = "red_packet_im";  //红包通知
    public static final String FAMILY_APPLY_TYPE = "family_apply";  //家族申请通知
    public static final String BLOG_NOTICE = "blog_notice"; //动态通知
    public static final String UPDATE_HOME_NAV = "update_home_nav"; //通知首页更新
    public static final String GIFT_TYPE = "gift";   //礼物
    public static final String FAMILY_INIT = "family_init";   //创建家族
    public static final String ACCOST_TYPE = "accost";  //搭讪类型
    public static final String ACCOST_CARD = "accost_card";  //搭讪信息卡片
    public static final String ACCOST_RESUME = "accost_resume";  //搭讪信息卡片
    public static final String ACCOST_LOCATION = "accost_location";  //附近搭讪类型
    public static final String FAMILY_TIPS = "family_tips";  //家族tip
    public static final String FAMILY_COUPLE_TIPS = "couple_tips";  //家族tip
    public static final String FAMILY_NOTICE = "family_notice";  //家族公告
    public static final String FAMILY_GIFT_NOTICE = "family_gift_notice";  //家族幸运礼物中奖跑马灯
    public static final String FAMILY_SAY_HELLO = "family_say_hello";  //进入家族欢迎
    public static final String FAMILY_GIFT_BAG_IM = "family_gift_bag_im";  //空投消息
    public static final String FAMILY_COUPLE_LETTER_NUM = "family_couple_letter_num";   //家族表白信
    public static final String FAMILY_COUPLE_GIFT_BAG_TO_UID = "family_couple_gift_bag_to_uid";   //家族表白信礼包贴片
    public static final String FAMILY_GIFT_BAG = "family_gift_bag";  //空投消息通知
    public static final String FAMILY_DICE_GAME = "family_dice_game";  //骰子 和 猜拳
    public static final String FAMILY_USER_NOBILITY_LEVEL_UP_NOTICE = "family_user_nobility_level_up_notice";  //家族贵族升级
    public static final String FAMILY_ACTIVITY = "activity";  //活动
    public static final String FAMILY_ACTIVITY_QIXI = "qixi";  //七夕活动
    public static final String FAMILY_ACTIVITY_P2P = "p2p";  //单聊活动活动
    public static final String FAMILY_ACTIVITY_FAMILY = "family";  //群聊活动通用
    public static final String GO_INFO_PARTY = "go_into_party";  //首日进入派对
    public static final String ENTER_FAMILY = "enter_family"; //通知进入家族
    public static final String FAMILY_OWNER_CHANGE = "family_owner_change"; //家族改变通知
    public static final String GIFT_COMBO_FLY = "gift_combo_fly"; //处理连击效果
    public static final String GIFT_BAG_RECEIVE_TIPS = "gift_bag_receive_tips";  //空投打开消息通知
    public static final String FAMILY_RED_PACKET_RAIN = "family_red_packet_rain";  //红包雨通知
    public static final String FAMILY_RED_PACKET_RAIN_RECEIVE = "family_red_packet_rain_receive";  //红包雨结束
    public static final String FAMILY_COUPLE_LETTER_POPUP = "family_couple_letter_popup";  //组情侣通知
    public static final String INTIMACY_MARQUEE = "intimacy_marquee";  //亲密度到达100
    public static final String FAMILY_GIFT_BAG_RECEIVE_IM = "family_gift_bag_receive_im";  //打开的礼物
    public static final String REPLY_ACCOST_TYPE = "reply_accost";  //搭讪类型
    public static final String ACCOST_EXPIRED_TYPE = "accost_expired";  //过期搭讪
    public static final String TIP_TYPE = "tips";   //tip
    public static final String TIP_ACCEPT_AND_RECHARGE = "accept_and_recharge";   //tip
    public static final String CALL_TYPE = "call";   //语音视频
    public static final String FAMILY_SHARE_TYPE = "family_share";   //家族邀请
    public static final String ALERT_TYPE = "alert";   //弹窗提示
    public static final String NOTICE_H5 = "notice_h5";   //通知H5更新
    public static final String UPDATE_CONFIG_LAUNCH = "update_config_launch";   //通知更新luancher
    public static final String DEFAULT = "default";
    public static final String INTIMACY = "intimacy";   //亲目睹更新
    public static final String EVENT = "event";  //时间更新（真人认证通过）
    public static final String TOAST = "toast";  //搭讪过多吐司
    public static final String REDIRECT = "redirect";  //重定向页面
    public static final String USER_STATUS = "user_status";  //用户状态
    public static final String ACCOST_NOW = "accost_now";   //系统牵线消息
    public static final String PAYMENT = "payment";   //支付异步通知同步
    public static final String RESET_RECOMMEND = "reset_recommend";  //推荐值
    public static final String WINDOW_TIPS = "window_tips"; //底部弹窗
    public static final String MSG_WINDOW_BOTTOM = "msg_window_bottom";  //底部弹出充值会员
    public static final String MSG_WINDOW_BOTTOM_WARN = "msg_window_bottom_warn";  //底部弹出违规警告
    public static final String USER_TASK = "user_task";  //用户任务
    public static final String FAMILY_RECRUIT = "family_recruit";  //家族招募红包
    public static final String FAMILY_FIRST_JOIN = "family_first_join";  //家族首次加入
    public static final String ACCOST_POPUP = "accost_popup";  //搭讪pop
    public static final String FAMILY_AIT = "family_ait";  //家族ait人数
    public static final String FAMILY_RANK = "family_rank";  //家族排行
    public static final String FAMILY_TASK_UPDATE = "family_task_update";  //家族任务刷新
    public static final String FAMILY_LEVEL_UP = "family_level_up";  //家族等级提升
    public static final String VOICE_ROOM_SEAT = "voice_room_seat";  //上下麦tip
    public static final String PARTY_VOICE_ROOM_SEAT = "party_voice_room_seat";  //聊天室上下麦tip
    public static final String USER_LEVEL_UP = "user_level_up";  //个人等级提升
    public static final String FAMILY_BOX_NOTICE_TO_UID = "family_box_notice_to_uid";  //家族宝箱
    public static final String FAMILY_BOX_NOTICE = "family_box_notice";  //家族宝箱
    public static final String FAMILY_SWEET_RANK = "family_sweet_rank";  //家族情侣
    public static final String RELIEVE_APPLY = "relieve_apply";  //家族情侣解除申请
    public static final String MSG_WARN_TIPS = "msg_warn_tips";  //消息添加感叹号
    public static final String MSG_WARN = "msg_warn";  //消息添加感叹号
    public static final String CHAT_ALERT = "chat_alert";  //聊天弹窗
    public static final String CHAT_ALERT_REAL = "real";  //真人弹窗
    public static final String INTIMACY_GIFT = "intimacy_notice_to_uid";  //亲密度等级礼物
    public static final String FAMILY_INTO_BANNER = "family_into_banner";  //家族进场动画
    public static final String FAMILY_INTO_BANNER_TO_UID = "family_into_banner_to_uid";  //家族进场动画
    public static final String NOBILITY_NOTICE_TO_UID = "nobility_notice_to_uid";  //贵族开通推送
    public static final String FAMILY_INTO_NOTICE = "family_into_notice";  //贵族进入svga
    public static final String FAMILY_JOIN_ROOM = "family_join_room";  //语聊房开启
    public static final String FAMILY_RED_PACKET_RAIN_COUNT_DOWN = "family_red_packet_rain_count_down";  //红包雨开始
    public static final String FAMILY_RED_PACKET_RAIN_TOP3_TO_ALL = "family_red_packet_rain_top3_to_all";  //红包雨前三奖励
    public static final String FAMILY_RED_PACKET_RAIN_COUNT_DOWN_MSG = "family_red_packet_rain_count_down_msg";  //红包雨时间通知

    public static final String LUCKY_GIFT = "lucky_gift";  //倍数

    public static final String QING_LANG = "qing_lang";


    public static final String CALL_AFTER_INTIMACY_TIPS = "call_after_intimacy_tips";  //亲密度达到可以音视频通话

    public static final String REMOVE_USER_CHAT = "removeUserChat";  //单独删除某个用户消息

    public static final String RED_PACKET_ROOM = "room"; // 聊天广场发送红包 {"des":"搭讪红包奖励","red_packet_id":132213}
    public static final String RED_PACKET_FAMILY = "family"; // 家族发送红包 {"des":"搭讪红包奖励","red_packet_id":132213}
    public static final String RED_PACKET_IM_OPEN_ROOM = "open_room"; //在聊天广场打开红包
    public static final String RED_PACKET_IM_OPEN_FAMILY = "open_family"; //在家族打开红包

    public static final String CALL_TYPE_VOICE = "voice_call";   //语音
    public static final String CALL_TYPE_VIDEO = "video_call";    //视频
    public static final String CALL_TYPE_VOICE_MATCH = "voice_call_match";    //语音速配
    public static final String CALL_TYPE_VIDEO_MATCH = "video_call_match";    //视频速配
    public static final String JOIN_LEAVE_ROOM = "join_leave_room";  //加入房间
    public static final String VIDEO_STYLE = "video_style";  //音视频通知


    public static final String CALL_MATCH_FORCE = "force";   //视频速配强制匹配
    public static final String CALL_MATCH_INQUIRY = "inquiry";   //视频速配询问匹配


    /*********party 信令***********/

    public static final String PARTY_ROOM_ATTACH = "party_room_attach";
    public static final String PARTY_JOIN_SYSTEM_NOTICE = "join_system_notice";   //party 系统通知
    public static final String PARTY_MICROPHONE_NOTICE = "party_microphone_notice";   //party 麦位通知
    public static final String PARTY_ROOM_PK = "party_room_pk";   //party pk 麦位通知
    public static final String PARTY_WELCOME = "welcome";   //party 欢迎
    public static final String PARTY_SPECIAL_WELCOME = "special_welcome";   //party 欢迎
    public static final String PARTY_NOTICE = "party_notice";   //
    public static final String PARTY_GIFT_PLAY = "party_gift_play";   //
    public static final String PARTY_GIFT_COMBO_PLAY = "party_gift_combo_play";   //
    public static final String PARTY_TURNTABLE_TV = "turntable_tv";   //转盘消息通知
    public static final String PARTY_BIG_EMOTICON = "big_emoticon";   //骰子消息
    public static final String PARTY_TURNTABLE_TV_ALL = "party_notice_all";   //全服广播
    public static final String PARTY_ROOM = "party_room";   //派对房间
    public static final String CMD_WIN_CONTROL = "win_control";   //派对房间
    public static final String BLINK_BOX_GIFT = "blink_box_gift";   //盲盒通知
    public static final String NOBILITY_LEVEL_UP = "nobility_level_up";   //贵族升级通知
    public static final String NOBILITY_LEVEL_UP_SPECIAL = "nobility_level_up_special";   //贵族升级飘萍通知
    public static final String PARTY_USER_EXT = "party_user_ext";   //派对个人拓展字段


    /*************************语音闲聊通知*****************************/

    public static final String VOICE_ROOM_ATTACH = "voice_room_attach";  //开启模式通知


    public int type;
    public String cmd;
    public String content;
    public String msg_id;
    public String cmd_type;
    public String to;
    public String from;
    public String child;
    public List<String> to_user_list;
    public String msg_integral;
    public String boy_accost_desc;
    public Intimacy msg_intimacy;
    public Vip vip;
    //1-积分 2-聊天卡
    public int msg_icon_type;

    //语音视频
    public static class CallMsg implements Serializable {
        public int state = -1;
        public String room_token;
        public String channel_name;
        public int answer_time;
        public int callee;   //为0代表主叫发送的消息  为1代表被叫发送的消息
        public String call_id;
        public int close_type;
        public String type; //type.force = 强制匹配，type.inquiry = 询问匹配
        public int to_user_is_online;
        public int text_type;   // 2:视频 1: 语音
        public String cost;
        public String msg;
    }


    //tips
    public static class Tips implements Serializable {
        public String des;
        public String from_type;
    }


    //弹窗消息
    public static class Alert implements Serializable {
        public boolean is_outside_enable;
        public boolean is_fail;
        public String title;
        public String des;
        public int template = 0;  //  2 //金币充值弹窗
        public AlertEvent left_button;
        public AlertEvent right_button;
        public AlertEvent button;
        public int pop_style;

    }

    public static class NobleNotice {
        public String title;
        public String tips;
        public int id;
    }

    public static class IntimacyGift {
        public String tips;
        public String url;
        public String desc;
    }

    public static class NobleEnterAni {
        public String user_icon;
        public String nickname;
        public String nickname_color;
        public String tips;
        public int id;
    }

    public static class AlertEvent {
        public String msg;
        public String event;  // 事件
        public boolean is_finish;

    }

    //亲密度通知
    public static class Intimacy {
        public String intimacy_val;
        public String intimacy;
        public String change;
        public boolean is_show_heart;
        public String msg_integral;
    }

    public static class Vip {
        public int picture_frame;
        public int chat_bubble;
        public int is_vip;
        public int role_id;  //id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
        public int new_tag;  //0 不显示新人标签  1 显示
        public int is_couple;  //0 不显示情侣 1 显示
        public int nobility_level;
        public String badge;
        public String badge_small;
    }


    //搭讪消息
    public static class Accost {
        public AccostGift gift_info;
        public String msg;
        public int accost_type;    // 首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5   搜索 6 聊天页面 7
        public int accost_from;    //

    }

    /**
     * 礼物bean
     */
    public static class AccostGift {
        public String name;
        public String image;
        public String coin;
        public String cate;
        public int combo;  //1 连击礼物
        public int tag_value;
        public int is_choose_num;
        public int animationType;
        public String from_user_nickname;
        public String from_user_id;
        public String animation;
        public List<String> animations;
        public int id;
        public int status;
        public BlindBoxNotifyData ext;
    }

    public static class Activity {
        public String icon;
        public String link;
        public int open;
        public float alpha;
        public ConfigInfo.Option option;
        public List<Activities> activities;
    }

    public static class Activities {
        public String type;
        public String icon;
        public String link;
        public int open;
        public float alpha;
        public ConfigInfo.Option option;
    }

    public static class FamilyLevelUp {
        public int level;
        public String level_icon;
        public String level_tips;
        public String level_tips_color;
    }

    public static class FamilyBox {
        public long count_down;
        public int defaultUsersCount;   //默认人数
        public int realUsersCount;      //发言人数
        public int defaultCoins;        //默认金币消耗
        public int realCoins;           //实际金币消耗
        public String tips;                //说明
        public List<String> activity_desc;
        public int status;  // 0 计时   1开抢   2抢到等待下一轮   3未抢到
    }

    public static class UserLevelUp {
        public String user_tips;
        public String user_title;
        public String user_level_icon;
        public String user_type; //rich：土豪  charm：魅力
    }

    /**
     * 空投
     */
    public static class Airdrop implements Serializable {
        public String type;
        public String title;
        public String des;
        public int sortId;
        public String animation;
        public int apply_id;
        public int gift_bag_id;
        public int family_id;
        public int rule;
        public String gift_bag_name;
        public int num;
        public int start_time;
        //红包雨队列
//        public List<ChatMsg.Airdrop> listRed;
        public ChatMsg.Activities activity;//活动icon
    }


    public static class Game {
        public int number;
        public String animation;
        public int user_id;
        public String image;
        public int index;
        public String animation_type;
        public String url;
        public int type;
    }

    /**
     * 红包雨
     */
    public static class Rain {
        public int id;
        public int x;
        public int y;
        public int z;
        public int coin;
        public String name;
        public String icon;
        public int num;
    }


    /**
     * 红包雨前三名
     */
    public static class RainStartForget3 {
        public List<RainStartForgetInner> top3;
        public String title;
        public String color;

        public static class RainStartForgetInner {
            public String tip;
            public String coin_str;
        }
    }

    /**
     * 申请消息
     */
    public static class AirdropOpen {
        public int gift_bag_id;
        public int to_user_id;
        public int from_user_id;
        public String des;
        public String from_user_name;
        public String to_user_name;
        public String text;  //召唤空投
        public String optimum;   //为本场手气最佳
        public String msg;
    }


    /**
     * 礼物消息
     */
    public static class Gift {
        public int gift_num;   //礼物数量
        public String msg;  //消息
        public String family_id;   //家族ID
        public AccostGift gift_info;
        public int is_append = 1;   // 1 显示上面连击效果
        public String tid;   //家族ID
        public List<String> to;
        public String name;
        public String image;
        public String coin;
        public String cate;
        public int combo;  //1 连击礼物
        public int tag_value;
        public int is_choose_num;
        public int animationType;
        public String from_user_nickname;
        public String from_user_id;
        public String animation;
        public List<String> to_name;
        public List<String> animations;
        public int id;
        public int status;
        public BlindBoxNotifyData ext;

        public String from_nickname;
    }

    /**
     * 金币红包收到
     */
    public static class GoldRedEnvelope {
        public String des;
        public int red_packet_id;
    }


    /**
     * 金币红包领取
     */
    public static class GoldRedReceive {
        public String temlate;
        public int red_packet_id;
        public String receive_user;
        public int receive_user_id;
        public String create_red_packet_user;
        public int create_red_packet_user_id;
    }


    /**
     * 红包消息
     */
    public static class RedPacket {
        public String des;
        public String desc;
        public String type;
        public int red_packet_id;
        public String scene;   //task_register_new_user新用户红包
    }


    /**
     * 申请消息
     */
    public static class ApplyMessage {
        public int num;
        public String message;
        public List<String> to_list;
        public String title;
        public String des;
        public int count;
    }

    /**
     * 个人时间通知
     */
    public static class EventType {
        public String type;
        public String msg;
        public String her_user_id;
    }


    /**
     * 表白信
     */
    public static class CoupleLetter {
        public String from_nickname;
        public String to_nickname;
        public String from_icon;
        public String from_user_id;
        public String to_user_id;
        public String type;
        public String to_icon;
        public String desc;
        public int from_sex;
        public int to_sex;
        public int from_age;
        public int to_age;
    }

    public static class UserTask {
        public String type; //online_start    online_end
        public int value;
        public String link;
    }

    /**
     * 返回的对象
     */
    public static class CallBackMessage {
        public int code;
        public String body;
        public String tips_msg;
        public String new_body;
        public int show_fail;
    }

    public static class FamilyShareMessage {
        public int id;
        public String share_msg;
        public String img_url;
        public String invite_id;

        public FamilyShareMessage() {
        }

        public FamilyShareMessage(int id, String share_msg, String img_url, String invite_id) {
            this.id = id;
            this.share_msg = share_msg;
            this.img_url = img_url;
            this.invite_id = invite_id;
        }
    }

    public static class PartyGiftMessage {
        public PartyGiftMessage() {
        }

        public PartyGiftMessage(String form, String toId, String to, String gift, int num) {
            this.form = form;
            this.to = to;
            this.toId = toId;
            this.gift = gift;
            this.num = num;
        }

        public String form;
        public String toId;
        public String to;
        public String gift;
        public int num;

    }


    /**
     * 用户状态
     */
    public static class UserStatus {
        public String status;
    }


    /***
     * 系统牵线消息
     */
    public static class AccostNow {
        public String pic;
        public String title;
        public String desc;
        public long timestamp;
    }

    /***
     * 加入家族
     */
    public static class JoinFamily {
        public String icon;
        public String title;
        public String name;
        public String url;
        public String left_icon;
        public String right_icon;
        public String svg;
        public String carName;
    }


    public static class RecommendValue {

        public String title;
        public AlertEvent button;
        public List<RecommendContent> content;

    }

    public static class RecommendContent {

        public String icon;
        public List<RecommendContentText> text;

    }


    public static class RecommendContentText {

        public String desc;
        public String color;

    }


    public static class Notice {
        public String from;
    }

    public static class EnterFamily {
        public String family_id;
    }

    /**
     * 聊天室消息
     */
    public static class PartyMsg {
        public int type;
        public String msg;
        public String font_color;
        public String desc;
        public String to_user_id;
        public String operate_nickname;
        public String gradename;  //贵族等级昵称
        public String nickname;
        public String limit_deadline;
        public String image;
        public ModifyData update_data;  //修改的数据
        public int microphone_pattern;  // 1自由模式 2 麦序模式
        public int is_mute;  //1 禁言  0 解除禁言
        public String nums;
        public String badge;
        public int user_id;
        public String svg;


    }

    /**
     * 盲盒通知
     */
    public static class BlindBoxNotifyData {
        public String msg_id;
        public int blink_type; //是否是盲盒类型
        public FromUser from_user;
        public FromUser to_user;
        public Gift gift;
        public Gift get_gift;

        public static class FromUser {
            public int uid;
            public String nickname;
            public int gift_num;
            public String animation;
        }

        public static class Gift {
            public int gift_id;
            public int get_gift_id;
            public String gift_name;
            public String cost;
            public int gift_num;
            public String animation;
            public List<String> animations;
        }
    }

    public static class ModifyData {
        public String room_name;  // 修改后的房间名
        public String title;
        public String announcement;   //修改后的公告
        public String bg_icon;   //修改后的背景图全地址
        public String icon;  //房间封面
    }

    /**
     * 开启pk
     */
    public static class PartyStart {
        public int duration;
        public int punish_time;
    }

    /**
     * cost
     */
    public static class PartyCost {
        public String cost;
        public int user_id;
        public int blue_cnt;
        public int red_cnt;
        public int mvp_user_id;
        public PkBank pk_bank;
    }

    public static class PkBank implements Parcelable {
        public ArrayList<RedTeam> red_team;
        public ArrayList<RedTeam> blue_team;

        public PkBank() {
        }

        protected PkBank(Parcel in) {
            this.red_team = in.createTypedArrayList(RedTeam.CREATOR);
            this.blue_team = in.createTypedArrayList(RedTeam.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(red_team);
            dest.writeTypedList(blue_team);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PkBank> CREATOR = new Creator<PkBank>() {
            @Override
            public PkBank createFromParcel(Parcel in) {
                return new PkBank(in);
            }

            @Override
            public PkBank[] newArray(int size) {
                return new PkBank[size];
            }
        };
    }

    public static class RedTeam implements Parcelable {
        public int rank;
        public String icon;
        public int user_id;

        public RedTeam() {
        }

        protected RedTeam(Parcel in) {
            rank = in.readInt();
            icon = in.readString();
            user_id = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(rank);
            dest.writeString(icon);
            dest.writeInt(user_id);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RedTeam> CREATOR = new Creator<RedTeam>() {
            @Override
            public RedTeam createFromParcel(Parcel in) {
                return new RedTeam(in);
            }

            @Override
            public RedTeam[] newArray(int size) {
                return new RedTeam[size];
            }
        };
    }

    /**
     * 停止pk
     */
    public static class PartyPkStop {
        public int win_site;   ////0平局 1红方胜利 2蓝方胜利
        public int punish_time;
        public PartyPkStatic victory_static;
    }

    /**
     * 资源
     */
    public static class PartyPkStatic {
        public String win;
        public String draw;
    }


    public static class JumpMessage implements Serializable {
        public ChatMsg cmd_data;
    }

    public static class AccostPopup {
        public int user_id;
        public String icon;
        public String nickname;
        public boolean is_selected = true;
        public AccostDto data;
        public String sex;
        public String age;
    }

    /**
     * 个人资料卡片信息
     */
    public static class AccostCard {
        public String icon;
        public int is_self;
        public int is_real;
        public String desc;
        public String hometown;
        public List<String> tags;
        public ArrayList<String> picture;

    }

    /**
     * 通知音视频违规信息
     */
    public static class VideoStyle {
        public boolean is_to_black;
        public String msg;
        public String cost;
        public int second = 10;
        public String call_id;
    }


    public static class RoomSeat {
        public int index = -1;
        public int status;
        public int reason;
        public int user_id;
        public String cost;
        public String nickname;
        public String icon;
    }


    public static class RoomInfo {
        public String announcement;
        public String name;
        public String content;
        public String room_name;
        //审核中的 text 房间名称
        public String name_audit;
        //审核中的 text  公告内容
        public String announcement_audit;
    }

    public static class RoomExtInfo {
        public int rich_level;
        public String role_id;
        public String nickname;
        public String badge;
        public String chat_bubble;
    }

    /**
     * 派对邀请用户上麦
     */
    public static class Inviter {
        public int inviter;
        public int room_id;

    }

    public static class WinControl {
        public String msg;
        public String user_msg;
        public int user_id;

    }

    /**
     * 'msg_id' => $msg_id,
     * 'msg' => sprintf('%s中%d倍大奖，恭喜恭喜！', $info['nickname'], $times),
     * 'desc' => sprintf('%d倍', $times),
     */
    public static class LuckyGift {
        public String msg_id;
        public String msg;
        public String desc;
        public int gift_id;
        public int user_id;

    }

    /**
     * 首日进入派对
     */
    public static class OneDay {
        public int party_room_id;
        public int room_id;

    }


    public static class NobilityLevelUp {
        public String nickname;
        public String msg;
        public int level;
        public String badge;  // 地址
        public String badge_small;
    }

}
