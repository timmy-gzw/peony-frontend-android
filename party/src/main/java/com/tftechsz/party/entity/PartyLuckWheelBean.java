package com.tftechsz.party.entity;

import java.util.List;

public class PartyLuckWheelBean {
    public List<List<CostMapDTO>> cost_map;
    public List<List<GiftListDTO>> gift_list;
    public String rule_url;//规则链接
    public List<String> reward_list;//通知列表

    public List<RechargeMap> recharge_map;

    public static class RechargeMap {
        public String buy_image;
        public String give_image;
        public int cost;
    }

    public static class CostMapDTO {
        public String desc;
        public String type;
        public String cost;
        public int number;
    }

    public static class GiftListDTO {
        public String image;
        public String name;
        public String coin;
        public int id;
    }
}
