package com.tftechsz.moment.mvp.entity;

public
/**
 *  包 名 : com.tftechsz.moment.mvp.entity

 *  描 述 : TODO
 */
class NoticeBean {

    /**
     * blog_id : 49
     * user_id : 268
     * from_type : reply-comment
     * content : 哈哈哈哈😂
     * icon : http://user-cdn.peony125.com/user/avatar/b80dbdb86eb2f448f3ec2339379abf1d.jpeg
     * nickname : 嘻嘻
     * image : http://blog-cdn.peony125.com/blog/blog/8071f27f4d4c4375adc90923aaa046ce.jpeg?x-oss-process=image/resize,m_lfit,h_800,w_800&x-oss-process=image/resize,m_lfit,h_800,w_800
     * created_at : 23分钟前
     */

    private int blog_id;
    private int user_id;
    private String from_type; // 通知类型：reply-comment=回复，comment=评论，praise=点赞
    private int status;// 1:正常，0:删除
    private int is_blog; // 是否存在动态：1.存在，0.不存在
    private int from_id;
    private String content;// 通知内容
    private String icon;
    private String nickname;
    private String image;
    private String created_at;
    public int picture_frame;
    private int is_vip;

    public boolean isVip() {
        return getIs_vip() == 1;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public int getIs_blog() {
        return is_blog;
    }

    public void setIs_blog(int is_blog) {
        this.is_blog = is_blog;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFrom_type() {
        return from_type;
    }

    public void setFrom_type(String from_type) {
        this.from_type = from_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
