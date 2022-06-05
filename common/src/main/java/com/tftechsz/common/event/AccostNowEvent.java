package com.tftechsz.common.event;

public class AccostNowEvent {
    public String pic;
    public String title;
    public String desc;
    public long timestamp;

    public AccostNowEvent(String pic, String title, String desc, long timestamp) {
        this.pic = pic;
        this.title = title;
        this.desc = desc;
        this.timestamp = timestamp;
    }
}
