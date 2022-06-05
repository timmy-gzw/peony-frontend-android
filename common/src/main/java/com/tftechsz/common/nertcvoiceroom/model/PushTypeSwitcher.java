package com.tftechsz.common.nertcvoiceroom.model;

/**
 * Created by luc on 1/19/21.
 */
public interface PushTypeSwitcher {
    void toCDN(String url);

    void toRTC(VoiceRoomInfo roomInfo, long uid);
}
