package com.tftechsz.common.entity;

import java.io.Serializable;

/**
 * 包 名 : com.tftechsz.common.entity
 * 描 述 : TODO
 */
public class ShareBean {

    public String type;
    public String icon;
    public String title;
    public SendDTO send;

    public static class SendDTO implements Serializable {
        public int id;
        public String image_url;
        public String target_url;
        public String title;
        public String content;
        public String invite_intimacy_title; //邀请用户
        public String invite_intimacy_message; //只能邀请亲密度大于3℃的用户哦
        public int invite_intimacy_limit; //3

    }
}
