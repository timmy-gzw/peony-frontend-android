package com.tftechsz.im.model.event;


import com.tftechsz.im.model.ContactInfo;

import java.util.List;

public class MessageListEvent {
    public List<ContactInfo> contactList;

    public MessageListEvent(List<ContactInfo> contactList) {
        this.contactList = contactList;
    }
}
