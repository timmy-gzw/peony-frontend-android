package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class DressUpBean {

    /**
     * id : 1
     * url : http://public-cdn.peony125.com/vip/peony_vip_txkv_img@3x.png?x-oss-process=image/resize,m_lfit,h_200,w_200
     * type : 1
     * title : VIP专属头像
     */

    private int id;
    private String url;
    private int type; //1-头像框 2-聊天气泡
    private String title;
    private int is_active;
    private String svg_name;
    private String svg_link;

    public String getSvg_name() {
        return svg_name;
    }

    public void setSvg_name(String svg_name) {
        this.svg_name = svg_name;
    }

    public String getSvg_link() {
        return svg_link;
    }

    public void setSvg_link(String svg_link) {
        this.svg_link = svg_link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

    }

    public boolean isActive() {
        return is_active == 1;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }
}
