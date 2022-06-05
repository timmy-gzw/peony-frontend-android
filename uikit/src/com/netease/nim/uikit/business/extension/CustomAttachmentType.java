package com.netease.nim.uikit.business.extension;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public interface CustomAttachmentType {
    // 多端统一
    int text = 1;//文本
    int pic = 2;//图片
    int smile = 3;//发送消息表情
    int voice_file = 4;//语音
    int video_file = 5; //视频文件
    int voice_call = 6;  // 语音呼叫
    int video_call = 7;//视频呼叫
    int tips = 8;//私聊界面,聊天室的tips
    int alert = 9;//弹窗转发
    int accost = 10;//搭讪
    int enter = 11;//进入聊天页面消息
    int gift = 12;//礼物
    int card = 13;//卡片信息
}
