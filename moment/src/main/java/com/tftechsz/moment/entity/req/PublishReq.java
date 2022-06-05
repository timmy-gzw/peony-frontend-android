package com.tftechsz.moment.entity.req;


import java.util.List;

/**
 * {
 * "content": "你好",    //动态内容文字
 * "media": "http://www.baidu.com",  //音频、视频url
 * "type": 1,  //1-视频 2-音频 3-图片
 * }
 */
public class PublishReq {
    private String content;
    private String media;
    private String type;
    private List<Integer> video_size;

    @Override
    public String toString() {
        return "发布信息是{" +
                "content='" + content + '\'' +
                ", media='" + media + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public List<Integer> getVideoSize() {
        return video_size;
    }

    public void setVideoSize(List<Integer> videoSize) {
        this.video_size = videoSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
