package com.tftechsz.common.constant;

public
/**
 *  包 名 : com.tftechsz.common.constant
 *  描 述 : TODO
 */
interface Interfaces {
    String notice_blog_delete = "该内容已不可见";
    int MAX_RED_ENVELOPE_PRICE = 90000; //最大发送金币红包数量
    int MIN_RED_ENVELOPE_PRICE = 100; //最少发送金币红包数量
    int MAX_RED_ENVELOPE_NUM = 100; //最大发送红包个数
    int MIN_RED_ENVELOPE_NUM = 3; //最少发送红包个数
    String RED_PACKAGE_TYPE = "red_package_type"; //红包类型  1 已领取   2已领完  3已过期
    int MAX_SELCTED_VIDOE_TIME = 15; //最长视频选择时间
    int MIN_SELCTED_VIDOE_TIME = 5; //最短视频选择时间

    int MAX_TRIM_VIDOE_TIME = 15; //视频最长裁剪时间
    int MIN_TRIM_VIDOE_TIME = MIN_SELCTED_VIDOE_TIME; //视频最短裁剪时间

    String REDPACKAGE_FINISHED = "手慢了，红包已领完";
    String REDPACKAGE_EXPIRED = "红包超过领取有效期";

    String CHANNELID = "nim_message_channel_001"; //消息通知
    String SP_MESSAGE_TAB_FIRST = "sp_message_tab_first"; //消息tab首次进入
    String EXTRA_UID = "extra_uid";
    String EXTRA_INDEX = "extra_index";
    String EXTRA_FIRST_ICON = "extra_first_icon";
    String EXTRA_IMAGE_URL = "extra_image_url";
    String EXTRA_IMAGE_POS = "extra_image_pos";
    String EXTRA_VIDEO_INFO = "extra_video_info";
    String EXTRA_TREND = "extra_trend";
    String VIDEO_PATH_KEY = "video_path_key";
    String EXTRA_ISBOY_ICON = "extra_isboy_icon";
    int VIDEO_TRIM_REQUEST_CODE = 998;
    int PAY_REQUEST_CODE = 997;
    int EXTRA_REAL_CAMERA = 996;
    int EXTRA_REAL_CAMERA_SFZ_FONT = 995;
    int EXTRA_REAL_CAMERA_SFZ_BACK = 994;
    String EXTRA_TRIM_VIDEO_PATH = "extra_trim_video_path";
    String EXTRA_TRIM_VIDEO_LENGHT = "extra_trim_video_path_lenght";
    int DEFAULT_BOY_AGE = -30; //男性默认年纪 负数
    int DEFAULT_GIRL_AGE = -22; //女性默认年纪 负数

    String LINK_WEBVIEW = "webview://";
    String LINK_PEONY = "peony://";
    String LINK_BROWSER = "browser://";
    String LINK_PEONY_TOAST = "peony://toast?";
    String LINK_PEONY_STORE_DOWNLOAD = "peony://store_download";

    String LINK_PEONY_JOIN_VIDEO_MATCH = "join_video_match"; //视屏通话
    String LINK_PEONY_JOIN_VOICE_MATCH = "join_voice_match"; //语音速配
    String LINK_PEONY_FAMILY_HOME = "family_home"; //家族
    String LINK_PEONY_CHAT_SQUARE = "chat_square"; //聊天广场

    String LINK_PEONY_RECHARGE = "recharge"; //充值
    String LINK_PEONY_INCOME = "income"; //收益
    String LINK_PEONY_SETTING = "setting"; //设置
    String LINK_PEONY_HELP_FEEDBACK = "help_feedback"; //反馈
    String LINK_PEONY_MY_CERTIFICATION = "my_auth"; //我的认证
    String LINK_PEONY_FACIAL = "facial"; //美颜设置
    String LINK_PEONY_ACCOST_SETTING = "accost_setting"; //搭讪设置
    String LINK_PEONY_ACCOST_SETTING_AUDIO = "accost_setting_audio"; //语音招呼
    String LINK_PEONY_ACCOST_SETTING_PIC = "accost_setting_pic"; //招呼相册
    String LINK_PEONY_ACCOST_SETTING_CUSTOMIZE = "accost_setting_customize"; //自定义招呼
    String LINK_PEONY_RECOMMANDVALUE = "recommandvalue";  //推荐值
    String LINK_PEONY_NEWTASK = "newtask";  //红包福利
    String LINK_PEONY_VISITOR = "visitor"; //访客记录
    String LINK_PEONY_HOME = "home"; //首页
    String LINK_PEONY_MYPHOTO = "myphoto"; //我的相册
    String LINK_PEONY_MYINFO = "myinfo"; //我的资料
    String LINK_PEONY_MYDETAIL = "mydetail"; //我的资料
    String LINK_PEONY_VOICESIGN = "voicesign"; //语音签名
    String LINK_PEONY_TREND = "trend"; //动态
    String LINK_PEONY_REAL = "real"; //真人
    String LINK_PEONY_SELF = "self"; //实名
    String LINK_PEONY_CREATE_LIVE = "blind_date_create"; //创建直播
    String LINK_PEONY_DATE_LIST = "blind_date_list"; //直播列表
    String LINK_PEONY_MY_OUTFIT = "my_outfit"; //我的装扮
    String LINK_PEONY_MY_NOBLE = "my_noble"; //我的贵族
    String LINK_PEONY_NOTE_VALUE = "note_value"; //音符值
    String LINK_PEONY_PARTY_SELF_CHECK = "party_self_check"; //派对实名认证
    String LINK_PEONY_WITHDRAW_POP = "withdraw_pop"; //提现弹窗
    String LINK_PEONY_ABOUT_US = "about_us"; //关于我们
    String LINK_PEONY_MESSAGE_LIST = "message_list"; //首页-消息列表tab

    String LINK_PEONY_INVITE = "invite_bind"; //邀请码填写

    String SHOW_IS_REAL = "show_is_real"; //真人
    String SHOW_IS_SELF = "show_is_self"; //实名
    String SHOW_IS_PARTY_SELF = "show_is_party_self"; //派对实名

    String MAIN_TAB_RECOMMEND = "recommend";
    String MAIN_TAB_NEAR = "near";

    String EXTRA_TREND_PIC_LIST = "extra_trend_pic_list";
    String EXTRA_TREND_PIC_INDEX = "extra_trend_pic_index";
    int PIC_SELCTED_NUM = 9;
    int TICKERVIEW_ANIMATION_LIKE = 0;
    int TICKERVIEW_ANIMATION_MONEY = 0;
    String EXTRA_TITLE = "extra_title";
    String EXTRA_MESSAGE = "extra_message";
    String ACCOST_RECORD = "录制语音招呼";
    String ACCOST_PIC = "招呼相册";
    String ACCOST_CUSTOMIZE = "自定义打招呼";
    String ACCOST_RECORD_TOMY = "我的语音";
    String EXTRA_CUSTOMIZE_EDIT_TYPE = "customize_edit_type"; // 0编辑   1添加
    String EXTRA_CUSTOMIZE_EDIT_ID = "customize_edit_id";
    String EXTRA_CUSTOMIZE_EDIT_CONTENT = "customize_edit_content";
    float VideoW = 1.1f;
    float VideoH = 3.5f;
    String SP_SEL_VIDEO = "sp_sel_video";
    String EXTRA_DATA = "trend_data";
    String EXTRA_TYPE = "extra_type";
    String EXTRA_PAGE = "extra_page";
    String EXTRA_COIN_VALUE = "extra_coin_value";
    String EXTRA_ID = "extra_id";
    String SP_PHONE = "sp_phone";
    String SP_TEAM_ID = "sp_team_id";
    String EXTRA_PATH = "extra_path";

    String WECHAT = "wechat";
    String ALIPAY = "alipay";
    String SHOUXINYI_WECHAT = "shouxinyi-wechat";
    String SHOUXINYI_ALIPAY = "shouxinyi-alipay";

    int WAITING_TIME = 60;
    String EXTRA_PHONE = "extra_phone";
    String SHARE_QUEST_TYPE_FAMILY = "apply_family";
    String SHARE_QUEST_TYPE_PARTY = "apply_party";

    String SHARE_RESULT_TYPE_APPLY = "apply"; //好友邀请
    String SHARE_RESULT_TYPE_WECHAT_MOMENTS = "wechat_moments"; //朋友圈
    String SHARE_RESULT_TYPE_WECHAT_FRIENDS = "wechat_friends"; //微信好友
    String SHARE_RESULT_TYPE_QQ = "qq"; //qq
    String SHARE_RESULT_TYPE_QZONE = "qzone"; //qq空间
    String SHARE_RESULT_TYPE_SINA = "sina"; //sina
    String POINT_SCENE_PAY_SUCCESS = "支付成功";
    String POINT_EVENT_CLICK = "click";
    String POINT_EVENT_PAGE = "page";
    String POINT_EVENT_CALLBACK = "point_event_callback";
    String POINT_INDEX_PAY_SUCCESS = "pay_success";
    String SP_IS_FAMILY_IN = "sp_is_family_in";   //默认支付方式 0  家族支付 1
    String EXTRA_RECHARGE_TYPE = "extra_recharge_type";
    String EXTRA_USER_INFO = "extra_user_info";
    String SP_UMENG_ID = "sp_umeng_id";
    String SP_OAID = "sp_oaid";
    String EXTRA_INVITE_ID = "extra_invite_id";
    String EXTRA_RE_REQUEST = "extra_re_request";
    int TYPE_CAMERA = 1;
    int TYPE_PICTURE = 2;
    String ACTIVITY_QIXI = "activityqixi";
    String SP_FAMILY_LEVEL_UP = "sp_family_level_up";
    String SP_FAMILY_BOX_ACTIVITY_DESC = "sp_family_box_activity_desc";
    int ADAPTER_TYPE_1 = 1;
    int ADAPTER_TYPE_2 = 2;
    int CHAT_TOP_SCROLL_TIMES = 5000;
    String SP_CHAT_ADD_RED_POINT_FAMILY_LOVERS = "sp_chat_add_red_point_family_lovers"; //添加按钮红点-家族情侣
    String SP_CHAT_BG = "sp_chat_bg_";
    String SP_IS_VIDEO_COMPRESS = "sp_is_video_compress";
    String SP_VIDEO_COMPRESS_FILTER = "sp_video_compress_filter";
    String SP_VIDEO_COMPRESS_BITRATE = "sp_video_compress_bitrate";
    String EXTRA_URL = "extra_url";
    String EXTRA_ALL_DATA = "extra_all_data";
    String EXTRA_SEL_DATA = "extra_sel_data";
    String WORKER_NOBLE_SVGA = "noble_svga";
    String WORKER_BUBBLE_PNG = "bubble_png";
    int MAX_SELECTED = 5; //音乐最大上传数量
    int EVENT_MESSAGE = 1000;   //礼物gift 播放
    String CMD_TYPE = "cmd_type";
    String JSON_STRING = "json_string";

    int USER_ITEM_NOT_ENOUGH = 20013;  // 我的物品数量不足
    int USER_ILLEGAL = 20014;  // 非派对女用户不可收礼
    String MAIN_TAB_LOTTIE_FOLDER = "main_tab_lottie";
    String[] SEGMENT_DATA_LUCKY = {"1", "9", "66", "188", "520"};
    String[] SEGMENT_DATA = {"1", "10", "52", "188", "520", "1314"};
    String RECHARGE_NUMBER = "recharge_number";
    String FIY_NUMBER = "fiy_number";
    String SCENE_NUMBER = "scene_number";
}
