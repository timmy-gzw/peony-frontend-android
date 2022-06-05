package com.tftechsz.im.model;

/**
 * 缘分牵线信息内容
 */
public class FateContentInfo {

    private String url;
    private String ext;
    private String content;
    private long size;
    private long dur;
    private int w;
    private int h;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDur() {
        return dur;
    }

    public void setDur(long dur) {
        this.dur = dur;
    }
}
