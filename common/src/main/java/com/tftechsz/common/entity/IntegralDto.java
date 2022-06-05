package com.tftechsz.common.entity;

import java.io.Serializable;

public class IntegralDto implements Serializable {

    public String integral; ////剩余积分
    public String note_value; //剩余音符值
    public String rmb; // //可以兑换的rmb
    public String coin; //
    public String free_coin; //绑定金币
    public String free_coin_desc; //提示绑定金币
    public NoteRules note_rules; //音符协议
    public String note_problem; //音符问题

    public static class NoteRules implements Serializable {
        public String title;   //音符兑换金币说明：
        public String content;
        public int minimum_exchange; //最小交换
        public int maximum_exchange; //最大交换
    }
}
