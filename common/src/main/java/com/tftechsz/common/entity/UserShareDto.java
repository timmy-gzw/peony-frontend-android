package com.tftechsz.common.entity;

import java.util.List;

/**
 * 包 名 : com.tftechsz.family.entity.dto
 * 描 述 : TODO
 */
public class UserShareDto {

    public List<ListDTO> list;
    public SendDTO send;

    public static class SendDTO {
        public int id;
        public String image_url;
        public String content;
        public String invite_intimacy_title;
        public String invite_intimacy_message;
        public int invite_intimacy_limit;
    }

    public static class ListDTO {
        public int user_id;
        public int sex;
        public String icon;
        public String nickname;
        public String age;
        public double intimacy_value;
        public String intimacy_message;
    }
}
