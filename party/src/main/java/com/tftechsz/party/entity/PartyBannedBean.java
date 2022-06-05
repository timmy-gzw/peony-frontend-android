package com.tftechsz.party.entity;

import java.util.List;

public class PartyBannedBean {
    public List<MuteDTO> kick;
    public List<MuteDTO> mute;


    public static class MuteDTO {
        public Integer type;
        public String desc;
        public boolean isCheck;
    }
}
