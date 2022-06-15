package com.tftechsz.common.event;

/**
 * 包 名 : com.tftechsz.moment.other
 * 描 述 : TODO
 */
public class AccostSuccessEvent {

    private final int eventFrom;
    private final String toUserId;
    private final String toUsername;
    private final String toUserAvatar;

    /**
     * @param eventFrom 首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
     */
    public AccostSuccessEvent(int eventFrom, String toUserId, String toUsername, String toUserAvatar) {
        this.eventFrom = eventFrom;
        this.toUserId = toUserId;
        this.toUsername = toUsername;
        this.toUserAvatar = toUserAvatar;
    }

    public int getEventFrom() {
        return eventFrom;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getToUsername() {
        return toUsername;
    }

    public String getToUserAvatar() {
        return toUserAvatar;
    }
}
