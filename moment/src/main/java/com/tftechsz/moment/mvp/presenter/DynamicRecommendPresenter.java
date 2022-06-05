package com.tftechsz.moment.mvp.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationViewNew;
import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.entity.CircleBean;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.moment.api.TrendApiService;
import com.tftechsz.moment.mvp.IView.IDynamicView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class DynamicRecommendPresenter extends BasePresenter<IDynamicView> {

    public TrendApiService service;
    AccostService accostService;
    AttentionService attentionService;
    MineService mineService;

    public DynamicRecommendPresenter() {
        EventBus.getDefault().register(this);
        service = RetrofitManager.getInstance().createBlogApi(TrendApiService.class);
        accostService = ARouter.getInstance().navigation(AccostService.class);
        attentionService = ARouter.getInstance().navigation(AttentionService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
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
                        getView().getInfoFial(Interfaces.notice_blog_delete);
                    }
                }));
    }

    /**
     * 同城动态列表 推荐 附近  关注
     */
    public void getDynamicRecommend(String dynamicType, int page) {
        addNet(service.getDynamicRecommend(dynamicType, page)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<CircleBean>>>() {

                    @Override
                    public void onSuccess(BaseResponse<List<CircleBean>> response) {
                        if (null == getView()) return;
                        getView().getTrendSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getTrendFail(code, msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null == getView()) return;
                        getView().getTrendError();
                    }
                }));
    }

    /**
     * 获取个人动态列表数据
     */
    public void getUserTrend(int page, String userId) {
        addNet(service.getUserTrend(page, userId)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<CircleBean>>>() {

                    @Override
                    public void onSuccess(BaseResponse<List<CircleBean>> response) {
                        if (null == getView()) return;
                        getView().getTrendSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getTrendError();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null == getView()) return;
                        getView().getTrendError();
                    }
                }));
    }

    /**
     * 获取个人动态列表数据
     */
    public void getSelfTrend(int page) {
        addNet(service.getSelfTrend(page)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<CircleBean>>>() {

                    @Override
                    public void onSuccess(BaseResponse<List<CircleBean>> response) {
                        if (null == getView()) return;
                        getView().getTrendSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getTrendError();

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null == getView()) return;
                        getView().getTrendError();
                    }
                }));
    }


    @Subscribe
    public void event(IncompatibleClassChangeError e) {

    }


    /**
     * 点赞
     **/
    public void blogPraise(int position, int blog_id) {
        addNet(service.praise(blog_id)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().praiseSuccess(position, response.getData());
                    }

                }));
    }


    public void startAnimation(ImageView ivAccost, ImageView ivChat, LottieAnimationViewNew lottieAnimationView) { //搭讪按钮动画  头像特效   播放声音
        if (lottieAnimationView != null) {
            ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.ACCOST);
            lottieAnimationView.setVisibility(View.VISIBLE);
            if (ivAccost != null)
                ivAccost.setVisibility(View.INVISIBLE);
            lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lottieAnimationView.setVisibility(View.GONE);
                    if (ivAccost != null) {
                        ivAccost.setVisibility(View.GONE);
                    }
                    if (ivChat != null) {
                        ivChat.setVisibility(View.VISIBLE);
                        ObjectAnimator oa3 = ObjectAnimator.ofFloat(ivChat, "scaleX", 0.0f, 1.0f);
                        ObjectAnimator oa4 = ObjectAnimator.ofFloat(ivChat, "scaleY", 0.0f, 1.0f);
                        AnimatorSet as2 = new AnimatorSet();
                        as2.play(oa3).with(oa4);
                        as2.setDuration(300);
                        as2.start();
                    }
                }
            });
            lottieAnimationView.playAnimation();
        }


    }


    /**
     * 删除动态
     **/
    public void delTrend(int position, int blog_id) {
        addNet(service.delTrend(blog_id)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().deleteTrendSuccess(position, response.getData());
                    }

                }));
    }

    /**
     * 次数添加
     **/
    public void trendViewCount(String blog_id) {
        addNet(service.trendViewCount(blog_id)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {

                    }
                }));
    }


    /**
     * 搭讪用户
     */
    public void accostUser(int position, String userId, int accost_type) {
        accostService.accostUser(userId, accost_type, 4, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                if (null == getView()) return;
                getView().accostUserSuccess(position, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });

    }


    /**
     * 关注用户
     */
    public void attentionUser(int userId) {
        attentionService.attentionUser(userId, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (null == getView()) return;
                getView().attentionSuccess(response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });


    }

    /**
     * 动态评论
     */
    public void trendComment(int blog_id, int comment_id, String content) {
        addNet((comment_id == 0 ?
                service.trendComment(blog_id, content)
                : service.trendCommentReply(comment_id, content))
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {

                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().commentSuccess(response.getData());
                    }

                }));

    }


    /**
     * 检查私信次数
     */
    public void getMsgCheck(String userId) {
        mineService.getMsgCheck(userId, new ResponseObserver<BaseResponse<MsgCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<MsgCheckDto> response) {
                if (null == getView()) return;
                getView().getCheckMsgSuccess(userId, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                if (null == getView()) return;
            }
        });

    }

}
