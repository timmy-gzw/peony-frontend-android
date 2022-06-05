package com.tftechsz.home.entity;

import java.util.List;

/**
 * 包 名 : com.tftechsz.home.entity
 * 描 述 : TODO
 */
public class InfoPictureBean {


    /**
     * picture : ["/user/photo/be508ede42d47918e03c00e3265d53e5.jpeg","/user/photo/7a6f2e74336dcc36f0bbeb2feb9b775f.jpeg","/user/photo/cd4bdfee24af7bff4981e7240dfc0ff7.jpeg","/user/photo/5e2dbaf5adc12382ddba51b419324f78.jpeg","/user/photo/26172ba182fbbd2e782dc09441eb2bc4.jpeg","/user/photo/4edeb4ba51c91fe5627c84114c2733fb.jpeg","/user/photo/34b8382826805fec3ac3b2732c2742f6.jpeg"]
     * user_id : 107
     * nickname : 嘿嘿嘿
     * is_real : 1
     * is_accost : 0
     * is_praise_picture : 0
     */

    private int user_id;
    private String icon;
    private String nickname;
    private int is_real; // 是否真人认证 0:未认证，1:认证
    private int is_accost;// 是否搭讪过 0:未搭讪，1:搭讪
    private int is_praise_picture; // 是否点赞 0:未点赞，1:点赞
    private int praise_picture_count; // 点赞数量
    public int is_vip;
    private List<String> picture;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getPraise_picture_count() {
        return praise_picture_count;
    }

    public void setPraise_picture_count(int praise_picture_count) {
        this.praise_picture_count = praise_picture_count;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getIs_real() {
        return is_real;
    }

    public boolean isReal() {
        return is_real == 1;
    }

    public void setIs_real(int is_real) {
        this.is_real = is_real;
    }

    public int getIs_accost() {
        return is_accost;
    }

    public boolean isAccost() { //是否搭讪过
        return is_accost == 1;
    }

    public void setIs_accost(int is_accost) {
        this.is_accost = is_accost;
    }

    public int getIs_praise_picture() {
        return is_praise_picture;
    }

    public void setIs_praise_picture(int is_praise_picture) {
        this.is_praise_picture = is_praise_picture;
    }

    public List<String> getPicture() {
        return picture;
    }

    public void setPicture(List<String> picture) {
        this.picture = picture;
    }
}
