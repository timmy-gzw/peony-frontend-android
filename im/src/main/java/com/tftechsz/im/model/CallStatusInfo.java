package com.tftechsz.im.model;

public class CallStatusInfo {
    public int talk_status;    //1 正常 包括大于一分钟  或者通话剩余不足54秒 客户端无需处理  2 不足1分钟  需要弹窗提示余额不足  3 余额不足直接挂断
    public int total_second;  //总共可以拨打时长
    public boolean is_pop_recharge;
}
