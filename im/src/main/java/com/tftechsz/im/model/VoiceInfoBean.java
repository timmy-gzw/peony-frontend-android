package com.tftechsz.im.model;

import java.io.Serializable;

/**

 */
public class VoiceInfoBean implements Serializable {

    /**
     * 音频文件名
     */
    private String fileName;
    private String fileUrl;

    /**
     * 语音时长（单位s）
     */
    private int duration;

    protected String sendId;//发送者者id(用户userId或者群groupId)

    /**
     * 消息id
     */
    protected String mMsgId;

    public VoiceInfoBean(){}

    public VoiceInfoBean(String fileName, int duration) {
        this.fileName = fileName;
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDuration() {
        return duration;
    }

    public VoiceInfoBean setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public String getSendId() {
        return sendId;
    }

    public VoiceInfoBean setSendId(String sendId) {
        this.sendId = sendId;
        return this;
    }

    public String getMsgId() {
        return mMsgId;
    }

    public VoiceInfoBean setMsgId(String msgId) {
        mMsgId = msgId;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public VoiceInfoBean setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }
}
