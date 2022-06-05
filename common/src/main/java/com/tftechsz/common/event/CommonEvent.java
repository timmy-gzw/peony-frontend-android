package com.tftechsz.common.event;

import com.tftechsz.common.entity.PartyInfoDto;

public class CommonEvent {


    public int type;
    public String code;
    public int familyId;
    //派对权限列表
    public PartyInfoDto partyInfoDto;

    public CommonEvent(int type) {
        this.type = type;
    }

    public CommonEvent(int type, String code) {
        this.type = type;
        this.code = code;
    }

    public CommonEvent(int type, int familyId) {
        this.type = type;
        this.familyId = familyId;
    }

    public CommonEvent(int type, int familyId, PartyInfoDto partyInfoDto) {
        this.type = type;
        this.familyId = familyId;
        this.partyInfoDto = partyInfoDto;
    }
}
