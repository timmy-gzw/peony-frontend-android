package com.tftechsz.common;

public class ARouterApi {
    public static final String MAIN_MAIN = "/main/MainActivity";   //主页吗
    public static final String FRAGMENT_HOME = "/home/homeFragment";   //首页
    public static final String FRAGMENT_TREND = "/trend/trendFragment";   //动态
    public final static String ACCOST_SERVICE = "/home/accostService";

    //chat
    public static final String FRAGMENT_CHAT_TAB = "/chat/chatTabFragment";   //消息
    public static final String FRAGMENT_CHAT = "/chat/chatFragment";   //消息
    public static final String ACTIVITY_VIDEO_CALL = "/chat/VideoCallActivity";   //音视频
    public static final String ACTIVITY_TEAM_CALL = "/chat/TeamCallActivity";   //音视频
    public static final String ACTIVITY_P2P_MESSAGE = "/chat/P2PMessageActivity";   //聊天
    public static final String ACTIVITY_TEAM_MESSAGE = "/chat/TeamMessageActivity";   //聊天
    public final static String ATTENTION_SERVICE = "/chat/attentionService";
    public final static String ACTIVITY_NOTICE = "/chat/ActivityNoticeActivity";
    public final static String ACTIVITY_LIVE_HOME = "/chat/LiveHomeActivity";
    public static final String ACTIVITY_PARTY_MESSAGE_DETAILS = "/chat/PartyChatDetailsActivity";   //派对私信详情
    public static final String ACTIVITY_PARTY_MESSAGE = "/chat/PartyChatActivity";   //派对私信和在线列表

    //home
    public final static String CALL_SERVICE = "/home/callService";
    public static final String ACTIVITY_USER_PIC_BROWSER = "/home/UserPicBrowserActivity";


    //trend
    public final static String ACTIVITY_BEFORE_REPORT = "/trend/BeforeReportActivity";
    public final static String ACTIVITY_REPORT = "/trend/reportActivity";
    public final static String ACTIVITY_MINE_TREND = "/trend/mineTrendActivity";
    public static final String FRAGMENT_TREND_LIST = "/trend/trendFragment";   //动态列表

    //mine
    public static final String ACTIVITY_SPLASH = "/mine/SplashActivity";   //启动
    public static final String FRAGMENT_MINE = "/mine/mineFragment";   //个人
    public static final String MINE_LOGIN = "/mine/loginActivity";   //登陆
    public static final String USER_INFO_SERVICE = "/mine/userService";  //用户信息
    public static final String ACTIVITY_MINE_DETAIL = "/mine/mineDetailActivity";  //用户信息详情
    public static final String ACTIVITY_LEVEL_DETAIL = "/mine/userLevelActivity";  //用户信息详情
    public static final String ACTIVITY_MY_CERTIFICATION = "/mine/MyCertificationActivity";  //我的认证
    public static final String ACTIVITY_REAL = "/mine/RealNameActivity";  //实名认证
    public static final String ACTIVITY_SELF_CHECK = "/mine/SelfCheckActivity";  //实名认证
    public static final String ACTIVITY_REAL_CAMERA = "/mine/RealCameraActivity";  //自定义相机
    public static final String ACTIVITY_REAL_SUCCESS = "/mine/RealNameSuccessActivity";  //实名认证成功
    public static final String ACTIVITY_REAL_AUTHENTICATION = "/mine/RealAuthenticationActivity";  //真人认证
    public static final String ACTIVITY_REAL_AUTHENTICATION_NEW = "/mine/RealAuthenticationActivityNew";  //真人认证
    public static final String ACTIVITY_REAL_AUTHENTICATION_STATUS = "/mine/RealAuthenticationStatusActivity";  //真人认证状态
    public static final String ACTIVITY_MINE_INTEGRAL = "/mine/MineIntegralActivity";  //我的积分
    public static final String ACTIVITY_MINE_INTEGRAL_NEW = "/mine/MineIntegralActivityNew";  //我的积分
    public static final String ACTIVITY_MINE_PHOTO = "/mine/MinePhotoActivity";  //我的相册
    public static final String ACTIVITY_CHARGE_LIST = "/mine/ChargeListActivity";  //金币充值列表
    public static final String ACTIVITY_CHARGE_LIST_NEW = "/mine/ChargeListNewActivity";  //金币充值列表
    public static final String ACTIVITY_CHARGE_PAY = "/mine/ChargePayActivity";  //金币充值列表
    public static final String ACTIVITY_INTEGRAL_DETAILED = "/mine/IntegralDetailedActivity";  //清单
    public final static String MINE_SERVICE = "/mine/mineService";
    public final static String MINE_PAY_SERVICE = "/mine/payService";
    public final static String ACTIVITY_SETTING = "/mine/SettingActivity";
    public final static String ACTIVITY_ACCOST_SETTING = "/mine/AccostSettingActivity";
    public final static String ACTIVITY_ACCOST_SETTING_AUDIO = "/mine/AccostRecordToMyActivity";
    public final static String ACTIVITY_ACCOST_SETTING_PIC = "/mine/AccostPicActivity";
    public final static String ACTIVITY_ACCOST_SETTING_CUSTOMIZE = "/mine/AccostCustomizeActivity";
    public final static String ACTIVITY_VISITOR = "/mine/VisitorActivity";
    public static final String ACTIVITY_VIP = "/mine/VipActivity";  //我的会员
    public static final String ACTIVITY_VIP_DRESS_UP = "/mine/VipDressUpActivity";  //会员装扮
    public final static String ACTIVITY_PRIVACY_SETTING = "/mine/PrivacySettingActivity";   //隐私设置
    public final static String ACTIVITY_VOICESIGN = "/mine/VoiceSignActivity";   //语音签名
    public final static String ACTIVITY_MINE_INFO = "/mine/MineInfoActivity";   //编辑资料
    public static final String ACTIVITY_MY_NOBLE = "/mine/NobleActivity";  //我的贵族
    public static final String ACTIVITY_FACE_CHECK = "/mine/FaceCheckActivity";
    public static final String ACTIVITY_ABOUT_US = "/mine/AboutUsActivity";
    public static final String ACTIVITY_FACIAL_SETTING = "/mine/facial";//美颜设置
    public static final String FRAGMENT_USER_INFO = "/mine/userInfo";   //我的基本信息
    public static final String ACTIVITY_ACCOUNT_MANAGER = "/mine/accountManagerActivity";   //账号管理
    public static final String ACTIVITY_ACCOUNT_BINDING = "/mine/accountBindingActivity";   //账号绑定
    public static final String ACTIVITY_CANCELLATION = "/mine/cancellationActivity";   //注销账户
    public static final String ACTIVITY_CHARGE_SETTING = "/mine/chargeSettingActivity";   //收费设置
    public static final String ACTIVITY_BLACK_LIST = "/mine/BlackListActivity";   //黑名单
    public static final String ACTIVITY_SIGN_CHAT_NUM = "/mine/SignChatNumActivity";   //聊天卡
    public static final String ACTIVITY_NOTIFY_SETTING = "/mine/NotifySettingActivity";   //新消息通知
    public static final String ACTIVITY_INCOME_DETAIL = "/mine/IncomeDetailActivity";   //收益明细
    public static final String ACTIVITY_GIFT_WALL = "/mine/giftWall";   //礼物墙


    //family
    public final static String ACTIVITY_EDIT_FAMILY = "/family/EditFamilyInfoActivity";   //编辑
    public final static String ACTIVITY_FAMILY_MEMBER = "/family/FamilyMemberActivity";   //我的家族成员
    public final static String ACTIVITY_FAMILY_APPLY = "/family/ApplyVerifyActivity";   //申请列表
    public final static String FAMILY_SERVICE = "/family/familyService";
    public final static String ACTIVITY_ROOM_MEMBER = "/family/RoomMemberActivity";   //聊天室成员
    public final static String ACTIVITY_CREATE_FAMILY = "/family/CreateFamilyInfoActivity";   //创建家族
    public final static String ACTIVITY_FAMILY_AIT_MEMBER = "/family/FamilyAitMemberActivity";   //ait我的家族成员
    public final static String ACTIVITY_FAMILY_AIT = "/family/FamilyAitActivity";   //ait我的家族成员
    public final static String ACTIVITY_GROUP_COUPLE = "/family/GroupCoupleActivity";   //组情侣

    public final static String ACTIVITY_SHARE = "/family/ShareActivity";


    //party
    public final static String PARTY_FLOAT_SERVICE = "/party/floatService";
    public final static String ACTIVITY_ROOM_STUDIO = "/party/PartyRoomActivity";   //语音直播间


    //VideoTrimmer
    public final static String ACTIVITY_VIDEOTRIMMER = "/trimmer/VideoTrimmerActivity";   //视频剪辑

    public final static String ACTIVITY_SCAN_MUSIC = "/party/ScanMusicActivity";   //扫描音乐

}
