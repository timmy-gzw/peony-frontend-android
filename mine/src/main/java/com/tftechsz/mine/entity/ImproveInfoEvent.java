package com.tftechsz.mine.entity;

public
/**
 *  包 名 : com.tftechsz.mine.entity

 *  描 述 : TODO
 */
class ImproveInfoEvent {
    public int eventType;//0 男生女生点击切换   1 键盘弹出关闭   2 配置信息
    public int clickPos;
    public boolean isPopup;
    public String icon; //头像
    public String nickname;  //用户昵称
    public String birthday;  //用户出生日期

    public ImproveInfoEvent(String icon, String nickname, String birthday) {
        eventType = 2;
        this.icon = icon;
        this.nickname = nickname;
        this.birthday = birthday;
    }

    public ImproveInfoEvent(boolean isPopup) {
        eventType = 1;
        this.isPopup = isPopup;
    }

    public ImproveInfoEvent(int clickPos) {
        this.clickPos = clickPos;
    }
}
