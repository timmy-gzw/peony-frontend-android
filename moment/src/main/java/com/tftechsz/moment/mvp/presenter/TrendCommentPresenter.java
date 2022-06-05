package com.tftechsz.moment.mvp.presenter;


import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.moment.api.TrendApiService;
import com.tftechsz.moment.entity.CommentListItem;
import com.tftechsz.moment.entity.LikeListItem;
import com.tftechsz.moment.mvp.IView.ITrendCommentView;

import java.util.List;


public class TrendCommentPresenter extends BasePresenter<ITrendCommentView> {
    public TrendApiService service;

    public TrendCommentPresenter() {
        service = RetrofitManager.getInstance().createBlogApi(TrendApiService.class);
    }

    /**
     * 获取动态评论
     */
    public void getTrendComment(int page, int blogId) {
        addNet(service.getTrendComment(page, blogId).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<CommentListItem>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<CommentListItem>> response) {
                        getView().getTrendCommentSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));

    }


    /**
     * 获取动态点赞
     */
    public void getTrendPraise(int page, int blogId) {
        addNet(service.getTrendPraise(page, blogId).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<LikeListItem>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<LikeListItem>> response) {
                        getView().getTrendPraiseSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));

    }


    /**
     * 获取动态评论
     */
    public void delTrendComment(int comment_id) {
        addNet(service.delTrendComment(comment_id).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        getView().delTrendCommentSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));

    }


}
