package com.tftechsz.common.nertcvoiceroom.model;

import java.io.Serializable;

/**
 * Created by luc on 1/18/21.
 * <p>
 * 主播进行 CDN 模式下，推拉流配置信息
 */
public class StreamConfig implements Serializable {

    /**
     * 主播推流地址
     */
    public String push_url;

    /**
     * 观众拉流地址（下面三个都是拉流地址，用户可按需求选择使用对应拉流协议）
     */
    public String http_pull_url;

    public String hls_pull_url;

    public String rtmp_pull_url;

    public String rts_pull_url;



    public StreamConfig(String pushUrl, String httpPullUrl, String rtmpPullUrl, String hlsPullUrl) {
        this.push_url = pushUrl;
        this.http_pull_url = httpPullUrl;
        this.rtmp_pull_url = rtmpPullUrl;
        this.hls_pull_url = hlsPullUrl;
    }
}
