package com.tftechsz.common.entity;

public class CoupleRelieveApplyInfo {
    /**
     *    "from_nickname": "善良的大白",
     *         "to_nickname": "聊聊",
     *         "from_icon": "http://user-cdn1.peony125.com/default-boy.png",
     *         "to_icon": "http://user-cdn1.peony125.com/user/avatar/f6e2736ba88553c2c2bbfab7d3d16ea8.jpeg",
     *         "from_sex": 1,
     *         "to_sex": 2,
     *         "couple_rule_desc": "1.点击我同意后，你们将解除情侣关系；\n2.你们的甜蜜值、情侣榜数据都会被清除；\n3.点击我同意后，会立马解除并且无法恢复哦，请考虑清楚后决定！"
     */
    public String from_nickname;
    public String to_nickname;
    public String from_icon;
    public String to_icon;
    public int from_sex;
    public int to_sex;
    public String couple_rule_desc;
}
