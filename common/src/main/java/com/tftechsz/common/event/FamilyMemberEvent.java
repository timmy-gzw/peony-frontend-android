package com.tftechsz.common.event;

public class FamilyMemberEvent {
    public int userId;
    public String name;
    public String icon;

    public FamilyMemberEvent(int userId, String name, String icon) {
        this.userId = userId;
        this.name = name;
        this.icon = icon;
    }

}
