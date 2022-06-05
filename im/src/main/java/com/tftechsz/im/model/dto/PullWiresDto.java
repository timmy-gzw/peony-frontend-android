package com.tftechsz.im.model.dto;

import java.io.Serializable;

public class PullWiresDto implements Serializable {

    public int msg_type;
    public long created_at;
    public long to_user_id;
    public String to_user_icon;
    public String to_user_nickname;
    public String msg_content;
}
