package com.netease.nim.uikit.business.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class AccostChatAttachment extends CustomAttachment {

    private String content;//  消息文本内容
    private String msg;
    private String cmd;
    private String cmd_type;
    private String from;
    private String to;
    private String body;
    private static final String KEY_MSG = "msg";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_CMD_TYPE = "cmd_type";
    private static final String KEY_TO = "to";
    private static final String KEY_FROM = "from";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_BODY = "body";

    public AccostChatAttachment(int type) {
        super(CustomAttachmentType.accost);
    }

    @Override
    protected void parseData(JSONObject data) {
        cmd = data.getString(KEY_CMD);
        content = data.getString(KEY_CONTENT);
        cmd_type = data.getString(KEY_CMD_TYPE);
        to = data.getString(KEY_TO);
        from = data.getString(KEY_FROM);
        msg = data.getString(KEY_MSG);
        body = data.getString(KEY_BODY);
    }

    @Override
    protected String packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_CMD_TYPE, cmd_type);
        data.put(KEY_CMD, cmd);
        data.put(KEY_TO, to);
        data.put(KEY_FROM, from);
        data.put(KEY_MSG, msg);
        data.put(KEY_CONTENT, content);
        data.put(KEY_BODY, body);
        return body;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd_type() {
        return cmd_type;
    }

    public void setCmd_type(String cmd_type) {
        this.cmd_type = cmd_type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
