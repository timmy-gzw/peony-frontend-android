package com.tftechsz.party.entity;

import java.util.List;

public class PartyTurntableBean {
    public String number;
    public String type;
    public List<RewardDTO> reward;
    public String created_at;

    public static class RewardDTO {
        public String gift_image;
        public Integer num;
    }
}
