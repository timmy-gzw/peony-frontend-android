package com.tftechsz.moment.other;

/**
 * 包 名 : com.tftechsz.moment.other
 * 描 述 : TODO
 */
public class CommentPraiseAccostEvent {

    public int type; //0点赞成功  1搭讪成功
    public int blog_id;
    public int praises;
    public int userId;

    public CommentPraiseAccostEvent(int blog_id, int praises) {
        this.blog_id = blog_id;
        this.praises = praises;
    }

    public CommentPraiseAccostEvent(int userId) {
        this.type = 1;
        this.userId = userId;
    }

}
