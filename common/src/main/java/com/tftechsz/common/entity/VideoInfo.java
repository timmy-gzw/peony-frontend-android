package com.tftechsz.common.entity;

import java.io.Serializable;

/**
 * 包 名 : com.tftechsz.common.entity
 * 描 述 : TODO
 */
public class VideoInfo implements Serializable {
    private String video_url;
    private boolean video_local;

    public VideoInfo(String video_url) {
        this.video_url = video_url;
    }

    public VideoInfo(String video_url, boolean video_local) {
        this.video_url = video_url;
        this.video_local = video_local;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public boolean isVideo_local() {
        return video_local;
    }

    public void setVideo_local(boolean video_local) {
        this.video_local = video_local;
    }
}
