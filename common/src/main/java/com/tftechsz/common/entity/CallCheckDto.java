package com.tftechsz.common.entity;

public class CallCheckDto {
    public String tips_msg;
    public boolean is_real_alert; //真人
    public boolean is_self_alert; //实名
    public CallList list;
    public Error error;

    public static class CallList {
        public CallTpe video;
        public CallTpe voice;
    }


    public static class CallTpe {
        public String title;
        public boolean is_lock;
    }


    public static class Error {
        public ErrorContent intimacy;
        public ErrorContent voice;
        public ErrorContent video;

    }

    public static class ErrorContent {
        public String msg;
        public String flag;
        public String cmd_type;  //direct_recharge
    }

}
