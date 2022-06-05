package com.tftechsz.im.model.dto;

import java.util.List;

public class GroupCoupleDto {

    public int user_id;
    public boolean is_couple; // 是否组情侣
    public boolean is_couple_to_me;// 是否与自己组情侣
    public String icon; // 头像
    public String nickname;
    public String couple_icon; // 组情侣头像
    public String coin;
    public String couple_sweet_to_me; // 与自己的甜蜜值
    public String couple_nickname; // 组情侣昵称
    public String couple_sweet; // 组情侣甜蜜值
    public String couple_day;
    public String desc;
    public int sex;  //1:男 2 ：女
    public int couple_sex;
    public String couple_rule_desc;//说明文案
    public List<CoupleTask> couple_task;//情侣任务

    public static class CoupleTask {
        /**
         * [id] => 3
         * [title] => 甜蜜升级
         * [tips] => 跟情侣的甜蜜值达到20W时, 双方可获得情侣聊天气泡(7天)
         * [status] => 0
         */
        public int id;
        public String title;
        public String tips;
        public int status;//status: 0/1/2 进行中、领取、已完成
        public String icon;
    }

}
