package com.tftechsz.common.entity;

import java.util.List;

public class NavigationLogEntity {

    public int user_id;
    public int type;//:(1.首页2.动态3.派对4.消息5.我的)
    public long time;
    public String app_system;
    public String app_name;

    // segmentation
    //(1.派对列表banner2.消息页banner3.在线客服4.家族头像5.首页活动6.收费设置-说明7服务协议8用户协议9隐私协议10转盘规则11p派对榜单12派对小时榜13.私聊活动入口14.家族列表活动入口15.语音房活动入口18家族详情-任务19积分任务20实名任务说明21礼物活动横幅点击)
    public int from_type;//2.from_type:(1.点击导航栏2.其他方式进入该列表)
    public List<String> room_list;// room_list(被显示出来的所有房间的room_id)

    // segmentation
    public String room_id;
    public String room_name;
    public String room_img;//派对封面
    public int room_type;//保留字段，房间类型

    // segmentation
    public String content;
    public int draw_nums;

    //banner位点击
    public String banner_link;
    // number(banner的序号)
    public int number;

    // event_link(banner链接)
    public String event_link;

    //(触发场景1.私信2.任务、充值引导弹窗点击充值3.个人主页4.家族5.语音房)
    public int scene;
    //banner 点击场景 -拆分new
    public String sceneStr;

    /**
     * cate : navigation_bar_click
     * scene : 底部导航栏点击
     * cate : turntable_rank_visit
     * scene: 转盘页面曝光
     * scene: gifts_sort_click
     * 礼物栏分类(type -(1.人气2.豪华3.新奇4.贵族5.背包))
     * scene: pop_luck_gift_click
     * 人气分类中的暴击礼物点击 (type-(1.幸运草2.金玫瑰3.棒棒糖))
     * scene: backpack_luck_gift_click
     * 背包分类中的暴击礼物点击
     * scene: outfit_click
     * 我的装扮点击
     * scene: outfit_visit(type-(1.聊天气泡2.头像框3.进场横幅4.座驾5.徽章))
     * 我的装扮点击
     * scene: mynoble_visit(type:(1.骑士2.男爵3.子爵4.伯爵5.侯爵6.公爵))
     * 我的贵族页面曝光
     * scene: mynoble_click(type:(1.我的贵族))
     * 我的页面_我的贵族点击
     * scene: secretary_link_click(type:(1.小秘书链接点击))
     * 新年活动小秘书入口点击
     *
     */
    public NavigationLogEntity(int user_id, int type, long time, String app_system, String app_name) {
        this.user_id = user_id;
        this.type = type;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate : family_red_packet_rain_visit
     * scene : 红包雨开始
     */
    public NavigationLogEntity(int user_id, long time, String app_system, String app_name, long family_id) {
        this.user_id = user_id;
        this.family_id = family_id;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate : party_list_visit
     * scene : 语音房间列表页曝光
     */
    public NavigationLogEntity(int user_id, int from_type, long time, List<String> listRoomId, String app_system, String app_name) {
        this.user_id = user_id;
        this.from_type = from_type;
        this.time = time;
        this.room_list = listRoomId;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate : fans_medal_click
     * scene : 派对_粉丝勋章入口点击  type:(1.粉丝勋章)
     * <p>
     * cate : blind_box_click
     * scene : 派对内盲盒礼物点击(1.惊奇盲盒2.亲密盲盒)
     */
    public NavigationLogEntity(int user_id, int room_id, int type, long time, String app_system, String app_name) {
        this.user_id = user_id;
        this.room_id = room_id + "";
        this.time = time;
        this.type = type;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate : enter_voice_room_click
     * scene : 点击进入房间-派对列表点击进入房间
     * cate : voice_room_visit
     * scene : 房间曝光-语音房间曝光
     * cate : voice_room_msg_list_visit
     * scene : 语音房私信页面曝光
     * cate : voice_room_online_list_visit
     * scene : 语音房在线列表曝光
     */
    public NavigationLogEntity(int user_id, String room_id, long time, String room_name, String room_img, int room_type, String app_system, String app_name) {
        this.user_id = user_id;
        this.room_id = room_id;
        this.time = time;
        this.room_name = room_name;
        this.room_img = room_img;
        this.room_type = room_type;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate : voice_room_upper_click
     * scene : 点击上麦
     */
    public NavigationLogEntity(int user_id, String room_id, long time, String room_name, String room_img, int room_type, String app_system, String app_name, int fromType) {
        this.user_id = user_id;
        this.room_id = room_id;
        this.time = time;
        this.room_name = room_name;
        this.room_img = room_img;
        this.room_type = room_type;
        this.app_system = app_system;
        this.app_name = app_name;
        this.from_type = fromType;
    }

    /**
     * cate : room_notice_visit
     * scene : 房间公告曝光
     */
    public NavigationLogEntity(int user_id, String room_id, long time, String room_name, String room_img, int room_type, String app_system, String app_name, int fromType, String content) {
        this.user_id = user_id;
        this.content = content;
        this.room_id = room_id;
        this.time = time;
        this.room_name = room_name;
        this.room_img = room_img;
        this.room_type = room_type;
        this.app_system = app_system;
        this.app_name = app_name;
        this.from_type = fromType;
    }

    /**
     * cate : turntable_entrance_click
     * scene: 转盘入口点击
     */
    public NavigationLogEntity(int user_id, int fromType, long time, String app_system, String app_name, String room_id) {
        this.user_id = user_id;
        this.room_id = room_id;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
        this.from_type = fromType;
    }


    /**
     * cate : turntable_rank_visit
     * scene: 转盘页面曝光
     */

    public NavigationLogEntity(int user_id, int fromType, long time, String app_system, String app_name, int type) {
        this.user_id = user_id;
        this.type = type;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
        this.from_type = fromType;
    }


    /**
     * cate : turntable_draw_click
     * scene: 抽奖按钮点击
     */
    public NavigationLogEntity(int user_id, int fromType, long time, String app_system, String app_name, int type, int draw_nums) {
        this.user_id = user_id;
        this.type = type;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
        this.from_type = fromType;
        this.draw_nums = draw_nums;
    }


    /**
     * cate : turntable_rank_visit
     * scene : 转盘榜单曝光
     * cate : turntable_draw_record_visit
     * scene: 抽奖记录页面曝光
     */
    public NavigationLogEntity(int user_id, long time, String app_system, String app_name, int from_type) {
        this.user_id = user_id;
        this.from_type = from_type;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    public String to_user_id;
    public long family_id;
    //上麦多个人
    public List<Integer> listUserIds;

    /**
     * cate : gift_send_popup_visit
     * scene : 送礼弹窗页面曝光
     * 多人
     */
    public NavigationLogEntity(int user_id, int from_type, long time, int type, String app_system, String app_name, String to_user_id, String room_id, long family_id, List<Integer> listUserIds) {
        this.user_id = user_id;
        this.from_type = from_type;
        this.time = time;
        this.type = type;
        this.app_system = app_system;
        this.room_id = room_id;
        this.app_name = app_name;
        this.to_user_id = to_user_id;
        this.listUserIds = listUserIds;
        this.family_id = family_id;
    }

    /**
     * cate : gift_send_popup_visit
     * scene : 送礼弹窗页面曝光
     * 单人
     */
    public NavigationLogEntity(int user_id, int from_type, long time, int type, String app_system, String app_name, String to_user_id, String room_id, long family_id) {
        this.user_id = user_id;
        this.from_type = from_type;
        this.time = time;
        this.type = type;
        this.app_system = app_system;
        this.room_id = room_id;
        this.app_name = app_name;
        this.to_user_id = to_user_id;
        this.family_id = family_id;
    }

    /**
     * cate :   event_entrance_click
     * scene :  站内活动入口点击
     * 单人
     * cate: new_year_banner_click(from_type:(23.消息页banner 24.家族广场banner 25.派对banner))
     * scene : 新年活动banner点击
     * <p>
     * cate: party_patch_click  from_type(26派对内新年活动贴片点击)
     * scene : 派对内新年活动贴片点击
     * <p>
     * cate: family_patch_click
     * scene:家族内新年活动贴片点击from_type(27.家族内新年活动贴片)
     */
    public NavigationLogEntity(int user_id, String event_link, int from_type, int number, long time, String app_system, String app_name, String sceneStr, String roomId, int family_id) {
        this.user_id = user_id;
        this.event_link = event_link;
        this.from_type = from_type;
        this.number = number;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
        this.sceneStr = sceneStr;
        this.room_id = roomId;
        this.family_id = family_id;
    }


    /**
     * cate :  banner_h5_click
     * scene : banner位点击
     * 单人
     */
    public NavigationLogEntity(int user_id, int from_type, String banner_link, int number, long time, String app_system, String app_name) {
        this.user_id = user_id;
        this.banner_link = banner_link;
        this.from_type = from_type;
        this.number = number;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate :  accost_click
     * scene : 搭讪按钮点击事件
     */
    public NavigationLogEntity(String app_system, String app_name, int user_id, String to_user_id, int type, int from_type, long time) {
        this.user_id = user_id;
        this.from_type = from_type;
        this.to_user_id = to_user_id;
        this.type = type;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
    }

    /**
     * cate :  金币充值拦截弹窗曝光
     * scene : coin_recharge_intercept_popup_visit
     * 1.user_id
     * 2.from_type(1.发送消息2.送礼物3.音视频通话4.转盘抽奖)
     * 3.scene(触发场景1.私信2.任务、充值引导弹窗点击充值3.个人主页4.家族5.语音房)
     * 4.to_user_id
     * 5.room_id
     * 6.family_id
     * 7.time
     * 8.app_system
     * 9.app_name
     */
    public NavigationLogEntity(int user_id, int from_type, int scene, String to_user_id, int room_id, long family_id, long time, String app_system, String app_name) {
        this.user_id = user_id;
        this.from_type = from_type;
        this.scene = scene;
        this.to_user_id = to_user_id;
        this.room_id = room_id + "";
        this.family_id = family_id;
        this.time = time;
        this.app_system = app_system;
        this.app_name = app_name;
    }

}
