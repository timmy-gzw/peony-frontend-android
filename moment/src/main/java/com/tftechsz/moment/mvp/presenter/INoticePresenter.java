package com.tftechsz.moment.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.moment.api.TrendApiService;
import com.tftechsz.moment.mvp.IView.INoticeView;
import com.tftechsz.moment.mvp.entity.NoticeBean;

import java.util.List;

public
/**
 *  包 名 : com.tftechsz.moment.mvp.presenter

 *  描 述 : TODO
 */
class INoticePresenter extends BasePresenter<INoticeView> {

    public TrendApiService service;

    public INoticePresenter() {
        service = RetrofitManager.getInstance().createBlogApi(TrendApiService.class);
    }

    public void getNotice(int page) {
        addNet(service.getNotice(page)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<NoticeBean>>>() {

                    @Override
                    public void onSuccess(BaseResponse<List<NoticeBean>> response) {
                        if (null == getView()) return;
                        getView().getNoticeSucess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().getNoticeFial();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getView().getNoticeFial();
                    }
                }));
    }


    /**
     * 动态评论
     */
    public void trendComment( int comment_id, String content) {
        addNet(service.trendCommentReply(comment_id, content)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {

                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().commentSuccess(response.getData());
                    }

                }));

    }

    public void get_info(int blog_id) {
        addNet(service.get_info(blog_id)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CircleBean>>() {

                    @Override
                    public void onSuccess(BaseResponse<CircleBean> response) {
                        if (null == getView()) return;
                        getView().getInfoSucess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        //super.onFail(code, msg);
                        getView().getInfoFial(Interfaces.notice_blog_delete);
                    }
                }));
    }
}
