package com.netease.nim.uikit.business.session.viewholder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.Base64;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgTipViewHolder extends MsgViewHolderText {

    public MsgTipViewHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected String getDisplayText() {
        JSONObject jsonObject = JSONObject.parseObject(message.getAttachStr());
        String data = new String(Base64.decodeBase64(jsonObject.getString("body").getBytes()));
        ChatMsg chatMsg = JSON.parseObject(data, ChatMsg.class);
        return chatMsg.content;
    }
}
