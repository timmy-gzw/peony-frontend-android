package com.tftechsz.party.entity;

import java.util.List;

public class PartyManagerListBean {
    public RoomManagerDTO room_manager;
    public BlackListDTO black_list;
    public KickListDTO kick_list;
    public MuteListDTO mute_list;

    public static class ListDTO {
        public Integer id;
        public String limit_deadline;
        public Integer sex;
        public String nickname;
        public String icon;
        public String age;
        public String op_nickname;
        public String birthday;
        public int user_id;
        public String op_ident;
    }
    public static class RoomManagerDTO {
        public Integer action_type;
        public List<ListDTO> list;
    }

    public static class BlackListDTO {
        public Integer action_type;
        public List<ListDTO> list;
    }

    public static class KickListDTO {
        public Integer action_type;
        public List<ListDTO> list;
    }

    public static class MuteListDTO {
        public Integer action_type;
        public List<ListDTO> list;
    }
 }
