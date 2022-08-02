package com.tftechsz.home.entity;

import com.netease.nim.uikit.common.ConfigInfo;

/**
 * 包 名 : com.tftechsz.home.entity
 * 描 述 : 签到成功
 */
public class SignInSuccessBean {
    private String title;//恭喜您获得
    private String cost;//1金币
    private String desc;//已连续签到1天
    private LinkBean link;

    public String getTitle() {
        return title;
    }

    public String getCost() {
        return cost;
    }

    public String getDesc() {
        return desc;
    }

    public LinkBean getLinkBean() {
        return link;
    }

    public static class LinkBean {
        private String link;
        private ConfigInfo.Option options;

        public String getLink() {
            return link;
        }

        public ConfigInfo.Option getOption() {
            return options;
        }
    }
}
