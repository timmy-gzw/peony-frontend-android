package com.tftechsz.party.entity;

import java.util.List;

public class PartyRankBean {
    public List<ListDTO> list;
    public MyDTO my;

    public static class MyDTO {
        public int user_id;
        public String icon;
        public String nickname;
        public String index;
        public List<GiftListDTO> gift_list;

        public static class GiftListDTO {
            public String gift_image;
            public String num;
        }
    }

    public static class ListDTO {
        public int user_id;
        public String icon;
        public String nickname;
        public String index;
        public List<GiftListDTO> gift_list;

        public static class GiftListDTO {
            public String gift_image;
            public String num;
        }
    }
}
