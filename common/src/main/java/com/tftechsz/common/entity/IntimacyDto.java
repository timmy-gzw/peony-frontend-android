package com.tftechsz.common.entity;

import java.util.List;

public class IntimacyDto {
    public String intimacy;   //:11.4,
    public String content;   //:"每消耗1金币，亲密度增加0.8℃",
    public int is_intimacy;   //:1,
    public String message_intimacy;   //:"1.5℃解锁语音和视频通话",
    public int is_friend;   //:1,
    public String message_friend;   //:"互相关注，发消息免费"
    public String message_title;

    /**
     * "intimacy_config":[
     * {
     * "min":1889,
     * "max":5200,
     * "title":"情深意切",
     * "type":1,
     * "ImTips":"\"甜蜜蜜\"聊天气泡7天",
     * "day":7,
     * "tips":"亲密度5200°C, 获得\"甜蜜蜜\"聊天气泡7天"
     * },
     * {
     * },
     * ],
     * "level":
     */
    public List<IntimacyConfigDTO> intimacy_config;
    public Integer level;

    public static class IntimacyConfigDTO {
        public Double min;
        public Double max;
        public String title;
        public String tips;
        public Integer type;
        public String imTips;
        public Integer day;
        public Integer status;
        public Integer level;
    }


}
