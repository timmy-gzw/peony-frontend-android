package com.tftechsz.moment.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.moment.entity.CommentListItem;
import com.tftechsz.moment.entity.LikeListItem;

import java.util.List;

public interface ITrendCommentView extends MvpView {

    //评论列表
    void getTrendCommentSuccess(List<CommentListItem> data);


    //点赞列表
    void getTrendPraiseSuccess(List<LikeListItem> data);

    void delTrendCommentSuccess(boolean isDel);
}
