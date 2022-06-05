package com.tftechsz.im.model.dto;

public class JoinLeaveRoom {

    public ToUser to_user;
    public int is_black;  // 1=拉黑;0=正常;
    public String chat_bg; //聊天背景

    public static class ToUser {

        public int status; // 1=正常;2=禁用;3=禁言;4=注销;5=禁用;

        public boolean isDisable() {
            return status == 2 || status == 5;
        }

        public boolean isLogout() {
            return status == 4;
        }
    }
}
