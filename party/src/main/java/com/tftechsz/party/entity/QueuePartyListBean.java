package com.tftechsz.party.entity;

import java.util.List;

public class QueuePartyListBean {

    public List<QueueInnerBean> queue;

    public int count;

    public static class QueueInnerBean {
        public int user_id;
        public String nickname;
        public int sex;
        public String icon;
        public String age;
        public int id;
        public int is_vip;
    }
}

