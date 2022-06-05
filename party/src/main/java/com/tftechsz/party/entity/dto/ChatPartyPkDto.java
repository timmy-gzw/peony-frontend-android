
package com.tftechsz.party.entity.dto;


import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;

import java.util.List;

/**
 * pk
 */
public class ChatPartyPkDto {

    public List<VoiceRoomSeat> pk_users;
    public List<Integer> manager;  //管理员
    public int pk_info_id;

}
