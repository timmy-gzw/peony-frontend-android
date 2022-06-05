package com.netease.nim.uikit.business.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhoujianghua on 2015/4/10.
 */
public class TipCustomAttachment extends CustomAttachment {

    private String content;

    public TipCustomAttachment(String content) {
        super(CustomAttachmentType.text);
        this.content = content;
    }


    @Override
    protected void parseData(JSONObject data) {
        content = data.toJSONString();
    }

    @Override
    protected String packData() {
        JSONObject data = null;
        try {
            data = JSONObject.parseObject(content);
        } catch (Exception e) {

        }
        return getContent();
    }

    public String getContent() {
        return content;
    }
}
