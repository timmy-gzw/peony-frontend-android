package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class AccostSettingListBean {
    public int id;
    public String text; //文本
    public String url; // 图片 语音 地址
    public String title; //语音-标题
    public int time;//语音-时长
    public int is_show;   //是否审核中  0：审核中  1：正常

    public AccostSettingListBean(String text) {// 文本
        this.text = text;
    }

    public AccostSettingListBean(String url, String title, int time) {//语音
        this.url = url;
        this.title = title;
        this.time = time;
    }

    public AccostSettingListBean(String url, String title) { //图片
        this.url = url;
    }
}
