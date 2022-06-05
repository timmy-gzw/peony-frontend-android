package com.tftechsz.mine.entity.dto;

import java.util.List;

public class ChargeItemDto {

    public List<ChargeItem> msg;
    public List<ChargeItem> voice;
    public List<ChargeItem> video;
    public static class ChargeItem {
        public int coin;    //消耗金币
        public String name;   // //标题
        public int level;   //需要的等级
    }


}
