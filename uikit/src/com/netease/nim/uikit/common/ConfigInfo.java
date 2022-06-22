package com.netease.nim.uikit.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class ConfigInfo {

    public Api api;
    public Sys sys;
    public ShareConfig share_config;


    public static class ShareConfig {
        public List<MineInfo> my;
        public List<MineInfo> my_main_nav;
        public HomeTopNav home_top_nav;
        public List<PaymentTypeDto> payment_type;
        public int matching_wait_time;
        public List<HomeTabNav> home_tab_config;
        public int home_user_list_contact_style;   //1 只有私信
        public String shopping_convert_msg;
        public int is_show_search;  //是否显示首页搜索按钮
        public int is_show_pop_notice;   //女性用户是否显示通知
        public int is_detail_style_new;   //女生搭讪和私信显示  0：只显私信  1：搭讪后显示私信，否则显示搭讪  2： 显示喜欢和私信
        public int is_show_vip; //是否显示会员功能
        public String show_vip_desc;
        public int is_handle_accost_yidun;   //是否进行易盾拦截  0:不走易盾  1：走易盾拦截
        public int is_limit_from_channel;   //是否渠道包  1：显示多一个弹窗
        public int is_call_after_join_room;   //1
        public int is_show_net_not_ok;  //是否显示网络不好提示
        public int is_open_behavior_accost_new;//首页图标
        public int is_open_behavior_accost_home;//首页图标显示搭讪还是喜欢
        public int is_open_behavior_accost_user_detail;//个人资料图标显示搭讪还是喜欢
        public int is_open_behavior_accost_blog;//动态图标显示搭讪还是喜欢
        public int is_open_behavior_accost_photo;//相册图标显示搭讪还是喜欢
        public int is_open_behavior_accost_muti_pop;//一键搭讪图标显示搭讪还是喜欢
        public UserDisable user_disable; //用户禁用
        public int is_open_boy_charge;  // 男用户是否开启收费设置 1：开启
        public int is_open_chat_card;  // 是否开启聊天卡
        public int is_girl_open_party_accost;   //0 隐藏 1显示

        public List<String> boy_quick_topic;//男生热聊话题
        public List<String> girl_quick_topic;//女生热聊话题
    }

    public static class UserDisable {
        public String link;
    }

    public static class HomeTabNav {
        public String title;
        public String type;
    }

    public static class HomeTopNav {
        public Nav nav_1;
        public Nav nav_2;
        public Nav nav_3;
        public Nav nav_4;
    }

    public static class Nav {
        public String title_1;
        public String title_2;
        public List<String> img_list;
        public String json;
        public String link;
        public String bg_img;
        public String right_img;
        public int radius;
    }


    public static class CDN {
        public String user;
        public String blog;
        public String photo;
    }

    public static class Oss {
        public CDN cdn;
        public String cdn_scheme;
    }


    public static class Api {
        public WeChat wechat;
        public List<MineInfo> my;
        public List<MineInfo> about_bot;
        public List<MineInfo> my_integral;
        public MineInfo integral_exchange_how_to_real_1;
        public MineInfo integral_exchange_how_to_real_2;
        public List<MineInfo> recharge_bottom;
        public List<MineInfo> vip_recharge;
        public List<MineInfo> noble_recharge;
        public Oss oss;
        public String airdropintd;   //空投协议
    }


    public static class WeChat {
        public String domain;
        public String domain_payment;
        public String appid;
        public String secret;
    }


    public static class MineInfo {
        public String title;
        public String icon;
        public String link;
        public String version;
        public int is_force;
        public String info;
        public int link_type;
        public Option option;
        public int is_remove;
        public String complete_msg;
        public String complete_msg_color;

        public MineInfo() {
        }

        public MineInfo(String link) {
            this.link = link;
        }

        public MineInfo(String link, Option option) {
            this.link = link;
            this.option = option;
        }

    }

    public static class Option implements Parcelable {
        public int is_topbar;    //0不显示
        public String topbar_color;

        public Option(int is_topbar, String topbar_color) {
            this.is_topbar = is_topbar;
            this.topbar_color = topbar_color;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.is_topbar);
            dest.writeString(this.topbar_color);
        }

        public void readFromParcel(Parcel source) {
            this.is_topbar = source.readInt();
            this.topbar_color = source.readString();
        }

        public Option() {
        }

        protected Option(Parcel in) {
            this.is_topbar = in.readInt();
            this.topbar_color = in.readString();
        }

        public static final Parcelable.Creator<Option> CREATOR = new Creator<Option>() {
            @Override
            public Option createFromParcel(Parcel source) {
                return new Option(source);
            }

            @Override
            public Option[] newArray(int size) {
                return new Option[size];
            }
        };
    }


    public static class Sys {
        public User user;
        public String common_domain;
        public String common_api_scheme;
        public String common_domain_2;
        public String common_api_scheme_2;
        public Content content;
        public int is_verified;   // 1 审核通过  0：正在审核
        public int user_protocol;   // 1 开起用户协议前面的勾选
        public int is_show_family;   //0 否  1：是
        public int is_user_intimacy_online;  //是否查询密友在线接口 // 0：否 1：是
        public int boy2girl_pic_intimacy;//'boy2girl_pic_intimacy' => 10, //男发女图要达到 的亲密度
        public Family family;
        public FeedbackContact feedback_contact;
        public List<FeedbackContactNew> feedback_contact_new;
        public List<String> room_id_list;
        public int is_show_im_feedback;  //是否显示芍药在线客服
        public String im_feedback_webview;  //客服地址
        public Im im;
        public int check_order_is_show_recharge;
        public int girl_is_show_sys_accost_slide;   //女生是否显示系统牵线
        public int girl_is_show_sys_accost_slide_party;   //派对女生是否显示系统牵线
        public Icon icon;
        public LoadingH5 loading_h5;
        public DownH5Resource noble_zip;
        public DownH5Resource bubble_zip;
        public Regex regex;
        public String logout_hint;
        public String feedback_title;
        public int is_open_auth;
        public OpenFamilyAdd is_open_family_add;
        public int is_open_family_room_music;  //0-全关 1-主播开启 2-全员开启
        public String vip_pic_host;
        public int is_fix_chat_history;// 接口请求限制 0正常 1才有1秒最多请求一次的限制
        public int is_video_compress; //是否开启视频压缩
        public int video_compress_bitrate; //压缩率 返回值越大  压缩后文件越小
        public int video_compress_filter; //过滤条件 低于 %sM 不压缩
        public int is_sumbmit_error_log;  // 是否上传错误日志到后台  1：上传
        public int yunxin_live_type;  // 0-cdn 1-rtc 2-rtc+cdn
        public int yunxin_live_family_type;  // 0-cdn 1-rtc 2-rtc+cdn
        public String yunxin_live_app_key;  //云信语音房key
        public String withdraw_tips;//提现文案
        public int is_main_tab_lottie_config; //是否读取主页tab的动画配置
    }

    public static class OpenFamilyAdd {
        public int gift_bag;
        public int dice;
        public int fist;
        public int couple;

    }

    public static class Regex {
        public String id_card;
        public String tell;
        public String email;
        public String chinese;
    }


    public static class LoadingH5 implements Serializable {
        public DownH5Resource lovelist;
        public DownH5Resource onhot;
    }


    public static class DownH5Resource {
        public String icon;
        public String zip_source;
        public String url;
    }


    public static class Icon {
        public String ic_tab_home_normal;
        public String ic_tab_home_selector;
        public String ic_tab_trend_normal;
        public String ic_tab_trend_selector;
        public String ic_tab_message_normal;
        public String ic_tab_message_selector;
        public String ic_tab_mine_normal;
        public String ic_tab_mine_selector;
        public String ic_face_off_tip;
    }


    public static class Im {
        public String call_greater_then_intimacy_girl_text;

        public String call_greater_then_intimacy_boy_text;

        public String intimacy_friend_empty_text;

    }


    public static class Content {
        public String picture_warm;
        public String no_face_protect;
        public String before_girl_to_boy_not_real;
        public String before_girl_to_boy_not_self;
        public String before_girl_to_boy_finish;
        public String boy_call_before_text;   //男性用户拨打二次弹窗文案
        public String audio_warn;  //音视频提示
        public String intimacy_marquee;
        public String boy_call_after_text;
        public List<RealIcon> real_icon;
        public List<RealIcon> party_self_icon;
        public String real_warn;
        public String party_self_warn;
    }

    public static class RealIcon {
        public String title;
        public String icon;
    }


    public static class User {   //用户信息
        public String boy_icon;
        public String girl_icon;
        public int intimacy_friend_condition_num;
    }


    public static class Family {
        public MineInfo h5_task;
        public String home_message;
        public String create_message;
        public String create_protocol_h5;
        public String chang_master_desc;
        public List<String> wechat_contact;
        public List<FeedbackContactNew> wechat_contact_new;
        public List<ListCast> list_cate;

    }


    public static class ListCast {

        public String title;
        public String cate;
    }

    public static class FeedbackContact {
        public String wechat;
    }

    public static class FeedbackContactNew {
        public String icon;
        public String msg;
        public String msg_copy;
    }

}
