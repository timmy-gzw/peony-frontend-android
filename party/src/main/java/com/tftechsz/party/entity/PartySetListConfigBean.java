package com.tftechsz.party.entity;

import java.util.List;

public class PartySetListConfigBean {
    public List<KickDTO> kick;
    public List<KickDTO> mute;

    public static class KickDTO {
        public int type;
        public String desc;
        public boolean isCheck;
    }

}
