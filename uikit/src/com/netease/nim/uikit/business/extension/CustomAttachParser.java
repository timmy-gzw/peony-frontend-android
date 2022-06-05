package com.netease.nim.uikit.business.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "body";

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            int type = object.getInteger(KEY_TYPE);
            JSONObject data = object.getJSONObject(KEY_DATA);
            switch (type) {
                case CustomAttachmentType.text:
                    return new DefaultCustomAttachment(json);
                case CustomAttachmentType.tips:
                    return new TipCustomAttachment(json);
                case CustomAttachmentType.accost:
                    attachment = new AccostChatAttachment(10);
                    break;
                default:
            }
            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {

        }
        return attachment;
    }

    public static String packData(int type, String data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }

        return object.toJSONString();
    }
}
