package com.tftechsz.moment.other;

public
/**
 *  包 名 : com.tftechsz.moment.other

 *  描 述 : TODO
 */
class CommentEvent {
    private int comment_id;
    private String user_name;
    private int type;
    private int blogId; //动态id

    public CommentEvent(int type, int blogId) {
        this.type = type;
        this.blogId = blogId;
    }

    public CommentEvent(int user_id, String user_name) {
        this.comment_id = user_id;
        this.user_name = user_name;
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public int getType() {
        return type;    //0评论回复弹框 -1评论删除  1点赞 2评论  -2帖子删除
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
