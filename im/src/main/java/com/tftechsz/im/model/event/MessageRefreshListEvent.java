package com.tftechsz.im.model.event;


import com.tftechsz.im.model.dto.MessageIntimacyDto;

import java.util.List;

/**
 * 刷新亲密度更新
 */
public class MessageRefreshListEvent {
    public List<MessageIntimacyDto> contactList;

    public MessageRefreshListEvent(List<MessageIntimacyDto> contactList) {
        this.contactList = contactList;
    }
}
