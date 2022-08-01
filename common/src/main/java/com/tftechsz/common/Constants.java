package com.tftechsz.common;

import java.util.Arrays;
import java.util.List;

/**
 * 公共常量
 */
public class Constants {
    public static final String APP_NAME = "peony";
    public static final String HOST_USER = "://user.";   // 用户
    public static final String HOST_EXCH = "://exch.";
    public static final String HOST_PARTY = "://party.";  //派对
    public static final String HOST_BLOG = "://blog.";  //动态
    public static final String HOST_FAMILY = "://family.";   // 家族
    public static final String HOST_CHAT_ROOM = "://chatroom.";   // 聊天室
    public static final String HOST_PAYMENT = "://payment.";  //支付
    public static final String HOST_IM = "://im.";
    public static final String HOST_CONFIG = "://config.";
    public static final String HOST_UPLOAD = "://upload.";
    public static final String HOST_TEST = "http://config.api.dev.peony.taifangsz.com:8080/";   // 测试
    public static final String HOST = "http://config.api.peony.taifangsz.com/";
    public static final String HOST_DOWN = "http://peony-public.oss-cn-shenzhen.aliyuncs.com/config/launch.txt";
    public static final String HOST_TEST_DOWN = "http://peony-public.oss-cn-shenzhen.aliyuncs.com/config/launch_dev.txt";
    public static final String HOST_RESERVE = "http://config.backup.api.peony.taifangsz.com/";
    public static final String HOST_H5_DEV = "http://h5.dev.peony.taifangsz.com/";   // 测试h5 base url
    public static final String HOST_H5 = "http://h5.peony.taifangsz.com/";
    public static final String HOST_REVIEW_CONFIG = "http://public.assets.peony.taifangsz.com/jsonConfig/{packageName}-{channel}-{versionName}.json";

    public static final String WX_SHARE_H5_THUMB_IMG = "https://public.assets.peony.taifangsz.com/logo.png?adb=123";//微信分享h5缩略图


    public static int DEVICE_FIRM = -1;

    public static final String CURRENT_HOST = "current_host";  //当前地址

    public static final String YUNXIN_APP_ID = "9c3106de8f4c4b69e7ff47b6b6c7aad3";   //云信
    public static final String YUNXIN_ROOM_APP_ID = "9c3106de8f4c4b69e7ff47b6b6c7aad3";   //语音房云信
    public static final String YUNXIN_PRODUCT_NUMBER = "YD00357078827886";   //云信反作弊产品编号
    public static final String SANYAN_APP_ID = "qYz3RbpO";   //闪验
    public static final String WX_APP_ID = "wx4d4897cbee9c06d9";  //微信
    public static final String WX_APP_SC = "20c726d79aa9b7d809300b1f58aca89c";  //微信
    public static final String PUSH_XM_APP_ID = "2882303761520165940";//小米推送
    public static final String PUSH_XM_APP_KEY = "5562016512940";//小米推送
    public static final String PUSH_XM_CERT_NAME = "peony-push-cert-xm";//小米推送
    public static final String PUSH_HW_APP_ID = "106353275";//华为推送
    public static final String PUSH_HW_CERT_NAME = "peony-push-cert-hw";//华为推送
    public static final String PUSH_MZ_APP_ID = "148885";//魅族推送
    public static final String PUSH_MZ_APP_KEY = "da1af0c8faac4c1f889f7302ebb2224d";//魅族推送
    public static final String PUSH_MZ_CERT_NAME = "peony-push-cert-meizu";//魅族推送
    public static final String PUSH_VIVO_CERT_NAME = "peony-push-cert-vivo";//vivo推送
    public static final String PUSH_OPPO_APP_ID = "30829691";//oppo推送
    public static final String PUSH_OPPO_APP_KEY = "493f11e3ebdc4f96b8174d0e242a3a72";//oppo推送
    public static final String PUSH_OPPO_APP_SECRET = "97be08d74bb8458e8fbe79cb2f308d1d";//oppo推送
    public static final String PUSH_OPPO_CERT_NAME = "peony-push-cert-opp";//oppo推送


    public static final String IS_COMPLETE_INFO = "is_complete_info";   //是否完善了个人信息

    public static final String LOCATION_LATITUDE = "location_lat";  //纬度
    public static final String LOCATION_LONGITUDE = "location_lon";//经度
    public static final String LOCATION_CITY = "location_city";//城市
    public static final String LOCATION_PROVINCE = "location_province";//省市
    public static final String FAMILY_TID = "family_tid";//家族TID


    public static final String IS_UPLOAD_OAID = "is_upload_oaid";  //是否上传了oaid
    public static final String IS_AGREE_AGREEMENT = "is_agree_agreement";  //是否同意隐私协议
    public static final String IS_FIRST_ACCOST = "is_first_accost";  //是否女生第一次搭讪
    public static final String IS_FIRST_IN_MESSAGE = "is_first_in_message";   //是否第一次进入聊天
    public static final String IS_SHOW_FACE_OFF = "is_show_face_off";   //是否弹出不露脸视频
    public static final String RECOMMEND_USER = "recommend_user";  //首页推荐用户
    public static final String NOT_FIRST_PLAY = "not_first_play";  //是否第一次充值
    public static final String PULL_WRIES_CONTENT = "pull_wries_content";  //牵线内容
    public static final String PULL_WRIES_TIME = "pull_wries_time";  //牵线内容
    public static final String CURRENT_TIME = "current_time";  //当前时间
    public static final String AGREED_TO_TOS = "protocol_status";   //协议勾选状态
    public static final String KEY_HAS_GET_INSTALL_PARAMS = "key_Has_Get_InstallParams";   //是否有h5过来的参数
    public static final String KEY_H5_PARAM = "key_h5_param";   //h5传
    public static final String H5_INVITE_CODE_PARAM = "h5_invite_code_param";   //h5分享，邀请码
    public static final String TEAM_IS_FIRST = "team_is_first";   //是否第一次进入群聊
    public static final String VOICE_IS_OPEN = "voice_is_open";   //是否开启语音房
    public static final String SIGN_CHAT_CARD = "chat_card";   //签到-聊天卡
    public static final String SHOW_NEAR_USER = "show_near_user";   //是否显示附近列表
    public static final String PARAM_ROOM_ID = "partyRoomId";   //获得参数id
    public static final String SHOW_PARTY_ICON = "show_party_icon";   //是否显示附近列表
    public static final String PARTY_IS_ON_SEAT = "party_is_on_seat";   //是否在麦上
    public static final String PARTY_ID = "param_id";   //房间id
    public static final String PARAM_YUN_ROOM_ID = "param_room_id";   //房间id
    public static final String PARAM_IS_CALL_CLOSE = "param_is_call_close";   //是否是打电话关闭
    public static final String PARTY_IS_RUN = "party_is_run";   //service是否云信
    public static final String YUNDUN_TOKEN = "fewibWoks0";   //获取的y易顿的token
    public static final String LAST_SHOW_MOMENT_GUIDE_TIME = "last_show_moment_guide_time";   //上次显示发布动态引导的时间戳ms（一天弹一次）
    public static final String LAST_SHOW_MATCH_POP_TIME = "last_show_match_pop_time";   //上次显示速配弹框的时间戳ms（一天弹一次）
    public static final String PARAMS_PERSONALIZED_RECOMMENDATION = "personalized_recommendation";//个性化推荐 默认开启
    public static final String KEY_SRL = "srl";//是否获取定位权限 true:申请权限 false：不申请
    public static final String KEY_IS_REVIEW = "key_is_review";//是否审核中 true：审核中
    public static final String KEY_CHAT_TAB_INDEX = "chat_tab_index";//主页-消息tab的index
    public static final String KEY_SYSTEM_VERSION = "SystemVersion";//获取当前手机系统版本号
    public static final String KEY_SYSTEM_MODEL = "SystemModel";//获取手机型号
    public static final String KEY_SYSTEM_BRAND = "SystemBrand";//获取手机厂商

    public static final String FAMILY_APPLY = "family_apply";  //通知申请
    public static final String FAMILY_AIT = "family_ait";  //ait消息
    public static final String BLOG_NOTICE = "blog_notice";  //动态通知

    public static final String DIRECT_RECHARGE = "direct_recharge";   //充值

    public static final String CUSTOMER_SERVICE = "1";   //客服小秘书
    public static final String ACTIVITY_NOTICE = "2";   //活动通知

    public static final String INVITED_EVENT = "invitedEvent";

    public static final int NOTIFY_APP_STOPPED = 1000;  //通知调用定位接口
    public static final int EVENT_CHAT_MESSAGE_TOP = 1001;  //通知更新置顶和取消置顶
    public static final int EVENT_CHAT_MESSAGE_CLEAR = 1002;  //通知清除聊天记录
    public static final int NOTIFY_ACCOST_SUCCESS = 1003;  //通知搭讪成功
    public static final int NOTIFY_UPDATE_USER_INFO_SUCCESS = 1004;  //通知更改用户信息成功
    public static final int NOTIFY_TREND_COMMENT_SUCCESS = 1005;  //动态评论成功
    public static final int NOTIFY_WX_LOGIN_SUCCESS = 1006;  //动态评论成功
    public static final int NOTIFY_CLOSE_MATCH = 1007;  //关闭语音速配速配页面
    public static final int NOTIFY_SEND_TREND_SUCCESS = 1008;  //发送动态成功
    public static final int NOTIFY_REFRESH_RECOMMEND = 1009;  //刷新推荐列表页面
    public static final int NOTIFY_UPDATE_DISSOLUTION_SUCCESS = 1010;  //通知解散家族成功
    public static final int NOTIFY_UPDATE_APPLY_LIST = 1011;  //通知刷新申请列表
    public static final int NOTIFY_UPDATE_FAMILY_INFO = 1012;  //通知刷新家族信息
    public static final int NOTIFY_UPDATE_EXIT_FAMILY = 1013;  //通知退出家族成功
    public static final int NOTIFY_UPDATE_CREATE_FAMILY = 1014;  //通知创建家族成功
    public static final int NOTIFY_USER_APPLY_JOIN = 1015;  //通知有人申请加入家族
    public static final int NOTIFY_APP_STARTED = 1016;  //通知调用定位接口
    public static final int NOTIFY_FAMILY_MESSAGE_MUTE = 1017;  //通知群主消息是否开启免打扰
    public static final int NOTIFY_CLOSE_MESSAGE = 1018;  //关闭聊天页面
    public static final int NOTIFY_BLOG_NOTICE = 1019;  //通知动态消息更新
    public static final int NOTIFY_MESSAGE_DOUBLE_CLICK = 1020;  //双击消息
    public static final int NOTIFY_DELETE_MESSAGE = 1021;  //删除聊天
    public static final int NOTIFY_DELETE_CHOOSE_MESSAGE = 1022;  //删除选中聊天
    public static final int NOTIFY_DELETE_MESSAGE_CANCEL = 1023;  //删除聊天取消
    public static final int NOTIFY_CLOSE_CALL = 1024;  //挂断语音语音视频通话
    public static final int NOTIFY_UPDATE_INTIMACY = 1025;  //更新亲密度
    public static final int NOTIFY_UPDATE_INTIMACY_REFRESH = 1026;  //更新亲密度刷新
    public static final int NOTIFY_PIC_ACCOST_SUCCESS = 10027;  //相册通知搭讪成功
    public static final int NOTIFY_TOP_CONFIG = 10028;  //通知刷新首页顶部
    public static final int NOTIFY_ALIPAY = 10029;  //通知支付
    public static final int NOTIFY_FOLLOW = 10030;  //关注通知
    public static final int NOTIFY_DELETE_CHOOSE_MESSAGE_ALL = 1031;  //删除选中聊天
    public static final int NOTIFY_DOWN_HOT_SUCCESS = 1032;  //下载成功
    public static final int NOTIFY_BLACK_SUCCESS = 1033;  //拉黑成功
    public static final int NOTIFY_BIND_PHONE_SUCCESS = 1034;  //绑定成功
    public static final int NOTIFY_ON_LINE_STATUS = 1035;
    public static final int NOTIFY_FAMILY_RECRUIT = 1036;
    public static final int NOTIFY_FAMILY_FIRST_JOIN = 1037;
    public static final int NOTIFY_UPDATE_AUDIO_SIGN_SUCCESS = 1038;  //通知更改语音签名成功
    public static final int NOTIFY_ACCOST_POPUP = 1039;
    public static final int NOTIFY_SIGN_IN_SUCCESS = 1040;  //通知签到成功
    public static final int NOTIFY_AIT_SELF = 1041;  //有人ait
    public static final int NOTIFY_FAMILY_RANK = 1042;  //家族排行
    public static final int NOTIFY_UPDATE_USER_INFO = 1043;  //通知更改用户信息
    public static final int NOTIFY_HIDE_SOFT = 1044;  //隐藏键盘
    public static final int NOTIFY_FINISH_REAL = 1045;  //结束真人认证界面
    public static final int NOTIFY_FAMILY_GIFT_BAG = 1046;  //空投消息通知
    public static final int NOTIFY_WX_PAY_STATUS = 1047;  //空投消息通知
    public static final int NOTIFY_ACTIVITY_QIXI = 1048;  //活动消息通知
    public static final int NOTIFY_FAMILY_TASK_UPDATE = 1049;  //家族任务刷新
    public static final int NOTIFY_REFRESH_FAMILY_BTN = 1050;  //刷新家族广场申请
    public static final int NOTIFY_FAMILY_LEVEL_UP = 1051;  //家族等级升级
    public static final int NOTIFY_USER_LEVEL_UP = 1052;  //个人等级升级
    public static final int NOTIFY_FAMILY_BOX = 1053;  //家族宝箱
    public static final int NOTIFY_FAMILY_COUPLE_LETTER = 1054;  //表白信消息通知
    public static final int NOTIFY_FAMILY_SWEET_RANK = 1055;  //家族情侣
    public static final int NOTIFY_CHAT_ALERT_REAL = 1056;  //聊天弹窗-真人
    public static final int NOTIFY_ACTIVITY_P2P = 1057;  //单聊活动
    public static final int NOTIFY_ACTIVITY_FAMILY = 1082;  //单聊福袋活动
    public static final int NOTIFY_INTIMACY_GIFT = 1058;  //亲密度聊天弹窗
    public static final int NOTIFY_CALL_COST = 1059;  //音视频花费金额
    public static final int NOTIFY_FAMILY_INTO_BANNER = 1059;  ////家族进场动画
    public static final int NOTIFY_FAMILY_INTO_BANNER_TO_UID = 1060;  ////家族进场动画
    public static final int NOTIFY_NOBILITY_NOTICE_TO_UID = 1061;  //贵族开通推送
    public static final int NOTIFY_UPDATE_VOICE_INFO = 1062;  //语音房间公告
    public static final int NOTIFY_EXIT_VOICE_ROOM = 1063;  //退出语音房间
    public static final int NOTIFY_EXIT_MINE_DETAIL = 1064;  //退出个人信息页面
    public static final int NOTIFY_FAMILY_CONTENT = 1065;  //家族广场复制信息
    public static final int NOTIFY_REMOVE_USER = 1066;  //移除用户
    public static final int NOTIFY_NOTICE_H5 = 1067;  //通知h5更新
    public static final int NOTIFY_DELETE_TREND_SUCCESS = 1068;  //删除动态成功
    public static final int NOTIFY_ENTER_PARTY_ROOM = 1069;  //进入排队房间
    public static final int NOTIFY_ENTER_PARTY_WHEEL_RESULT = 1070;  //转盘结果通知
    public static final int NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN = 1071;  //转盘结果再一次
    public static final int NOTIFY_ENTER_PARTY_UPDATE_NAME = 1072;  //修改派对名字
    public static final int NOTIFY_ENTER_PARTY_UPDATE_PER_MANAGER = 1073;  //派对 权限管理
    public static final int NOTIFY_PARTY_GIFT_PLAY = 1074;  //派对礼物
    public static final int NOTIFY_PARTY_UPDATE = 1075;  //刷新聊天室信息
    public static final int NOTIFY_CLOSE_PARTY = 1076;  //成功关闭派对
    public static final int NOTIFY_UPDATE_CONFIG_LAUNCH = 1077;  //更新lunch
    public static final int NOTIFY_UPDATE_PARTY_MUSIC = 1078;  //更新派对音乐
    public static final int NOTIFY_INFO_DIALOG = 1079;  //通知弹窗资料
    public static final int NOTIFY_PARTY_SCROLLER_GONE = 1080;  //派对警告框关闭
    public static final int NOTIFY_FAMILY_REFRESH = 1081;  //刷新家族的排行信息
    public static final int NOTIFY_LOGIN_STATUS = 1082;  //当前登录状态
    public static final int NOTIFY_REFRESH_NEAR = 1083;  //通知刷新附近列表
    public static final int NOTIFY_START_LOC = 1084;  //通知开启了权限
    public static final int NOTIFY_REQUEST_LOC = 1085;  //询问权限
    public static final int NOTIFY_MINE_USER_INFO = 1086;  //通知更改用户信息成功
    public static final int NOTIFY_ENTER_FAMILY = 1087;  //通知进入家族
    public static final int NOTIFY_FAMILY_COUPLE_PASTER = 1088;  //组情侣礼包贴片
    public static final int NOTIFY_FAMILY_RELIEVE_APPLY = 1089;  //家族情侣解除申请


    public static final int PAR_LOVE_SWITCH = 5;  //爱意值开关
    public static final String NOTIFY_USER_ACCOST = "NOTIFY_USER_ACCOST";  //缘份牵线推荐开关
    public static final String REAL_AUTHENTICATION_ACTIVITY = "realAuthenticationActivity";  // 真人认证页面
    public static final String CHAT_ACTIVITY = "chatActivity";  // 单聊页面
    public static final String USER_DETAIL_ACTIVITY = "userDetailActivity";  // 个人详情页面
    public static final String USER_ATTENTION = "attentionUser";  // 关注用户
    public static final String USER_ACCOST = "accostUser";  // 搭讪用户
    public static final String MY_BLOG_LIST = "myBlogList";  // 我的动态列表
    public static final String MY_PHOTO_LIST = "myPhotoList";  // 我的相册
    public static final String MY_GOLD_LIST = "my_gold_list";  // 我的金币清单
    public static final String EDIT_INFO = "edit_info";  // 编辑资料
    public static final String FAMILY_APPLY_JOIN = "familyApplyJoin";  // 申请列表
    public static final String FAMILY_APPLY_LEAVE = "familyApplyLeave";  // 申请离开列表
    public static final String CALL_VOICE = "voice_call";  // 语音
    public static final String CALL_VIDEO = "video_call";  // 视频
    public static final String CHAT_TEAM_ACTIVITY = "familyIm";  // 群聊页面
    public static final String GOTO_VIP = "goto_vip"; //打开充值VIP
    public static final String FAMILY_ANNOUNCEMENT = "family_announcement";   //家族公告
    public static final String UPDATE_CONFIG_LAUNCH = "update_config_launch";  //更新lunch
    public static final String CURRENT_REQUEST_URL = "current_request_url";  //更新lunch
    public static final String USER_COUPLE_RELIEVE = "userCoupleRelieve";  //解除情侣


    public static final int NOTIFY_TOP_SCROLLVIEW = 1059;  //通知刷新首页顶部

    public static final String ACCOST_GIFT = "gift/";   //搭讪礼物路径

    public static final int MAX_NAME_LENGTH = 12;  //姓名长度限制

    public static final String MIME_JPEG = "image/jpeg";

    public static final List<Integer> VIP_PIC_FRAME = Arrays.asList(0, 1, 2, 6, 7, 18, 19, 20, 21); //头像框id
    public static final List<Integer> VIP_CHAT_BUBBLE = Arrays.asList(0, 3, 4, 8, 9, 10, 11, 16, 17); //气泡id
    public static final List<Integer> NOBLE_BANNER = Arrays.asList(0, 22, 23, 24, 25, 200, 201, 202, 203, 204, 4001); //进场横幅

    public static final String WEB_PARAMS_APP_NAME = "{app_name}";
    public static final String WEB_PARAMS_APP_VERSION = "{app_version}";
    public static final String WEB_PARAMS_APP_SYSTEM = "{app_system}";
    public static final String EXTRA_STATUS = "extra_status";
    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_PATH = "extra_path";
}
