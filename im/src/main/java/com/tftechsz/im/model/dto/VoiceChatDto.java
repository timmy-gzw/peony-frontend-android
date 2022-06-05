package com.tftechsz.im.model.dto;

import com.tftechsz.common.nertcvoiceroom.model.StreamConfig;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;

import java.util.List;

public class VoiceChatDto {
    public int room_status;
    public String room_id;
    public String room_name = "";
    public String room_token = "";
    public String name;
    public int apply_num;  //申请审核
    public String announcement;  //公告
    public String rtmp_pull_url;
    public List<VoiceRoomSeat> microphone;
    public VoiceRoomSeat microphone_host;
    public StreamConfig stream_config;
    //  1-审核中 0-正常
    public int checkStatus;
    //审核中的 text 房间名称
    public String name_audit;
    //审核中的 text  公告内容
    public String announcement_audit;

}
