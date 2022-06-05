package com.tftechsz.mine.entity.dto;

/**
 * 包 名 : com.tftechsz.mine.entity.dto
 * 描 述 : TODO
 */
public class MinePhotoDto {
    private String url;
    private int is_show;

    public MinePhotoDto(String url, int is_show) {
        this.url = url;
        this.is_show = is_show;
    }

    public boolean isShow() {
        return getIs_show() == 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIs_show() {
        return is_show;
    }

    public void setIs_show(int is_show) {
        this.is_show = is_show;
    }
}
